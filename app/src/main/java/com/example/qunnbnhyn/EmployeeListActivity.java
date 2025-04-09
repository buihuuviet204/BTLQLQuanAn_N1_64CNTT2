package com.example.qunnbnhyn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class EmployeeListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NhanVienAdapter adapter; // Adapter để liên kết dữ liệu với RecyclerView
    private List<NhanVien> nhanVienList; // Danh sách chứa tất cả nhân viên từ Firebase
    private List<NhanVien> filteredNhanVienList; // Danh sách nhân viên đã lọc để hiển thị
    private DatabaseReference database; // Tham chiếu tới cơ sở dữ liệu Firebase
    private ImageButton imgbBack;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list); // Gán layout cho Activity

        // Khởi tạo RecyclerView và gán LayoutManager dạng danh sách dọc
        recyclerView = findViewById(R.id.recycler_view_nhan_vien);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo danh sách nhân viên và danh sách lọc
        nhanVienList = new ArrayList<>();
        filteredNhanVienList = new ArrayList<>();

        // Khởi tạo adapter với danh sách đã lọc
        adapter = new NhanVienAdapter(filteredNhanVienList, nhanVien -> {
            //khi nhấn vào 1 nhân viên -> chuyển màn hình
            Log.d("EmployeeListActivity", "maNhanVien: " + nhanVien.getMaNhanVien());
            Intent intent = new Intent(EmployeeListActivity.this, GanCaActivity.class);
            intent.putExtra("maNhanVien", nhanVien.getMaNhanVien()); //truyền mnv qua intent
            startActivity(intent); //mở activity ms
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
                    Toast.makeText(EmployeeListActivity.this, "Không có nhân viên nào để hiển thị", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hiển thị thông báo lỗi nếu tải dữ liệu thất bại
                Toast.makeText(EmployeeListActivity.this, "Lỗi tải dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}