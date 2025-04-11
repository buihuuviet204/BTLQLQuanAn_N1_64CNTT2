package com.example.qunnbnhyn.dondat;

public class ItemMonGoi {
    private String tenMon;
    private String soLuong;

    public ItemMonGoi() {
    }

    public ItemMonGoi(String tenMon, String soLuong) {
        this.tenMon = tenMon;
        this.soLuong = soLuong;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public String getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(String soLuong) {
        this.soLuong = soLuong;
    }
}
