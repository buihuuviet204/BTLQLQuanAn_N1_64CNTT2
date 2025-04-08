package com.example.qunnbnhyn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class ActivityFragmentSalary extends Fragment {

    private Spinner spinnerTimeSalary;
    private EditText etDatePickerSalary;
    private Button btnConfirmSalary;
    private LinearLayout llSalaryList;

    public ActivityFragmentSalary() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_salary, container, false);

        spinnerTimeSalary = view.findViewById(R.id.spinner_time_salary);
        etDatePickerSalary = view.findViewById(R.id.et_date_picker_salary);
        btnConfirmSalary = view.findViewById(R.id.btn_confirm_salary);
        llSalaryList = view.findViewById(R.id.ll_salary_list);

        btnConfirmSalary.setOnClickListener(v -> updateSalaryList());

        return view;
    }

    private void updateSalaryList() {
        String dateInput = etDatePickerSalary.getText().toString().trim();
        int selectedOption = spinnerTimeSalary.getSelectedItemPosition();

        if (dateInput.isEmpty()) {
            etDatePickerSalary.setError("Vui lòng nhập ngày/tháng");
            return;
        }

        llSalaryList.removeAllViews();

        // Dữ liệu mẫu
        String[][] employees = (selectedOption == 0) ? // Theo ngày
                new String[][]{{"Nguyễn Văn A", "NV001", "Sáng", "100,000"}, {"Trần Thị B", "NV002", "Chiều", "120,000"}} :
                new String[][]{{"Nguyễn Văn A", "NV001", "Sáng", "3,000,000"}, {"Trần Thị B", "NV002", "Chiều", "3,500,000"}};

        for (String[] emp : employees) {
            View employeeView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, null);
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setPadding(8, 8, 8, 8);

            TextView tvName = new TextView(getContext());
            tvName.setText(emp[0]);
            tvName.setTextSize(16);
            tvName.setTextColor(0xFF212121);
            tvName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvId = new TextView(getContext());
            tvId.setText(emp[1]);
            tvId.setTextSize(16);
            tvId.setTextColor(0xFF757575);
            tvId.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvShift = new TextView(getContext());
            tvShift.setText(emp[2]);
            tvShift.setTextSize(16);
            tvShift.setTextColor(0xFF757575);
            tvShift.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvIncome = new TextView(getContext());
            tvIncome.setText(emp[3] + " VNĐ");
            tvIncome.setTextSize(18);
            tvIncome.setTextColor(0xFFFF5722);
            tvIncome.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            layout.addView(tvName);
            layout.addView(tvId);
            layout.addView(tvShift);
            layout.addView(tvIncome);
            llSalaryList.addView(layout);
        }
    }
}