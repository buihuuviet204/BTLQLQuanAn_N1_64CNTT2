package com.example.qunnbnhyn;

public class Invoice {
    private String id;
    private String maKhach;
    private Long timestamp;
    private Long tongTien;
    private String paymentMethod, dateStr;

    public Invoice(String id, String maKhach, Long timestamp, Long tongTien, String paymentMethod, String dateStr) {
        this.id = id;
        this.maKhach = maKhach;
        this.timestamp = timestamp;
        this.tongTien = tongTien;
        this.paymentMethod = paymentMethod;
        this.dateStr = dateStr;
    }

    public String getId() { return id; }
    public String getMaKhach() { return maKhach; }
    public Long getTimestamp() { return timestamp; }
    public Long getTongTien() { return tongTien; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getDateStr() { return dateStr; }
}