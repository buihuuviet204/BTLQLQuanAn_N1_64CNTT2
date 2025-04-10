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
                                    maNhanVien = user.getUid();
                                    getFullName(maNhanVien);
                                } else {
                                    txtMessage.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            }
        });
    }
    private void changActivityByRole() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Employees").child(maNhanVien).child("position");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String role = (String) snapshot.getValue(String.class);
                Intent intent;
                if (role != null && role.equals("Nhân viên")) {
                    intent = new Intent(Login.this, MenuNVActivity.class);
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
            }
        });
    }
    private void getFullName(String maNhanVien) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Employees").child(maNhanVien).child("name");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fullName = (String) snapshot.getValue(String.class);
                changActivityByRole();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Login", "Lỗi khi lấy tên: " + error.getMessage());
                fullName = null;
            }
        });
    }


}