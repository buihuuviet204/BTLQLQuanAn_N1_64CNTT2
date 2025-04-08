package com.example.qunnbnhyn;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ActivityShowReport extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private int lastSelectedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_report);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Load default fragment
        loadFragment(new ActivityFragmentIncome(), false);

        // Handle navigation item clicks
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int newPosition = 0;

            // Sử dụng if-else thay vì switch-case
            if (item.getItemId() == R.id.nav_income) {
                selectedFragment = new ActivityFragmentIncome();
                newPosition = 0;
            } else if (item.getItemId() == R.id.nav_salary) {
                selectedFragment = new ActivityFragmentSalary();
                newPosition = 1;
            } else if (item.getItemId() == R.id.nav_popular) {
                selectedFragment = new ActivityFragmentPopularFood();
                newPosition = 2;
            }

            return loadFragment(selectedFragment, newPosition > lastSelectedPosition);
        });
    }

    private boolean loadFragment(Fragment fragment, boolean isMovingRight) {
        if (fragment != null) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            if (currentFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            } else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }

            lastSelectedPosition = bottomNavigationView.getSelectedItemId() == R.id.nav_income ? 0 :
                    bottomNavigationView.getSelectedItemId() == R.id.nav_salary ? 1 : 2;
            return true;
        }
        return false;
    }
}