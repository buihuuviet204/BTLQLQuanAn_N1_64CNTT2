package com.example.qunnbnhyn.datmon;

import android.widget.EditText;

import com.example.qunnbnhyn.QLM.MonAn;

import java.util.HashMap;

public interface OnTotalChangeListener {
    void onTotalChanged(double total);
    void onSlgMAChanged(HashMap<MonAn, Integer> dsMongoi);
    void monAnSearched(MonAn monAn);
    void onSlgChanged(HashMap<String,Integer> dsMongoi, EditText editText);
}
