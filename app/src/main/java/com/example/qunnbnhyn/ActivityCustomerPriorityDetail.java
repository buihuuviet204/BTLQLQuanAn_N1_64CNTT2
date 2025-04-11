package com.example.qunnbnhyn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActivityCustomerPriorityDetail extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private DatabaseReference invoiceReference;
    private String customerId;
    private TextView tvName, tvPhone, tvVisitCount, tvPoints;
    private LinearLayout llInvoiceList;
    private ImageView imgViewBack;
    private String customerPhoneNumber;
    private long tongDiem = 0;
    private ChildEventListener invoiceListener; // Listener để lắng nghe thay đổi trong hoa_don

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_priority_detail);

        customerId = getIntent().getStringExtra("customerId");
        if (customerId == null) {
            Toast.makeText(this, "Không tìm thấy ID khách hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo các view
        tvName = findViewById(R.id.tvCustomerName);
        tvPhone = findViewById(R.id.tvPhoneNumber);
        tvVisitCount = findViewById(R.id.tvVisitCount);
        tvPoints = findViewById(R.id.tvPoints);
        llInvoiceList = findViewById(R.id.llInvoiceList);
        imgViewBack = findViewById(R.id.imgViewBack);

        // Khởi tạo Firebase references
        databaseReference = FirebaseDatabase.getInstance("https://quananbinhyen-cntt2-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("customers").child(customerId);
        invoiceReference = FirebaseDatabase.getInstance("https://quananbinhyen-cntt2-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("hoa_don");

        // Load thông tin khách hàng và hóa đơn
        loadCustomerDetails();

        // Thiết lập listener để lắng nghe thay đổi trong hoa_don
        setupInvoiceListener();

        // Xử lý nút Back
        imgViewBack.setOnClickListener(v -> finish());
    }

    private void loadCustomerDetails() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Customer customer = snapshot.getValue(Customer.class);
                if (customer != null) {
                    tvName.setText(customer.getName());
                    tvPhone.setText(customer.getPhoneNumber());
                    customerPhoneNumber = customer.getPhoneNumber(); // Lưu số điện thoại để so sánh với maKhach
                    // Load hóa đơn và cập nhật visitCount, points
                    loadInvoices();
                } else {
                    Toast.makeText(ActivityCustomerPriorityDetail.this, "Không tìm thấy thông tin khách hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ActivityCustomerPriorityDetail.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Hàm tính điểm từ tổng tiền (1 điểm cho mỗi 20,000 VNĐ)
    private long calculatePoints(long tongTien) {
        return tongTien / 20000;
    }

    // Thiết lập listener để lắng nghe thay đổi trong hoa_don
    private void setupInvoiceListener() {
        invoiceListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                // Khi có hóa đơn mới được thêm, cập nhật lại visitCount và points
                updateCustomerStats();
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                // Khi có hóa đơn bị sửa, cập nhật lại visitCount và points
                updateCustomerStats();
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                // Khi có hóa đơn bị xóa, cập nhật lại visitCount và points
                updateCustomerStats();
            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
                // Không cần xử lý
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ActivityCustomerPriorityDetail.this, "Lỗi lắng nghe hóa đơn: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        invoiceReference.addChildEventListener(invoiceListener);
    }

    // Cập nhật visitCount và points dựa trên tất cả hóa đơn
    private void updateCustomerStats() {
        if (customerPhoneNumber == null) {
            return; // Chưa có customerPhoneNumber, không thể cập nhật
        }

        invoiceReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int visitCount = 0;
                long totalPoints = 0;

                // Duyệt qua tất cả hóa đơn để tính visitCount và points
                for (DataSnapshot invoiceSnapshot : snapshot.getChildren()) {
                    String maKhach = invoiceSnapshot.child("maKhach").getValue(String.class);
                    if (maKhach != null && maKhach.equals(customerPhoneNumber)) {
                        visitCount++;
                        long tongTien = invoiceSnapshot.child("tongTien").getValue(Long.class) != null ?
                                invoiceSnapshot.child("tongTien").getValue(Long.class) : 0;
                        totalPoints += calculatePoints(tongTien);
                    }
                }

                // Cập nhật visitCount và points vào customers
                databaseReference.child("visitCount").setValue(visitCount);
                databaseReference.child("points").setValue(totalPoints);

                // Cập nhật UI
                tvVisitCount.setText(String.valueOf(visitCount));
                tvPoints.setText(String.valueOf(totalPoints));

                // Load lại danh sách hóa đơn để cập nhật giao diện
                loadInvoices();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ActivityCustomerPriorityDetail.this, "Lỗi tải hóa đơn: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadInvoices() {
        invoiceReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                llInvoiceList.removeAllViews(); // Xóa danh sách cũ trước khi thêm mới
                int visitCount = 0;

                for (DataSnapshot invoiceSnapshot : snapshot.getChildren()) {
                    String maKhach = invoiceSnapshot.child("maKhach").getValue(String.class);
                    if (maKhach != null && maKhach.equals(customerPhoneNumber)) {
                        visitCount++;
                        long tongTien = invoiceSnapshot.child("tongTien").getValue(Long.class) != null ?
                                invoiceSnapshot.child("tongTien").getValue(Long.class) : 0;
                        String dateStr = invoiceSnapshot.child("ngLap").getValue(String.class);

                        // Tạo view cho mỗi hóa đơn
                        LinearLayout layout = new LinearLayout(ActivityCustomerPriorityDetail.this);
                        layout.setOrientation(LinearLayout.HORIZONTAL);
                        layout.setPadding(8, 8, 8, 8);

                        TextView tvVisit = new TextView(ActivityCustomerPriorityDetail.this);
                        tvVisit.setText("Lần ăn: " + visitCount);
                        tvVisit.setTextSize(16);
                        tvVisit.setTextColor(0xFF212121);
                        tvVisit.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                        TextView tvTotal = new TextView(ActivityCustomerPriorityDetail.this);
                        tvTotal.setText(String.format("%,d VNĐ", tongTien));
                        tvTotal.setTextSize(16);
                        tvTotal.setTextColor(0xFF212121);
                        tvTotal.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                        TextView tvDate = new TextView(ActivityCustomerPriorityDetail.this);
                        tvDate.setText(dateStr);
                        tvDate.setTextSize(16);
                        tvDate.setTextColor(0xFF212121);
                        tvDate.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                        layout.addView(tvVisit);
                        layout.addView(tvTotal);
                        layout.addView(tvDate);
                        llInvoiceList.addView(layout);

                        // Thêm đường phân cách giữa các hóa đơn
                        View divider = new View(ActivityCustomerPriorityDetail.this);
                        divider.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 1));
                        divider.setBackgroundColor(0xFFE0E0E0);
                        llInvoiceList.addView(divider);
                    }
                }

                if (visitCount == 0) {
                    TextView tvNoInvoices = new TextView(ActivityCustomerPriorityDetail.this);
                    tvNoInvoices.setText("Không có hóa đơn nào");
                    tvNoInvoices.setTextSize(16);
                    tvNoInvoices.setTextColor(0xFF757575);
                    tvNoInvoices.setPadding(8, 8, 8, 8);
                    llInvoiceList.addView(tvNoInvoices);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ActivityCustomerPriorityDetail.this, "Lỗi tải hóa đơn: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Xóa listener khi Activity bị hủy để tránh rò rỉ bộ nhớ
        if (invoiceListener != null) {
            invoiceReference.removeEventListener(invoiceListener);
        }
    }
}