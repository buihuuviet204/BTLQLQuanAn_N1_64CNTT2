package com.example.qunnbnhyn;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ChinhSuaThongTin extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NhanVienAdapter adapter;
    private List<NhanVien> nhanVienList;
    private DatabaseReference database;
    private ImageButton imgbBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chinh_sua_thong_tin);

        // Khởi tạo RecyclerView
        recyclerView = findViewById(R.id.recycler_view_nhan_vien);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        nhanVienList = new ArrayList<>();
        adapter = new NhanVienAdapter(nhanVienList);
        recyclerView.setAdapter(adapter);

        // Khởi tạo nút Back
        imgbBack = findViewById(R.id.imgb_back);
        imgbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Quay lại màn hình trước
            }
        });

        // Kết nối Firebase
        database = FirebaseDatabase.getInstance().getReference("Employees");

        // Lấy dữ liệu từ Firebase
        loadNhanVienData();
    }

    private void loadNhanVienData() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nhanVienList.clear(); // Xóa danh sách cũ trước khi cập nhật
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String maNhanVien = snapshot.getKey(); // Lấy mã nhân viên từ key
                    String name = snapshot.child("name").getValue(String.class);
                    String birthDate = snapshot.child("birthDate").getValue(String.class);
                    String gender = snapshot.child("gender").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String hometown = snapshot.child("hometown").getValue(String.class);
                    String position = snapshot.child("position").getValue(String.class);
                    String password = snapshot.child("password").getValue(String.class);
                    String avatarBase64 = snapshot.child("avatarBase64").getValue(String.class);

                    NhanVien nhanVien = new NhanVien(maNhanVien, name, birthDate, gender, email, phone, hometown, position, password, avatarBase64);
                    nhanVienList.add(nhanVien);
                    Log.d("ChinhSuaThongTin", "Loaded: " + maNhanVien);
                }
                adapter.notifyDataSetChanged(); // Cập nhật RecyclerView
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChinhSuaThongTin.this, "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}