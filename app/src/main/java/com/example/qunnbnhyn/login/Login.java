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

import com.example.qunnbnhyn.ActivityMainNV;
import com.example.qunnbnhyn.MainActivity;
import com.example.qunnbnhyn.QLM.MonAn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qunnbnhyn.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Login extends AppCompatActivity {
    private Button btn_login;
    private EditText editUsername, editPassword;
    private TextView txtMessage;
    private HashMap<String, MonAn> menu;
    FirebaseAuth myAuth;
    private String fullName;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_login = findViewById(R.id.btn_login);
        txtMessage = findViewById(R.id.txt_message);
        editPassword = findViewById(R.id.edit_password);
        editUsername = findViewById(R.id.edit_username);
        menu = new HashMap<>();
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
                                    uid = user.getUid();
                                    getFullName(uid);
                                } else {
                                    txtMessage.setVisibility(View.VISIBLE);
                                    txtMessage.setText("Đăng nhập thất bại: " + task.getException().getMessage());
                                }
                            }
                        });
            }
        });
    }

    private void changActivityByRole() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Employees").child(uid).child("position");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String role = (String) snapshot.getValue(String.class);
                Intent intent;
                if (role != null && role.equals("Nhân viên")) {
                    intent = new Intent(Login.this, ActivityMainNV.class);
                } else {
                    intent = new Intent(Login.this, MainActivity.class);
                }
                Log.d("name: ", fullName);
                intent.putExtra("full_name", fullName);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Login", "Lỗi khi lấy vai trò: " + error.getMessage());
                Toast.makeText(Login.this, "Lỗi khi lấy vai trò.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFullName(String uid) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Employees").child(uid).child("name");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fullName = (String) snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Login", "Lỗi khi lấy tên: " + error.getMessage());
                fullName = null;
                changActivityByRole();
            }
        });
    }
}