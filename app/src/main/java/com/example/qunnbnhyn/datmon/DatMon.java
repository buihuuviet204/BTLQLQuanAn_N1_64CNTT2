package com.example.qunnbnhyn.datmon;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DatMon extends AppCompatActivity implements OnTotalChangeListener {
    private Button btnXacNhan;
    HashMap<MonAn, Integer> CTHD;
    HashMap<String, MonAn> menu;
    com.example.qunnbnhyn.datmon.CTHD cthd;
    HoaDon hoadon;
    private DatabaseReference monAnRef;
    private RecyclerView recyclerView;
    private List<ListMon> thucdon;
    private TextView txttongtien;
    private List<MonAn> listMiCay, listTraSua, listTraHQua, listDAVat, listCombo;
    private List<EditText> listEditText;
    private ThucDonAdapter thucDonAdapter;
    private ImageButton btnFilter,btnHome,btnSearch;
    private Spinner spinnerLoai;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dat_mon);
        btnXacNhan = findViewById(R.id.btn_xacnhan);
        btnFilter = findViewById(R.id.btn_filter);
        btnHome = findViewById(R.id.btn_home);
        btnSearch = findViewById(R.id.btn_search);
        monAnRef = FirebaseDatabase.getInstance().getReference("thuc_don"); // Hoặc "mon_an" nếu đó là nút gốc
        txttongtien = findViewById(R.id.txt_tongtien);
        listEditText = new ArrayList<>();
        recyclerView = findViewById(R.id.rcl_menu);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listDAVat = new ArrayList<>();
        listCombo = new ArrayList<>();
        listMiCay = new ArrayList<>();
        listTraSua = new ArrayList<>();
        listTraHQua = new ArrayList<>();
        thucdon = new ArrayList<>();
        spinnerLoai = findViewById(R.id.spinner_loai);
        String[] options = {"Mi Kay","Tra sua","Tra hoa qua","Nuoc co ga","Do an vat","Combo","Tat ca"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options);
        spinnerLoai.setAdapter(adapter);
        cthd = new CTHD();
        menu = new HashMap<>();
        thucDonAdapter = new ThucDonAdapter(thucdon,this); // Khởi tạo adapter
        Log.d("size: ",""+listEditText.size());
        hoadon = new HoaDon();
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        spinnerLoai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                thucdon.clear(); // Xóa danh sách hiện tại
                String selectedCategory = (String) parent.getItemAtPosition(position);

                if (selectedCategory.equals("Tat ca")) {
                    // Nếu chọn "Tất cả", thêm lại tất cả các loại món ăn vào thucdon
                    if (!listMiCay.isEmpty()) {
                        thucdon.add(new ListMon(ListMon.TYPE_HEADER, "Mi Kay", null));
                        thucdon.add(new ListMon(ListMon.TYPE_LIST, null, listMiCay));
                    }
                    if (!listTraSua.isEmpty()) {
                        thucdon.add(new ListMon(ListMon.TYPE_HEADER, "Tra saa", null));
                        thucdon.add(new ListMon(ListMon.TYPE_LIST, null, listTraSua));
                    }
                    if (!listTraHQua.isEmpty()) {
                        thucdon.add(new ListMon(ListMon.TYPE_HEADER, "Tra hoa qua", null));
                        thucdon.add(new ListMon(ListMon.TYPE_LIST, null, listTraHQua));
                    }
                    if (!listDAVat.isEmpty()) {
                        thucdon.add(new ListMon(ListMon.TYPE_HEADER, "Do an vat", null));
                        thucdon.add(new ListMon(ListMon.TYPE_LIST, null, listDAVat));
                    }
                    if (!listCombo.isEmpty()) {
                        thucdon.add(new ListMon(ListMon.TYPE_HEADER, "Combo", null));
                        thucdon.add(new ListMon(ListMon.TYPE_LIST, null, listCombo));
                    }
                } else {
                    // Nếu chọn một loại cụ thể, chỉ thêm các món ăn thuộc loại đó vào thucdon
                    if (selectedCategory.equals("Mi Kay") && !listMiCay.isEmpty()) {
                        thucdon.add(new ListMon(ListMon.TYPE_HEADER, "Mi cay", null));
                        thucdon.add(new ListMon(ListMon.TYPE_LIST, null, listMiCay));
                    } else if (selectedCategory.equals("Tra sua") && !listTraSua.isEmpty()) {
                        thucdon.add(new ListMon(ListMon.TYPE_HEADER, "Trà sữa", null));
                        thucdon.add(new ListMon(ListMon.TYPE_LIST, null, listTraSua));
                    } else if (selectedCategory.equals("Tra hoa qua") && !listTraHQua.isEmpty()) {
                        thucdon.add(new ListMon(ListMon.TYPE_HEADER, "Trà hoa quả", null));
                        thucdon.add(new ListMon(ListMon.TYPE_LIST, null, listTraHQua));
                    } else if (selectedCategory.equals("Do an vat") && !listDAVat.isEmpty()) {
                        thucdon.add(new ListMon(ListMon.TYPE_HEADER, "Đồ ăn vặt", null));
                        thucdon.add(new ListMon(ListMon.TYPE_LIST, null, listDAVat));
                    } else if (selectedCategory.equals("Combo") && !listCombo.isEmpty()) {
                        thucdon.add(new ListMon(ListMon.TYPE_HEADER, "Combo", null));
                        thucdon.add(new ListMon(ListMon.TYPE_LIST, null, listCombo));
                    }
                }
                thucDonAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        recyclerView.setAdapter(thucDonAdapter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnerLoai.getVisibility() == View.VISIBLE) {
                    spinnerLoai.setVisibility(View.GONE);
                } else {
                    spinnerLoai.setVisibility(View.VISIBLE);
                }
            }
        });
        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent intent = new Intent(DatMon.this, XacNhanDat.class);
                 intent.putExtra("CTHD",cthd);
                 intent.putExtra("hoa_don",hoadon);
                 intent.putExtra("menu",menu);
                 startActivityForResult(intent,1);
            }
        });

        readThucDonData();
    }
    private void clearAllEditText(ThucDonAdapter thucDonAdapter){
    }
    private void readThucDonData() {
        monAnRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCombo.clear();
                listMiCay.clear();
                listTraSua.clear();
                listDAVat.clear();
                listTraHQua.clear();
                thucdon.clear(); // Xóa danh sách cũ trước khi thêm dữ liệu mới
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MonAn monAn = dataSnapshot.getValue(MonAn.class);
                    monAn.setMaMon(dataSnapshot.getKey().toString());
                    if (monAn != null) {
                        String loai = monAn.getLoai();
                        if (loai.equals("Mi Kay")) listMiCay.add(monAn);
                        else if (loai.equals("Tra sua")) listTraSua.add(monAn);
                        else if (loai.equals("Trà hoa quả")) listTraHQua.add(monAn);
                        else if (loai.equals("Đồ ăn vặt")) listDAVat.add(monAn);
                        else if (loai.equals("Combo")) listCombo.add(monAn);
                        Log.d("Key = ",dataSnapshot.getKey());
                        Log.d("Url = ",monAn.getImageMonAn());
                    }
                    menu.put(dataSnapshot.getKey().toString(),monAn);
                }
                if (!listMiCay.isEmpty()) {
                    thucdon.add(new ListMon(ListMon.TYPE_HEADER, "Mì cay", null));
                    thucdon.add(new ListMon(ListMon.TYPE_LIST, null, listMiCay));
                }
                if (!listTraSua.isEmpty()) {
                    thucdon.add(new ListMon(ListMon.TYPE_HEADER, "Trà sữa", null));
                    thucdon.add(new ListMon(ListMon.TYPE_LIST, null, listTraSua));
                }
                if (!listTraHQua.isEmpty()) {
                    thucdon.add(new ListMon(ListMon.TYPE_HEADER, "Trà hoa quả", null));
                    thucdon.add(new ListMon(ListMon.TYPE_LIST, null, listTraHQua));
                }
                if (!listDAVat.isEmpty()) {
                    thucdon.add(new ListMon(ListMon.TYPE_HEADER, "Đồ ăn vặt", null));
                    thucdon.add(new ListMon(ListMon.TYPE_LIST, null, listDAVat));
                }
                if (!listCombo.isEmpty()) {
                    thucdon.add(new ListMon(ListMon.TYPE_HEADER, "Combo", null));
                    thucdon.add(new ListMon(ListMon.TYPE_LIST, null, listCombo));
                }
                thucDonAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView sau khi có dữ liệu

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Lỗi đọc dữ liệu: " + error.getMessage());
                Toast.makeText(DatMon.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        RecyclerView recyclerView1 = findViewById(R.id.rcl_menu);
//        ThucDonAdapter outerAdapter = (ThucDonAdapter) recyclerView1.getAdapter();
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1 && resultCode == RESULT_OK) {
//            for(int i = 0; i < outerAdapter.getItemCount();i++){
//                RecyclerView.ViewHolder outer = recyclerView1.findViewHolderForAdapterPosition(i);
//                if(outer instanceof ThucDonAdapter.ListViewHolder){
//                    RecyclerView recyclerView2 = outer.itemView.findViewById(R.id.subRecyclerView);
//                    ListMonAdapter inner = (ListMonAdapter) recyclerView2.getAdapter();
//                    if(inner != null){
//                        for(int j = 0; j < inner.getItemCount();j++){
//                            RecyclerView.ViewHolder innerviewholder = recyclerView2.findViewHolderForAdapterPosition(j);
//                            if(innerviewholder instanceof ListMonAdapter.ListMonViewHolder){
//                                EditText editText = innerviewholder.itemView.findViewById(R.id.edit_soluong);
//                                if(editText != null){
//                                    editText.setText("");
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
        super.onActivityResult(requestCode, resultCode, data);
        List<EditText> editTextCopy = new ArrayList<>(listEditText);
        for (EditText editText : editTextCopy) {
            if (editText != null) {
                editText.setText(""); // Reset về rỗng
            }
        }
    }

    @Override
    public void onTotalChanged(double total) {
    }

    @Override
    public void onSlgMAChanged(HashMap<MonAn, Integer> dsMongoi) {
//        double tong = 0;
//        hoadon.setDsMongoi(dsMongoi);
//        txttongtien.setText("Tổng tien: " + hoadon.getTongTien() + "VND");
    }
    @Override
    public void onSlgChanged(HashMap<String, Integer> dsMongoi, EditText editText) {
        double tong = 0;
        for(String key : dsMongoi.keySet()){
            tong += menu.get(key).getGiaban() * dsMongoi.get(key);
        }
        cthd.setCTDH(dsMongoi);
        hoadon.setTongtien(tong);
        hoadon.setTrangthai(false);
        hoadon.setNgLap(new Date());
        hoadon.setMaKhach("NULL");
        listEditText.add(editText);
        for(EditText edit: listEditText) Log.d("Edit: ",edit.getText().toString());
        txttongtien.setText("Tổng tien: " + tong + "VND");
        Log.d("Edittext: ",listEditText.size()+"");
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}