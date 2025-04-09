package com.example.qunnbnhyn.datmon;

import static java.lang.System.in;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qunnbnhyn.QLM.MonAn;
import com.example.qunnbnhyn.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XacNhanDat extends AppCompatActivity {
    private TextView txtTongTien;
    private Button btnXacNhan;
    private HoaDon hoadon;
    private Intent intent;
    DatabaseReference myRef ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xac_nhan_dat);
        myRef = FirebaseDatabase.getInstance().getReference();

        intent = getIntent();
        hoadon =(HoaDon) intent.getSerializableExtra("hoa_don");
        HashMap<String,MonAn> menu = (HashMap<String, MonAn>) intent.getSerializableExtra("menu");
        if(hoadon != null)  Log.d("Tong tien",hoadon.getTongTien() + "");
        RecyclerView recyclerView = findViewById(R.id.rcl_cthd);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<ItemCTHD> data = new ArrayList<>();
        for(String key : hoadon.getCTDH().keySet()){
            ItemCTHD itemCTHD = new ItemCTHD(menu.get(key).getName().toString(),menu.get(key).getGiaban() + "", hoadon.getCTDH().get(key) + "");
            data.add(itemCTHD);
        }
        for(String key : hoadon.getCTDH().keySet()){
            Log.d(key,hoadon.getCTDH().get(key).toString());
        }
        CTHDAdapter adapter = new CTHDAdapter(data);
        recyclerView.setAdapter(adapter);
        txtTongTien = findViewById(R.id.txt_tongtien);
        txtTongTien.setText("Tong tien: " + hoadon.getTongTien() +"VND");
        btnXacNhan = findViewById(R.id.btn_xacnhandat);
        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editSoBan = findViewById(R.id.edit_soban);
                String soBan = editSoBan.getText().toString();
                myRef.child("ban_an").child(soBan)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String maHD = snapshot.getValue(String.class).toString();
                                if(maHD.equals("")) addHoaDon(soBan);
                                else updateHD(maHD);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d("LOI","LOI");
                            }
                        });
            }
        });

    }
    private void updateHD(String maHD) {
        myRef.child("hoa_don").child(maHD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HoaDon existingHoaDon = snapshot.getValue(HoaDon.class);
                    if (existingHoaDon == null) {
                        Toast.makeText(XacNhanDat.this, "Không lấy được hóa đơn từ Firebase", Toast.LENGTH_LONG).show();
                        return;
                    }
                    existingHoaDon.setTongTien(existingHoaDon.getTongTien() + hoadon.getTongTien()) ;
                    HashMap<String, Integer> existingCTDH = existingHoaDon.getCTDH();
                    HashMap<String, Integer> updateCTHD = hoadon.getCTDH();
                    for (String key : updateCTHD.keySet()) {
                        if (existingCTDH.containsKey(key)) {
                            int currentQuantity = existingCTDH.get(key);
                            int newQuantity = updateCTHD.get(key);
                            existingCTDH.put(key, currentQuantity + newQuantity);
                        } else {
                            existingCTDH.put(key, updateCTHD.get(key));
                        }
                    }
                    existingHoaDon.setCTDH(existingCTDH);
                    myRef.child("hoa_don").child(maHD).setValue(existingHoaDon)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(XacNhanDat.this, "Cập nhật hóa đơn thành công", Toast.LENGTH_LONG).show();
                                setResult(RESULT_OK);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(XacNhanDat.this, "Lỗi khi cập nhật hóa đơn: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                } else {
                    Toast.makeText(XacNhanDat.this, "Hóa đơn không tồn tại", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("LOI", "Lỗi: " + error.getMessage());
                Toast.makeText(XacNhanDat.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addHoaDon(String soban){
        DatabaseReference newHoaDonRef = myRef.child("hoa_don").push();
        String maHD = newHoaDonRef.getKey();
        newHoaDonRef.setValue(hoadon)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(XacNhanDat.this, "Thêm thành công", Toast.LENGTH_LONG).show();

                })
                .addOnFailureListener(aVoid -> {
                    Toast.makeText(XacNhanDat.this, "Thêm thành công", Toast.LENGTH_LONG).show();
                });
        myRef.child("ban_an").child(soban).setValue(maHD)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(XacNhanDat.this, "Thêm thành công", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(aVoid -> {
                    Toast.makeText(XacNhanDat.this, "Thêm thành công", Toast.LENGTH_LONG).show();
                });

    }

}