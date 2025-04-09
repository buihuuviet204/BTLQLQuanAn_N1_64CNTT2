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
    private SimpleDateFormat dayFormat, monthFormat;
    private List<String> monthOptions;
    private String selectedMonthValue;
    private List<Dish> dishList;
    private boolean isMenuLoaded = false; // Biến để kiểm tra xem menu đã được tải chưa

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

        // Initialize Firebase Realtime Database
        invoiceReference = FirebaseDatabase.getInstance("https://quananbinhyen-cntt2-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("hoa_don");
        menuReference = FirebaseDatabase.getInstance("https://quananbinhyen-cntt2-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("thuc_don");

        // Initialize date formats with timezone
        dayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        monthFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        dayFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        monthFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));

        // Initialize month options
        monthOptions = new ArrayList<>();
        monthOptions.add("Chọn tháng");
        selectedMonthValue = "";
        dishList = new ArrayList<>();

        // Set up Spinner options for filter type
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.time_options,
                android.R.layout.simple_spinner_item
        );
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimePopular.setAdapter(filterAdapter);

        // Set up Spinner listener to reset selection
        spinnerTimePopular.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvDatePickerPopular.setText("");
                selectedMonthValue = "";
                llPopularList.removeAllViews();
                if (position == 0) { // Theo ngày
                    tvDatePickerPopular.setHint("Chọn ngày");
                    tvDatePickerPopular.setVisibility(View.VISIBLE);
                } else if (position == 1) { // Theo tháng
                    tvDatePickerPopular.setHint("Chọn tháng");
                    tvDatePickerPopular.setVisibility(View.VISIBLE);
                } else { // Tất cả
                    tvDatePickerPopular.setVisibility(View.GONE);
                }
                updateMonthOptions();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Show AlertDialog with month options when TextView is clicked
        tvDatePickerPopular.setOnClickListener(v -> showMonthOptionsDialog());

        // Set up Confirm button listener
        btnConfirmPopular.setOnClickListener(v -> {
            if (!isMenuLoaded) {
                Toast.makeText(getContext(), "Đang tải dữ liệu thực đơn, vui lòng chờ...", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dishList.isEmpty()) {
                Toast.makeText(getContext(), "Không có món ăn nào trong thực đơn", Toast.LENGTH_SHORT).show();
                return;
            }
            if (spinnerTimePopular.getSelectedItemPosition() == 2) { // Tất cả
                updatePopularListForAll();
            } else if (selectedMonthValue.isEmpty() || selectedMonthValue.equals("Chọn tháng")) {
                Toast.makeText(getContext(), "Vui lòng chọn tháng", Toast.LENGTH_SHORT).show();
            } else { // Theo tháng
                updatePopularList();
            }
        });

        // Set up back button listener
        imageView.setOnClickListener(v -> requireActivity().finish());

        // Fetch menu and invoices
        fetchMenu();
        fetchInvoices();

        return view;
    }

    private void showMonthOptionsDialog() {
        String[] optionsArray = monthOptions.toArray(new String[0]);

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

                if (dishList.isEmpty()) {
                    Log.e("PopularFood", "dishList is empty after loading from thuc_don");
                    Toast.makeText(getContext(), "Không có món ăn nào trong thực đơn", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("PopularFood", "Menu loaded successfully, dishList size: " + dishList.size());
                }
                isMenuLoaded = true;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("PopularFood", "Error loading thuc_don: " + error.getMessage());
                Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu thực đơn: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                isMenuLoaded = true; // Đánh dấu để tránh chờ vô hạn
            }
        });
    }

    private void fetchInvoices() {
        invoiceReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                updateMonthOptions();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("PopularFood", "Error loading hoa_don: " + error.getMessage());
                Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu hóa đơn: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMonthOptions() {
        monthOptions.clear();
        monthOptions.add("Chọn tháng");

        Set<String> uniqueMonths = new HashSet<>();
        invoiceReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot invoiceSnapshot : snapshot.getChildren()) {
                    String dateStr = invoiceSnapshot.child("ngLap").getValue(String.class);
                    if (dateStr != null) {
                        try {
                            Date date = dayFormat.parse(dateStr);
                            if (date != null) {
                                String month = monthFormat.format(date);
                                uniqueMonths.add(month);
                            }
                        } catch (ParseException e) {
                            Log.w("PopularFood", "Invalid date format for ngLap: " + dateStr);
                        }
                    } else {
                        Log.w("PopularFood", "ngLap is null for invoice: " + invoiceSnapshot.getKey());
                    }
                }
                monthOptions.addAll(uniqueMonths);
                Log.d("PopularFood", "Month options updated: " + monthOptions.toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("PopularFood", "Error updating month options: " + error.getMessage());
                Toast.makeText(getContext(), "Lỗi khi cập nhật tháng: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

            // Map để lưu số lượng bán ra và doanh thu của từng món
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
                                Date date = dayFormat.parse(dateStr);
                                if (date != null) {
                                    long timestamp = date.getTime();
                                    if (timestamp >= startTimestamp && timestamp <= endTimestamp) {
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
                                }
                            } catch (ParseException e) {
                                Log.w("PopularFood", "Invalid date format for ngLap: " + dateStr);
                            }
                        }
                    }

                    // Lọc các món có số lượng bán ra > 0 và sắp xếp theo số lượng bán ra
                    List<Dish> filteredDishList = new ArrayList<>();
                    for (Dish dish : dishStats.values()) {
                        if (dish.getSoLuongBanRa() > 0) {
                            filteredDishList.add(dish);
                        }
                    }
                    Collections.sort(filteredDishList, (d1, d2) -> Long.compare(d2.getSoLuongBanRa(), d1.getSoLuongBanRa()));

                    // Hiển thị danh sách món ăn phổ biến
                    for (Dish dish : filteredDishList) {
                        LinearLayout layout = new LinearLayout(getContext());
                        layout.setOrientation(LinearLayout.HORIZONTAL);
                        layout.setPadding(16, 16, 16, 16);

                        // Hình ảnh món ăn
                        ImageView ivDishImage = new ImageView(getContext());
                        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(80, 80);
                        imageParams.setMargins(0, 0, 16, 0);
                        ivDishImage.setLayoutParams(imageParams);
                        ivDishImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        Picasso.get().load(dish.getImageMonAn()).placeholder(R.drawable.placeholder).into(ivDishImage);

                        // Thông tin món ăn
                        LinearLayout textLayout = new LinearLayout(getContext());
                        textLayout.setOrientation(LinearLayout.VERTICAL);
                        textLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                        TextView tvName = new TextView(getContext());
                        tvName.setText(dish.getName());
                        tvName.setTextSize(16);
                        tvName.setTextColor(0xFF212121);

                        TextView tvQuantity = new TextView(getContext());
                        tvQuantity.setText("Số lượng: " + dish.getSoLuongBanRa());
                        tvQuantity.setTextSize(14);
                        tvQuantity.setTextColor(0xFF757575);
                        tvQuantity.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                        TextView tvRevenue = new TextView(getContext());
                        tvRevenue.setText("Doanh thu: " + String.format("%,d VNĐ", dish.getDoanhThu()));
                        tvRevenue.setTextSize(14);
                        tvRevenue.setTextColor(0xFFFF5722);
                        tvRevenue.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                        textLayout.addView(tvName);
                        textLayout.addView(tvQuantity);
                        textLayout.addView(tvRevenue);

                        layout.addView(ivDishImage);
                        layout.addView(textLayout);
                        llPopularList.addView(layout);
                    }

                    if (filteredDishList.isEmpty()) {
                        Toast.makeText(getContext(), "Không có món ăn nào được bán trong tháng " + selectedMonthValue, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e("PopularFood", "Error loading hoa_don: " + error.getMessage());
                    Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (ParseException e) {
            Log.e("PopularFood", "ParseException: " + e.getMessage());
            Toast.makeText(getContext(), "Định dạng tháng không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePopularListForAll() {
        llPopularList.removeAllViews();

        // Kiểm tra xem dishList có dữ liệu không
        if (dishList.isEmpty()) {
            Log.e("PopularFood", "dishList is empty in updatePopularListForAll");
            Toast.makeText(getContext(), "Danh sách món ăn trống, không thể hiển thị", Toast.LENGTH_SHORT).show();
            return;
        }

        // Map để lưu số lượng bán ra và doanh thu của từng món
        Map<String, Dish> dishStats = new HashMap<>();
        for (Dish dish : dishList) {
            dishStats.put(dish.getId(), new Dish(dish.getId(), dish.getName(), dish.getImageMonAn(), dish.getGiaBan()));
        }

        invoiceReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Kiểm tra xem có hóa đơn nào không
                if (!snapshot.exists()) {
                    Log.e("PopularFood", "No invoices found in database");
                    Toast.makeText(getContext(), "Không có hóa đơn nào trong cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
                    return;
                }

                int invoiceCount = 0;
                int detailCount = 0;

                for (DataSnapshot invoiceSnapshot : snapshot.getChildren()) {
                    invoiceCount++;
                    DataSnapshot detailsSnapshot = invoiceSnapshot.child("ctdh");
                    if (detailsSnapshot.exists()) {
                        for (DataSnapshot detailSnapshot : detailsSnapshot.getChildren()) {
                            detailCount++;
                            String dishId = detailSnapshot.getKey();
                            Long soLuong = detailSnapshot.getValue(Long.class);
                            if (soLuong != null) {
                                Dish dish = dishStats.get(dishId);
                                if (dish != null) {
                                    dish.setSoLuongBanRa(dish.getSoLuongBanRa() + soLuong);
                                    dish.setDoanhThu(dish.getDoanhThu() + (soLuong * dish.getGiaBan()));
                                    Log.d("PopularFood", "Updated dish: " + dish.getName() + ", Quantity: " + soLuong);
                                } else {
                                    Log.w("PopularFood", "Dish not found for ID: " + dishId);
                                }
                            } else {
                                Log.w("PopularFood", "Invalid quantity for dish ID: " + dishId);
                            }
                        }
                    } else {
                        Log.w("PopularFood", "No ctdh found for invoice: " + invoiceSnapshot.getKey());
                    }
                }

                Log.d("PopularFood", "Total invoices processed: " + invoiceCount);
                Log.d("PopularFood", "Total details processed: " + detailCount);

                // Lọc các món có số lượng bán ra > 0 và sắp xếp theo số lượng bán ra
                List<Dish> filteredDishList = new ArrayList<>();
                for (Dish dish : dishStats.values()) {
                    if (dish.getSoLuongBanRa() > 0) {
                        filteredDishList.add(dish);
                    }
                }
                Collections.sort(filteredDishList, (d1, d2) -> Long.compare(d2.getSoLuongBanRa(), d1.getSoLuongBanRa()));

                // Kiểm tra filteredDishList
                if (filteredDishList.isEmpty()) {
                    Log.e("PopularFood", "filteredDishList is empty after processing");
                    Toast.makeText(getContext(), "Không có món ăn nào được bán", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Hiển thị danh sách món ăn phổ biến
                for (Dish dish : filteredDishList) {
                    LinearLayout layout = new LinearLayout(getContext());
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    layout.setPadding(16, 16, 16, 16);

                    // Hình ảnh món ăn
                    ImageView ivDishImage = new ImageView(getContext());
                    LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(80, 80);
                    imageParams.setMargins(0, 0, 16, 0);
                    ivDishImage.setLayoutParams(imageParams);
                    ivDishImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Picasso.get().load(dish.getImageMonAn()).placeholder(R.drawable.placeholder).into(ivDishImage);

                    // Thông tin món ăn
                    LinearLayout textLayout = new LinearLayout(getContext());
                    textLayout.setOrientation(LinearLayout.VERTICAL);
                    textLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                    TextView tvName = new TextView(getContext());
                    tvName.setText(dish.getName());
                    tvName.setTextSize(16);
                    tvName.setTextColor(0xFF212121);

                    TextView tvQuantity = new TextView(getContext());
                    tvQuantity.setText("Số lượng: " + dish.getSoLuongBanRa());
                    tvQuantity.setTextSize(14);
                    tvQuantity.setTextColor(0xFF757575);
                    tvQuantity.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                    TextView tvRevenue = new TextView(getContext());
                    tvRevenue.setText("Doanh thu: " + String.format("%,d VNĐ", dish.getDoanhThu()));
                    tvRevenue.setTextSize(14);
                    tvRevenue.setTextColor(0xFFFF5722);
                    tvRevenue.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                    textLayout.addView(tvName);
                    textLayout.addView(tvQuantity);
                    textLayout.addView(tvRevenue);

                    layout.addView(ivDishImage);
                    layout.addView(textLayout);
                    llPopularList.addView(layout);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("PopularFood", "Error loading hoa_don: " + error.getMessage());
                Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}