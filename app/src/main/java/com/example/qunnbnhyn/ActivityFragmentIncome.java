package com.example.qunnbnhyn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActivityFragmentIncome extends Fragment {

    private Spinner spinnerTime;
    private EditText etDatePicker;
    private Button btnConfirm;
    private TextView tvDailyIncome, tvMonthlyIncome, tvCashPayment, tvTransferPayment;
    private ImageView imageView;

    private FirebaseFirestore db;
    private SimpleDateFormat dayFormat, monthFormat;

    public ActivityFragmentIncome() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income, container, false);

        // Initialize UI components
        spinnerTime = view.findViewById(R.id.spinner_time);
        etDatePicker = view.findViewById(R.id.et_date_picker);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        tvDailyIncome = view.findViewById(R.id.tv_daily_income);
        tvMonthlyIncome = view.findViewById(R.id.tv_monthly_income);
        tvCashPayment = view.findViewById(R.id.tv_cash_payment);
        tvTransferPayment = view.findViewById(R.id.tv_transfer_payment);
        imageView = view.findViewById(R.id.imageView);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance("https://quananbinhyen-cntt2-default-rtdb.asia-southeast1.firebasedatabase.app");

        // Initialize date formats
        dayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        monthFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());

        // Set up spinner options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.time_options, // Define this in res/values/arrays.xml as {"Theo ngày", "Theo tháng"}
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(adapter);

        // Set up button listeners
        btnConfirm.setOnClickListener(v -> fetchInvoices());
        imageView.setOnClickListener(v -> requireActivity().finish());

        return view;
    }

    private void fetchInvoices() {
        String dateInput = etDatePicker.getText().toString().trim();
        int selectedOption = spinnerTime.getSelectedItemPosition();

        if (dateInput.isEmpty()) {
            etDatePicker.setError("Vui lòng nhập ngày/tháng");
            return;
        }

        try {
            Date date;
            String startDateStr, endDateStr;

            if (selectedOption == 0) { // Theo ngày
                // Parse the input date (e.g., "08/03/2025")
                date = dayFormat.parse(dateInput);
                if (date == null) {
                    Toast.makeText(getContext(), "Định dạng ngày không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                // For daily query, start and end are the same day
                startDateStr = dayFormat.format(date);
                endDateStr = startDateStr;

            } else { // Theo tháng
                // Parse the input month (e.g., "03/2025")
                date = monthFormat.parse(dateInput);
                if (date == null) {
                    Toast.makeText(getContext(), "Định dạng tháng không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                // For monthly query, calculate the start and end of the month
                SimpleDateFormat dayMonthFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                startDateStr = "01/" + dateInput; // First day of the month
                Date startDate = dayMonthFormat.parse(startDateStr);

                // Calculate the last day of the month
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                calendar.setTime(date);
                int lastDay = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
                endDateStr = lastDay + "/" + dateInput; // Last day of the month
            }

            // Query Firestore for invoices within the date range
            queryFirestore(startDateStr, endDateStr, selectedOption);

        } catch (ParseException e) {
            Toast.makeText(getContext(), "Định dạng ngày/tháng không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private void queryFirestore(String startDateStr, String endDateStr, int selectedOption) {
        // Convert dates to timestamps for Firestore query
        SimpleDateFormat firestoreFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date startDate = firestoreFormat.parse(startDateStr);
            Date endDate = firestoreFormat.parse(endDateStr);

            // Add one day to endDate to include the whole day
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
            Date endDatePlusOne = calendar.getTime();

            long startTimestamp = startDate.getTime();
            long endTimestamp = endDatePlusOne.getTime();

            // Query Firestore
            db.collection("invoices")
                    .whereGreaterThanOrEqualTo("timestamp", startTimestamp)
                    .whereLessThan("timestamp", endTimestamp)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            long dailyIncome = 0;
                            long monthlyIncome = 0;
                            long cashPayment = 0;
                            long transferPayment = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                long amount = document.getLong("amount");
                                String paymentMethod = document.getString("paymentMethod");

                                if (selectedOption == 0) { // Daily
                                    dailyIncome += amount;
                                } else { // Monthly
                                    monthlyIncome += amount;
                                }

                                if ("cash".equalsIgnoreCase(paymentMethod)) {
                                    cashPayment += amount;
                                } else if ("transfer".equalsIgnoreCase(paymentMethod)) {
                                    transferPayment += amount;
                                }
                            }

                            // Update UI
                            if (selectedOption == 0) { // Daily
                                tvDailyIncome.setText(String.format("%,d VNĐ", dailyIncome));
                                tvMonthlyIncome.setText("N/A");
                            } else { // Monthly
                                tvDailyIncome.setText("N/A");
                                tvMonthlyIncome.setText(String.format("%,d VNĐ", monthlyIncome));
                            }
                            tvCashPayment.setText(String.format("%,d VNĐ", cashPayment));
                            tvTransferPayment.setText(String.format("%,d VNĐ", transferPayment));

                        } else {
                            Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (ParseException e) {
            Toast.makeText(getContext(), "Lỗi xử lý ngày tháng", Toast.LENGTH_SHORT).show();
        }
    }
}