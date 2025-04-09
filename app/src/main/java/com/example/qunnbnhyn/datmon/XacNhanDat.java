package com.example.qunnbnhyn.datmon;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qunnbnhyn.QLM.MonAn;
import com.example.qunnbnhyn.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XacNhanDat extends AppCompatActivity {
    private TextView txtTongTien;
    private Button btnXacNhan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xac_nhan_dat);

        Intent intent = getIntent();
        HoaDon hoadon =(HoaDon) intent.getSerializableExtra("hoa_don");
        CTHD cthd = (CTHD) intent.getSerializableExtra("CTHD");

        HashMap<String,MonAn> menu = (HashMap<String, MonAn>) intent.getSerializableExtra("menu");
        if(hoadon != null)  Log.d("Tong tien",hoadon.getTongtien() + "");
        RecyclerView recyclerView = findViewById(R.id.rcl_cthd);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<ItemCTHD> data = new ArrayList<>();
        for(String key : cthd.getCTDH().keySet()){
            ItemCTHD itemCTHD = new ItemCTHD(menu.get(key).getName().toString(),menu.get(key).getGiaban() + "", cthd.getCTDH().get(key) + "");
            data.add(itemCTHD);
        }
        CTHDAdapter adapter = new CTHDAdapter(data);
        recyclerView.setAdapter(adapter);
        txtTongTien = findViewById(R.id.txt_tongtien);
        txtTongTien.setText("Tong tien: " + hoadon.getTongtien() +"VND");
        btnXacNhan = findViewById(R.id.btn_xacnhandat);
        EditText editSoBan = findViewById(R.id.edit_soban);
        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String soBan = editSoBan.getText().toString();
                hoadon.setSoBan(soBan);
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("hoa_don").push();
                String key = myRef.getKey();
                myRef.setValue(hoadon)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(XacNhanDat.this, "Thêm thành công", Toast.LENGTH_LONG).show();

                        })
                        .addOnFailureListener(aVoid -> {
                            Toast.makeText(XacNhanDat.this, "Thêm thành công", Toast.LENGTH_LONG).show();
                        });
                myRef = FirebaseDatabase.getInstance().getReference("CTHD");
                myRef.child(key).setValue(cthd)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(XacNhanDat.this, "Thêm thành công", Toast.LENGTH_LONG).show();
                            intent.putExtra("CLEAR_FIELDS", true); // Gửi tín hiệu làm trống
                            setResult(RESULT_OK, intent);
                            finish();

                        })
                        .addOnFailureListener(aVoid -> {
                            Toast.makeText(XacNhanDat.this, "Thêm thành công", Toast.LENGTH_LONG).show();
                        });

            }
        });

    }

}