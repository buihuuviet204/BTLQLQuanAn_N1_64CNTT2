package com.example.qunnbnhyn.datmon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import java.util.HashMap;
import java.util.List;

public class DatMon extends AppCompatActivity implements OnChangeListener {
    private Button btnXacNhan;
    private HashMap<String, Integer> ctdh;
    private HashMap<String, MonAn> menu;

    private HoaDon hoadon;
    private DatabaseReference monAnRef;
    private RecyclerView recyclerView;
    private List<ItemThucDon> thucdon;
    private TextView txttongtien;
    private List<MonAn> listMiCay, listTraSua, listTraHQua, listDAVat, listCombo;
    private List<EditText> listEditText;
    private ThucDonAdapter thucDonAdapter;
    private ImageButton btnFilter, btnHome;
    private Button btnAll;
    private Spinner spinnerLoai;
    private EditText editSearch;
    RecyclerView recyclerView1;
    private MonAn monAnSearched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dat_mon);
        btnXacNhan = findViewById(R.id.btn_xacnhan);
        btnFilter = findViewById(R.id.btn_filter);
        btnHome = findViewById(R.id.btn_home);
        monAnRef = FirebaseDatabase.getInstance().getReference("thuc_don");
        txttongtien = findViewById(R.id.txt_tongtien);
        listEditText = new ArrayList<>();
        recyclerView = findViewById(R.id.rcl_menu);
        btnAll = findViewById(R.id.btn_back);
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
        ctdh = new HashMap<>();
        menu = new HashMap<>();

        hoadon = new HoaDon();
        hoadon.setCTDH(ctdh);
        editSearch = findViewById(R.id.edit_search);
        btnHome.setOnClickListener(v -> finish());
        recyclerView1 = findViewById(R.id.rcl_search);
        Intent intentMenu = getIntent();
        menu = (HashMap<String, MonAn>) intentMenu.getSerializableExtra("menu");
        initMenu();
        // Khởi tạo và gán adapter trước khi gọi filter và notifyDataSetChanged()
        filter("Tat ca");
        thucDonAdapter = new ThucDonAdapter(thucdon, this,ctdh);

        recyclerView.setAdapter(thucDonAdapter);




        for (String key : menu.keySet()) {
            Log.d("DatMon", "Menu key: " + key + ", value: " + menu.get(key).getName());
        }
        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thucdon.clear();
                filter("Tat ca");
                thucDonAdapter.notifyDataSetChanged();
                btnAll.setVisibility(View.GONE);
            }
        });
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String tenSearch = editSearch.getText().toString();
                List<MonAn> listMonSearch = new ArrayList<>();
                for(String key: menu.keySet()){
                    if(isSubStringIgnoreCase(menu.get(key).getName(),tenSearch)){
                        listMonSearch.add(menu.get(key));
                    }
                }
                ItemSearchAdapter itemSearchAdapter = new ItemSearchAdapter(listMonSearch,DatMon.this);

                recyclerView1.setLayoutManager(new LinearLayoutManager(DatMon.this));
                recyclerView1.setAdapter(itemSearchAdapter);
                recyclerView1.setVisibility(View.VISIBLE);

            }
        });
        spinnerLoai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = (String) parent.getItemAtPosition(position);
                filter(selectedCategory);
                thucDonAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



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
            startActivityForResult(intent, 1);
        });


    }
    public boolean isSubStringIgnoreCase(String main, String sub) {
        if (main == null || sub == null) {
            return false;
        }
        return main.toLowerCase().contains(sub.toLowerCase());
    }
    private void initMenu(){
        for(String key : menu.keySet()){
            if (menu.get(key).getLoai().equals("Mi Kay"))   listMiCay.add(menu.get(key));
            if (menu.get(key).getLoai().equals("Tra sua"))  listTraSua.add(menu.get(key));
            if (menu.get(key).getLoai().equals("Tra hoa qua"))  listTraHQua.add(menu.get(key));
            if (menu.get(key).getLoai().equals("Do an vat")) listDAVat.add(menu.get(key));
            if (menu.get(key).getLoai().equals("Combo")) listCombo.add(menu.get(key));
        }
    }
    private void filter(String loai){
        thucdon.clear();
        if (loai.equals("Tat ca")) {
            if (!listMiCay.isEmpty()) {
                thucdon.add(new ItemThucDon(ItemThucDon.TYPE_HEADER, "Mi Kay", null));
                thucdon.add(new ItemThucDon(ItemThucDon.TYPE_LIST, null, listMiCay));
            }
            if (!listTraSua.isEmpty()) {
                thucdon.add(new ItemThucDon(ItemThucDon.TYPE_HEADER, "Tra sua", null));
                thucdon.add(new ItemThucDon(ItemThucDon.TYPE_LIST, null, listTraSua));
            }
            if (!listTraHQua.isEmpty()) {
                thucdon.add(new ItemThucDon(ItemThucDon.TYPE_HEADER, "Tra hoa qua", null));
                thucdon.add(new ItemThucDon(ItemThucDon.TYPE_LIST, null, listTraHQua));
            }
            if (!listDAVat.isEmpty()) {
                thucdon.add(new ItemThucDon(ItemThucDon.TYPE_HEADER, "Do an vat", null));
                thucdon.add(new ItemThucDon(ItemThucDon.TYPE_LIST, null, listDAVat));
            }
            if (!listCombo.isEmpty()) {
                thucdon.add(new ItemThucDon(ItemThucDon.TYPE_HEADER, "Combo", null));
                thucdon.add(new ItemThucDon(ItemThucDon.TYPE_LIST, null, listCombo));
            }
        } else {
            if (loai.equals("Mi Kay") && !listMiCay.isEmpty()) {
                thucdon.add(new ItemThucDon(ItemThucDon.TYPE_HEADER, "Mi cay", null));
                thucdon.add(new ItemThucDon(ItemThucDon.TYPE_LIST, null, listMiCay));
            } else if (loai.equals("Tra sua") && !listTraSua.isEmpty()) {
                thucdon.add(new ItemThucDon(ItemThucDon.TYPE_HEADER, "Trà sữa", null));
                thucdon.add(new ItemThucDon(ItemThucDon.TYPE_LIST, null, listTraSua));
            } else if (loai.equals("Tra hoa qua") && !listTraHQua.isEmpty()) {
                thucdon.add(new ItemThucDon(ItemThucDon.TYPE_HEADER, "Trà hoa quả", null));
                thucdon.add(new ItemThucDon(ItemThucDon.TYPE_LIST, null, listTraHQua));
            } else if (loai.equals("Do an vat") && !listDAVat.isEmpty()) {
                thucdon.add(new ItemThucDon(ItemThucDon.TYPE_HEADER, "Đồ ăn vặt", null));
                thucdon.add(new ItemThucDon(ItemThucDon.TYPE_LIST, null, listDAVat));
            } else if (loai.equals("Combo") && !listCombo.isEmpty()) {
                thucdon.add(new ItemThucDon(ItemThucDon.TYPE_HEADER, "Combo", null));
                thucdon.add(new ItemThucDon(ItemThucDon.TYPE_LIST, null, listCombo));
            }
        }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View currentFocus = getCurrentFocus();
            boolean isTouchOnEditText = isTouchInsideView(event, editSearch);
            boolean isTouchOnRecyclerView = isTouchInsideView(event, recyclerView1);
            boolean isRecyclerViewVisible = recyclerView1.getVisibility() == View.VISIBLE;

            if (isRecyclerViewVisible && !isTouchOnEditText && !isTouchOnRecyclerView) {
                recyclerView1.setVisibility(View.GONE);
                if (currentFocus == editSearch) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                    editSearch.clearFocus();
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
    private boolean isTouchInsideView(MotionEvent event, View view) {
        if (view == null || view.getVisibility() != View.VISIBLE) {
            return false;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        return (event.getX() >= x && event.getX() <= x + view.getWidth() &&
                event.getY() >= y && event.getY() <= y + view.getHeight());
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            ctdh.clear();
            hoadon.setCTDH(ctdh);
            hoadon.setTongTien(0);
            txttongtien.setText("Tổng tiền: 0 VND");
            thucDonAdapter.setCtdh(ctdh);
            thucDonAdapter.notifyDataSetChanged();

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
    public void monAnSearched(MonAn monAn) {
        List<MonAn> list = new ArrayList<>();
        list.add(monAn);
        thucdon.clear();
        recyclerView1.setVisibility(View.GONE);
        thucdon.add(new ItemThucDon(ItemThucDon.TYPE_LIST,null,list));
        btnAll.setVisibility(View.VISIBLE);
        thucDonAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSlgChanged(HashMap<String, Integer> dsMongoi, EditText editText) {
        ctdh.putAll(dsMongoi);
        hoadon.setCTDH(ctdh);

        double tong = 0;
        for (String key : ctdh.keySet()) {
            MonAn monAn = menu.get(key);
            if (monAn != null) {
                tong += monAn.getGiaban() * ctdh.get(key);
            }
        }
        hoadon.setTongTien(tong);



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