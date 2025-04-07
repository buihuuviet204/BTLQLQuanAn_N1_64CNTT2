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

public class ChinhSuaThongTin extends AppCompatActivity {

    private RecyclerView recyclerView; // Khai báo RecyclerView để hiển thị danh sách nhân viên
    private NhanVienAdapter adapter; // Adapter để liên kết dữ liệu với RecyclerView
    private List<NhanVien> nhanVienList; // Danh sách chứa các đối tượng NhanVien
    private DatabaseReference database; // Tham chiếu tới cơ sở dữ liệu Firebase
    private ImageButton imgbBack; // Nút quay lại

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chinh_sua_thong_tin); // Gán layout cho Activity

        // Khởi tạo RecyclerView và gán LayoutManager dạng danh sách dọc
        recyclerView = findViewById(R.id.recycler_view_nhan_vien);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo danh sách nhân viên và adapter
        nhanVienList = new ArrayList<>();
        adapter = new NhanVienAdapter(nhanVienList, nhanVien -> {
            // Khi nhấn vào một nhân viên, chuyển sang màn hình chỉnh sửa chi tiết
            Intent intent = new Intent(this, ChinhSuaThongTinChiTiet.class);
            intent.putExtra("maNhanVien", nhanVien.getMaNhanVien()); // Truyền mã nhân viên qua Intent
            startActivity(intent); // Mở Activity mới
        });
        recyclerView.setAdapter(adapter); // Gán adapter cho RecyclerView

        // Kết nối tới node "Employees" trong Firebase
        database = FirebaseDatabase.getInstance().getReference("Employees");
        loadNhanVienData(); // Gọi hàm tải dữ liệu nhân viên

        // Xử lý sự kiện nút quay lại
        imgbBack = findViewById(R.id.imgb_back);
        imgbBack.setOnClickListener(v -> finish()); // Khi nhấn nút, kết thúc Activity hiện tại
    }

    // Hàm tải dữ liệu nhân viên từ Firebase
    private void loadNhanVienData() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nhanVienList.clear(); // Xóa danh sách cũ để cập nhật mới
                // Duyệt qua từng phần tử trong snapshot
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Chuyển dữ liệu từ snapshot thành đối tượng NhanVien
                    NhanVien nhanVien = snapshot.getValue(NhanVien.class);
                    if (nhanVien != null) {
                        nhanVien.setMaNhanVien(snapshot.getKey()); // Gán mã nhân viên từ key của snapshot
                        nhanVienList.add(nhanVien); // Thêm nhân viên vào danh sách
                    }
                }
                adapter.notifyDataSetChanged(); // Cập nhật giao diện RecyclerView
                // Hiển thị thông báo nếu không có nhân viên nào
                if (nhanVienList.isEmpty()) {
                    Toast.makeText(ChinhSuaThongTin.this, "Không có nhân viên nào để hiển thị", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hiển thị thông báo lỗi nếu tải dữ liệu thất bại
                Toast.makeText(ChinhSuaThongTin.this, "Lỗi tải dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}