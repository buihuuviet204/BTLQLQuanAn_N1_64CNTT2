package com.example.qunnbnhyn;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import java.io.ByteArrayOutputStream;

public class ThemNhanVien extends AppCompatActivity {
    private TextInputEditText txtMaNhanVien, txtHoTen, txtNgaySinh, txtEmail, txtSoDienThoai, txtQueQuan, txtMatKhau;
    private AutoCompleteTextView actvGioiTinh, actvChucVu;
    private ImageButton imgBtnAvatar;
    private Button btnThem, btnBack;
    private DatabaseReference database;
    private Uri imageUri; // Lưu đường dẫn ảnh được chọn
    private FirebaseAuth mAuth;

    // Khởi tạo launcher để chọn ảnh từ thư viện
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = (result.getData()).getData();
                    imgBtnAvatar.setImageURI(imageUri); // Hiển thị ảnh lên ImageButton
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_nhan_vien);

        // Khởi tạo các view
        txtMaNhanVien = findViewById(R.id.txt_ma_nhan_vien);
        txtHoTen = findViewById(R.id.txt_ho_ten);
        txtNgaySinh = findViewById(R.id.txt_date);
        txtEmail = findViewById(R.id.txt_email);
        txtSoDienThoai = findViewById(R.id.txt_so_dien_thoai);
        txtQueQuan = findViewById(R.id.txt_que_quan);
        txtMatKhau = findViewById(R.id.txt_mat_khau);
        actvGioiTinh = findViewById(R.id.actv_gioi_tinh);
        actvChucVu = findViewById(R.id.actv_chuc_vu);
        imgBtnAvatar = findViewById(R.id.img_btn_avatar);
        btnThem = findViewById(R.id.btn_them);
        btnBack = findViewById(R.id.btn_quaylai);

        // Kết nối Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Kết nối Firebase Realtime Database
        database = FirebaseDatabase.getInstance().getReference("Employees");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        // Thiết lập dropdown cho Giới tính
        String[] gioiTinhOptions = {"Nam", "Nữ", "Khác"};
        ArrayAdapter<String> gioiTinhAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, gioiTinhOptions);
        actvGioiTinh.setAdapter(gioiTinhAdapter);

        // Thiết lập dropdown cho Chức vụ
        String[] chucVuOptions = {"Nhân viên", "Quản lý"};
        ArrayAdapter<String> chucVuAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, chucVuOptions);
        actvChucVu.setAdapter(chucVuAdapter);

        // Sự kiện chọn ảnh
        imgBtnAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                imagePickerLauncher.launch(intent);
            }
        });

        // Sự kiện nút Back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Quay lại màn hình trước
            }
        });

        // Sự kiện nút Thêm
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEmployee();
            }
        });
    }

    private void addEmployee() {
        // Lấy dữ liệu từ các trường nhập liệu
        String maNhanVien = txtMaNhanVien.getText().toString().trim();
        String hoTen = txtHoTen.getText().toString().trim();
        String ngaySinh = txtNgaySinh.getText().toString().trim();
        String gioiTinh = actvGioiTinh.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String soDienThoai = txtSoDienThoai.getText().toString().trim();
        String queQuan = txtQueQuan.getText().toString().trim();
        String chucVu = actvChucVu.getText().toString().trim();
        String matKhau = txtMatKhau.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (maNhanVien.isEmpty() || hoTen.isEmpty() || ngaySinh.isEmpty() || gioiTinh.isEmpty() ||
                email.isEmpty() || soDienThoai.isEmpty() || queQuan.isEmpty() || chucVu.isEmpty() || matKhau.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo map để lưu dữ liệu nhân viên
        Map<String, Object> employeeData = new HashMap<>();
        employeeData.put("name", hoTen);
        employeeData.put("birthDate", ngaySinh);
        employeeData.put("gender", gioiTinh);
        employeeData.put("email", email);
        employeeData.put("phone", soDienThoai);
        employeeData.put("hometown", queQuan);
        employeeData.put("position", chucVu);
        employeeData.put("password", matKhau);

        // Nếu có ảnh, chuyển thành Base64 và lưu vào employeeData
        if (imageUri != null) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); // Nén ảnh để giảm kích thước
                byte[] byteArray = baos.toByteArray();
                String imageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                employeeData.put("avatarBase64", imageBase64);
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi khi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // firebase authentication
        String account = txtEmail.getText().toString().trim();
        String pass = txtMatKhau.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(account, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String uid = user.getUid(); // Lấy UID của người dùng
                    Toast.makeText(getApplicationContext(), "Tạo tài khoản thành công: " + user.getEmail(), Toast.LENGTH_SHORT).show();

                    // Lưu dữ liệu vào Realtime Database với UID làm key
                    saveEmployeeToDatabase(uid, employeeData);
                } else {
                    Toast.makeText(ThemNhanVien.this, "Tạo tài khoản thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Lưu dữ liệu trực tiếp vào Realtime Database
//        saveEmployeeToDatabase(maNhanVien, employeeData);
    }
//
//    private void saveEmployeeToDatabase(String maNhanVien, Map<String, Object> employeeData) {
//        // Lưu dữ liệu vào Firebase Realtime Database
//        database.child(maNhanVien).setValue(employeeData)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(this, "Thêm nhân viên thành công!", Toast.LENGTH_SHORT).show();
//                    clearFields(); // Xóa các trường sau khi thêm thành công
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "Lỗi khi thêm nhân viên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }

    private void saveEmployeeToDatabase(String uid, Map<String, Object> employeeData) {
        // Lưu dữ liệu vào Firebase Realtime Database với UID làm key
        database.child(uid).setValue(employeeData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Thêm nhân viên thành công!", Toast.LENGTH_SHORT).show();
                    clearFields(); // Xóa các trường sau khi thêm thành công
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi thêm nhân viên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearFields() {
        txtMaNhanVien.setText("");
        txtHoTen.setText("");
        txtNgaySinh.setText("");
        actvGioiTinh.setText("");
        txtEmail.setText("");
        txtSoDienThoai.setText("");
        txtQueQuan.setText("");
        actvChucVu.setText("");
        txtMatKhau.setText("");
        imgBtnAvatar.setImageResource(R.drawable.ic_launcher_foreground); // Reset ảnh về mặc định
        imageUri = null;
    }
}