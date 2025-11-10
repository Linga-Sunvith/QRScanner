package com.techmanina.qrscanner;

public class StatusResponse {
    public int ResponseCode;
    public String ResponseMessage;
    public TransactionData TransactionData;

    public static class TransactionData {
        public String TransactionStatus;
        public String PaymentMode;
        public String RRN;
        public String ApprovalCode;
        public String Amount;
    }
}
