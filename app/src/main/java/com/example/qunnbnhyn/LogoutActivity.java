package com.example.qunnbnhyn;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LogoutActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        // Ánh xạ các thành phần giao diện
        btnBack = findViewById(R.id.btnBack);
        btnOk = findViewById(R.id.btnOk);

        // Xử lý sự kiện khi nhấn nút "Quay lại"
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Quay lại màn hình trước đó (MainActivity)
            }
        });

        // Xử lý sự kiện khi nhấn nút "Xác nhận"
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị thông báo đăng xuất thành công
                Toast.makeText(LogoutActivity.this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
                //finish(); // Đóng activity sau khi đăng xuất

                // Nếu bạn muốn đóng toàn bộ ứng dụng sau khi đăng xuất, bạn có thể thêm:
                // finishAffinity();

                // Trong btnConfirm.setOnClickListener
                //Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //startActivity(intent);
                //finish();
            }
        });
    }
}
