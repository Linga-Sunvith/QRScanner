package com.techmanina.qrscanner;

public class UploadRequest {
    public String TransactionNumber;
    public int SequenceNumber;
    public String AllowedPaymentMode;
    public String Amount;
    public String UserID;
    public int MerchantID;
    public String SecurityToken;
    public String StoreID;
    public int Clientid;
    public int AutoCancelDurationInMinutes;

    public UploadRequest(String txnNumber, int seq, String paymentMode, String amt, String user,
                         int merchantID, String token, String storeID, int clientID, int cancelTime) {
        this.TransactionNumber = txnNumber;
        this.SequenceNumber = seq;
        this.AllowedPaymentMode = paymentMode;
        this.Amount = amt;
        this.UserID = user;
        this.MerchantID = merchantID;
        this.SecurityToken = token;
        this.StoreID = storeID;
        this.Clientid = clientID;
        this.AutoCancelDurationInMinutes = cancelTime;
    }
}
