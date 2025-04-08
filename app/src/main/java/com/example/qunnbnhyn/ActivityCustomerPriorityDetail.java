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

        databaseReference = FirebaseDatabase.getInstance("https://quananbinhyen-cntt2-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("customers").child(customerId);
        invoiceReference = FirebaseDatabase.getInstance("https://quananbinhyen-cntt2-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("hoa_don");

        loadCustomerDetails();
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
                    tvPoints.setText(String.valueOf(customer.getPoints()));
                    customerPhoneNumber = customer.getPhoneNumber(); // Lưu số điện thoại để so sánh với maKhach
                    loadInvoices();
                    //tvPoints.setText(String.valueOf(tongDiem));
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
                    if (maKhach != null && maKhach.equals(customerPhoneNumber)) {
                        visitCount++;
                        long tongTien = invoiceSnapshot.child("tongtien").getValue(Long.class) != null ?
                                invoiceSnapshot.child("tongtien").getValue(Long.class) : 0;


                        DataSnapshot ngLapSnapshot = invoiceSnapshot.child("ngLap");
                        long time = ngLapSnapshot.child("time").getValue(Long.class) != null ?
                                ngLapSnapshot.child("time").getValue(Long.class) : 0;
                        int timezoneOffset = ngLapSnapshot.child("timezoneOffset").getValue(Integer.class) != null ?
                                ngLapSnapshot.child("timezoneOffset").getValue(Integer.class) : 0;

                        // Chuyển timestamp thành ngày
                        long adjustedTime = time + (timezoneOffset * 60 * 1000); // Điều chỉnh múi giờ
                        String dateStr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                .format(new Date(adjustedTime));

                        // Tạo view cho mỗi hóa đơn
                        View invoiceView = LayoutInflater.from(ActivityCustomerPriorityDetail.this)
                                .inflate(android.R.layout.simple_list_item_1, null);
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
                        tongDiem += tongTien;
                        tvTotal.setTextColor(0xFF212121);
                        tvTotal.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                        tvPoints.setText(String.valueOf(tongDiem/20000));

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

                // Cập nhật số lần ăn (tvVisitCount) dựa trên tổng số hóa đơn
                tvVisitCount.setText(String.valueOf(visitCount));

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