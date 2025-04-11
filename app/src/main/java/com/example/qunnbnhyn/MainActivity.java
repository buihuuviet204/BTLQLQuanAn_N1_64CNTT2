package com.example.qunnbnhyn;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Button btnAdd, btnThanhtoan;

    private ImageView diningTableImageView, logout, finger;
    private ImageView supplies, customService, happyClient, discount;
    private String maNhanVien;
    private DatabaseReference database;
    private DatabaseReference timeTrackingRef;
    private TextView txtName;
    private FirebaseAuth myAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        btnAdd = (Button) findViewById(R.id.btn_quanly);
        btnThanhtoan = (Button) findViewById(R.id.btn_thanhtoan);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it1 = new Intent(MainActivity.this, QuanLyNhanVien.class);
                startActivity(it1);
            }
        });
        btnThanhtoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it2 = new Intent(MainActivity.this, ThanhToan.class);
                startActivity(it2);
            }
        setContentView(R.layout.activity_main_nv);

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

        // Kết nối tới node Chấm công
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        timeTrackingRef = FirebaseDatabase.getInstance().getReference("Cham_cong").child(currentDate).child(maNhanVien);

        // Khởi tạo FirebaseAuth
        myAuth = FirebaseAuth.getInstance();

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
        logout = findViewById(R.id.fram3).findViewById(R.id.logout);
        finger = findViewById(R.id.finger);

        // Gắn sự kiện onClick cho ImageView "Quản lý bàn ăn"
        diningTableImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EmployeeListActivity.class);
                startActivity(intent);
            }

    }
}