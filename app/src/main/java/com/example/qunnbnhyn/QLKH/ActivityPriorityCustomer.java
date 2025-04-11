package com.example.qunnbnhyn.QLKH;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
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
import java.util.List;

public class ActivityPriorityCustomer extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomerAdapter adapter;
    private List<Customer> customerList;
    private DatabaseReference databaseReference;
    ImageView imgViewBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_priority);

        recyclerView = findViewById(R.id.rcPriorityCustomer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        customerList = new ArrayList<>();
        adapter = new CustomerAdapter(customerList, customer -> {
            // Chuyển sang màn hình chi tiết khi nhấn vào khách hàng
            Intent intent = new Intent(this, ActivityCustomerPriorityDetail.class);
            intent.putExtra("customerId", customer.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance("https://quananbinhyen-cntt2-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("customers");
        loadCustomers();
        imgViewBack = findViewById(R.id.imgViewBack);
        imgViewBack.setOnClickListener(v -> finish());
    }

    private void loadCustomers() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                customerList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Customer customer = snapshot.getValue(Customer.class);
                    if (customer != null) {
                        customerList.add(customer); // Hiển thị tất cả khách hàng
                    }
                }
                adapter.notifyDataSetChanged();
                if (customerList.isEmpty()) {
                    Toast.makeText(ActivityPriorityCustomer.this, "Không có khách hàng nào", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ActivityPriorityCustomer.this, "Lỗi tải dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}