package com.techmanina.qrscanner;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
//    device serial number: 2270032607
//    MerchantID: 29610
//    Clientid: 1013483
//    SecurityToken: a4c9741b-2889-47b8-be2f-ba42081a246e
//    StoreID: 1221258
    private ApiService apiService;
    private Handler handler = new Handler();

    // Merchant credentials (UAT)
    private final int merchantID = 29610;
    private final String token = "a4c9741b-2889-47b8-be2f-ba42081a246e";
    private final String storeID = "1221258";
    private final int clientID = 1013483;
    private final int amount = 500;
    private long ptrid = 0;
    private int pollCount = 0; // To avoid infinite loop in test mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("PINE", "Starting Pine Labs Cloud Integration test...");

        // Initialize API client
        apiService = ApiClient.getClient().create(ApiService.class);

        // Start transaction flow
        startTransaction();
    }

    @Nullable
    // Step 1: UploadBilledTransaction
    private void startTransaction() {
        Log.d("PINE", "Uploading billed transaction...");

        UploadRequest request = new UploadRequest(
                String.valueOf(System.currentTimeMillis()), // unique TransactionNumber
                1,                      // Sequence number
                "10",                    // AllowedPaymentMode (1 = Card)
                String.valueOf(amount), // Amount
                "user123",              // UserID
                merchantID,
                token,
                storeID,
                clientID,
                5                       // AutoCancelDurationInMinutes
        );

        apiService.uploadBilledTransaction(request).enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UploadResponse res = response.body();
                    Log.i("PINE", "Upload Response: " + new Gson().toJson(res));

                    if (res.ResponseCode == 0) {
                        ptrid = res.PlutusTransactionReferenceID;
                        Log.i("PINE", "PTRID received: " + ptrid);
                        Log.i("PINE", "Ask cashier to enter PTRID on terminal.");
                        pollStatus(); // Start polling status
                    } else {
                        Log.e("PINE", "Upload failed: " + res.ResponseMessage);
                    }
                } else {
                    Log.e("PINE", "Upload failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                Log.e("PINE", "Upload Error: " + t.getMessage());
            }
        });
    }

    // Step 2: Poll GetCloudBasedTxnStatus
    private void pollStatus() {
        pollCount++;
        if (pollCount > 30) { // safety stop after 1 minute (12 * 5s)
            Log.w("PINE", "Polling timeout reached, cancelling transaction...");
            cancelTransaction();
            return;
        }

        StatusRequest req = new StatusRequest(merchantID, token, storeID, clientID, ptrid);
        Log.d("PINE", "Checking status for PTRID: " + ptrid);

        apiService.getTxnStatus(req).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StatusResponse res = response.body();
                    Log.i("PINE", "Status Response: " + new Gson().toJson(res));
                    if (res.TransactionData != null) {
                        String status = res.TransactionData.TransactionStatus;
                        Log.i("PINE", "Transaction Status: " + status);

                        switch (status.toUpperCase()) {
                            case "SUCCESS":
                                Log.i("PINE", "✅ Payment Successful!");
                                break;
                            case "FAILED":
                                Log.e("PINE", "❌ Payment Failed!");
                                break;
                            case "CANCELLED":
                                Log.w("PINE", "⚠️ Payment Cancelled!");
                                break;
                            default:
                                Log.d("PINE", "⏳ Still waiting... (" + pollCount + ")");
                                handler.postDelayed(() -> pollStatus(), 5000);
                                break;
                        }
                    }
                } else {
                    Log.e("PINE", "Status check failed: " + response.message());
                    handler.postDelayed(() -> pollStatus(), 5000);
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                Log.e("PINE", "Status check error: " + t.getMessage());
                handler.postDelayed(() -> pollStatus(), 5000);
            }
        });
    }

    // Step 3: CancelTransaction (optional)
    private void cancelTransaction() {
        Log.w("PINE", "Cancelling transaction with PTRID: " + ptrid);

        CancelRequest cancelReq = new CancelRequest(
                merchantID, token, storeID, clientID, ptrid, amount);

        apiService.cancelTransaction(cancelReq).enqueue(new Callback<CancelResponse>() {
            @Override
            public void onResponse(Call<CancelResponse> call, Response<CancelResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.w("PINE", "Cancel Response: " + new Gson().toJson(response.body()));
                } else {
                    Log.e("PINE", "Cancel failed: " + response.message());

                }
            }

            @Override
            public void onFailure(Call<CancelResponse> call, Throwable t) {
                Log.e("PINE", "Cancel error: " + t.getMessage());
            }
        });
    }
}
