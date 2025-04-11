package com.example.qunnbnhyn.QLM;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qunnbnhyn.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SuaMonFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private List<MonAn> listMon;
    private DatabaseReference myRef;
    private RecyclerView rcl_sua;
    private SuaMonAdapter adapter; // Khai báo adapter ở cấp độ lớp

    private String mParam1;
    private String mParam2;

    public SuaMonFragment() {
        // Required empty public constructor
    }

    public static SuaMonFragment newInstance(String param1, String param2) {
        SuaMonFragment fragment = new SuaMonFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sua_mon, container, false);

        // Khởi tạo RecyclerView và Adapter
        rcl_sua = view.findViewById(R.id.rcl_monan);
        listMon = new ArrayList<>();
        adapter = new SuaMonAdapter(listMon); // Khởi tạo adapter với danh sách rỗng
        rcl_sua.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rcl_sua.setAdapter(adapter);

        // Lấy dữ liệu từ Firebase
        myRef = FirebaseDatabase.getInstance().getReference("thuc_don");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listMon.clear(); // Xóa dữ liệu cũ
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MonAn monAn = dataSnapshot.getValue(MonAn.class);
                    if (monAn != null) {
                        monAn.setMaMon(dataSnapshot.getKey());
                        listMon.add(monAn);
                    }
                }
                adapter.notifyDataSetChanged(); // Thông báo adapter cập nhật giao diện
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
                Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }
}