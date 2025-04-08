package com.example.qunnbnhyn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ThanhToan extends AppCompatActivity {
    private Button btnBan1, btnBan2, btnBan3, btnBan4, btnBan5, btnBan6, btnBan7, btnBan8, btnBan9, btnBan10, btnBan11, btnBan12, btnBan13, btnBan14, btnBan15;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thanh_toan);

        btnBan1 = (Button) findViewById(R.id.btn_ban1);
        btnBan2 = (Button) findViewById(R.id.btn_ban2);
        btnBan3 = (Button) findViewById(R.id.btn_ban3);
        btnBan4 = (Button) findViewById(R.id.btn_ban4);
        btnBan5 = (Button) findViewById(R.id.btn_ban5);
        btnBan6 = (Button) findViewById(R.id.btn_ban6);
        btnBan7 = (Button) findViewById(R.id.btn_ban7);
        btnBan8 = (Button) findViewById(R.id.btn_ban8);
        btnBan9 = (Button) findViewById(R.id.btn_ban9);
        btnBan10 = (Button) findViewById(R.id.btn_ban10);
        btnBan11 = (Button) findViewById(R.id.btn_ban11);
        btnBan12 = (Button) findViewById(R.id.btn_ban12);
        btnBan13 = (Button) findViewById(R.id.btn_ban13);
        btnBan14 = (Button) findViewById(R.id.btn_ban14);
        btnBan15 = (Button) findViewById(R.id.btn_ban15);

        // Thiết lập OnClickListener cho từng Button
        btnBan1.setOnClickListener((View.OnClickListener) this);
        btnBan2.setOnClickListener((View.OnClickListener) this);
        btnBan3.setOnClickListener((View.OnClickListener) this);
        btnBan4.setOnClickListener((View.OnClickListener) this);
        btnBan5.setOnClickListener((View.OnClickListener) this);
        btnBan6.setOnClickListener((View.OnClickListener) this);
        btnBan7.setOnClickListener((View.OnClickListener) this);
        btnBan8.setOnClickListener((View.OnClickListener) this);
        btnBan9.setOnClickListener((View.OnClickListener) this);
        btnBan10.setOnClickListener((View.OnClickListener) this);
        btnBan11.setOnClickListener((View.OnClickListener) this);
        btnBan12.setOnClickListener((View.OnClickListener) this);
    }
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
        }

        intent.putExtra("soBan", soBan);
        startActivity(intent);
    }
}