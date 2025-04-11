package com.example.qunnbnhyn.datmon;

import java.io.Serializable;
import java.util.HashMap;

public class CTHD implements Serializable {
    private String maHD;
    private HashMap<String,Integer> CTDH;

    public CTHD() {
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public HashMap<String, Integer> getCTDH() {
        return CTDH;
    }

    public void setCTDH(HashMap<String, Integer> CTDH) {
        this.CTDH = CTDH;
    }

    public CTHD(String maHD, HashMap<String, Integer> CTDH) {
        this.maHD = maHD;
        this.CTDH = CTDH;
    }
}
