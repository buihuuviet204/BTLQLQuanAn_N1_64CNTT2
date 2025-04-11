package com.example.qunnbnhyn.QLM;

import android.os.Bundle;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cloudinary.android.MediaManager;
import com.example.qunnbnhyn.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

public class QLMON extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Spinner spinner;
    String[] options = {"Mi Kay","Tra sua","Tra hoa qua","Nuoc co ga","Do an vat","Combo"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qlmon);
        bottomNavigationView = findViewById(R.id.navbarbottom);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.framelayout,new ThemMonFragment())
                .commit();
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if(item.getItemId() == R.id.it_add) selectedFragment = new ThemMonFragment();
            else if(item.getItemId() == R.id.it_edit)   selectedFragment = new SuaMonFragment();
            else if(item.getItemId() == R.id.it_delete) selectedFragment = new XoaMonFragment();
//            RecyclerView recyclerView = getRecyclerViewFromFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.framelayout,selectedFragment)
                    .commit();
            return true;
        });
    }
}