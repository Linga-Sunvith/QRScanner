package com.techmanina.qrscanner;//package com.example.pinelabsdemo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

        @POST("UploadBilledTransaction")
        Call<UploadResponse> uploadBilledTransaction(@Body UploadRequest request);

        @POST("GetCloudBasedTxnStatus")
        Call<StatusResponse> getTxnStatus(@Body StatusRequest request);

        @POST("CancelTransaction")
        Call<CancelResponse> cancelTransaction(@Body CancelRequest request);
    }


