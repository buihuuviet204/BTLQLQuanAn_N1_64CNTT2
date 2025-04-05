package com.example.qunnbnhyn;

import android.os.Bundle;
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

public class ActivityPriorityCustomer extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PriorityCustomerAdapter adapter;
    private List<Customer> customerList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_priority);

        recyclerView = findViewById(R.id.rcPriorityCustomer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        customerList = new ArrayList<>();
        adapter = new PriorityCustomerAdapter(customerList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("customers");
        loadCustomers();
    }

    private void loadCustomers() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                customerList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Customer customer = snapshot.getValue(Customer.class);
                    customerList.add(customer);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}