package com.example.qunnbnhyn;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ActivityEditCustomer extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomerAdapter adapter;
    private List<Customer> customerList;
    private DatabaseReference databaseReference;
    ImageView imgViewBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_customer);

        recyclerView = findViewById(R.id.rcEditCustomer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        customerList = new ArrayList<>();
        adapter = new CustomerAdapter(customerList, customer -> {
            Intent intent = new Intent(this, ActivityEditCustomerDetail.class);
            intent.putExtra("customerId", customer.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance("https://quananbinhyen-cntt2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("customers");
        loadCustomers();
        imgViewBack = findViewById(R.id.imgViewBackEdit);
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
                        customerList.add(customer);
                    }
                }
                adapter.notifyDataSetChanged();
                if (customerList.isEmpty()) {
                    Toast.makeText(ActivityEditCustomer.this, "Không có khách hàng nào để hiển thị", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ActivityEditCustomer.this, "Lỗi tải dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}