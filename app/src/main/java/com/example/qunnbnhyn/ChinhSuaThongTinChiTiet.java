package com.example.qunnbnhyn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;
import android.widget.AutoCompleteTextView;

public class ChinhSuaThongTinChiTiet extends AppCompatActivity {
    private TextInputEditText txtMaNhanVien, txtHoTen, txtNgaySinh, txtEmail, txtSoDienThoai, txtQueQuan, txtMatKhau;
    private AutoCompleteTextView actvGioiTinh, actvChucVu;
    private Button btnSua;
    private ImageButton btnBack;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ChinhSuaThongTinChiTiet", "onCreate started");

        try {
            setContentView(R.layout.activity_chinh_sua_thong_tin_chi_tiet);
            Log.d("ChinhSuaThongTinChiTiet", "Layout loaded successfully");
        } catch (Exception e) {
            Log.e("ChinhSuaThongTinChiTiet", "Error loading layout: " + e.getMessage());
            Toast.makeText(this, "Lỗi tải giao diện: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Khởi tạo các view
        try {
            txtMaNhanVien = findViewById(R.id.txt_ma_nhan_vien);
            txtHoTen = findViewById(R.id.txt_ho_ten);
            txtNgaySinh = findViewById(R.id.txt_date);
            txtEmail = findViewById(R.id.txt_email);
            txtSoDienThoai = findViewById(R.id.txt_so_dien_thoai);
            txtQueQuan = findViewById(R.id.txt_que_quan);
            txtMatKhau = findViewById(R.id.txt_mat_khau);
            actvGioiTinh = findViewById(R.id.actv_gioi_tinh);
            actvChucVu = findViewById(R.id.actv_chuc_vu);
            btnSua = findViewById(R.id.btn_sua);
            btnBack = findViewById(R.id.btn_back);
            Log.d("ChinhSuaThongTinChiTiet", "Views initialized successfully");
        } catch (Exception e) {
            Log.e("ChinhSuaThongTinChiTiet", "Error initializing views: " + e.getMessage());
            Toast.makeText(this, "Lỗi khởi tạo view: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Kết nối Firebase
        database = FirebaseDatabase.getInstance().getReference("Employees");

        // Thiết lập dropdown cho Giới tính
        String[] gioiTinhOptions = {"Nam", "Nữ", "Khác"};
        ArrayAdapter<String> gioiTinhAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, gioiTinhOptions);
        actvGioiTinh.setAdapter(gioiTinhAdapter);

        // Thiết lập dropdown cho Chức vụ
        String[] chucVuOptions = {"Nhân viên", "Quản lý"};
        ArrayAdapter<String> chucVuAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, chucVuOptions);
        actvChucVu.setAdapter(chucVuAdapter);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String maNhanVien = intent.getStringExtra("maNhanVien");
        String name = intent.getStringExtra("name");
        String birthDate = intent.getStringExtra("birthDate");
        String gender = intent.getStringExtra("gender");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");
        String hometown = intent.getStringExtra("hometown");
        String position = intent.getStringExtra("position");
        String password = intent.getStringExtra("password");

        // Hiển thị dữ liệu lên giao diện
        try {
            txtMaNhanVien.setText(maNhanVien);
            txtHoTen.setText(name);
            txtNgaySinh.setText(birthDate);
            actvGioiTinh.setText(gender, false);
            txtEmail.setText(email);
            txtSoDienThoai.setText(phone);
            txtQueQuan.setText(hometown);
            actvChucVu.setText(position, false);
            txtMatKhau.setText(password);
            Log.d("ChinhSuaThongTinChiTiet", "Data loaded: " + maNhanVien);
        } catch (Exception e) {
            Log.e("ChinhSuaThongTinChiTiet", "Error setting data to views: " + e.getMessage());
            Toast.makeText(this, "Lỗi hiển thị dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Vô hiệu hóa chỉnh sửa mã nhân viên
        txtMaNhanVien.setEnabled(false);

        // Sự kiện nút Back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Sự kiện nút Sửa
        btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNhanVien(maNhanVien);
            }
        });
    }

    private void updateNhanVien(String maNhanVien) {
        String hoTen = txtHoTen.getText().toString().trim();
        String ngaySinh = txtNgaySinh.getText().toString().trim();
        String gioiTinh = actvGioiTinh.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String soDienThoai = txtSoDienThoai.getText().toString().trim();
        String queQuan = txtQueQuan.getText().toString().trim();
        String chucVu = actvChucVu.getText().toString().trim();
        String matKhau = txtMatKhau.getText().toString().trim();

        if (hoTen.isEmpty() || ngaySinh.isEmpty() || gioiTinh.isEmpty() ||
                email.isEmpty() || soDienThoai.isEmpty() || queQuan.isEmpty() ||
                chucVu.isEmpty() || matKhau.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("name", hoTen);
        updatedData.put("birthDate", ngaySinh);
        updatedData.put("gender", gioiTinh);
        updatedData.put("email", email);
        updatedData.put("phone", soDienThoai);
        updatedData.put("hometown", queQuan);
        updatedData.put("position", chucVu);
        updatedData.put("password", matKhau);

        database.child(maNhanVien).updateChildren(updatedData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}