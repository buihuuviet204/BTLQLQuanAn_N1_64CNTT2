package com.example.qunnbnhyn;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qunnbnhyn.QLKH.ActivityCustomerHome;
import com.example.qunnbnhyn.QLM.MonAn;
import com.example.qunnbnhyn.TT.ThanhToan;
import com.example.qunnbnhyn.datmon.DatMon;
import com.example.qunnbnhyn.datmon.ItemThucDon;
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

    private ImageButton btnShift, logout, finger,btnPhucVu;
    private ImageButton order, happyClient, payment;
    private String maNhanVien;
    private DatabaseReference database;
    private TextView txtName;

    private DatabaseReference timeTrackingRef;
    private FirebaseAuth myAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nv);

        // Kết nối tới node Employees trên Firebase (sau khi maNhanVien đã được gán)
        Intent intent = getIntent();
        maNhanVien = intent.getStringExtra("maNhanVien");
        Log.d("Ma NV",maNhanVien);
        database = FirebaseDatabase.getInstance().getReference("Employees").child(maNhanVien);

        // Kết nối tới node Chấm công
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        timeTrackingRef = FirebaseDatabase.getInstance().getReference("Cham_cong").child(currentDate).child(maNhanVien);

        // Khởi tạo FirebaseAuth
        myAuth = FirebaseAuth.getInstance();
        btnPhucVu = findViewById(R.id.btn_phuc_vu);
        // Ánh xạ các view
        txtName = findViewById(R.id.txtName);
        Log.d("name",intent.getStringExtra("full_name"));
        txtName.setText(intent.getStringExtra("full_name"));

        // Ánh xạ ImageView "Quản lý bàn ăn" từ GridLayout
        btnShift = findViewById(R.id.btn_shift);
        order = findViewById(R.id.order);
        happyClient = findViewById(R.id.happy_client);
        payment = findViewById(R.id.payment);
        logout = findViewById(R.id.logout);
        finger = findViewById(R.id.finger);
        HashMap<String, MonAn> menu = new HashMap<>();
        FirebaseDatabase.getInstance().getReference("thuc_don").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MonAn monAn = dataSnapshot.getValue(MonAn.class);
                    monAn.setMaMon(dataSnapshot.getKey().toString());
                    menu.put(dataSnapshot.getKey().toString(), monAn);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuNVActivity.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentOrder = new Intent(MenuNVActivity.this,DatMon.class);
                intentOrder.putExtra("menu",menu);
                Log.d("HHAA","AHHA");
                startActivity(intentOrder);
            }
        });
        btnPhucVu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MenuNVActivity.this, DonDatActivity.class);
                startActivity(intent1);
            }
        });

        finger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy thông tin ca làm việc (shift) từ node Employees
                database.child("shift").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        Map<String, String> cong = new HashMap<>();
                        cong.put("check-in",currentTime);
                        cong.put("check-out","null");
                        cong.put("shift",snapshot.getValue(String.class));
                        FirebaseDatabase.getInstance().getReference("Cham_cong").child(currentDate).child(maNhanVien).setValue(cong);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        // Gắn sự kiện onClick cho ImageView "Quản lý bàn ăn"
        btnShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang ManageTableActivity
                Intent intent = new Intent(MenuNVActivity.this, NVListActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị dialog xác nhận đăng xuất
                new AlertDialog.Builder(MenuNVActivity.this)
                        .setTitle("Xác nhận đăng xuất")
                        .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Lấy thời gian hiện tại
                                String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                                // Cập nhật thời gian check-out vào node Chấm công
                                timeTrackingRef.child("check-out").setValue(currentTime).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Đăng xuất khỏi Firebase Authentication
                                        myAuth.signOut();

                                        // Hiển thị thông báo đăng xuất thành công
                                        Toast.makeText(MenuNVActivity.this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();

                                        // Chuyển về LoginActivity và xóa toàn bộ activity stack
                                        Intent intent = new Intent(MenuNVActivity.this, Login.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(MenuNVActivity.this, "Lỗi lưu giờ checkout: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss(); // Đóng dialog, không làm gì
                            }
                        })
                        .show();
            }
        });




        happyClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MenuNVActivity.this, ActivityCustomerHome.class);
                startActivity(intent1);
            }
        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MenuNVActivity.this, ThanhToan.class);
                startActivity(intent1);
            }
        });

    }
}
