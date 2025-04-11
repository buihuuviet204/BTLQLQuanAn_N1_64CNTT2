package com.example.qunnbnhyn.QLNV;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qunnbnhyn.R;

public class QuanLyNhanVien extends AppCompatActivity {
    private Button btnAdd, btnEdit, btnDelete, btnTimeKeeping;
    private ImageButton btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quan_ly_nhan_vien);

        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAdd = (Button) findViewById(R.id.btn_them);
        btnAdd.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent themNhanVien = new Intent(QuanLyNhanVien.this, ThemNhanVien.class);
                startActivity(themNhanVien);
            }
        });

        btnEdit = (Button) findViewById(R.id.btn_sua);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent suaThongTin = new Intent(QuanLyNhanVien.this, ChinhSuaThongTin.class);
                startActivity(suaThongTin);
            }
        });

        btnDelete = (Button) findViewById(R.id.btn_xoa);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent xoaNhanVien = new Intent(QuanLyNhanVien.this, XoaNhanVien.class);
                startActivity(xoaNhanVien);
            }
        });
    }
}