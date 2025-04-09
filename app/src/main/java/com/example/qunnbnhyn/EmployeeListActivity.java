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

public class EmployeeListActivity extends AppCompatActivity {
    private RecyclerView rvEmployeeList;
    private EmployeeAdapter adapter;
    private List<Employee> employeeList;
    private DatabaseReference database;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list); //gan layout cho activity

        // Khởi tạo RecyclerView và gán LayoutManager dạng danh sách dọc
        rvEmployeeList = findViewById(R.id.rvEmployeeList);
        rvEmployeeList.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo danh sách nhân viên
        employeeList = new ArrayList<>();
        rvEmployeeList.setAdapter(adapter); // Gán adapter cho RecyclerView

        // Kết nối tới node "Employees" trong Firebase
        database = FirebaseDatabase.getInstance().getReference("Employees");
        loadNhanVienData(); // Gọi hàm tải dữ liệu nhân viên


        // Xử lý sự kiện nút quay lại
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish()); // Khi nhấn nút, kết thúc Activity hiện tại
    }

    // Hàm tải dữ liệu nhân viên từ Firebase
    private void loadNhanVienData() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                employeeList.clear(); // Xóa danh sách cũ để cập nhật mới
                adapter.notifyDataSetChanged(); // Cập nhật giao diện RecyclerView
                // Hiển thị thông báo nếu không có nhân viên nào
                if (employeeList.isEmpty()) {
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
