package com.example.qunnbnhyn.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cloudinary.android.MediaManager;
import com.example.qunnbnhyn.MainActivity;
import com.example.qunnbnhyn.MenuNVActivity;
import com.example.qunnbnhyn.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    private Button btn_login;
    private EditText editUsername, editPassword;
    private TextView txtMessage;
    private FirebaseAuth myAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ánh xạ view
        btn_login = findViewById(R.id.btn_login);
        txtMessage = findViewById(R.id.txt_message);
        editPassword = findViewById(R.id.edit_password);
        editUsername = findViewById(R.id.edit_username);
        myAuth = FirebaseAuth.getInstance();

        // Khởi tạo Cloudinary
        Map config = new HashMap();
        config.put("cloud_name", "dr94s8psw");
        config.put("api_key", "391264998483567");
        config.put("api_secret", "JfVY6wjk278v4hU3zodwvdDhTWI");
        MediaManager.init(this, config);

        // Sự kiện nút đăng nhập
        btn_login.setOnClickListener(v -> {
            String email = editUsername.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            // Kiểm tra đầu vào
            if (TextUtils.isEmpty(email)) {
                editUsername.setError("Vui lòng nhập email");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                editPassword.setError("Vui lòng nhập mật khẩu");
                return;
            }

            // Đăng nhập với Firebase Authentication
            myAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = myAuth.getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();
                                getEmployeeData(uid);
                            } else {
                                Toast.makeText(Login.this, "Lỗi: Không lấy được thông tin người dùng", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            txtMessage.setVisibility(View.VISIBLE);
                            txtMessage.setText("Đăng nhập thất bại: " + task.getException().getMessage());
                        }
                    });
        });
    }

    // Lấy thông tin nhân viên từ Firebase
    private void getEmployeeData(String uid) {
        DatabaseReference employeeRef = FirebaseDatabase.getInstance().getReference("Employees").child(uid);
        employeeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Lấy thông tin từ Firebase
                    String fullName = snapshot.child("name").getValue(String.class);
                    String position = snapshot.child("position").getValue(String.class);

                    // Kiểm tra dữ liệu
                    if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(position)) {
                        Toast.makeText(Login.this, "Lỗi: Dữ liệu nhân viên không đầy đủ", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Sử dụng uid làm maNhanVien
                    String maNhanVien = uid;

                    // Chuyển activity theo vai trò
                    Intent intent;
                    if (position.equals("Nhân viên")) {
                        intent = new Intent(Login.this, MenuNVActivity.class);
                    } else {
                        intent = new Intent(Login.this, MainActivity.class);
                    }

                    // Truyền dữ liệu
                    intent.putExtra("maNhanVien", maNhanVien);
                    intent.putExtra("full_name", fullName);
                    Log.d("Login", "Truyền maNhanVien: " + maNhanVien + ", fullName: " + fullName);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Login.this, "Lỗi: Không tìm thấy thông tin nhân viên", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Login.this, "Lỗi truy vấn dữ liệu: " + error.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("Login", "Lỗi Firebase: " + error.getMessage());
            }
        });
    }
}