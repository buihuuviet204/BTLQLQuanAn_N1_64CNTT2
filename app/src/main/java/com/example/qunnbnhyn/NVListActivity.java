package com.example.qunnbnhyn;

import android.content.Intent;
import android.os.Bundle;
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

public class NVListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NhanVienAdapter adapter;
    private List<NhanVien> nhanVienList;
    private List<NhanVien> filteredNhanVienList;
    private DatabaseReference database;
    private ImageButton imgbBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);

        recyclerView = findViewById(R.id.recycler_view_nhan_vien);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        nhanVienList = new ArrayList<>();
        filteredNhanVienList = new ArrayList<>();

        // Cập nhật adapter để chuyển sang CheckCaActivity
        adapter = new NhanVienAdapter(filteredNhanVienList, nhanVien -> {
            Intent intent = new Intent(NVListActivity.this, CheckCaActivity.class);
            intent.putExtra("maNhanVien", nhanVien.getMaNhanVien());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance().getReference("Employees");
        loadNhanVienData();

        imgbBack = findViewById(R.id.imgb_back);
        imgbBack.setOnClickListener(v -> finish());
    }

    private void loadNhanVienData() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nhanVienList.clear(); // Xóa danh sách cũ để cập nhật mới
                filteredNhanVienList.clear(); // Xóa danh sách lọc
                // Duyệt qua từng phần tử trong snapshot
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Chuyển dữ liệu từ snapshot thành đối tượng NhanVien
                    NhanVien nhanVien = snapshot.getValue(NhanVien.class);
                    if (nhanVien != null) {
                        nhanVien.setMaNhanVien(snapshot.getKey()); // Gán mã nhân viên từ key của snapshot
                        nhanVienList.add(nhanVien); // Thêm nhân viên vào danh sách chính
                        filteredNhanVienList.add(nhanVien); // Thêm vào danh sách lọc ban đầu
                    }
                }
                adapter.notifyDataSetChanged(); // Cập nhật giao diện RecyclerView
                // Hiển thị thông báo nếu không có nhân viên nào
                if (nhanVienList.isEmpty()) {
                    Toast.makeText(NVListActivity.this, "Không có nhân viên nào để hiển thị", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hiển thị thông báo lỗi nếu tải dữ liệu thất bại
                Toast.makeText(NVListActivity.this, "Lỗi tải dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}