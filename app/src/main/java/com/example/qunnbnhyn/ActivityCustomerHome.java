package com.example.qunnbnhyn;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;

public class ActivityCustomerHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_home_page);

        // Khởi tạo các nút
        Button btnAddCustomer = findViewById(R.id.btnAddCustomer);
        Button btnEditCustomer = findViewById(R.id.btnEditCustomer);
        Button btnDeleteCustomer = findViewById(R.id.btnDeleteCustomer);
        Button btnPriorityCustomer = findViewById(R.id.btnPriorityCustomer);

        // Xử lý sự kiện click
        btnAddCustomer.setOnClickListener(v -> startActivity(new Intent(this, ActivityAddCustomer.class)));
        btnEditCustomer.setOnClickListener(v -> startActivity(new Intent(this, ActivityEditCustomer.class)));
        btnDeleteCustomer.setOnClickListener(v -> startActivity(new Intent(this, ActivityDeleteCustomer.class)));
        btnPriorityCustomer.setOnClickListener(v -> startActivity(new Intent(this, ActivityPriorityCustomer.class)));
    }
}