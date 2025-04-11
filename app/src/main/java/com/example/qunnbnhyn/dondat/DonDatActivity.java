package com.example.qunnbnhyn.dondat;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qunnbnhyn.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DonDatActivity extends AppCompatActivity {
    private RecyclerView rclDonDat;
    private DatabaseReference database;
    List<ItemDonDat> itemDonDats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_don_dat);
        rclDonDat = findViewById(R.id.rcl_dondat);
        itemDonDats = new ArrayList<>();
        database = FirebaseDatabase.getInstance().getReference("ban_an");
        readData();
        rclDonDat.setLayoutManager(new LinearLayoutManager(this));
        ListDonDatAdapter listDonDatAdapter = new ListDonDatAdapter(itemDonDats);
        rclDonDat.setAdapter(listDonDatAdapter);
    }
    public void readData(){
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemDonDats.clear();
                for(DataSnapshot banSnapshot : snapshot.getChildren()){
                    DataSnapshot chiTietOrderSnapshot = banSnapshot.child("chi tiet order");
                    if (chiTietOrderSnapshot.exists()) {
                        List<ItemMonGoi> listMon = new ArrayList<>();
                        for (DataSnapshot orderSnapshot : chiTietOrderSnapshot.getChildren()) {
                            String tenMon = orderSnapshot.getKey();
                            String soLuong = orderSnapshot.getValue(Integer.class) != null ? orderSnapshot.getValue(Integer.class).toString() : "0";
                            ItemMonGoi itemMonGoi = new ItemMonGoi(tenMon, soLuong);
                            listMon.add(itemMonGoi);
                        }
                        String maBan = banSnapshot.getKey();
                        ItemDonDat itemDonDat = new ItemDonDat(maBan, listMon);
                        itemDonDats.add(itemDonDat);
                        Log.d("DonDatActivity", "Đọc được đơn đặt cho bàn: " + maBan + ", số món: " + listMon.size());
                    } else {
                        Log.d("DonDatActivity", "Bàn " + banSnapshot.getKey() + " không có chi tiết order.");
                    }
                }
                if (rclDonDat.getAdapter() != null) {
                    rclDonDat.getAdapter().notifyDataSetChanged();
                } else {
                    ListDonDatAdapter listDonDatAdapter = new ListDonDatAdapter(itemDonDats);
                    rclDonDat.setAdapter(listDonDatAdapter);
                }
                Log.d("DonDatActivity", "Tổng số đơn đặt đọc được: " + itemDonDats.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DonDatActivity", "Lỗi đọc dữ liệu: " + error.getMessage());
            }
        });

    }
}