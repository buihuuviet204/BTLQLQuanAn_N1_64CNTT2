package com.example.qunnbnhyn;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CheckCaActivity extends AppCompatActivity {

    private TextInputEditText txtMaNhanVien, txtHoTen, txtShift;
    private ImageButton imagebtn;
    private DatabaseReference database;
    private String maNhanVien;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkca);

        // Lấy mã nhân viên từ Intent
        maNhanVien = getIntent().getStringExtra("maNhanVien");
        Log.d("CheckCaActivity", "Received maNhanVien: " + maNhanVien);
        if (maNhanVien == null) {
            Toast.makeText(this, "Không tìm thấy mã nhân viên", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ các thành phần giao diện
        imagebtn = findViewById(R.id.imagebtn);
        tvTitle = findViewById(R.id.tvTitle);
        txtMaNhanVien = findViewById(R.id.txt_ma_nhan_vien);
        txtHoTen = findViewById(R.id.txt_ho_ten);
        txtShift = findViewById(R.id.txt_shift);

        // Kết nối tới node của nhân viên cụ thể trong Firebase
        database = FirebaseDatabase.getInstance().getReference("Employees").child(maNhanVien);

        // Tải dữ liệu nhân viên từ Firebase
        loadNhanVienData();

        // Xử lý sự kiện nút quay lại
        imagebtn.setOnClickListener(v -> finish());
    }

    // Hàm tải thông tin nhân viên từ Firebase
    private void loadNhanVienData() {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                NhanVien nhanVien = snapshot.getValue(NhanVien.class);
                if (nhanVien != null) {
                    txtMaNhanVien.setText(maNhanVien);
                    txtHoTen.setText(nhanVien.getName());
                    txtShift.setText(nhanVien.getShift() != null ? nhanVien.getShift() : "Chưa gán ca làm");
                } else {
                    Toast.makeText(CheckCaActivity.this, "Không tìm thấy thông tin nhân viên", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(CheckCaActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}