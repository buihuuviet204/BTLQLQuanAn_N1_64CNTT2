package com.example.qunnbnhyn;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GanCaActivity extends AppCompatActivity {

    private TextInputEditText txtMaNhanVien, txtHoTen;
    private Button btnConfirm;
    private ImageButton imagebtn;
    private DatabaseReference database;
    private String maNhanVien;
    private TextView tvTitle;
    private RadioGroup rgShifts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ganca);

        // Lấy mã nhân viên từ Intent
        maNhanVien = getIntent().getStringExtra("maNhanVien");
        Log.d("GanCaActivity", "Received maNhanVien: " + maNhanVien);
        if (maNhanVien == null) {
            Toast.makeText(this, "Không tìm thấy mã nhân viên", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ các thành phần giao diện
        imagebtn = findViewById(R.id.imagebtn);
        tvTitle = findViewById(R.id.tvTitle);
        rgShifts = findViewById(R.id.rgShifts);
        btnConfirm = findViewById(R.id.btnConfirm);
        txtMaNhanVien = findViewById(R.id.txt_ma_nhan_vien); // Thêm ánh xạ
        txtHoTen = findViewById(R.id.txt_ho_ten); // Thêm ánh xạ

        // Kết nối tới node của nhân viên cụ thể trong Firebase
        database = FirebaseDatabase.getInstance().getReference("Employees").child(maNhanVien);

        // Tải dữ liệu nhân viên từ Firebase
        loadNhanVienData();

        // Xử lý sự kiện nút quay lại
        imagebtn.setOnClickListener(v -> finish());

        // Xử lý sự kiện nút cập nhật
        btnConfirm.setOnClickListener(v -> {
            int selectedId = rgShifts.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Vui lòng chọn ca làm!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Xác định ca làm dựa trên ID của RadioButton được chọn
            String shift;
            if (selectedId == R.id.rbMorning) {
                shift = "Ca sáng (8h - 12h)";
            } else if (selectedId == R.id.rbAfternoon) {
                shift = "Ca chiều (12h - 16h)";
            } else {
                shift = "Ca tối (16h - 22h)";
            }

            // Cập nhật ca làm lên Firebase
            database.child("shift").setValue(shift).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(GanCaActivity.this, "Cập nhật ca làm thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(GanCaActivity.this, "Cập nhật thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
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
                } else {
                    Toast.makeText(GanCaActivity.this, "Không tìm thấy thông tin nhân viên", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(GanCaActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}