package com.example.qunnbnhyn.datmon;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class HoaDon implements Serializable {
    private String maHoaDon;
    private String maKhach;
    private String ngLap;
    private HashMap<String,Integer> CTDH;

    public HashMap<String, Integer> getCTDH() {
        return CTDH;
    }

    public void setCTDH(HashMap<String, Integer> CTDH) {
        this.CTDH = CTDH;
    }

    public HoaDon(String maHoaDon,   String maKhach, String ngLap, HashMap<String, Integer> CTDH) {
        this.maHoaDon = maHoaDon;
        this.maKhach = maKhach;
        this.ngLap = ngLap;
        this.CTDH = CTDH;
    }

    public HoaDon(String maHoaDon,   String maKhach, String ngLap) {
        this.maHoaDon = maHoaDon;
        this.maKhach = maKhach;
        this.ngLap = ngLap;
    }

    public HoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }



    public HoaDon() {
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }



    private double tongTien;

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public HoaDon( String maHoaDon, String maKhach, String ngLap, HashMap<String, Integer> CTDH, double tongTien) {
        this.maHoaDon = maHoaDon;
        this.maKhach = maKhach;
        this.ngLap = ngLap;
        this.CTDH = CTDH;
        this.tongTien = tongTien;
    }

    public String getMaKhach() {
        return maKhach;
    }

    public void setMaKhach(String maKhach) {
        this.maKhach = maKhach;
    }

    public String getNgLap() {
        return ngLap;
    }

    public void setNgLap(String ngLap) {
        this.ngLap = ngLap;
    }



}
