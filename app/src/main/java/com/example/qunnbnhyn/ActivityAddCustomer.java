package com.example.qunnbnhyn;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityAddCustomer extends AppCompatActivity {

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_customer);

        databaseReference = FirebaseDatabase.getInstance("https://quananbinhyen-cntt2-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("customers");

        TextInputEditText textBoxName = findViewById(R.id.textBoxName);
        TextInputEditText textBoxDate = findViewById(R.id.textBoxDate);
        TextInputEditText textBoxPhoneNumber = findViewById(R.id.textBoxPhoneNumber);
        TextInputEditText textBoxAddress = findViewById(R.id.textBoxAddress);
        TextInputEditText textBoxIDCard = findViewById(R.id.textBoxIDCard);
        MaterialButton btnAddNewCustomer = findViewById(R.id.btnAddNewCustomer);
        ImageView imgViewBack = findViewById(R.id.imgViewBack);

        imgViewBack.setOnClickListener(v -> finish());

        btnAddNewCustomer.setOnClickListener(v -> {
            String name = textBoxName.getText().toString().trim();
            String date = textBoxDate.getText().toString().trim();
            String phone = textBoxPhoneNumber.getText().toString().trim();
            String address = textBoxAddress.getText().toString().trim();
            String idCard = textBoxIDCard.getText().toString().trim();

            if (name.isEmpty() || date.isEmpty() || phone.isEmpty() || address.isEmpty() || idCard.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_LONG).show();
                return;
            }

            Customer customer = new Customer(name, date, phone, address, idCard);
            String customerId = databaseReference.push().getKey();
            customer.setId(customerId);

            databaseReference.child(customerId).setValue(customer)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Thêm khách hàng thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }
}