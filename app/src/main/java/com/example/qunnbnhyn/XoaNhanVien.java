package com.example.qunnbnhyn;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class XoaNhanVien extends AppCompatActivity {
    private RecyclerView recyclerView; // Khai báo RecyclerView để hiển thị danh sách nhân viên
    private NhanVienAdapter adapter; // Adapter để liên kết dữ liệu với RecyclerView
    private List<NhanVien> nhanVienList; // Danh sách chứa tất cả nhân viên từ Firebase
    private List<NhanVien> filteredNhanVienList; // Danh sách nhân viên đã lọc để hiển thị
    private DatabaseReference database; // Tham chiếu tới cơ sở dữ liệu Firebase
    private ImageButton imgbBack; // Nút quay lại
    private SearchView searchView; // SearchView để tìm kiếm nhân viên

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_xoa_nhan_vien);

        // Khởi tạo RecyclerView và gán LayoutManager dạng danh sách dọc
        recyclerView = findViewById(R.id.recycler_view_xoa_nhan_vien);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo danh sách nhân viên và danh sách lọc
        nhanVienList = new ArrayList<>();
        filteredNhanVienList = new ArrayList<>();

        // Khởi tạo adapter với danh sách đã lọc
        adapter = new NhanVienAdapter(filteredNhanVienList, nhanVien -> {
            // Khi nhấn vào một nhân viên, chuyển sang màn hình xóa chi tiết
            Intent intent = new Intent(this, XoaNhanVienChiTiet.class);
            intent.putExtra("maNhanVien", nhanVien.getMaNhanVien()); // Truyền mã nhân viên qua Intent
            startActivity(intent); // Mở Activity mới
        });
        recyclerView.setAdapter(adapter); // Gán adapter cho RecyclerView

        // Kết nối tới node "Employees" trong Firebase
        database = FirebaseDatabase.getInstance().getReference("Employees");
        loadNhanVienData(); // Gọi hàm tải dữ liệu nhân viên

        // Khởi tạo SearchView và xử lý tìm kiếm
        searchView = findViewById(R.id.search_view_nhan_vien);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Khi người dùng nhấn nút tìm kiếm trên bàn phím
                filterNhanVien(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Khi người dùng nhập từng ký tự, tự động lọc danh sách
                filterNhanVien(newText);
                return true;
            }
        });

        // Xử lý sự kiện nút quay lại
        imgbBack = findViewById(R.id.imgb_back);
        imgbBack.setOnClickListener(v -> finish()); // Khi nhấn nút, kết thúc Activity hiện tại
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
                    Toast.makeText(XoaNhanVien.this, "Không có nhân viên nào để hiển thị", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hiển thị thông báo lỗi nếu tải dữ liệu thất bại
                Toast.makeText(XoaNhanVien.this, "Lỗi tải dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void filterNhanVien(String query) {
        filteredNhanVienList.clear(); // Xóa danh sách lọc hiện tại
        if (query.isEmpty()) {
            // Nếu từ khóa rỗng, hiển thị toàn bộ danh sách nhân viên
            filteredNhanVienList.addAll(nhanVienList);
        } else {
            // Lọc danh sách nhân viên theo từ khóa
            String queryLowerCase = query.toLowerCase();
            for (NhanVien nhanVien : nhanVienList) {
                if (nhanVien.getName().toLowerCase().contains(queryLowerCase)) {
                    filteredNhanVienList.add(nhanVien); // Thêm nhân viên khớp với từ khóa vào danh sách lọc
                }
            }
        }
        adapter.notifyDataSetChanged(); // Cập nhật giao diện RecyclerView với danh sách đã lọc
        // Hiển thị thông báo nếu không tìm thấy nhân viên nào
        if (filteredNhanVienList.isEmpty()) {
            Toast.makeText(XoaNhanVien.this, "Không tìm thấy nhân viên nào", Toast.LENGTH_SHORT).show();
        }
    }
}