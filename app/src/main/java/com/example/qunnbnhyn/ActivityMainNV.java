package com.example.qunnbnhyn;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ActivityMainNV extends AppCompatActivity {
    private TextView txtName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nv);
        Intent intent = getIntent();
        txtName = findViewById(R.id.txtName);
        txtName.setText(intent.getStringExtra("full_name"));
    }
}