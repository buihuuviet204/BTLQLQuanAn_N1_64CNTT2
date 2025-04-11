package com.example.qunnbnhyn.ThongKe;

import android.content.Context;
import android.os.Bundle;
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
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qunnbnhyn.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ActivityFragmentSalary extends Fragment {

    private RecyclerView rvEmployees;
    private ImageView imageView;
    private Spinner filterTypeSpinner;
    private Spinner dateMonthSpinner;
    private Button btnConfirm;

    private DatabaseReference databaseReference;
    private DatabaseReference databaseChamCong;

    private static final int HOURLY_RATE = 25000; // 25,000 VNĐ per hour
    private static final int MORNING_SHIFT_HOURS = 3; // Ca sáng: 3 hours
    private static final int AFTERNOON_SHIFT_HOURS = 4; // Ca chiều: 4 hours
    private static final int EVENING_SHIFT_HOURS = 5; // Ca tối: 5 hours

    private String selectedFilter = "day"; // Default filter: day
    private String selectedDateMonth; // Selected date or month
    private List<String> dateMonthOptions = new ArrayList<>(); // Options for date/month spinner
    private List<String> allDates = new ArrayList<>(); // Store all dates fetched once
    private List<Employee> employeesList = new ArrayList<>(); // Store employees for filtering
    private List<Object> displayList = new ArrayList<>(); // For RecyclerView (headers + employees)
    private EmployeeAdapter employeeAdapter;
    private Context appContext; // Store context to avoid null pointer
    private int employeesToFetch = 0; // Counter for employees to fetch
    private int employeesFetched = 0; // Counter for employees fetched
    private ValueEventListener employeesListener; // Store listener to remove it later
    private SimpleDateFormat dayFormat;

    public ActivityFragmentSalary() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        appContext = context; // Store the context when the Fragment is attached
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_salary, container, false);

        // Initialize UI components
        rvEmployees = view.findViewById(R.id.rv_employees);
        imageView = view.findViewById(R.id.imageView2);
        filterTypeSpinner = view.findViewById(R.id.filter_type_spinner);
        dateMonthSpinner = view.findViewById(R.id.date_month_spinner);
        btnConfirm = view.findViewById(R.id.btn_confirm);

        // Initialize RecyclerView
        displayList = new ArrayList<>();
        employeeAdapter = new EmployeeAdapter(displayList);
        rvEmployees.setLayoutManager(new LinearLayoutManager(appContext));
        rvEmployees.setAdapter(employeeAdapter);

        // Initialize Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance("https://quananbinhyen-cntt2-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Employees");

        databaseChamCong = FirebaseDatabase.getInstance("https://quananbinhyen-cntt2-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Cham_cong");

        // Initialize date format
        dayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Set up back button
        imageView.setOnClickListener(v -> requireActivity().finish());

        // Set up filter type spinner (Day/Month)
        ArrayAdapter<CharSequence> filterTypeAdapter = ArrayAdapter.createFromResource(appContext,
                R.array.filter_type_options, android.R.layout.simple_spinner_item);
        filterTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterTypeSpinner.setAdapter(filterTypeAdapter);
        filterTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFilter = position == 0 ? "day" : "month";
                populateDateMonthSpinner(); // Update date/month spinner based on filter
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedFilter = "day";
            }
        });

        // Set up date/month spinner
        dateMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDateMonth = dateMonthOptions.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (!dateMonthOptions.isEmpty()) {
                    selectedDateMonth = dateMonthOptions.get(0);
                }
            }
        });

        // Set up confirm button
        btnConfirm.setOnClickListener(v -> {
            if (selectedDateMonth == null || selectedDateMonth.isEmpty()) {
                Toast.makeText(appContext, "Vui lòng chọn ngày/tháng", Toast.LENGTH_SHORT).show();
                return;
            }
            if (employeesFetched < employeesToFetch) {
                Toast.makeText(appContext, "Đang tải dữ liệu, vui lòng chờ...", Toast.LENGTH_SHORT).show();
                return;
            }
            updateDisplayList(employeesList); // Update UI only when confirm is clicked
        });

        // Fetch dates and employees after databaseChamCong is initialized
        fetchChamCongDatesOnce();
        fetchEmployeesFromChamCong();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove the ValueEventListener to prevent duplicate triggers
        if (employeesListener != null) {
            databaseChamCong.removeEventListener(employeesListener);
        }
    }

    private void fetchChamCongDatesOnce() {
        databaseChamCong.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                allDates.clear(); // Clear previous dates
                Set<String> dates = new HashSet<>();

                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    String date = dateSnapshot.getKey(); // e.g., "2025-04-10"
                    dates.add(date);
                }

                allDates.addAll(dates);
                Collections.sort(allDates); // Sort dates for better display
                populateDateMonthSpinner(); // Populate spinner with fetched dates
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(appContext, "Lỗi khi lấy dữ liệu chấm công: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateDateMonthSpinner() {
        dateMonthOptions.clear();

        Set<String> options = new HashSet<>();
        for (String date : allDates) {
            if (selectedFilter.equals("day")) {
                options.add(date);
            } else {
                String month = date.substring(0, 7); // e.g., "2025-04"
                options.add(month);
            }
        }

        dateMonthOptions.addAll(options);
        Collections.sort(dateMonthOptions); // Sort options for better display
        if (!dateMonthOptions.isEmpty()) {
            selectedDateMonth = dateMonthOptions.get(0); // Default to first option
        } else {
            selectedDateMonth = null;
        }

        // Check if Fragment is still attached before updating UI
        if (appContext != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(appContext,
                    android.R.layout.simple_spinner_item, dateMonthOptions);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dateMonthSpinner.setAdapter(adapter);
        }
    }

    private void fetchEmployeesFromChamCong() {
        employeesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                employeesList.clear();
                employeesToFetch = 0;
                employeesFetched = 0;

                Set<String> employeeIds = new HashSet<>();
                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot employeeSnapshot : dateSnapshot.getChildren()) {
                        String employeeId = employeeSnapshot.getKey();
                        employeeIds.add(employeeId);
                    }
                }

                // Fetch details for each employee ID
                employeesToFetch = employeeIds.size();
                if (employeeIds.isEmpty()) {
                    employeesFetched = employeesToFetch;
                    Toast.makeText(appContext, "Không có nhân viên nào trong chấm công", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (String employeeId : employeeIds) {
                    databaseReference.child(employeeId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot employeeSnapshot) {
                            try {
                                String name = employeeSnapshot.child("name").getValue(String.class);
                                String email = employeeSnapshot.child("email").getValue(String.class);
                                String position = employeeSnapshot.child("position").getValue(String.class);
                                String shiftType = employeeSnapshot.child("shift").getValue(String.class);
                                // Nếu position là null hoặc là "Nhân viên", thì thêm vào danh sách
                                if (position == null || "Nhân viên".equals(position)) {
                                    fetchShiftsForEmployee(employeeId, name, email, shiftType);
                                } else {
                                    employeesFetched++;
                                    if (employeesFetched == employeesToFetch) {
                                        Toast.makeText(appContext, "Đã tải xong dữ liệu nhân viên", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (Exception e) {
                                Toast.makeText(appContext, "Lỗi khi đọc dữ liệu nhân viên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                employeesFetched++;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            Toast.makeText(appContext, "Lỗi khi lấy dữ liệu nhân viên: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            employeesFetched++;
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(appContext, "Lỗi khi lấy dữ liệu chấm công: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        databaseChamCong.addValueEventListener(employeesListener);
    }

    private void fetchShiftsForEmployee(String employeeId, String name, String email, String shiftType) {
        databaseChamCong.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Shift> shifts = new ArrayList<>();

                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    String date = dateSnapshot.getKey();
                    DataSnapshot employeeShiftSnapshot = dateSnapshot.child(employeeId);
                    if (employeeShiftSnapshot.exists()) {
                        String checkIn = employeeShiftSnapshot.child("check-in").getValue(String.class);
                        String checkOut = employeeShiftSnapshot.child("check-out").getValue(String.class);
                        shifts.add(new Shift(date, checkIn, checkOut, shiftType));
                    }
                }

                Employee employee = new Employee(employeeId, name, email, shifts, shiftType);
                employeesList.add(employee);
                employeesFetched++;

                // Notify user when all data is fetched
                if (employeesFetched == employeesToFetch) {
                    Toast.makeText(appContext, "Đã tải xong dữ liệu nhân viên", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(appContext, "Lỗi khi lấy dữ liệu chấm công: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                employeesFetched++;
            }
        });
    }

    private void updateDisplayList(List<Employee> employees) {
        displayList.clear();

        if (selectedFilter.equals("day") && selectedDateMonth != null) {
            // Lọc theo ngày: Hiển thị tất cả nhân viên có ca làm trong ngày được chọn
            for (Employee emp : employees) {
                List<Shift> filteredShifts = new ArrayList<>();
                for (Shift shift : emp.getShifts()) {
                    if (shift.getDate().equals(selectedDateMonth)) {
                        filteredShifts.add(shift);
                    }
                }

                if (filteredShifts.isEmpty()) {
                    continue; // Bỏ qua nhân viên không có ca làm trong ngày được chọn
                }

                // Tính lương cho nhân viên này
                Long employeeSalary = calculateEmployeeSalary(filteredShifts);
                Employee filteredEmployee = new Employee(emp.getEmployeeId(), emp.getName(), emp.getEmail(), filteredShifts, emp.getShiftType());
                filteredEmployee.setSalary(employeeSalary);
                displayList.add(filteredEmployee);
            }

            if (displayList.isEmpty()) {
                Toast.makeText(appContext, "Không có nhân viên nào làm việc trong ngày " + selectedDateMonth, Toast.LENGTH_SHORT).show();
            }
        } else if (selectedDateMonth != null) {
            // Lọc theo tháng: Gom nhóm nhân viên theo ngày
            Map<String, List<Employee>> employeesByDate = new HashMap<>();

            // Gom nhóm nhân viên theo ngày
            for (Employee emp : employees) {
                List<Shift> filteredShifts = new ArrayList<>();
                for (Shift shift : emp.getShifts()) {
                    if (shift.getDate().startsWith(selectedDateMonth)) {
                        filteredShifts.add(shift);
                    }
                }

                if (filteredShifts.isEmpty()) {
                    continue; // Bỏ qua nhân viên không có ca làm trong tháng được chọn
                }

                // Tính lương cho nhân viên này
                Long employeeSalary = calculateEmployeeSalary(filteredShifts);
                Employee filteredEmployee = new Employee(emp.getEmployeeId(), emp.getName(), emp.getEmail(), filteredShifts, emp.getShiftType());
                filteredEmployee.setSalary(employeeSalary);

                // Gom nhóm theo ngày
                for (Shift shift : filteredShifts) {
                    String date = shift.getDate();
                    employeesByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(filteredEmployee);
                }
            }

            // Sắp xếp các ngày theo thứ tự tăng dần
            List<String> sortedDates = new ArrayList<>(employeesByDate.keySet());
            Collections.sort(sortedDates);

            // Thêm các nhóm vào displayList
            for (String date : sortedDates) {
                displayList.add("Ngày: " + date); // Thêm header ngày
                List<Employee> employeesOnDate = employeesByDate.get(date);
                // Loại bỏ nhân viên trùng lặp
                Set<String> uniqueEmployeeIds = new HashSet<>();
                List<Employee> uniqueEmployees = new ArrayList<>();
                for (Employee emp : employeesOnDate) {
                    if (uniqueEmployeeIds.add(emp.getEmployeeId())) {
                        // Tạo danh sách shift không trùng lặp dựa trên shiftType
                        Set<String> uniqueShiftTypes = new HashSet<>();
                        List<Shift> uniqueShifts = new ArrayList<>();
                        for (Shift shift : emp.getShifts()) {
                            if (shift.getDate().startsWith(selectedDateMonth) && uniqueShiftTypes.add(shift.getShiftType())) {
                                uniqueShifts.add(shift);
                            }
                        }
                        Employee uniqueEmployee = new Employee(emp.getEmployeeId(), emp.getName(), emp.getEmail(), uniqueShifts, emp.getShiftType());
                        uniqueEmployee.setSalary(emp.getSalary());
                        uniqueEmployees.add(uniqueEmployee);
                    }
                }
                displayList.addAll(uniqueEmployees); // Thêm danh sách nhân viên trong ngày đó
            }

            if (displayList.isEmpty()) {
                Toast.makeText(appContext, "Không có nhân viên nào làm việc trong tháng " + selectedDateMonth, Toast.LENGTH_SHORT).show();
            }
        }

        employeeAdapter.notifyDataSetChanged();
    }

    private Long calculateEmployeeSalary(List<Shift> shifts) {
        long totalSalary = 0;
        boolean hasIncompleteShift = false;

        for (Shift shift : shifts) {
            // Nếu có check-in nhưng không có check-out, lương của ca đó là null
            if (shift.getCheckIn() != null && (shift.getCheckOut() == null || shift.getCheckOut().equals("null"))) {
                hasIncompleteShift = true;
                continue; // Bỏ qua ca này khi tính lương
            }

            // Tính lương cho ca làm đã hoàn thành
            int hours = getShiftHours(shift.getShiftType());
            totalSalary += hours * HOURLY_RATE;
        }

        // Nếu có ca chưa hoàn thành (chưa check-out), trả về null
        if (hasIncompleteShift) {
            return null;
        }

        return totalSalary;
    }

    private int getShiftHours(String shiftType) {
        if (shiftType == null) return 0;
        if (shiftType.contains("Ca sáng")) return MORNING_SHIFT_HOURS;
        if (shiftType.contains("Ca chiều")) return AFTERNOON_SHIFT_HOURS;
        if (shiftType.contains("Ca tối")) return EVENING_SHIFT_HOURS;
        return 0;
    }

    // Employee model class
    private static class Employee {
        private String employeeId;
        private String name;
        private String email;
        private List<Shift> shifts;
        private Long salary; // Lương của nhân viên
        private String shiftType; // Thêm trường shiftType

        public Employee(String employeeId, String name, String email, List<Shift> shifts, String shiftType) {
            this.employeeId = employeeId;
            this.name = name;
            this.email = email;
            this.shifts = shifts;
            this.salary = null;
            this.shiftType = shiftType;
        }

        public String getEmployeeId() {
            return employeeId;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public List<Shift> getShifts() {
            return shifts;
        }

        public Long getSalary() {
            return salary;
        }

        public void setSalary(Long salary) {
            this.salary = salary;
        }

        public String getShiftType() {
            return shiftType;
        }
    }

    // Shift model class
    private static class Shift {
        private String date;
        private String checkIn;
        private String checkOut;
        private String shiftType;

        public Shift(String date, String checkIn, String checkOut, String shiftType) {
            this.date = date;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
            this.shiftType = shiftType;
        }

        public String getDate() {
            return date;
        }

        public String getCheckIn() {
            return checkIn;
        }

        public String getCheckOut() {
            return checkOut;
        }

        public String getShiftType() {
            return shiftType;
        }
    }

    // Adapter for RecyclerView (Employee level)
    private class EmployeeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_HEADER = 0;
        private static final int TYPE_EMPLOYEE = 1;

        private List<Object> items;

        public EmployeeAdapter(List<Object> items) {
            this.items = items;
        }

        @Override
        public int getItemViewType(int position) {
            if (items.get(position) instanceof String) {
                return TYPE_HEADER;
            } else {
                return TYPE_EMPLOYEE;
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                return new HeaderViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employee, parent, false);
                return new EmployeeViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof HeaderViewHolder) {
                HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
                headerHolder.textView.setText((String) items.get(position));
                headerHolder.textView.setTextSize(16);
                headerHolder.textView.setTextColor(0xFF212121);
                headerHolder.textView.setPadding(16, 16, 16, 0);
            } else {
                EmployeeViewHolder employeeHolder = (EmployeeViewHolder) holder;
                Employee employee = (Employee) items.get(position);
                employeeHolder.bind(employee);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    // ViewHolder for header (date)
    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }

    // Adapter for RecyclerView (Shift level)
    private class ShiftAdapter extends RecyclerView.Adapter<ActivityFragmentSalary.ShiftViewHolder> {
        private List<Shift> items;

        public ShiftAdapter(List<Shift> items) {
            this.items = items;
        }

        public void updateShifts(List<Shift> newItems) {
            this.items.clear();
            this.items.addAll(newItems);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ActivityFragmentSalary.ShiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shift, parent, false);
            return new ActivityFragmentSalary.ShiftViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ActivityFragmentSalary.ShiftViewHolder holder, int position) {
            Shift shift = items.get(position);
            holder.tvShift.setText("Ca làm: " + (shift.getShiftType() != null ? shift.getShiftType() : "N/A"));
            if (selectedFilter.equals("day")) {
                holder.tvCheckIn.setVisibility(View.VISIBLE);
                holder.tvCheckOut.setVisibility(View.VISIBLE);
                holder.tvCheckIn.setText("Giờ check-in: " + (shift.getCheckIn() != null ? shift.getCheckIn() : "N/A"));
                holder.tvCheckOut.setText("Giờ check-out: " + (shift.getCheckOut() != null ? shift.getCheckOut() : "N/A"));
            } else {
                holder.tvCheckIn.setVisibility(View.GONE);
                holder.tvCheckOut.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    // ViewHolder for shift
    private static class ShiftViewHolder extends RecyclerView.ViewHolder {
        TextView tvShift, tvCheckIn, tvCheckOut;

        public ShiftViewHolder(@NonNull View itemView) {
            super(itemView);
            tvShift = itemView.findViewById(R.id.tv_shift);
            tvCheckIn = itemView.findViewById(R.id.tv_check_in);
            tvCheckOut = itemView.findViewById(R.id.tv_check_out);
        }
    }

    // ViewHolder for each employee
    private class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvSalary;
        RecyclerView rvShifts;
        ShiftAdapter shiftAdapter;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_employee_name);
            tvEmail = itemView.findViewById(R.id.tv_employee_email);
            tvSalary = itemView.findViewById(R.id.tv_employee_salary);
            rvShifts = itemView.findViewById(R.id.rv_shifts);

            // Initialize nested RecyclerView for shifts
            rvShifts.setLayoutManager(new LinearLayoutManager(appContext));
            shiftAdapter = new ShiftAdapter(new ArrayList<>());
            rvShifts.setAdapter(shiftAdapter);
        }

        public void bind(Employee employee) {
            tvName.setText("Họ và tên: " + (employee.getName() != null ? employee.getName() : "N/A"));
            tvEmail.setText("Email: " + (employee.getEmail() != null ? employee.getEmail() : "N/A"));

            // Hiển thị lương của nhân viên
            if (employee.getSalary() != null) {
                tvSalary.setText(String.format("Tổng lương: %,d VNĐ", employee.getSalary()));
            } else {
                tvSalary.setText("Tổng lương: N/A (Chưa hoàn thành ca làm)");
            }

            // Cập nhật danh sách ca làm
            List<Shift> shiftItems = new ArrayList<>();
            if (selectedFilter.equals("day")) {
                shiftItems.addAll(employee.getShifts());
            } else {
                // Chỉ hiển thị các ca làm trong tháng được chọn, không trùng lặp shiftType
                Set<String> uniqueShiftTypes = new HashSet<>();
                for (Shift shift : employee.getShifts()) {
                    if (shift.getDate().startsWith(selectedDateMonth) && uniqueShiftTypes.add(shift.getShiftType())) {
                        shiftItems.add(shift);
                    }
                }
            }

            shiftAdapter.updateShifts(shiftItems);
        }
    }
}