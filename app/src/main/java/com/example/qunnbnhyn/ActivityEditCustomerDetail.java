package com.example.qunnbnhyn;

import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivityEditCustomerDetail extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private String customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_infomation_customer);

        customerId = getIntent().getStringExtra("customerId");
        if (customerId == null) {
            Toast.makeText(this, "Không tìm thấy ID khách hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance("https://quananbinhyen-cntt2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("customers").child(customerId);

        TextInputEditText textBoxName = findViewById(R.id.textBoxName);
        TextInputEditText textBoxDate = findViewById(R.id.textBoxDate);
        TextInputEditText textBoxPhoneNumber = findViewById(R.id.textBoxPhoneNumber);
        TextInputEditText textBoxAddress = findViewById(R.id.textBoxAddress);
        TextInputEditText textBoxIDCard = findViewById(R.id.textBoxIDCard);
        MaterialButton btnConfirm = findViewById(R.id.btnAddNewCustomer);

        // Load dữ liệu từ Firebase
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Customer customer = snapshot.getValue(Customer.class);
                if (customer != null) {
                    textBoxName.setText(customer.getName());
                    textBoxDate.setText(customer.getDateOfBirth());
                    textBoxPhoneNumber.setText(customer.getPhoneNumber());
                    textBoxAddress.setText(customer.getAddress());
                    textBoxIDCard.setText(customer.getIdCard());
                } else {
                    Toast.makeText(ActivityEditCustomerDetail.this, "Không tìm thấy thông tin khách hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ActivityEditCustomerDetail.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý nút xác nhận
        btnConfirm.setOnClickListener(v -> {
            String name = textBoxName.getText().toString().trim();
            String date = textBoxDate.getText().toString().trim();
            String phone = textBoxPhoneNumber.getText().toString().trim();
            String address = textBoxAddress.getText().toString().trim();
            String idCard = textBoxIDCard.getText().toString().trim();

            if (name.isEmpty() || date.isEmpty() || phone.isEmpty() || address.isEmpty() || idCard.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            Customer updatedCustomer = new Customer(name, date, phone, address, idCard);
            updatedCustomer.setId(customerId);

            databaseReference.setValue(updatedCustomer)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });
    }
}