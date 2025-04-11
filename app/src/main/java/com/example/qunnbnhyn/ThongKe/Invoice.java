package com.example.qunnbnhyn.ThongKe;

public class Invoice {
    private String id;
    private String tenKhach; // Thay maKhach thành tenKhach
    private Long timestamp;
    private Long tongTien;
    private String paymentMethod, dateStr;

    public Invoice(String id, String tenKhach, Long timestamp, Long tongTien, String paymentMethod, String dateStr) {
        this.id = id;
        this.tenKhach = tenKhach; // Thay maKhach thành tenKhach
        this.timestamp = timestamp;
        this.tongTien = tongTien;
        this.paymentMethod = paymentMethod;
        this.dateStr = dateStr;
    }

    public String getId() { return id; }
    public String getTenKhach() { return tenKhach; } // Thay getMaKhach thành getTenKhach
    public Long getTimestamp() { return timestamp; }
    public Long getTongTien() { return tongTien; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getDateStr() { return dateStr; }
}
