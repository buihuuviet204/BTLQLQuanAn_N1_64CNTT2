package com.example.qunnbnhyn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ActivityMainNV extends AppCompatActivity {

    private ImageView diningTableImageView, logout, finger;
    private ImageView supplies, customService, happyClient, discount;
    private String maNhanVien;
    private DatabaseReference database;
    private TextView txtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nv);

        // Lấy maNhanVien từ Intent trước
        Intent intent = getIntent();
        maNhanVien = intent.getStringExtra("maNhanVien");

        // Kết nối tới node Employees trên Firebase (sau khi maNhanVien đã được gán)
        database = FirebaseDatabase.getInstance().getReference("Employees").child(maNhanVien);

        // Ánh xạ các view
        txtName = findViewById(R.id.txt_NameNV);
        String fullName = intent.getStringExtra("full_name");
        Log.d("name", fullName != null ? fullName : "null");
        txtName.setText(fullName != null ? fullName : "Khách");

        diningTableImageView = findViewById(R.id.fram1).findViewById(R.id.dining_table_image);
        supplies = findViewById(R.id.fram1).findViewById(R.id.supplies);
        customService = findViewById(R.id.fram2).findViewById(R.id.custom_service);
        happyClient = findViewById(R.id.fram2).findViewById(R.id.happy_client);
        finger = findViewById(R.id.finger);






        finger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database.child("shift").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        Map<String, String> cong = new HashMap<>();
                        cong.put("check-in",currentTime);
                        cong.put("check-out","null");
                        cong.put("shift",snapshot.getValue(String.class));
                        FirebaseDatabase.getInstance().getReference("Cham_cong").child(currentDate).child(maNhanVien).setValue(cong);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                // Lưu thời gian check-in vào Firebase/

            }
        });

//        supplies.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });

//        customService.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
//
//        happyClient.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
//
//        discount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
    }
}