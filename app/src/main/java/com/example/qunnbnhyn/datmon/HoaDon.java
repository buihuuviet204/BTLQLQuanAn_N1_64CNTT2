package com.example.qunnbnhyn.datmon;

import java.io.Serializable;
import java.util.Date;

public class HoaDon implements Serializable {
    private String maHoaDon;
    private String soBan;
    private double tongtien;
    private boolean trangthai;
    private String maKhach;
    private Date ngLap;

    public HoaDon(String maHoaDon, String soBan, double tongtien, boolean trangthai, String maKhach, Date ngLap) {
        this.maHoaDon = maHoaDon;
        this.soBan = soBan;
        this.tongtien = tongtien;
        this.trangthai = trangthai;
        this.maKhach = maKhach;
        this.ngLap = ngLap;
    }

    public HoaDon(String maHoaDon, String soBan, double tongtien, boolean trangthai) {
        this.maHoaDon = maHoaDon;
        this.soBan = soBan;
        this.tongtien = tongtien;
        this.trangthai = trangthai;
    }

    public boolean isTrangthai() {
        return trangthai;
    }

    public void setTrangthai(boolean trangthai) {
        this.trangthai = trangthai;
    }

    public HoaDon() {
        trangthai = false;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getSoBan() {
        return soBan;
    }

    public void setSoBan(String soBan) {
        this.soBan = soBan;
    }

    public double getTongtien() {
        return tongtien;
    }

    public String getMaKhach() {
        return maKhach;
    }

    public void setMaKhach(String maKhach) {
        this.maKhach = maKhach;
    }

    public Date getNgLap() {
        return ngLap;
    }

    public void setNgLap(Date ngLap) {
        this.ngLap = ngLap;
    }

    public void setTongtien(double tongtien) {
        this.tongtien = tongtien;
    }

    public HoaDon(String maHoaDon, String soBan, double tongtien) {
        this.maHoaDon = maHoaDon;
        this.soBan = soBan;
        this.tongtien = tongtien;
        this.trangthai = false;
    }
}
