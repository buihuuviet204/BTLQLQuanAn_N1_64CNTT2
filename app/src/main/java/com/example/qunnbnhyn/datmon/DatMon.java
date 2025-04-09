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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DatMon extends AppCompatActivity implements OnTotalChangeListener {
    private Button btnXacNhan;
    private HashMap<String, Integer> ctdh; // Đổi tên từ CTHD thành ctdh để nhất quán
    private HashMap<String, MonAn> menu;
    private com.example.qunnbnhyn.datmon.CTHD cthd;
    private HoaDon hoadon;
    private DatabaseReference monAnRef;
    private RecyclerView recyclerView;
    private List<ListMon> thucdon;
    private TextView txttongtien;
    private List<MonAn> listMiCay, listTraSua, listTraHQua, listDAVat, listCombo;
    private List<EditText> listEditText;
    private ThucDonAdapter thucDonAdapter;
    private ImageButton btnFilter, btnHome, btnSearch;
    private Spinner spinnerLoai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dat_mon);
        btnXacNhan = findViewById(R.id.btn_xacnhan);
        btnFilter = findViewById(R.id.btn_filter);
        btnHome = findViewById(R.id.btn_home);
        btnSearch = findViewById(R.id.btn_search);
        monAnRef = FirebaseDatabase.getInstance().getReference("thuc_don");
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
        String[] options = {"Mi Kay", "Tra sua", "Tra hoa qua", "Nuoc co ga", "Do an vat", "Combo", "Tat ca"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options);
        spinnerLoai.setAdapter(adapter);
        ctdh = new HashMap<>(); // Khởi tạo ctdh
        cthd = new CTHD();
        menu = new HashMap<>();
        thucDonAdapter = new ThucDonAdapter(thucdon, this);
        thucDonAdapter.setCtdh(ctdh); // Truyền ctdh vào adapter
        hoadon = new HoaDon();
        hoadon.setCTDH(ctdh);

        btnHome.setOnClickListener(v -> finish());

        spinnerLoai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                thucdon.clear();
                String selectedCategory = (String) parent.getItemAtPosition(position);

                if (selectedCategory.equals("Tat ca")) {
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

        btnFilter.setOnClickListener(v -> {
            if (spinnerLoai.getVisibility() == View.VISIBLE) {
                spinnerLoai.setVisibility(View.GONE);
            } else {
                spinnerLoai.setVisibility(View.VISIBLE);
            }
        });

        btnXacNhan.setOnClickListener(v -> {
            Intent intent = new Intent(DatMon.this, XacNhanDat.class);
            intent.putExtra("hoa_don", hoadon);
            intent.putExtra("menu", menu);
            Log.d("TT = ", hoadon.getTongTien() + "");
            startActivityForResult(intent, 1);
        });

        readThucDonData();
    }

    private void readThucDonData() {
        monAnRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("Thay doi: ","Request");
                listCombo.clear();
                listMiCay.clear();
                listTraSua.clear();
                listDAVat.clear();
                listTraHQua.clear();
                thucdon.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MonAn monAn = dataSnapshot.getValue(MonAn.class);
                    monAn.setMaMon(dataSnapshot.getKey().toString());
                    if (monAn != null) {
                        String loai = monAn.getLoai();
                        if (loai.equals("Mi Kay")) listMiCay.add(monAn);
                        else if (loai.equals("Tra sua")) listTraSua.add(monAn);
                        else if (loai.equals("Tra hoa qua")) listTraHQua.add(monAn);
                        else if (loai.equals("Do an vat")) listDAVat.add(monAn);
                        else if (loai.equals("Combo")) listCombo.add(monAn);
                        Log.d("Key = ", dataSnapshot.getKey());
                        Log.d("Url = ", monAn.getImageMonAn());
                    }
                    menu.put(dataSnapshot.getKey().toString(), monAn);
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
                thucDonAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Lỗi đọc dữ liệu: " + error.getMessage());
                Toast.makeText(DatMon.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Reset CTDH và tổng tiền
            ctdh.clear();
            hoadon.setCTDH(ctdh);
            hoadon.setTongTien(0);
            txttongtien.setText("Tổng tiền: 0 VND");

            // Cập nhật CTDH trong adapter để reset giao diện
            thucDonAdapter.setCtdh(ctdh);
            thucDonAdapter.notifyDataSetChanged();

            // Reset listEditText
            listEditText.clear();
        }
    }

    @Override
    public void onTotalChanged(double total) {
    }

    @Override
    public void onSlgMAChanged(HashMap<MonAn, Integer> dsMongoi) {
    }

    @Override
    public void onSlgChanged(HashMap<String, Integer> dsMongoi, EditText editText) {
        // Gộp dsMongoi vào CTDH
        ctdh.putAll(dsMongoi);
        // Xóa các món có số lượng 0
        ctdh.entrySet().removeIf(entry -> entry.getValue() <= 0);
        hoadon.setCTDH(ctdh);

        // Tính tổng tiền dựa trên CTDH hoàn chỉnh
        double tong = 0;
        for (String key : ctdh.keySet()) {
            MonAn monAn = menu.get(key);
            if (monAn != null) {
                tong += monAn.getGiaban() * ctdh.get(key);
            }
        }
        hoadon.setTongTien(tong);
        hoadon.setTrangthai(false);

        // Định dạng ngày thành chuỗi
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String ngayLap = sdf.format(new Date());
        hoadon.setNgLap(ngayLap);

        hoadon.setMaKhach("NULL");
        listEditText.add(editText);
        for (EditText edit : listEditText) Log.d("Edit: ", edit.getText().toString());
        txttongtien.setText("Tổng tiền: " + tong + " VND");
        Log.d("Edittext: ", listEditText.size() + "");
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}