package com.example.qunnbnhyn;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MenuNVActivity extends AppCompatActivity {

    private ImageView diningTableImageView;
    private ImageView order, happyClient, payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ ImageView "Quản lý bàn ăn" từ GridLayout
        diningTableImageView = findViewById(R.id.fram1).findViewById(R.id.dining_table_image);
        order = findViewById(R.id.fram1).findViewById(R.id.order);
        happyClient = findViewById(R.id.fram2).findViewById(R.id.happy_client);
        payment = findViewById(R.id.fram2).findViewById(R.id.payment);

        // Gắn sự kiện onClick cho ImageView "Quản lý bàn ăn"
        diningTableImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang ManageTableActivity
                //   Intent intent = new Intent(MenuNVActivity.this, ManageTableActivity.class);
                //    startActivity(intent);
            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        happyClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
