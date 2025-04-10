package com.example.qunnbnhyn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogoutActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private Button btnOk;
    private DatabaseReference database;
    private String maNhanVien;
    private FirebaseAuth myAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        // Lấy maNhanVien từ Intent
        maNhanVien = getIntent().getStringExtra("maNhanVien");
        if (maNhanVien == null || maNhanVien.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy mã nhân viên", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ các thành phần giao diện
        btnBack = findViewById(R.id.btnBack);
        btnOk = findViewById(R.id.btnOk);

        // Kết nối tới node Employees trên Firebase
        database = FirebaseDatabase.getInstance().getReference("Employees").child(maNhanVien);

        // Khởi tạo FirebaseAuth
        myAuth = FirebaseAuth.getInstance();

        // Xử lý sự kiện khi nhấn nút "Quay lại"
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Quay lại màn hình trước đó (MainActivity hoặc MenuNVActivity)
            }
        });

        // Xử lý sự kiện khi nhấn nút "Xác nhận"
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy thời gian hiện tại
                String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                // Lưu thời gian check-out vào Firebase
                database.child("checkOut").setValue(currentTime).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Đăng xuất khỏi Firebase Authentication
                        myAuth.signOut();

                        // Hiển thị thông báo đăng xuất thành công
                        Toast.makeText(LogoutActivity.this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();

                        // Chuyển về LoginActivity và xóa toàn bộ activity stack
                        Intent intent = new Intent(LogoutActivity.this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LogoutActivity.this, "Lỗi đăng xuất: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}