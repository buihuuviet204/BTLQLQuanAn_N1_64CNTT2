package com.example.qunnbnhyn;

import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivittyAddCustomer extends AppCompatActivity {

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_customer);

        // Khởi tạo Firebase với URL cụ thể
        databaseReference = FirebaseDatabase.getInstance("https://quananbinhyen-cntt2-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("customers");

        // Khởi tạo views
        TextInputEditText textBoxName = findViewById(R.id.textBoxName);
        TextInputEditText textBoxDate = findViewById(R.id.textBoxDate);
        TextInputEditText textBoxPhoneNumber = findViewById(R.id.textBoxPhoneNumber);
        TextInputEditText textBoxAddress = findViewById(R.id.textBoxAddress);
        TextInputEditText textBoxIDCard = findViewById(R.id.textBoxIDCard);
        MaterialButton btnAddNewCustomer = findViewById(R.id.btnAddNewCustomer);

        // Xử lý nút xác nhận
        btnAddNewCustomer.setOnClickListener(v -> {
            Toast.makeText(this, "Nút được nhấn", Toast.LENGTH_SHORT).show(); // Kiểm tra sự kiện

            String name = textBoxName.getText().toString().trim();
            String date = textBoxDate.getText().toString().trim();
            String phone = textBoxPhoneNumber.getText().toString().trim();
            String address = textBoxAddress.getText().toString().trim();
            String idCard = textBoxIDCard.getText().toString().trim();

            if (name.isEmpty() || date.isEmpty() || phone.isEmpty() || address.isEmpty() || idCard.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            Customer customer = new Customer(name, date, phone, address, idCard);
            String customerId = databaseReference.push().getKey();
            customer.setId(customerId);

            Toast.makeText(this, "Đang lưu dữ liệu...", Toast.LENGTH_SHORT).show(); // Kiểm tra trước khi lưu

            databaseReference.child(customerId).setValue(customer)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Thêm khách hàng thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace(); // In lỗi chi tiết vào Logcat
                    });
        });
    }
}
