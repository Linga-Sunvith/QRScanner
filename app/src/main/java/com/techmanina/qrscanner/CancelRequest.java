package com.techmanina.qrscanner;

public class CancelRequest {
    public int MerchantID;
    public String SecurityToken;
    public String StoreID;
    public int Clientid;
    public long PlutusTransactionReferenceID;
    public int Amount;

    public CancelRequest(int merchantID, String token, String storeID, int clientID, long ptrid, int amount) {
        this.MerchantID = merchantID;
        this.SecurityToken = token;
        this.StoreID = storeID;
        this.Clientid = clientID;
        this.PlutusTransactionReferenceID = ptrid;
        this.Amount = amount;
    }
}
