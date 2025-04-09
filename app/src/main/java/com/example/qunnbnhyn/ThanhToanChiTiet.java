package com.example.qunnbnhyn;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ThanhToanChiTiet extends AppCompatActivity {
    private TextView tvSoHoaDon, tvSoBan, tvNgay, tvTrangThai, tvTongTien;
    private EditText etSoDienThoai, etDoiDiem;
    private TableLayout tableChiTiet;
    private DatabaseReference databaseReference;
    private ImageButton imbBack;
    private Button btnThanhToan, btnKiemTra;
    private int tongTien = 0;
    private int stt = 1;
    private String maKhachHang = null;
    private int currentPoints = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan_chi_tiet);

        // Ánh xạ các thành phần giao diện
        tvSoHoaDon = findViewById(R.id.tv_so_hoa_don);
        tvSoBan = findViewById(R.id.tv_so_ban);
        tvNgay = findViewById(R.id.tv_ngay);
        tvTrangThai = findViewById(R.id.tv_trang_thai);
        tvTongTien = findViewById(R.id.tv_tong_tien);
        etSoDienThoai = findViewById(R.id.et_so_dien_thoai);
        etDoiDiem = findViewById(R.id.et_doi_diem);
        tableChiTiet = findViewById(R.id.table_chi_tiet);
        imbBack = findViewById(R.id.btn_back);
        btnThanhToan = findViewById(R.id.btn_thanh_toan);
        btnKiemTra = findViewById(R.id.btn_kiem_tra);

        // Sự kiện nhấn nút Back
        imbBack.setOnClickListener(v -> finish());

        // Lấy số bàn từ Intent
        int soBan = getIntent().getIntExtra("SO_BAN", 0);

        // Khởi tạo Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Hiển thị số bàn
        tvSoBan.setText("Số bàn: " + soBan);

        // Đặt lại giá trị ban đầu
        tongTien = 0;
        stt = 1;

        // Lấy dữ liệu từ Firebase
        loadDataFromFirebase(soBan);

        // Sự kiện nhấn nút Kiểm tra
        btnKiemTra.setOnClickListener(v -> kiemTraSoDienThoai());

        // Sự kiện nhấn nút Thanh toán
        btnThanhToan.setOnClickListener(v -> thanhToan());
    }

    private void loadDataFromFirebase(int soBan) {
        databaseReference.child("ban_an").child(String.valueOf(soBan - 1)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot banAnSnapshot) {
                String maHoaDon = banAnSnapshot.getValue(String.class);
                Log.d("FirebaseDebug", "Mã hóa đơn (ban_an[" + (soBan - 1) + "]): " + maHoaDon);

                if (maHoaDon == null || maHoaDon.isEmpty() || maHoaDon.equals("null")) {
                    tvSoHoaDon.setText("Số hóa đơn: Chưa có");
                    tvNgay.setText("Ngày: --");
                    tvTrangThai.setText("Trạng thái: Chưa thanh toán");
                    tvTongTien.setText("0");
                    tableChiTiet.removeAllViews();
                    return;
                }

                tvSoHoaDon.setText("Số hóa đơn: " + maHoaDon);

                databaseReference.child("hoa_don").child(maHoaDon).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot hoaDonSnapshot) {
                        if (!hoaDonSnapshot.exists()) {
                            tvNgay.setText("Ngày: --");
                            tvTrangThai.setText("Trạng thái: Chưa thanh toán");
                            tvTongTien.setText("0");
                            tableChiTiet.removeAllViews();
                            return;
                        }

                        String ngLap = hoaDonSnapshot.child("ngLap").getValue(String.class);
                        if (ngLap != null && !ngLap.isEmpty()) {
                            tvNgay.setText("Ngày: " + ngLap);
                        } else {
                            tvNgay.setText("Ngày: --");
                        }

                        Boolean trangThai = hoaDonSnapshot.child("trangthai").getValue(Boolean.class);
                        tvTrangThai.setText("Trạng thái: " + (trangThai != null && trangThai ? "Đã thanh toán" : "Chưa thanh toán"));

                        DataSnapshot ctdhSnapshot = hoaDonSnapshot.child("ctdh");
                        if (!ctdhSnapshot.exists() || !ctdhSnapshot.hasChildren()) {
                            tvTongTien.setText("0");
                            tableChiTiet.removeAllViews();
                            return;
                        }

                        long soMon = ctdhSnapshot.getChildrenCount();
                        final int[] monDaXuLy = {0};

                        tableChiTiet.removeAllViews();

                        for (DataSnapshot monSnapshot : ctdhSnapshot.getChildren()) {
                            String maMon = monSnapshot.getKey();
                            Integer soLuong = monSnapshot.getValue(Integer.class);

                            if (soLuong == null) {
                                monDaXuLy[0]++;
                                if (monDaXuLy[0] == soMon) {
                                    tvTongTien.setText(String.valueOf(tongTien));
                                }
                                continue;
                            }

                            databaseReference.child("thuc_don").child(maMon).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot thucDonSnapshot) {
                                    if (!thucDonSnapshot.exists()) {
                                        monDaXuLy[0]++;
                                        if (monDaXuLy[0] == soMon) {
                                            tvTongTien.setText(String.valueOf(tongTien));
                                        }
                                        return;
                                    }

                                    String tenMon = thucDonSnapshot.child("name").getValue(String.class);
                                    Integer giaBan = thucDonSnapshot.child("giaban").getValue(Integer.class);

                                    if (tenMon != null && giaBan != null) {
                                        int thanhTien = soLuong * giaBan;
                                        tongTien += thanhTien;

                                        TableRow row = new TableRow(ThanhToanChiTiet.this);
                                        row.setPadding(8, 8, 8, 8);

                                        TextView tvStt = new TextView(ThanhToanChiTiet.this);
                                        tvStt.setText(String.valueOf(stt++));
                                        tvStt.setTextSize(16);
                                        tvStt.setTextColor(Color.BLACK);
                                        tvStt.setGravity(android.view.Gravity.CENTER);
                                        tvStt.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.75f));

                                        TextView tvTenMon = new TextView(ThanhToanChiTiet.this);
                                        tvTenMon.setText(tenMon);
                                        tvTenMon.setTextSize(16);
                                        tvTenMon.setTextColor(Color.BLACK);
                                        tvTenMon.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));

                                        TextView tvSoLuong = new TextView(ThanhToanChiTiet.this);
                                        tvSoLuong.setText(String.valueOf(soLuong));
                                        tvSoLuong.setTextSize(16);
                                        tvSoLuong.setTextColor(Color.BLACK);
                                        tvSoLuong.setGravity(android.view.Gravity.CENTER);
                                        tvSoLuong.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.5f));

                                        TextView tvGia = new TextView(ThanhToanChiTiet.this);
                                        tvGia.setText(String.valueOf(giaBan));
                                        tvGia.setTextSize(16);
                                        tvGia.setTextColor(Color.BLACK);
                                        tvGia.setGravity(android.view.Gravity.END);
                                        tvGia.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));

                                        TextView tvThanhTien = new TextView(ThanhToanChiTiet.this);
                                        tvThanhTien.setText(String.valueOf(thanhTien));
                                        tvThanhTien.setTextSize(16);
                                        tvThanhTien.setTextColor(Color.BLACK);
                                        tvThanhTien.setGravity(android.view.Gravity.END);
                                        tvThanhTien.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.5f));

                                        row.addView(tvStt);
                                        row.addView(tvTenMon);
                                        row.addView(tvSoLuong);
                                        row.addView(tvGia);
                                        row.addView(tvThanhTien);

                                        tableChiTiet.addView(row);
                                    }

                                    monDaXuLy[0]++;
                                    if (monDaXuLy[0] == soMon) {
                                        tvTongTien.setText(String.valueOf(tongTien));
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e("Firebase", "Lỗi khi lấy dữ liệu thực đơn: " + databaseError.getMessage());
                                    monDaXuLy[0]++;
                                    if (monDaXuLy[0] == soMon) {
                                        tvTongTien.setText(String.valueOf(tongTien));
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Firebase", "Lỗi khi lấy dữ liệu hóa đơn: " + databaseError.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Lỗi khi lấy dữ liệu bàn: " + databaseError.getMessage());
            }
        });
    }

    private void kiemTraSoDienThoai() {
        String sdt = etSoDienThoai.getText().toString().trim();
        if (sdt.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Lỗi")
                    .setMessage("Vui lòng nhập số điện thoại!")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        databaseReference.child("customers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean found = false;
                for (DataSnapshot customerSnapshot : snapshot.getChildren()) {
                    String phoneNumber = customerSnapshot.child("phoneNumber").getValue(String.class);
                    if (phoneNumber != null && phoneNumber.equals(sdt)) {
                        found = true;
                        maKhachHang = customerSnapshot.getKey();
                        currentPoints = customerSnapshot.child("points").getValue(Integer.class);

                        // Tăng điểm +1
                        currentPoints += 1;
                        databaseReference.child("customers").child(maKhachHang).child("points").setValue(currentPoints);

                        new AlertDialog.Builder(ThanhToanChiTiet.this)
                                .setTitle("Thông báo")
                                .setMessage("Đã tích 1 điểm. Bạn hiện có " + currentPoints + " điểm.")
                                .setPositiveButton("OK", null)
                                .show();

                        // Kiểm tra nếu điểm >= 50
                        if (currentPoints >= 50) {
                            new AlertDialog.Builder(ThanhToanChiTiet.this)
                                    .setTitle("Đổi điểm")
                                    .setMessage("Bạn có " + currentPoints + " điểm. Có muốn đổi điểm không? (1 điểm = 1000 VNĐ)")
                                    .setPositiveButton("Có", (dialog, which) -> etDoiDiem.setEnabled(true))
                                    .setNegativeButton("Không", null)
                                    .show();
                        }
                        break;
                    }
                }

                if (!found) {
                    maKhachHang = null;
                    currentPoints = 0;
                    etDoiDiem.setText("");
                    etDoiDiem.setEnabled(false);
                    new AlertDialog.Builder(ThanhToanChiTiet.this)
                            .setTitle("Thông báo")
                            .setMessage("Số điện thoại không tồn tại!")
                            .setPositiveButton("OK", null)
                            .show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Lỗi khi kiểm tra khách hàng: " + error.getMessage());
            }
        });
    }

    private void thanhToan() {
        String maHoaDon = tvSoHoaDon.getText().toString().replace("Số hóa đơn: ", "");
        if (maHoaDon.equals("Chưa có")) {
            new AlertDialog.Builder(this)
                    .setTitle("Lỗi")
                    .setMessage("Không có hóa đơn để thanh toán!")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        // Xử lý đổi điểm nếu có
        if (maKhachHang != null && currentPoints >= 50) {
            String diemDoiStr = etDoiDiem.getText().toString().trim();
            if (!diemDoiStr.isEmpty()) {
                int diemDoi = Integer.parseInt(diemDoiStr);
                if (diemDoi > currentPoints) {
                    new AlertDialog.Builder(this)
                            .setTitle("Lỗi")
                            .setMessage("Số điểm muốn đổi vượt quá điểm hiện có (" + currentPoints + ")!")
                            .setPositiveButton("OK", null)
                            .show();
                    return;
                }

                int tienGiam = diemDoi * 1000;
                int tongTienMoi = tongTien - tienGiam;
                if (tongTienMoi < 0) tongTienMoi = 0;

                // Cập nhật điểm còn lại trên Firebase
                int diemConLai = currentPoints - diemDoi;
                databaseReference.child("customers").child(maKhachHang).child("points").setValue(diemConLai);
                tvTongTien.setText(String.valueOf(tongTienMoi));
            }
        }

        // Cập nhật trạng thái hóa đơn
        databaseReference.child("hoa_don").child(maHoaDon).child("trangthai").setValue(true);
        tvTrangThai.setText("Trạng thái: Đã thanh toán");

        // Xóa mã hóa đơn khỏi bàn
        int soBan = Integer.parseInt(tvSoBan.getText().toString().replace("Số bàn: ", ""));
        databaseReference.child("ban_an").child(String.valueOf(soBan - 1)).setValue("");

        new AlertDialog.Builder(this)
                .setTitle("Thành công")
                .setMessage("Thanh toán hoàn tất!")
                .setPositiveButton("OK", (dialog, which) -> finish())
                .show();
    }
}