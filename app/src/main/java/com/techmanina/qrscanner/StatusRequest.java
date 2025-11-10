package com.techmanina.qrscanner;

public class StatusRequest {
    public int MerchantID;
    public String SecurityToken;
    public String StoreID;
    public int Clientid;
    public long PlutusTransactionReferenceID;

    public StatusRequest(int merchantID, String token, String storeID, int clientID, long ptrid) {
        this.MerchantID = merchantID;
        this.SecurityToken = token;
        this.StoreID = storeID;
        this.Clientid = clientID;
        this.PlutusTransactionReferenceID = ptrid;
    }
}
