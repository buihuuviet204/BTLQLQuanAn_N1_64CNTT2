package com.example.qunnbnhyn;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class XoaNhanVienChiTiet extends AppCompatActivity {
    // Khai báo các trường nhập liệu và thành phần giao diện
    private TextInputEditText txtMaNhanVien, txtHoTen, txtNgaySinh, txtEmail, txtSoDienThoai, txtQueQuan, txtMatKhau;
    private AutoCompleteTextView actvGioiTinh, actvChucVu; // Dropdown cho giới tính và chức vụ
    private Button btnXoa;
    private ImageButton btnBack; // Nút quay lại
    private DatabaseReference database; // Tham chiếu tới Firebase
    private String maNhanVien; // Mã nhân viên được truyền vào

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xoa_nhan_vien_chi_tiet);

        // Lấy mã nhân viên từ Intent
        maNhanVien = getIntent().getStringExtra("maNhanVien");
        if (maNhanVien == null) {
            // Nếu không có mã nhân viên, hiển thị thông báo và thoát
            Toast.makeText(this, "Không tìm thấy mã nhân viên", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo các view từ layout
        txtMaNhanVien = findViewById(R.id.txt_ma_nhan_vien);
        txtHoTen = findViewById(R.id.txt_ho_ten);
        txtNgaySinh = findViewById(R.id.txt_date);
        txtEmail = findViewById(R.id.txt_email);
        txtSoDienThoai = findViewById(R.id.txt_so_dien_thoai);
        txtQueQuan = findViewById(R.id.txt_que_quan);
        txtMatKhau = findViewById(R.id.txt_mat_khau);
        actvGioiTinh = findViewById(R.id.actv_gioi_tinh);
        actvChucVu = findViewById(R.id.actv_chuc_vu);
        btnXoa = findViewById(R.id.btn_xoa);
        btnBack = findViewById(R.id.btn_back);

        // Kết nối tới node của nhân viên cụ thể trong Firebase
        database = FirebaseDatabase.getInstance().getReference("Employees").child(maNhanVien);

        // Thiết lập dropdown cho giới tính
        String[] gioiTinhOptions = {"Nam", "Nữ", "Khác"};
        ArrayAdapter<String> gioiTinhAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, gioiTinhOptions);
        actvGioiTinh.setAdapter(gioiTinhAdapter);

        // Thiết lập dropdown cho chức vụ
        String[] chucVuOptions = {"Nhân viên", "Quản lý"};
        ArrayAdapter<String> chucVuAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, chucVuOptions);
        actvChucVu.setAdapter(chucVuAdapter);

        // Tải dữ liệu nhân viên từ Firebase
        loadNhanVienData();

        // Vô hiệu hóa trường mã nhân viên để không chỉnh sửa
        txtMaNhanVien.setEnabled(false);
        // Xử lý sự kiện nút quay lại
        btnBack.setOnClickListener(v -> finish());
        // Xử lý sự kiện nút xóa (trước đây là sửa)
        btnXoa.setOnClickListener(v -> deleteNhanVien()); // Gọi hàm xóa
    }

    // Hàm tải thông tin nhân viên từ Firebase
    private void loadNhanVienData() {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Lấy dữ liệu nhân viên từ snapshot
                NhanVien nhanVien = snapshot.getValue(NhanVien.class);
                if (nhanVien != null) {
                    // Gán dữ liệu vào các trường nhập liệu
                    txtMaNhanVien.setText(maNhanVien);
                    txtHoTen.setText(nhanVien.getName());
                    txtNgaySinh.setText(nhanVien.getBirthDate());
                    actvGioiTinh.setText(nhanVien.getGender(), false);
                    txtEmail.setText(nhanVien.getEmail());
                    txtSoDienThoai.setText(nhanVien.getPhone());
                    txtQueQuan.setText(nhanVien.getHometown());
                    actvChucVu.setText(nhanVien.getPosition(), false);
                    txtMatKhau.setText(nhanVien.getPassword());
                } else {
                    // Hiển thị thông báo nếu không tìm thấy dữ liệu
                    Toast.makeText(XoaNhanVienChiTiet.this, "Không tìm thấy thông tin nhân viên", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Hiển thị thông báo lỗi nếu tải dữ liệu thất bại
                Toast.makeText(XoaNhanVienChiTiet.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Hàm xóa thông tin nhân viên khỏi Firebase
    private void deleteNhanVien() {
        // Xóa node nhân viên khỏi Firebase
        database.removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Hiển thị thông báo thành công và thoát
                    Toast.makeText(this, "Xóa nhân viên thành công", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Hiển thị thông báo lỗi nếu xóa thất bại
                    Toast.makeText(this, "Lỗi xóa nhân viên: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}