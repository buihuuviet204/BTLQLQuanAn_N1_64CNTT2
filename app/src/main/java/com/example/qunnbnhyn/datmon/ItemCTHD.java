package com.example.qunnbnhyn.datmon;

public class ItemCTHD {
    private String tenMon;
    private String giaBan;
    private String soLuong;
    private String thanhTien;

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public String getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(String giaBan) {
        this.giaBan = giaBan;
    }

    public String getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(String soLuong) {
        this.soLuong = soLuong;
    }

    public String getThanhTien() {
        return ""+( Double.parseDouble(soLuong) * Double.parseDouble(giaBan));
    }

    public void setThanhTien(String thanhTien) {
        this.thanhTien = thanhTien;
    }

    public ItemCTHD(String tenMon, String giaBan, String soLuong) {
        this.tenMon = tenMon;
        this.giaBan = giaBan;
        this.soLuong = soLuong;
    }

    public ItemCTHD(String tenMon, String giaBan, String soLuong, String thanhTien) {
        this.tenMon = tenMon;
        this.giaBan = giaBan;
        this.soLuong = soLuong;
        this.thanhTien = thanhTien;
    }
}
