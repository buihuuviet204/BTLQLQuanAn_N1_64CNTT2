package com.example.qunnbnhyn;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    private ImageButton imbBack;
    private Button btnThanhToan, btnKiemTra;
    private DatabaseReference databaseReference;
    private int tongTien = 0; // Tổng tiền gốc
    private int stt = 1;
    private String maKhachHang = null;
    private int currentPoints = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan_chi_tiet);

        // Ánh xạ giao diện
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

        // Kết nối Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Lấy số bàn từ Intent
        int soBan = getIntent().getIntExtra("SO_BAN", 0);
        tvSoBan.setText("Số bàn: " + soBan);

        // Nút Back
        imbBack.setOnClickListener(v -> finish());

        // Nút Kiểm tra số điện thoại
        btnKiemTra.setOnClickListener(v -> kiemTraSoDienThoai());

        // Nút Thanh toán
        btnThanhToan.setOnClickListener(v -> thanhToan());

        // Theo dõi thay đổi số điểm đổi
        etDoiDiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                capNhatTongTien();
            }
        });

        // Tải dữ liệu từ Firebase
        taiDuLieu(soBan);
    }

    private void taiDuLieu(int soBan) {
        databaseReference.child("ban_an").child(String.valueOf(soBan - 1)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String maHoaDon = snapshot.getValue(String.class);
                if (maHoaDon == null || maHoaDon.equals("null")) {
                    tvSoHoaDon.setText("Số hóa đơn: Chưa có");
                    tvNgay.setText("Ngày: --");
                    tvTrangThai.setText("Trạng thái: Chưa thanh toán");
                    tvTongTien.setText("0");
                    return;
                }

                tvSoHoaDon.setText("Số hóa đơn: " + maHoaDon);

                databaseReference.child("hoa_don").child(maHoaDon).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String ngay = snapshot.child("ngLap").getValue(String.class);
                        Boolean trangThai = snapshot.child("trangthai").getValue(Boolean.class);

                        tvNgay.setText("Ngày: " + (ngay != null ? ngay : "--"));
                        tvTrangThai.setText("Trạng thái: " + (trangThai != null && trangThai ? "Đã thanh toán" : "Chưa thanh toán"));

                        DataSnapshot chiTiet = snapshot.child("ctdh");
                        if (!chiTiet.hasChildren()) {
                            tvTongTien.setText("0");
                            return;
                        }

                        for (DataSnapshot mon : chiTiet.getChildren()) {
                            String maMon = mon.getKey();
                            int soLuong = mon.getValue(Integer.class);

                            databaseReference.child("thuc_don").child(maMon).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    String tenMon = snapshot.child("name").getValue(String.class);
                                    Integer giaBan = snapshot.child("giaban").getValue(Integer.class);

                                    if (tenMon != null && giaBan != null) {
                                        int thanhTien = soLuong * giaBan;
                                        tongTien += thanhTien;

                                        TableRow row = new TableRow(ThanhToanChiTiet.this);
                                        row.addView(taoTextView(String.valueOf(stt++), 0.75f, true));
                                        row.addView(taoTextView(tenMon, 2f, false));
                                        row.addView(taoTextView(String.valueOf(soLuong), 0.5f, true));
                                        row.addView(taoTextView(String.valueOf(giaBan), 1f, false));
                                        row.addView(taoTextView(String.valueOf(thanhTien), 1.5f, false));
                                        tableChiTiet.addView(row);

                                        tvTongTien.setText(String.valueOf(tongTien));
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {}
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private TextView taoTextView(String text, float weight, boolean center) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(16);
        tv.setTextColor(Color.BLACK);
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight);
        tv.setLayoutParams(params);
        if (center) tv.setGravity(android.view.Gravity.CENTER);
        return tv;
    }

    private void kiemTraSoDienThoai() {
        String sdt = etSoDienThoai.getText().toString().trim();
        if (sdt.isEmpty()) {
            hienThongBao("Lỗi", "Vui lòng nhập số điện thoại!");
            return;
        }

        databaseReference.child("customers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot customer : snapshot.getChildren()) {
                    String phone = customer.child("phoneNumber").getValue(String.class);
                    if (phone != null && phone.equals(sdt)) {
                        maKhachHang = customer.getKey();
                        currentPoints = customer.child("points").getValue(Integer.class);
                        hienThongBao("Thông báo", "Số điện thoại này có " + currentPoints + " điểm.");
                        if (currentPoints >= 50) {
                            hoiDoiDiem();
                        }
                        return;
                    }
                }
                maKhachHang = null;
                currentPoints = 0;
                etDoiDiem.setText("");
                etDoiDiem.setEnabled(false);
                hienThongBao("Thông báo", "Số điện thoại không tồn tại!");
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void hoiDoiDiem() {
        new AlertDialog.Builder(this)
                .setTitle("Đổi điểm")
                .setMessage("Bạn có " + currentPoints + " điểm. Có muốn đổi không? (1 điểm = 1000 VNĐ)")
                .setPositiveButton("Có", (dialog, which) -> etDoiDiem.setEnabled(true))
                .setNegativeButton("Không", null)
                .show();
    }

    private void capNhatTongTien() {
        String diemDoiStr = etDoiDiem.getText().toString().trim();
        int diemDoi = 0;
        if (!diemDoiStr.isEmpty()) {
            try {
                diemDoi = Integer.parseInt(diemDoiStr);
                if (diemDoi > currentPoints) {
                    etDoiDiem.setError("Điểm vượt quá " + currentPoints);
                    return;
                }
            } catch (NumberFormatException e) {
                etDoiDiem.setError("Nhập số hợp lệ");
                return;
            }
        }
        int tienGiam = diemDoi * 1000;
        int tongTienMoi = tongTien - tienGiam;
        if (tongTienMoi < 0) tongTienMoi = 0;
        tvTongTien.setText(String.valueOf(tongTienMoi));
    }

    private void thanhToan() {
        String maHoaDon = tvSoHoaDon.getText().toString().replace("Số hóa đơn: ", "");
        if (maHoaDon.equals("Chưa có")) {
            hienThongBao("Lỗi", "Không có hóa đơn để thanh toán!");
            return;
        }

        String diemDoiStr = etDoiDiem.getText().toString().trim();
        if (!diemDoiStr.isEmpty()) {
            int diemDoi = Integer.parseInt(diemDoiStr);
            if (diemDoi > currentPoints) {
                hienThongBao("Lỗi", "Điểm đổi vượt quá " + currentPoints + "!");
                return;
            }
            currentPoints -= diemDoi;
            databaseReference.child("customers").child(maKhachHang).child("points").setValue(currentPoints);
        }

        databaseReference.child("hoa_don").child(maHoaDon).child("trangthai").setValue(true);
        tvTrangThai.setText("Trạng thái: Đã thanh toán");

        int soBan = Integer.parseInt(tvSoBan.getText().toString().replace("Số bàn: ", ""));
        databaseReference.child("ban_an").child(String.valueOf(soBan - 1)).setValue("");

        if (maKhachHang != null) {
            currentPoints += 1;
            databaseReference.child("customers").child(maKhachHang).child("points").setValue(currentPoints);
        }

        String thongBao = "Thanh toán hoàn tất!";
        if (maKhachHang != null) {
            thongBao += " Đã cộng 1 điểm, bạn có " + currentPoints + " điểm.";
        }
        hienThongBao("Thành công", thongBao, true);
    }

    private void hienThongBao(String title, String message) {
        hienThongBao(title, message, false);
    }

    private void hienThongBao(String title, String message, boolean ketThuc) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null);
        if (ketThuc) {
            builder.setOnDismissListener(dialog -> finish());
        }
        builder.show();
    }
}