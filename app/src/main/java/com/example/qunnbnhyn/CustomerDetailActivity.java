package com.example.qunnbnhyn;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CustomerDetailActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private String customerId;
    private TextView tvName, tvPhone, tvVisitCount, tvPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_detail);

        customerId = getIntent().getStringExtra("customerId");
        if (customerId == null) {
            Toast.makeText(this, "Không tìm thấy ID khách hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo các TextView
        tvName = findViewById(R.id.tvCustomerName);
        tvPhone = findViewById(R.id.tvPhoneNumber);
        tvVisitCount = findViewById(R.id.tvVisitCount);
        tvPoints = findViewById(R.id.tvPoints);

        databaseReference = FirebaseDatabase.getInstance("https://quananbinhyen-cntt2-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("customers").child(customerId);

        loadCustomerDetails();
    }

    private void loadCustomerDetails() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Customer customer = snapshot.getValue(Customer.class);
                if (customer != null) {
                    tvName.setText(customer.getName());
                    tvPhone.setText(customer.getPhoneNumber());
                    tvVisitCount.setText(String.valueOf(customer.getVisitCount()));
                    tvPoints.setText(String.valueOf(customer.getPoints()));
                } else {
                    Toast.makeText(CustomerDetailActivity.this, "Không tìm thấy thông tin khách hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(CustomerDetailActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}