package com.example.qunnbnhyn.datmon;

import com.example.qunnbnhyn.QLM.MonAn;

import java.util.List;

public class ListMon {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_LIST = 1;
    private int type;
    private String loai;
    private List<MonAn> listmon;

    public ListMon(int type, String loai, List<MonAn> listmon) {
        this.type = type;
        this.loai = loai;
        this.listmon = listmon;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLoai() {
        return loai;
    }

    public void setLoai(String loai) {
        this.loai = loai;
    }

    public List<MonAn> getListmon() {
        return listmon;
    }

    public void setListmon(List<MonAn> listmon) {
        this.listmon = listmon;
    }
}
