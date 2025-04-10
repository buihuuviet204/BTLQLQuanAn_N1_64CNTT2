package com.example.qunnbnhyn.dondat;

import com.example.qunnbnhyn.QLM.MonAn;

import java.util.List;

public class ItemDonDat {
    private String soBan;
    List<ItemMonGoi> listMon;

    public String getSoBan() {
        return soBan;
    }

    public void setSoBan(String soBan) {
        this.soBan = soBan;
    }

    public List<ItemMonGoi> getListMon() {
        return listMon;
    }

    public void setListMon(List<ItemMonGoi> listMon) {
        this.listMon = listMon;
    }

    public ItemDonDat(String soBan, List<ItemMonGoi> listMon) {
        this.soBan = soBan;
        this.listMon = listMon;
    }
}
