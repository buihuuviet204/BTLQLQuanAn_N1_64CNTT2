package com.example.qunnbnhyn;

public class NhanVien {
    private String maNhanVien;
    private String name;
    private String shift;

    public NhanVien() {
    }

    public NhanVien(String maNhanVien, String name, String birthDate, String gender, String email,
                    String phone, String hometown, String position, String password, String avatarBase64) {
        this.maNhanVien = maNhanVien;
        this.name = name;

    }

    // Getters and Setters
    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }
}