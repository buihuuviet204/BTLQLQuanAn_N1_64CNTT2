package com.example.qunnbnhyn.QLKH;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qunnbnhyn.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivityCustomerPriorityDetail extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private DatabaseReference invoiceReference;
    private String customerId;
    private TextView tvName, tvPhone, tvVisitCount, tvPoints;
    private LinearLayout llInvoiceList;
    private ImageView imgViewBack;
    private String customerPhoneNumber;

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

        // Xử lý nút Back
        imgViewBack.setOnClickListener(v -> finish());
    }

    private void loadCustomerDetails() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Customer customer = snapshot.getValue(Customer.class);
                if (customer != null) {
                    // Hiển thị thông tin khách hàng
                    tvName.setText(customer.getName());
                    tvPhone.setText(customer.getPhoneNumber());
                    customerPhoneNumber = customer.getPhoneNumber(); // Lưu số điện thoại để so sánh với maKhach

                    // Lấy visitCount và points trực tiếp từ Firebase và cập nhật UI
                    long visitCount = snapshot.child("visitCount").getValue(Long.class) != null ?
                            snapshot.child("visitCount").getValue(Long.class) : 0;
                    long points = snapshot.child("points").getValue(Long.class) != null ?
                            snapshot.child("points").getValue(Long.class) : 0;

                    tvVisitCount.setText(String.valueOf(visitCount));
                    tvPoints.setText(String.valueOf(points));

                    // Load danh sách hóa đơn
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

    private void loadInvoices() {
        invoiceReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                llInvoiceList.removeAllViews(); // Xóa danh sách cũ trước khi thêm mới
                int visitCount = 0;

                for (DataSnapshot invoiceSnapshot : snapshot.getChildren()) {
                    String maKhach = invoiceSnapshot.child("maKhach").getValue(String.class);
                    if (maKhach != null && maKhach.equals(customerId)) {
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
}
