package com.example.qunnbnhyn;

import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityEditCustomerDetail extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private String customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_infomation_customer);

        customerId = getIntent().getStringExtra("customerId");
        databaseReference = FirebaseDatabase.getInstance().getReference("customers").child(customerId);

        TextInputEditText textBoxName = findViewById(R.id.textBoxName);
        TextInputEditText textBoxDate = findViewById(R.id.textBoxDate);
        TextInputEditText textBoxPhoneNumber = findViewById(R.id.textBoxPhoneNumber);
        TextInputEditText textBoxAddress = findViewById(R.id.textBoxAddress);
        TextInputEditText textBoxIDCard = findViewById(R.id.textBoxIDCard);
        MaterialButton btnConfirm = findViewById(R.id.btnAddNewCustomer);

        // Load data
        databaseReference.get().addOnSuccessListener(snapshot -> {
            Customer customer = snapshot.getValue(Customer.class);
            if (customer != null) {
                textBoxName.setText(customer.getName());
                textBoxDate.setText(customer.getDateOfBirth());
                textBoxPhoneNumber.setText(customer.getPhoneNumber());
                textBoxAddress.setText(customer.getAddress());
                textBoxIDCard.setText(customer.getIdCard());
            }
        });

        btnConfirm.setOnClickListener(v -> {
            String name = textBoxName.getText().toString().trim();
            String date = textBoxDate.getText().toString().trim();
            String phone = textBoxPhoneNumber.getText().toString().trim();
            String address = textBoxAddress.getText().toString().trim();
            String idCard = textBoxIDCard.getText().toString().trim();

            Customer updatedCustomer = new Customer(name, date, phone, address, idCard);
            updatedCustomer.setId(customerId);
            databaseReference.setValue(updatedCustomer)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}