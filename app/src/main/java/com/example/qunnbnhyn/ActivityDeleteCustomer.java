package com.example.qunnbnhyn;

import android.app.AlertDialog;
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

public class ActivityDeleteCustomer extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomerAdapter adapter;
    private List<Customer> customerList;
    private DatabaseReference databaseReference;
    ImageView imgViewBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete_customer);

        recyclerView = findViewById(R.id.rcDeleteCustomer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        customerList = new ArrayList<>();
        adapter = new CustomerAdapter(customerList, this::showDeleteConfirmation);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance("https://quananbinhyen-cntt2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("customers");
        loadCustomers();
        imgViewBack = findViewById(R.id.imgViewBackDelete);
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ActivityDeleteCustomer.this, "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmation(Customer customer) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa khách hàng " + customer.getName() + "?")
                .setPositiveButton("Có", (dialog, which) -> {
                    databaseReference.child(customer.getId()).removeValue()
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Không", null)
                .show();
    }
}