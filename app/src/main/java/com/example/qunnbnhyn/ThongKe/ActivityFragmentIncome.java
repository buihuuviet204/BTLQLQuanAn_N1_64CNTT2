package com.example.qunnbnhyn.ThongKe;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qunnbnhyn.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.TimeZone;

public class ActivityFragmentIncome extends Fragment {

    private ImageView imageView;
    private Spinner spinnerTime;
    private TextView tvDatePicker;
    private Button btnConfirm;
    private RecyclerView rvInvoices;
    private TextView tvTotal, tvCashPayment, tvTransferPayment;
    private DatabaseReference databaseReference;
    private List<Invoice> invoiceList;
    private List<Object> filteredInvoiceList;
    private InvoiceAdapter invoiceAdapter;
    private SimpleDateFormat dayFormat, monthFormat;
    private List<String> dateOptions;
    private String selectedDateValue;
    private long totalMoney, cashPayment, transferPayment;

    public ActivityFragmentIncome() {
        // Required empty public constructor
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income, container, false);

        // Initialize UI components
        imageView = view.findViewById(R.id.imageView);
        spinnerTime = view.findViewById(R.id.spinner_time);
        tvDatePicker = view.findViewById(R.id.tv_date_picker);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        rvInvoices = view.findViewById(R.id.rv_invoices);
        tvTotal = view.findViewById(R.id.tvTotal);
        tvCashPayment = view.findViewById(R.id.tvCashPayment);
        tvTransferPayment = view.findViewById(R.id.tvTransferPayment);

        // Initialize RecyclerView
        invoiceList = new ArrayList<>();
        filteredInvoiceList = new ArrayList<>();
        invoiceAdapter = new InvoiceAdapter(filteredInvoiceList);
        rvInvoices.setLayoutManager(new LinearLayoutManager(getContext()));
        rvInvoices.setAdapter(invoiceAdapter);

        // Initialize Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance("https://quananbinhyen-cntt2-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("hoa_don");

        // Initialize date formats with timezone
        dayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        monthFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        dayFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        monthFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));

        // Initialize date options
        dateOptions = new ArrayList<>();
        dateOptions.add("Chọn ngày/tháng");
        selectedDateValue = "";
        totalMoney = 0;
        cashPayment = 0;
        transferPayment = 0;

        // Set up Spinner options for filter type
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.time_options,
                android.R.layout.simple_spinner_item
        );
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(filterAdapter);

        // Set up Spinner listener to change TextView hint and reset selection
        spinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvDatePicker.setText("");
                selectedDateValue = "";
                filteredInvoiceList.clear();
                invoiceAdapter.notifyDataSetChanged();
                tvTotal.setText("Tổng thu: 0 VNĐ");
                tvCashPayment.setText("Tiền mặt: 0 VNĐ");
                tvTransferPayment.setText("Chuyển khoản: 0 VNĐ");
                if (position == 0) { // Theo ngày
                    tvDatePicker.setHint("Chọn ngày");
                    tvDatePicker.setVisibility(View.VISIBLE);
                } else if (position == 1) { // Theo tháng
                    tvDatePicker.setHint("Chọn tháng");
                    tvDatePicker.setVisibility(View.VISIBLE);
                } else { // Tất cả
                    tvDatePicker.setVisibility(View.GONE);
                }
                updateDateOptions();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Show AlertDialog with date/month options when TextView is clicked
        tvDatePicker.setOnClickListener(v -> showDateOptionsDialog());

        // Set up Confirm button listener
        btnConfirm.setOnClickListener(v -> {
            if (spinnerTime.getSelectedItemPosition() == 2) { // Tất cả
                showAllInvoices();
            } else if (selectedDateValue.isEmpty() || selectedDateValue.equals("Chọn ngày/tháng")) {
                Toast.makeText(getContext(), "Vui lòng chọn ngày/tháng", Toast.LENGTH_SHORT).show();
            } else if (spinnerTime.getSelectedItemPosition() == 0) { // Theo ngày
                filterInvoicesByDay(selectedDateValue);
            } else { // Theo tháng
                filterInvoicesByMonth(selectedDateValue);
            }
        });

        // Set up button listener for back button
        imageView.setOnClickListener(v -> requireActivity().finish());

        // Fetch all invoices
        fetchInvoices();

        return view;
    }

    private void showDateOptionsDialog() {
        String[] optionsArray = dateOptions.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chọn ngày/tháng");
        builder.setItems(optionsArray, (dialog, which) -> {
            String selected = optionsArray[which];
            if (!selected.equals("Chọn ngày/tháng")) {
                tvDatePicker.setText(selected);
                selectedDateValue = selected;
            } else {
                tvDatePicker.setText("");
                selectedDateValue = "";
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void fetchInvoices() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                invoiceList.clear();

                for (DataSnapshot invoiceSnapshot : snapshot.getChildren()) {
                    try {
                        String invoiceId = invoiceSnapshot.getKey();
                        String maKhach = invoiceSnapshot.child("maKhach").getValue(String.class);
                        String dateStr = invoiceSnapshot.child("ngLap").getValue(String.class);
                        Long tongTien = invoiceSnapshot.child("tongTien").getValue(Long.class);
                        String phuongThucThanhToan = invoiceSnapshot.child("phuongThucThanhToan").getValue(String.class);

                        // Parse the date string to a timestamp
                        Long timestamp = null;
                        if (dateStr != null) {
                            Date date = dayFormat.parse(dateStr);
                            if (date != null) {
                                timestamp = date.getTime();
                            }
                        }

                        Invoice invoice = new Invoice(invoiceId, maKhach, timestamp, tongTien,  phuongThucThanhToan, dateStr);
                        invoiceList.add(invoice);
                        Log.d("Invoice", "Added invoice: " + invoiceId + ", Date: " + (timestamp != null ? dayFormat.format(new Date(timestamp)) : "N/A"));
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Lỗi khi đọc dữ liệu hóa đơn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                updateDateOptions();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDateOptions() {
        dateOptions.clear();
        dateOptions.add("Chọn ngày/tháng");

        Set<String> uniqueDates = new HashSet<>();
        for (Invoice invoice : invoiceList) {
            Long timestamp = invoice.getTimestamp();
            if (timestamp != null) {
                Date date = new Date(timestamp);
                if (spinnerTime.getSelectedItemPosition() == 0) { // Theo ngày
                    String day = dayFormat.format(date);
                    uniqueDates.add(day);
                } else if (spinnerTime.getSelectedItemPosition() == 1) { // Theo tháng
                    String month = monthFormat.format(date);
                    uniqueDates.add(month);
                }
            }
        }

        dateOptions.addAll(uniqueDates);
        Log.d("DateOptions", "Date options: " + dateOptions.toString());
    }

    private void showAllInvoices() {
        filteredInvoiceList.clear();
        totalMoney = 0;
        cashPayment = 0;
        transferPayment = 0;

        List<Invoice> tempList = new ArrayList<>(invoiceList);
        Collections.sort(tempList, (i1, i2) -> Long.compare(i2.getTimestamp(), i1.getTimestamp()));
        filteredInvoiceList.addAll(tempList);

        for (Object item : filteredInvoiceList) {
            if (item instanceof Invoice) {
                Invoice invoice = (Invoice) item;
                Long tongTien = invoice.getTongTien();
                if (tongTien != null) {
                    totalMoney += tongTien;
                    String paymentMethod = invoice.getPaymentMethod();
                    if ("Tiền mặt".equals(paymentMethod)) {
                        cashPayment += tongTien;
                    } else if ("Chuyển khoản".equals(paymentMethod)) {
                        transferPayment += tongTien;
                    }
                }
            }
        }

        Log.d("Filter", "Số lượng hóa đơn đã lọc: " + filteredInvoiceList.size());
        invoiceAdapter.notifyDataSetChanged();
        tvTotal.setText(String.format("Tổng thu: %,d VNĐ", totalMoney));
        tvCashPayment.setText(String.format("Tiền mặt: %,d VNĐ", cashPayment));
        tvTransferPayment.setText(String.format("Chuyển khoản: %,d VNĐ", transferPayment));

        if (filteredInvoiceList.isEmpty()) {
            Toast.makeText(getContext(), "Không có hóa đơn nào", Toast.LENGTH_SHORT).show();
        }
    }

    private void filterInvoicesByDay(String selectedDate) {
        filteredInvoiceList.clear();
        totalMoney = 0;
        cashPayment = 0;
        transferPayment = 0;

        try {
            Date selected = dayFormat.parse(selectedDate);
            if (selected == null) return;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(selected);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long startTimestamp = calendar.getTimeInMillis();

            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            long endTimestamp = calendar.getTimeInMillis();

            List<Invoice> tempList = new ArrayList<>();
            for (Invoice invoice : invoiceList) {
                Long timestamp = invoice.getTimestamp();
                if (timestamp != null && timestamp >= startTimestamp && timestamp <= endTimestamp) {
                    tempList.add(invoice);
                }
            }

            // Sắp xếp hóa đơn theo thời gian (mới nhất trước)
            Collections.sort(tempList, (i1, i2) -> Long.compare(i2.getTimestamp(), i1.getTimestamp()));
            filteredInvoiceList.addAll(tempList);

            for (Object item : filteredInvoiceList) {
                if (item instanceof Invoice) {
                    Invoice invoice = (Invoice) item;
                    Long tongTien = invoice.getTongTien();
                    if (tongTien != null) {
                        totalMoney += tongTien;
                        String paymentMethod = invoice.getPaymentMethod();
                        if ("Tiền mặt".equals(paymentMethod)) {
                            cashPayment += tongTien;
                        } else if ("Chuyển khoản".equals(paymentMethod)) {
                            transferPayment += tongTien;
                        }
                    }
                }
            }

            Log.d("Filter", "Số lượng hóa đơn đã lọc: " + filteredInvoiceList.size());
            invoiceAdapter.notifyDataSetChanged();
            tvTotal.setText(String.format("Tổng thu: %,d VNĐ", totalMoney));
            tvCashPayment.setText(String.format("Tiền mặt: %,d VNĐ", cashPayment));
            tvTransferPayment.setText(String.format("Chuyển khoản: %,d VNĐ", transferPayment));

            if (filteredInvoiceList.isEmpty()) {
                Toast.makeText(getContext(), "Không có hóa đơn cho ngày " + selectedDate, Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            Toast.makeText(getContext(), "Định dạng ngày không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private void filterInvoicesByMonth(String selectedMonthYear) {
        filteredInvoiceList.clear();
        totalMoney = 0;
        cashPayment = 0;
        transferPayment = 0;

        try {
            Date selected = monthFormat.parse(selectedMonthYear);
            if (selected == null) return;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(selected);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long startTimestamp = calendar.getTimeInMillis();

            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            long endTimestamp = calendar.getTimeInMillis();

            // Map để gom nhóm hóa đơn theo ngày
            Map<String, List<Invoice>> invoicesByDay = new HashMap<>();

            // Lọc hóa đơn trong tháng và gom nhóm theo ngày
            for (Invoice invoice : invoiceList) {
                Long timestamp = invoice.getTimestamp();
                if (timestamp != null && timestamp >= startTimestamp && timestamp <= endTimestamp) {
                    String dayKey = dayFormat.format(new Date(timestamp));
                    invoicesByDay.computeIfAbsent(dayKey, k -> new ArrayList<>()).add(invoice);

                    Long tongTien = invoice.getTongTien();
                    if (tongTien != null) {
                        totalMoney += tongTien;
                        String paymentMethod = invoice.getPaymentMethod();
                        if ("Tiền mặt".equals(paymentMethod)) {
                            cashPayment += tongTien;
                        } else if ("Chuyển khoản".equals(paymentMethod)) {
                            transferPayment += tongTien;
                        }
                    }
                }
            }

            // Sắp xếp các ngày theo thứ tự giảm dần (mới nhất trước)
            List<String> sortedDays = new ArrayList<>(invoicesByDay.keySet());
            Collections.sort(sortedDays, (d1, d2) -> {
                try {
                    Date date1 = dayFormat.parse(d1);
                    Date date2 = dayFormat.parse(d2);
                    return date2.compareTo(date1); // Mới nhất trước
                } catch (ParseException e) {
                    return 0;
                }
            });

            // Thêm các nhóm vào filteredInvoiceList
            for (String day : sortedDays) {
                // Thêm header (ngày)
                filteredInvoiceList.add("Ngày: " + day);
                // Thêm danh sách hóa đơn trong ngày đó
                List<Invoice> dayInvoices = invoicesByDay.get(day);
                // Sắp xếp hóa đơn trong ngày theo thời gian (mới nhất trước)
                Collections.sort(dayInvoices, (i1, i2) -> Long.compare(i2.getTimestamp(), i1.getTimestamp()));
                filteredInvoiceList.addAll(dayInvoices);
            }

            Log.d("Filter", "Số lượng hóa đơn đã lọc: " + filteredInvoiceList.size());
            invoiceAdapter.notifyDataSetChanged();
            tvTotal.setText(String.format("Tổng thu: %,d VNĐ", totalMoney));
            tvCashPayment.setText(String.format("Tiền mặt: %,d VNĐ", cashPayment));
            tvTransferPayment.setText(String.format("Chuyển khoản: %,d VNĐ", transferPayment));

            if (filteredInvoiceList.isEmpty()) {
                Toast.makeText(getContext(), "Không có hóa đơn cho tháng " + selectedMonthYear, Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            Toast.makeText(getContext(), "Định dạng tháng không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }
}