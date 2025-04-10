package com.example.qunnbnhyn;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qunnbnhyn.MenuNVActivity;
import com.example.qunnbnhyn.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qunnbnhyn.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    private Button btn_login;
    private EditText editUsername, editPassword;
    private TextView txtMessage;
    FirebaseAuth myAuth;
    private String fullName;
    private String maNhanVien;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_login = findViewById(R.id.btn_login);
        txtMessage = findViewById(R.id.txt_message);
        editPassword = findViewById(R.id.edit_password);
        editUsername = findViewById(R.id.edit_username);
        myAuth = FirebaseAuth.getInstance();
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editUsername.getText().toString();
                String password = editPassword.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    editUsername.setError("Vui long nhap email");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    editPassword.setError("Vui long nhap mat khau");
                    return;
                }
                myAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = myAuth.getCurrentUser();
                                    if (user != null) {
                                        maNhanVien = user.getUid(); // Lấy UID từ FirebaseUser
                                        getFullName(maNhanVien);
                                    } else {
                                        Toast.makeText(Login.this, "Không lấy được thông tin người dùng", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    txtMessage.setVisibility(View.VISIBLE);
                                    Toast.makeText(Login.this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }
    private void changActivityByRole() {
        if (maNhanVien == null || maNhanVien.isEmpty()) {
            Log.e("Login", "maNhanVien is null or empty");
            Toast.makeText(Login.this, "Không tìm thấy mã nhân viên", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Login.this, MainActivity.class);
            intent.putExtra("full_name", fullName != null ? fullName : "Khách");
            intent.putExtra("maNhanVien", maNhanVien != null ? maNhanVien : "");
            startActivity(intent);
            finish();
            return;
        }

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Employees").child(maNhanVien).child("position");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String role = snapshot.getValue(String.class);
                Intent intent;
                if (role != null && role.equals("Nhân viên")) {
                    Log.d("Login", "Vai trò: Nhân viên, chuyển sang MenuNVActivity");
                    intent = new Intent(Login.this, MenuNVActivity.class);
                } else {
                    Log.d("Login", "Vai trò không phải Nhân viên hoặc không tồn tại, chuyển sang MainActivity");
                    intent = new Intent(Login.this, MainActivity.class);
                }
                intent.putExtra("full_name", fullName != null ? fullName : "Khách");
                intent.putExtra("maNhanVien", maNhanVien);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getFullName(String maNhanVien) {
        if (maNhanVien == null || maNhanVien.isEmpty()) {
            Log.e("Login", "maNhanVien is null or empty");
            Toast.makeText(Login.this, "Không tìm thấy mã nhân viên", Toast.LENGTH_LONG).show();
            fullName = "Khách"; // Giá trị mặc định
            changActivityByRole();
            return;
        }

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Employees").child(maNhanVien).child("name");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fullName = snapshot.getValue(String.class);
                if (fullName == null) {
                    Log.w("Login", "Tên người dùng không tồn tại, gán giá trị mặc định");
                    Toast.makeText(Login.this, "Không tìm thấy tên người dùng", Toast.LENGTH_SHORT).show();
                    fullName = "Khách"; // Giá trị mặc định
                }
                changActivityByRole();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Login", "Lỗi khi lấy tên: " + error.getMessage());
                Toast.makeText(Login.this, "Lỗi khi lấy tên: " + error.getMessage(), Toast.LENGTH_LONG).show();
                fullName = "Khách"; // Giá trị mặc định nếu có lỗi
                changActivityByRole();
            }
        });
    }
}