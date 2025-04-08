package com.example.qunnbnhyn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class ActivityFragmentIncome extends Fragment {

    private Spinner spinnerTime;
    private EditText etDatePicker;
    private Button btnConfirm;
    private TextView tvDailyIncome, tvMonthlyIncome, tvCashPayment, tvTransferPayment;

    public ActivityFragmentIncome() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income, container, false);

        spinnerTime = view.findViewById(R.id.spinner_time);
        etDatePicker = view.findViewById(R.id.et_date_picker);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        tvDailyIncome = view.findViewById(R.id.tv_daily_income);
        tvMonthlyIncome = view.findViewById(R.id.tv_monthly_income);
        tvCashPayment = view.findViewById(R.id.tv_cash_payment);
        tvTransferPayment = view.findViewById(R.id.tv_transfer_payment);

        btnConfirm.setOnClickListener(v -> updateStats());

        return view;
    }

    private void updateStats() {
        String dateInput = etDatePicker.getText().toString().trim();
        int selectedOption = spinnerTime.getSelectedItemPosition();

        if (dateInput.isEmpty()) {
            etDatePicker.setError("Vui lòng nhập ngày/tháng");
            return;
        }

        // Dữ liệu mẫu (có thể thay bằng dữ liệu thực tế)
        if (selectedOption == 0) { // Theo ngày
            tvDailyIncome.setText(String.format("%,d VNĐ", 5000000));
            tvMonthlyIncome.setText(String.format("%,d VNĐ", 15000000));
            tvCashPayment.setText(String.format("%,d VNĐ", 3000000));
            tvTransferPayment.setText(String.format("%,d VNĐ", 2000000));
        } else { // Theo tháng
            tvDailyIncome.setText(String.format("%,d VNĐ", 5000000));
            tvMonthlyIncome.setText(String.format("%,d VNĐ", 15000000));
            tvCashPayment.setText(String.format("%,d VNĐ", 9000000));
            tvTransferPayment.setText(String.format("%,d VNĐ", 6000000));
        }
    }
}