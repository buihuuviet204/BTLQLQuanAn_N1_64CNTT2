package com.example.qunnbnhyn.QLNV;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qunnbnhyn.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChinhSuaThongTinChiTiet extends AppCompatActivity {

    // Khai báo các trường nhập liệu và thành phần giao diện
    private TextInputEditText txtMaNhanVien, txtHoTen, txtNgaySinh, txtEmail, txtSoDienThoai, txtQueQuan, txtMatKhau;
    private AutoCompleteTextView actvGioiTinh, actvChucVu; // Dropdown cho giới tính và chức vụ
    private Button btnSua, btnQuayLai;
    private DatabaseReference database; // Tham chiếu tới Firebase
    private String maNhanVien; // Mã nhân viên được truyền vào
    private String currentAvatarBase64; // Lưu trữ avatarBase64 hiện tại của nhân viên

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chinh_sua_thong_tin_chi_tiet); // Gán layout cho Activity

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
        btnSua = findViewById(R.id.btn_sua);
        btnQuayLai = findViewById(R.id.btn_quaylai);

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
        btnQuayLai.setOnClickListener(v -> finish());
        // Xử lý sự kiện nút cập nhật
        btnSua.setOnClickListener(v -> updateNhanVien());
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
                    currentAvatarBase64 = nhanVien.getAvatarBase64(); // Lưu avatarBase64 hiện tại
                } else {
                    // Hiển thị thông báo nếu không tìm thấy dữ liệu
                    Toast.makeText(ChinhSuaThongTinChiTiet.this, "Không tìm thấy thông tin nhân viên", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Hiển thị thông báo lỗi nếu tải dữ liệu thất bại
                Toast.makeText(ChinhSuaThongTinChiTiet.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Hàm cập nhật thông tin nhân viên
    private void updateNhanVien() {
        // Lấy dữ liệu từ các trường nhập liệu
        String hoTen = txtHoTen.getText().toString().trim();
        String ngaySinh = txtNgaySinh.getText().toString().trim();
        String gioiTinh = actvGioiTinh.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String soDienThoai = txtSoDienThoai.getText().toString().trim();
        String queQuan = txtQueQuan.getText().toString().trim();
        String chucVu = actvChucVu.getText().toString().trim();
        String matKhau = txtMatKhau.getText().toString().trim();

        // Kiểm tra xem các trường có bị bỏ trống hay không
        if (hoTen.isEmpty() || ngaySinh.isEmpty() || gioiTinh.isEmpty() || email.isEmpty() ||
                soDienThoai.isEmpty() || queQuan.isEmpty() || chucVu.isEmpty() || matKhau.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng NhanVien mới với thông tin đã cập nhật, giữ nguyên avatar
        NhanVien updatedNhanVien = new NhanVien(maNhanVien, hoTen, ngaySinh, gioiTinh, email,
                soDienThoai, queQuan, chucVu, matKhau, currentAvatarBase64);

        // Lưu dữ liệu vào Firebase
        database.setValue(updatedNhanVien)
                .addOnSuccessListener(aVoid -> {
                    // Hiển thị thông báo thành công và thoát
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Hiển thị thông báo lỗi nếu cập nhật thất bại
                    Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}