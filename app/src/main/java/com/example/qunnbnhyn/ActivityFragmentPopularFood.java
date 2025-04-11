package com.example.qunnbnhyn;

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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class ActivityFragmentPopularFood extends Fragment {

    private Spinner spinnerTimePopular;
    private TextView tvDatePickerPopular;
    private Button btnConfirmPopular;
    private LinearLayout llPopularList;
    private ImageView imageView;
    private DatabaseReference invoiceReference;
    private DatabaseReference menuReference;
    private SimpleDateFormat monthFormat;
    private List<String> monthOptions;
    private String selectedMonthValue;
    private List<Dish> dishList;
    private boolean isMenuLoaded = false;
    private boolean isMonthOptionsLoaded = false;
    private ValueEventListener monthListener; // Lưu listener để quản lý

    public ActivityFragmentPopularFood() {
        // Required empty public constructor
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_popular_food, container, false);

        // Initialize UI components
        spinnerTimePopular = view.findViewById(R.id.spinner_time_popular);
        tvDatePickerPopular = view.findViewById(R.id.tv_date_picker_popular);
        btnConfirmPopular = view.findViewById(R.id.btn_confirm_popular);
        llPopularList = view.findViewById(R.id.ll_popular_list);
        imageView = view.findViewById(R.id.imageView3);

        // Initialize Firebase
        invoiceReference = FirebaseDatabase.getInstance("https://quananbinhyen-cntt2-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("hoa_don");
        menuReference = FirebaseDatabase.getInstance("https://quananbinhyen-cntt2-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("thuc_don");

        // Initialize date format
        monthFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        monthFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));

        // Initialize data
        monthOptions = new ArrayList<>();
        monthOptions.add("Chọn tháng");
        selectedMonthValue = "";
        dishList = new ArrayList<>();

        // Set up Spinner with only two options
        String[] filterOptions = {"Theo tháng", "Tất cả"};
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                filterOptions
        );
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimePopular.setAdapter(filterAdapter);

        // Spinner listener
        spinnerTimePopular.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvDatePickerPopular.setText("");
                selectedMonthValue = "";
                llPopularList.removeAllViews();
                if (position == 0) { // Theo tháng
                    tvDatePickerPopular.setHint("Chọn tháng");
                    tvDatePickerPopular.setVisibility(View.VISIBLE);
                } else { // Tất cả
                    tvDatePickerPopular.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Month picker listener
        tvDatePickerPopular.setOnClickListener(v -> showMonthOptionsDialog());

        // Confirm button listener
        btnConfirmPopular.setOnClickListener(v -> {
            if (!isMenuLoaded) {
                Toast.makeText(getContext(), "Đang tải dữ liệu thực đơn...", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dishList.isEmpty()) {
                Toast.makeText(getContext(), "Không có món ăn nào trong thực đơn", Toast.LENGTH_SHORT).show();
                return;
            }
            if (spinnerTimePopular.getSelectedItemPosition() == 1) { // Tất cả
                updatePopularListForAll();
            } else if (selectedMonthValue.isEmpty() || selectedMonthValue.equals("Chọn tháng")) {
                Toast.makeText(getContext(), "Vui lòng chọn tháng", Toast.LENGTH_SHORT).show();
            } else { // Theo tháng
                updatePopularList();
            }
        });

        // Back button listener
        imageView.setOnClickListener(v -> requireActivity().finish());

        // Fetch data
        fetchMenu();
        fetchInvoices(); // Tải tháng một lần duy nhất

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Hủy listener khi Fragment bị hủy để tránh leak
        if (monthListener != null) {
            invoiceReference.removeEventListener(monthListener);
        }
    }

    private void showMonthOptionsDialog() {
        if (!isMonthOptionsLoaded) {
            Toast.makeText(getContext(), "Đang tải danh sách tháng, vui lòng chờ...", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] optionsArray = monthOptions.toArray(new String[0]);
        Log.d("PopularFood", "Showing dialog with options: " + monthOptions.toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chọn tháng");
        builder.setItems(optionsArray, (dialog, which) -> {
            String selected = optionsArray[which];
            if (!selected.equals("Chọn tháng")) {
                tvDatePickerPopular.setText(selected);
                selectedMonthValue = selected;
            } else {
                tvDatePickerPopular.setText("");
                selectedMonthValue = "";
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void fetchMenu() {
        menuReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dishList.clear();
                for (DataSnapshot dishSnapshot : snapshot.getChildren()) {
                    String id = dishSnapshot.getKey();
                    String name = dishSnapshot.child("name").getValue(String.class);
                    String imageMonAn = dishSnapshot.child("imageMonAn").getValue(String.class);
                    Long giaBan = dishSnapshot.child("giaban").getValue(Long.class);

                    Dish dish = new Dish(id, name, imageMonAn, giaBan);
                    dishList.add(dish);
                }
                isMenuLoaded = true;
                if (dishList.isEmpty()) {
                    Toast.makeText(getContext(), "Không có món ăn nào trong thực đơn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu thực đơn", Toast.LENGTH_SHORT).show();
                isMenuLoaded = true;
            }
        });
    }

    private void fetchInvoices() {
        monthOptions.clear();
        monthOptions.add("Chọn tháng");

        Set<String> uniqueMonths = new HashSet<>();
        monthListener = invoiceReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                uniqueMonths.clear(); // Xóa để tránh tích lũy dữ liệu cũ
                for (DataSnapshot invoiceSnapshot : snapshot.getChildren()) {
                    String dateStr = invoiceSnapshot.child("ngLap").getValue(String.class);
                    if (dateStr != null) {
                        try {
                            Date date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateStr);
                            if (date != null) {
                                String month = monthFormat.format(date);
                                uniqueMonths.add(month);
                            }
                        } catch (ParseException e) {
                            Log.w("PopularFood", "Invalid date format: " + dateStr);
                        }
                    }
                }
                monthOptions.clear();
                monthOptions.add("Chọn tháng");
                monthOptions.addAll(uniqueMonths);
                isMonthOptionsLoaded = true;
                Log.d("PopularFood", "Month options initialized: " + monthOptions.toString());
                invoiceReference.removeEventListener(this); // Hủy listener sau khi tải xong
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu hóa đơn", Toast.LENGTH_SHORT).show();
                isMonthOptionsLoaded = true;
            }
        });
    }

    private void updatePopularList() {
        llPopularList.removeAllViews();

        try {
            Date selected = monthFormat.parse(selectedMonthValue);
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

            Map<String, Dish> dishStats = new HashMap<>();
            for (Dish dish : dishList) {
                dishStats.put(dish.getId(), new Dish(dish.getId(), dish.getName(), dish.getImageMonAn(), dish.getGiaBan()));
            }

            invoiceReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot invoiceSnapshot : snapshot.getChildren()) {
                        String dateStr = invoiceSnapshot.child("ngLap").getValue(String.class);
                        if (dateStr != null) {
                            try {
                                Date date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateStr);
                                if (date != null && date.getTime() >= startTimestamp && date.getTime() <= endTimestamp) {
                                    DataSnapshot detailsSnapshot = invoiceSnapshot.child("ctdh");
                                    for (DataSnapshot detailSnapshot : detailsSnapshot.getChildren()) {
                                        String dishId = detailSnapshot.getKey();
                                        Long soLuong = detailSnapshot.getValue(Long.class);
                                        if (soLuong != null) {
                                            Dish dish = dishStats.get(dishId);
                                            if (dish != null) {
                                                dish.setSoLuongBanRa(dish.getSoLuongBanRa() + soLuong);
                                                dish.setDoanhThu(dish.getDoanhThu() + (soLuong * dish.getGiaBan()));
                                            }
                                        }
                                    }
                                }
                            } catch (ParseException e) {
                                Log.w("PopularFood", "Invalid date format: " + dateStr);
                            }
                        }
                    }

                    List<Dish> filteredDishList = new ArrayList<>();
                    for (Dish dish : dishStats.values()) {
                        if (dish.getSoLuongBanRa() > 0) {
                            filteredDishList.add(dish);
                        }
                    }
                    Collections.sort(filteredDishList, (d1, d2) -> Long.compare(d2.getSoLuongBanRa(), d1.getSoLuongBanRa()));

                    displayDishList(filteredDishList);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (ParseException e) {
            Toast.makeText(getContext(), "Định dạng tháng không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePopularListForAll() {
        llPopularList.removeAllViews();

        if (dishList.isEmpty()) {
            Toast.makeText(getContext(), "Danh sách món ăn trống", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Dish> dishStats = new HashMap<>();
        for (Dish dish : dishList) {
            dishStats.put(dish.getId(), new Dish(dish.getId(), dish.getName(), dish.getImageMonAn(), dish.getGiaBan()));
        }

        invoiceReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(getContext(), "Không có hóa đơn nào", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot invoiceSnapshot : snapshot.getChildren()) {
                    DataSnapshot detailsSnapshot = invoiceSnapshot.child("ctdh");
                    if (detailsSnapshot.exists()) {
                        for (DataSnapshot detailSnapshot : detailsSnapshot.getChildren()) {
                            String dishId = detailSnapshot.getKey();
                            Long soLuong = detailSnapshot.getValue(Long.class);
                            if (soLuong != null) {
                                Dish dish = dishStats.get(dishId);
                                if (dish != null) {
                                    dish.setSoLuongBanRa(dish.getSoLuongBanRa() + soLuong);
                                    dish.setDoanhThu(dish.getDoanhThu() + (soLuong * dish.getGiaBan()));
                                }
                            }
                        }
                    }
                }

                List<Dish> filteredDishList = new ArrayList<>();
                for (Dish dish : dishStats.values()) {
                    if (dish.getSoLuongBanRa() > 0) {
                        filteredDishList.add(dish);
                    }
                }
                Collections.sort(filteredDishList, (d1, d2) -> Long.compare(d2.getSoLuongBanRa(), d1.getSoLuongBanRa()));

                displayDishList(filteredDishList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayDishList(List<Dish> filteredDishList) {
        if (filteredDishList.isEmpty()) {
            Toast.makeText(getContext(), "Không có món ăn nào được bán", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Dish dish : filteredDishList) {
            CardView cardView = new CardView(requireContext());
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(16, 16, 16, 16);
            cardView.setLayoutParams(cardParams);
            cardView.setCardElevation(4);
            cardView.setRadius(8);

            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(16, 16, 16, 16);

            ImageView ivDishImage = new ImageView(getContext());
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    200
            );
            imageParams.setMargins(0, 0, 0, 16);
            ivDishImage.setLayoutParams(imageParams);
            ivDishImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.get().load(dish.getImageMonAn()).placeholder(R.drawable.placeholder).into(ivDishImage);

            TextView tvName = new TextView(getContext());
            tvName.setText(dish.getName());
            tvName.setTextSize(18);
            tvName.setTextColor(0xFF212121);
            tvName.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            TextView tvQuantity = new TextView(getContext());
            tvQuantity.setText("Số lượng: " + dish.getSoLuongBanRa());
            tvQuantity.setTextSize(16);
            tvQuantity.setTextColor(0xFF757575);
            tvQuantity.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            TextView tvRevenue = new TextView(getContext());
            tvRevenue.setText("Doanh thu: " + String.format("%,d VNĐ", dish.getDoanhThu()));
            tvRevenue.setTextSize(16);
            tvRevenue.setTextColor(0xFFFF5722);
            tvRevenue.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            layout.addView(ivDishImage);
            layout.addView(tvName);
            layout.addView(tvQuantity);
            layout.addView(tvRevenue);
            cardView.addView(layout);
            llPopularList.addView(cardView);
        }
    }
}