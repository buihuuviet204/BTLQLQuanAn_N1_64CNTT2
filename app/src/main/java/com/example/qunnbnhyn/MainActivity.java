package com.example.qunnbnhyn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ImageView diningTableImageView, logout, timetable;
    private ImageView supplies, customService, happyClient, discount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ ImageView "Quản lý bàn ăn" từ GridLayout
        diningTableImageView = findViewById(R.id.fram1).findViewById(R.id.dining_table_image);
        supplies = findViewById(R.id.fram1).findViewById(R.id.supplies);
        customService = findViewById(R.id.fram2).findViewById(R.id.custom_service);
        happyClient = findViewById(R.id.fram2).findViewById(R.id.happy_client);
        discount = findViewById(R.id.fram3).findViewById(R.id.discount);
        logout = findViewById(R.id.logout);
        timetable = findViewById(R.id.timetable);

        // Gắn sự kiện onClick cho ImageView "Quản lý bàn ăn"
        diningTableImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang EmployeeListActivity
                    Intent intent = new Intent(MainActivity.this, EmployeeListActivity.class);
                    startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang LogoutActivity
                Intent intent = new Intent(MainActivity.this, LogoutActivity.class);
                startActivity(intent);
            }
        });

        timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        supplies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        customService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        happyClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        discount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}