package com.example.qunnbnhyn;

public class Dish {
    private String id;
    private String name;
    private String imageMonAn;
    private long giaBan;
    private long soLuongBanRa; // Số lượng bán ra
    private long doanhThu;     // Doanh thu

    public Dish(String id, String name, String imageMonAn, long giaBan) {
        this.id = id;
        this.name = name;
        this.imageMonAn = imageMonAn;
        this.giaBan = giaBan;
        this.soLuongBanRa = 0;
        this.doanhThu = 0;
    }

    // Getters và setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getImageMonAn() { return imageMonAn; }
    public long getGiaBan() { return giaBan; }
    public long getSoLuongBanRa() { return soLuongBanRa; }
    public long getDoanhThu() { return doanhThu; }
    public void setSoLuongBanRa(long soLuongBanRa) { this.soLuongBanRa = soLuongBanRa; }
    public void setDoanhThu(long doanhThu) { this.doanhThu = doanhThu; }
}