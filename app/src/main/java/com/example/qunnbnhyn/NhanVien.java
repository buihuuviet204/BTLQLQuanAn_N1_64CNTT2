package com.example.qunnbnhyn;

public class NhanVien {
    private String maNhanVien;
    private String name;
    private String shift;
    private String checkIn;
    private String checkOut;

    public NhanVien() {
    }

    public NhanVien(String maNhanVien, String name) {
        this.maNhanVien = maNhanVien;
        this.name = name;

    }

    // Getters and Setters
    public String getMaNhanVien() {
        return maNhanVien;
    }
    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getShift() {
        return shift;
    }
    public void setShift(String shift) { this.shift = shift; }

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
    }
}