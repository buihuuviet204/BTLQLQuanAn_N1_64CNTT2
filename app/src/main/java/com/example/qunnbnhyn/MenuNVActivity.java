package com.example.qunnbnhyn;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qunnbnhyn.QLKH.ActivityCustomerHome;
import com.example.qunnbnhyn.QLM.MonAn;
import com.example.qunnbnhyn.TT.ThanhToan;
import com.example.qunnbnhyn.datmon.DatMon;
import com.example.qunnbnhyn.dondat.DonDatActivity;
import com.example.qunnbnhyn.login.Login;
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

public class MenuNVActivity extends AppCompatActivity {
    private ImageButton btnShift, logout, finger, btnPhucVu;
    private ImageButton order, happyClient, payment;
    private String maNhanVien;
    private TextView txtName;
    private DatabaseReference database;
    private DatabaseReference timeTrackingRef;
    private FirebaseAuth myAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nv);

        // Lấy maNhanVien từ Intent
        Intent incomingIntent = getIntent();
        maNhanVien = incomingIntent.getStringExtra("maNhanVien");
        String fullName = incomingIntent.getStringExtra("full_name");
        Log.d("MenuNV", "Received maNhanVien: " + maNhanVien + ", fullName: " + fullName);

        // Kiểm tra maNhanVien
        if (maNhanVien == null) {
            Toast.makeText(this, "Lỗi: Không nhận được mã nhân viên", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ view
        txtName = findViewById(R.id.txtName);
        btnShift = findViewById(R.id.btn_shift);
        order = findViewById(R.id.order);
        happyClient = findViewById(R.id.happy_client);
        payment = findViewById(R.id.payment);
        logout = findViewById(R.id.logout);
        finger = findViewById(R.id.finger);
        btnPhucVu = findViewById(R.id.btn_phuc_vu);

        // Hiển thị tên nhân viên
        txtName.setText(fullName != null ? fullName : "N/A");

        // Khởi tạo Firebase
        myAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("Employees").child(maNhanVien);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        timeTrackingRef = FirebaseDatabase.getInstance().getReference("Cham_cong").child(currentDate).child(maNhanVien);

        // Tải menu từ Firebase
        HashMap<String, MonAn> menu = new HashMap<>();
        FirebaseDatabase.getInstance().getReference("thuc_don").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                menu.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MonAn monAn = dataSnapshot.getValue(MonAn.class);
                    monAn.setMaMon(dataSnapshot.getKey());
                    menu.put(dataSnapshot.getKey(), monAn);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuNVActivity.this, "Lỗi tải thực đơn: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Sự kiện nút Đặt món
        order.setOnClickListener(v -> {
            Intent orderIntent = new Intent(MenuNVActivity.this, DatMon.class);
            orderIntent.putExtra("menu", menu);
            startActivity(orderIntent);
        });

        // Sự kiện nút Phục vụ
        btnPhucVu.setOnClickListener(v -> {
            Intent phucVuIntent = new Intent(MenuNVActivity.this, DonDatActivity.class);
            startActivity(phucVuIntent);
        });

        // Sự kiện nút Chấm công
        finger.setOnClickListener(v -> {
            String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            database.child("shift").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String shift = snapshot.getValue(String.class);
                    Map<String, String> cong = new HashMap<>();
                    cong.put("check-in", currentTime);
                    cong.put("check-out", "null");
                    cong.put("shift", shift != null ? shift : "Chưa gán");
                    FirebaseDatabase.getInstance().getReference("Cham_cong").child(currentDate).child(maNhanVien).setValue(cong)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MenuNVActivity.this, "Chấm công thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MenuNVActivity.this, "Lỗi chấm công: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MenuNVActivity.this, "Lỗi tải ca làm: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Sự kiện nút Check ca
        btnShift.setOnClickListener(v -> {
            Intent shiftIntent = new Intent(MenuNVActivity.this, CheckCaActivity.class);
            shiftIntent.putExtra("maNhanVien", maNhanVien);
            startActivity(shiftIntent);
        });

        // Sự kiện nút Đăng xuất
        logout.setOnClickListener(v -> {
            new AlertDialog.Builder(MenuNVActivity.this)
                    .setTitle("Xác nhận đăng xuất")
                    .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                    .setPositiveButton("OK", (dialog, which) -> {
                        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                        timeTrackingRef.child("check-out").setValue(currentTime).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                myAuth.signOut();
                                Toast.makeText(MenuNVActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                                Intent loginIntent = new Intent(MenuNVActivity.this, Login.class);
                                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(loginIntent);
                                finish();
                            } else {
                                Toast.makeText(MenuNVActivity.this, "Lỗi lưu giờ checkout: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        // Sự kiện nút Khách hàng
        happyClient.setOnClickListener(v -> {
            Intent clientIntent = new Intent(MenuNVActivity.this, ActivityCustomerHome.class);
            startActivity(clientIntent);
        });

        // Sự kiện nút Thanh toán
        payment.setOnClickListener(v -> {
            Intent paymentIntent = new Intent(MenuNVActivity.this, ThanhToan.class);
            startActivity(paymentIntent);
        });
    }
}