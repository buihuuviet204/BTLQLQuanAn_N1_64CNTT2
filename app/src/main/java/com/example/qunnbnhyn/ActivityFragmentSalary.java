package com.example.qunnbnhyn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActivityFragmentSalary extends Fragment {

    private LinearLayout llSalaryList;
    private ImageView imageView;

    private DatabaseReference databaseReference;

    private static final int HOURLY_RATE = 17000; // 30,000 VNĐ per hour
    private static final int SHIFT_1_HOURS = 5;   // Ca 1: 5 hours
    private static final int SHIFT_2_HOURS = 4;   // Ca 2: 6 hours
    private static final int DAYS_PER_MONTH = 30; // Assume 30 days for monthly calculation

    public ActivityFragmentSalary() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_salary, container, false);

        // Initialize UI components
        llSalaryList = view.findViewById(R.id.ll_salary_list);
        imageView = view.findViewById(R.id.imageView2);

        // Initialize Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance("https://quananbinhyen-cntt2-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Employees");

        // Set up button listener for back button
        imageView.setOnClickListener(v -> requireActivity().finish());

        // Fetch employee data immediately
        fetchEmployees();

        return view;
    }

    private void fetchEmployees() {
        // Query Realtime Database for employees
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Employee> employees = new ArrayList<>();

                for (DataSnapshot employeeSnapshot : snapshot.getChildren()) {
                    try {
                        // Safely fetch fields, handling potential type mismatches
                        String name = employeeSnapshot.child("name").getValue(String.class);
                        String employeeId = employeeSnapshot.child("email").getValue(String.class);
                        String shift = employeeSnapshot.child("shift").getValue(String.class);
                        String position = employeeSnapshot.child("position").getValue(String.class);

                        if ("Nhân viên".equals(position)) {
                            Employee employee = new Employee(name, employeeId, shift);
                            employees.add(employee);
                        }
                    } catch (Exception e) {
                        // Log the error and skip this employee
                        Toast.makeText(getContext(), "Lỗi khi đọc dữ liệu nhân viên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                // Update UI with employee salary data
                updateSalaryList(employees);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSalaryList(List<Employee> employees) {
        llSalaryList.removeAllViews();

        for (Employee emp : employees) {
            // Calculate daily salary based on shift
            int hours = emp.getShift() != null && "1".equals(emp.getShift()) ? SHIFT_1_HOURS : SHIFT_2_HOURS;
            long dailySalary = hours * HOURLY_RATE; // Daily salary
            long monthlySalary = dailySalary * DAYS_PER_MONTH; // Monthly salary (assuming 30 days)

            // Create a view for each employee
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(16, 16, 16, 16);

            // Vertical layout for name, ID, and shift (each on a separate line)
            LinearLayout infoLayout = new LinearLayout(getContext());
            infoLayout.setOrientation(LinearLayout.VERTICAL);

            TextView tvName = new TextView(getContext());
            tvName.setText("Họ và tên: " + emp.getName());
            tvName.setTextSize(16);
            tvName.setTextColor(0xFF212121);
            tvName.setPadding(0, 0, 0, 8); // Add some padding between lines
            tvName.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            TextView tvId = new TextView(getContext());
            tvId.setText("ID: " + (emp.getEmployeeId() != null ? emp.getEmployeeId() : "N/A"));
            tvId.setTextSize(16);
            tvId.setTextColor(0xFF757575);
            tvId.setPadding(0, 0, 0, 8); // Add some padding between lines
            tvId.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            TextView tvShift = new TextView(getContext());
            tvShift.setText("Ca làm: " + (emp.getShift() != null ? emp.getShift() : "N/A"));
            tvShift.setTextSize(16);
            tvShift.setTextColor(0xFF757575);
            tvShift.setPadding(0, 0, 0, 8); // Add some padding between lines
            tvShift.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            infoLayout.addView(tvName);
            infoLayout.addView(tvId);
            infoLayout.addView(tvShift);

            // Horizontal layout for daily and monthly salary
            LinearLayout salaryLayout = new LinearLayout(getContext());
            salaryLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvDailySalary = new TextView(getContext());
            tvDailySalary.setText(String.format("Lương ngày: \n%,d VNĐ", dailySalary));
            tvDailySalary.setTextSize(16);
            tvDailySalary.setTextColor(0xFFFF5722);
            tvDailySalary.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvMonthlySalary = new TextView(getContext());
            tvMonthlySalary.setText(String.format("Lương tháng: %,d VNĐ", monthlySalary));
            tvMonthlySalary.setTextSize(16);
            tvMonthlySalary.setTextColor(0xFF2196F3);
            tvMonthlySalary.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            salaryLayout.addView(tvDailySalary);
            salaryLayout.addView(tvMonthlySalary);

            // Add both layouts to the main layout
            layout.addView(infoLayout);
            layout.addView(salaryLayout);

            // Add a divider between employees
            View divider = new View(getContext());
            divider.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
            ));
            divider.setBackgroundColor(0xFFE0E0E0);

            llSalaryList.addView(layout);
            llSalaryList.addView(divider);
        }
    }

    // Employee model class
    private static class Employee {
        private String name;
        private String employeeId;
        private String shift;

        public Employee(String name, String employeeId, String shift) {
            this.name = name;
            this.employeeId = employeeId;
            this.shift = shift;
        }

        public String getName() {
            return name;
        }

        public String getEmployeeId() {
            return employeeId;
        }

        public String getShift() {
            return shift;
        }
    }
}