package com.example.qunnbnhyn.QLM;


import java.io.Serializable;

public class MonAn implements Serializable {
    private String maMon;
    private String tenmon;
    private String urlImageMonan;
    private double giaban;
    private String loai;

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        this.maMon = maMon;
    }

    public MonAn(String name) {
        this.tenmon = name;
    }

    public MonAn() {
    }

    public MonAn(String name, double giaban, String loai) {
        this.tenmon = name;
        this.giaban = giaban;
        this.loai = loai;
    }

    public String getLoai() {
        return loai;
    }

    public void setLoai(String loai) {
        this.loai = loai;
    }

    public MonAn(String name, String imageMonAn, double giaban, String loai) {
        this.tenmon = name;
        this.urlImageMonan = imageMonAn;
        this.giaban = giaban;
        this.loai = loai;
    }

    public MonAn(String name, String imageMonAn, double giaban) {
        this.tenmon = name;
        this.urlImageMonan = imageMonAn;
        this.giaban = giaban;
    }

    public String getName() {
        return tenmon;
    }

    public void setName(String name) {
        this.tenmon = name;
    }

    public String getImageMonAn() {
        return urlImageMonan;
    }

    public void setImageMonAn(String imageMonAn) {
        this.urlImageMonan = imageMonAn;
    }

    public double getGiaban() {
        return giaban;
    }

    public void setGiaban(double giaban) {
        this.giaban = giaban;
    }
}
