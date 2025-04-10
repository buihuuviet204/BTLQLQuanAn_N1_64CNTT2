package com.example.qunnbnhyn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ImageView diningTableImageView, logout, finger;
    private ImageView supplies, customService, happyClient, discount;
    private String maNhanVien;
    private DatabaseReference database;
    private TextView txtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Lấy maNhanVien từ Intent trước
        Intent intent = getIntent();
        maNhanVien = intent.getStringExtra("maNhanVien");
        if (maNhanVien == null || maNhanVien.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy mã nhân viên. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(MainActivity.this, Login.class);
            startActivity(loginIntent);
            finish();
            return;
        }
        // Kết nối tới node Employees trên Firebase (sau khi maNhanVien đã được gán)
        database = FirebaseDatabase.getInstance().getReference("Employees").child(maNhanVien);

        // Ánh xạ các view
        txtName = findViewById(R.id.txtName);
        String fullName = intent.getStringExtra("full_name");
        Log.d("name", fullName != null ? fullName : "null");
        txtName.setText(fullName != null ? fullName : "Khách");

        diningTableImageView = findViewById(R.id.fram1).findViewById(R.id.dining_table_image);
        supplies = findViewById(R.id.fram1).findViewById(R.id.supplies);
        customService = findViewById(R.id.fram2).findViewById(R.id.custom_service);
        happyClient = findViewById(R.id.fram2).findViewById(R.id.happy_client);
        discount = findViewById(R.id.fram3).findViewById(R.id.discount);
        logout = findViewById(R.id.logout);
        finger = findViewById(R.id.finger);


        // Gắn sự kiện onClick cho ImageView "Quản lý bàn ăn"
        diningTableImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EmployeeListActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LogoutActivity.class);
                startActivity(intent);
            }
        });

    /*    finger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                // Lưu thời gian check-in vào Firebase/
                database.child("checkIn").setValue(currentTime).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Hiển thị thông báo "Xin cảm ơn"
                        Toast.makeText(MainActivity.this, "Xin cảm ơn", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Lỗi chấm công: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });*/

        supplies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        customService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        happyClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        discount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}