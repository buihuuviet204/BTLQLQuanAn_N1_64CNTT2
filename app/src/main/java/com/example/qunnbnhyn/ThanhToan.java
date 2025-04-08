package com.example.qunnbnhyn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ThanhToan extends AppCompatActivity implements View.OnClickListener {
    private CardView btnBan1, btnBan2, btnBan3, btnBan4, btnBan5, btnBan6, btnBan7, btnBan8, btnBan9, btnBan10, btnBan11, btnBan12, btnBan13, btnBan14, btnBan15;
    private ImageButton imbBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);

        btnBan1 = findViewById(R.id.btn_ban1);
        btnBan2 = findViewById(R.id.btn_ban2);
        btnBan3 = findViewById(R.id.btn_ban3);
        btnBan4 = findViewById(R.id.btn_ban4);
        btnBan5 = findViewById(R.id.btn_ban5);
        btnBan6 = findViewById(R.id.btn_ban6);
        btnBan7 = findViewById(R.id.btn_ban7);
        btnBan8 = findViewById(R.id.btn_ban8);
        btnBan9 = findViewById(R.id.btn_ban9);
        btnBan10 = findViewById(R.id.btn_ban10);
        btnBan11 = findViewById(R.id.btn_ban11);
        btnBan12 = findViewById(R.id.btn_ban12);
        btnBan13 = findViewById(R.id.btn_ban13);
        btnBan14 = findViewById(R.id.btn_ban14);
        btnBan15 = findViewById(R.id.btn_ban15);
        imbBack = findViewById(R.id.imb_back);
        imbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Thiết lập OnClickListener cho từng CardView
        btnBan1.setOnClickListener(this);
        btnBan2.setOnClickListener(this);
        btnBan3.setOnClickListener(this);
        btnBan4.setOnClickListener(this);
        btnBan5.setOnClickListener(this);
        btnBan6.setOnClickListener(this);
        btnBan7.setOnClickListener(this);
        btnBan8.setOnClickListener(this);
        btnBan9.setOnClickListener(this);
        btnBan10.setOnClickListener(this);
        btnBan11.setOnClickListener(this);
        btnBan12.setOnClickListener(this);
        btnBan13.setOnClickListener(this);
        btnBan14.setOnClickListener(this);
        btnBan15.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, ThanhToanChiTiet.class);
        int soBan = 0;

        if (v.getId() == R.id.btn_ban1) {
            soBan = 1;
        } else if (v.getId() == R.id.btn_ban2) {
            soBan = 2;
        } else if (v.getId() == R.id.btn_ban3) {
            soBan = 3;
        } else if (v.getId() == R.id.btn_ban4) {
            soBan = 4;
        } else if (v.getId() == R.id.btn_ban5) {
            soBan = 5;
        } else if (v.getId() == R.id.btn_ban6) {
            soBan = 6;
        } else if (v.getId() == R.id.btn_ban7) {
            soBan = 7;
        } else if (v.getId() == R.id.btn_ban8) {
            soBan = 8;
        } else if (v.getId() == R.id.btn_ban9) {
            soBan = 9;
        } else if (v.getId() == R.id.btn_ban10) {
            soBan = 10;
        } else if (v.getId() == R.id.btn_ban11) {
            soBan = 11;
        } else if (v.getId() == R.id.btn_ban12) {
            soBan = 12;
        } else if (v.getId() == R.id.btn_ban13) {
            soBan = 13;
        } else if (v.getId() == R.id.btn_ban14) {
            soBan = 14;
        } else if (v.getId() == R.id.btn_ban15) {
            soBan = 15;
        }

        intent.putExtra("soBan", soBan);
        startActivity(intent);
    }
}