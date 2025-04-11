package com.example.qunnbnhyn.TT;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qunnbnhyn.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    private int currentVisitCount = 0; // To store the customer's visit count

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
        btnThanhToan.setOnClickListener(v -> hienChonPhuongThuc());

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
        databaseReference.child("ban_an").child(String.valueOf(soBan)).child("So hoa don").addListenerForSingleValueEvent(new ValueEventListener() {
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
                boolean customerFound = false;
                for (DataSnapshot customer : snapshot.getChildren()) {
                    String phone = customer.child("phoneNumber").getValue(String.class);
                    if (phone != null && phone.equals(sdt)) {
                        customerFound = true;
                        maKhachHang = customer.getKey();
                        currentPoints = customer.child("points").getValue(Integer.class);
                        currentVisitCount = customer.child("visitCount").getValue(Integer.class);

                        // Increment visit count
                        currentVisitCount += 1;
                        databaseReference.child("customers").child(maKhachHang).child("visitCount").setValue(currentVisitCount);

                        hienThongBao("Thông báo", "Số điện thoại này có " + currentPoints + " điểm. Số lần đến: " + currentVisitCount);
                        if (currentPoints >= 50) {
                            hoiDoiDiem();
                        }
                        return;
                    }
                }
                if (!customerFound) {
                    maKhachHang = null;
                    currentPoints = 0;
                    currentVisitCount = 0;
                    etDoiDiem.setText("");
                    etDoiDiem.setEnabled(false);
                    hienThongBao("Thông báo", "Số điện thoại không tồn tại!");
                }
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

    private void hienChonPhuongThuc() {
        String maHoaDon = tvSoHoaDon.getText().toString().replace("Số hóa đơn: ", "");
        if (maHoaDon.equals("Chưa có")) {
            hienThongBao("Lỗi", "Không có hóa đơn để thanh toán!");
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Chọn phương thức thanh toán")
                .setMessage("Bạn muốn thanh toán bằng cách nào?")
                .setPositiveButton("Tiền mặt", (dialog, which) -> xuLyThanhToan("Tiền mặt"))
                .setNegativeButton("Chuyển khoản", (dialog, which) -> hienMaQR())
                .setNeutralButton("Hủy", null)
                .show();
    }

    private void hienMaQR() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_qr_payment, null);

        ImageView qrImage = dialogView.findViewById(R.id.img_qr_code);
        TextView tvStk = dialogView.findViewById(R.id.tv_stk);
        TextView tvTongTienThanhToan = dialogView.findViewById(R.id.tv_tong_tien_thanh_toan);

        String diemDoiStr = etDoiDiem.getText().toString().trim();
        int diemDoi = 0;
        if (!diemDoiStr.isEmpty()) {
            diemDoi = Integer.parseInt(diemDoiStr);
        }
        int tienGiam = diemDoi * 1000;
        int tongTienMoi = tongTien - tienGiam;
        if (tongTienMoi < 0) tongTienMoi = 0;
        tvTongTienThanhToan.setText("Tổng tiền: " + tongTienMoi + " VNĐ");

        new AlertDialog.Builder(this)
                .setTitle("Quét mã QR để thanh toán")
                .setView(dialogView)
                .setPositiveButton("Đã thanh toán", (dialog, which) -> xuLyThanhToan("Chuyển khoản"))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void xuLyThanhToan(String phuongThuc) {
        String maHoaDon = tvSoHoaDon.getText().toString().replace("Số hóa đơn: ", "");

        // Tính tổng tiền thanh toán (sau khi trừ điểm đổi)
        String diemDoiStr = etDoiDiem.getText().toString().trim();
        int diemDoi = 0;
        if (!diemDoiStr.isEmpty()) {
            diemDoi = Integer.parseInt(diemDoiStr);
            if (diemDoi > currentPoints) {
                hienThongBao("Lỗi", "Điểm đổi vượt quá " + currentPoints + "!");
                return;
            }
            currentPoints -= diemDoi;
            databaseReference.child("customers").child(maKhachHang).child("points").setValue(currentPoints);
        }
        int tienGiam = diemDoi * 1000;
        final int tongTienThanhToan = Math.max(tongTien - tienGiam, 0); // Tổng tiền thực tế sau khi trừ điểm

        // Tính điểm tích lũy: 1 điểm cho mỗi 20,000 VNĐ
        int pointsToAdd = tongTienThanhToan / 20000;
        if (maKhachHang != null) {
            currentPoints += pointsToAdd;
            databaseReference.child("customers").child(maKhachHang).child("points").setValue(currentPoints);
        }

        // Lấy ngày hiện tại (định dạng YYYY-MM-DD)
        String ngayHienTai = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Cập nhật trạng thái hóa đơn và thông tin thanh toán trong node hoa_don
        DatabaseReference hoaDonRef = databaseReference.child("hoa_don").child(maHoaDon);
        hoaDonRef.child("trangthai").setValue(true);
        hoaDonRef.child("phuongThucThanhToan").setValue(phuongThuc); // Lưu phương thức thanh toán
        hoaDonRef.child("ngayThanhToan").setValue(ngayHienTai);      // Lưu ngày thanh toán
        hoaDonRef.child("tongTienThanhToan").setValue(tongTienThanhToan); // Lưu tổng tiền thanh toán
        if (maKhachHang != null) {
            hoaDonRef.child("maKhachHang").setValue(maKhachHang); // Lưu mã khách hàng nếu có
        }
        tvTrangThai.setText("Trạng thái: Đã thanh toán");

        // Xóa mã hóa đơn khỏi bàn
        int soBan = Integer.parseInt(tvSoBan.getText().toString().replace("Số bàn: ", ""));
        databaseReference.child("ban_an").child(String.valueOf(soBan - 1)).setValue("");

        // Lưu thông tin thanh toán lên node thanh_toan_lich_su
        String phuongThucNode = phuongThuc.equals("Tiền mặt") ? "tien_mat" : "chuyen_khoan";
        DatabaseReference thanhToanRef = databaseReference.child("thanh_toan_lich_su").child(ngayHienTai).child(phuongThucNode);

        thanhToanRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int soLan = 0;
                int tongTienHienTai = 0;

                // Lấy dữ liệu hiện tại (nếu có)
                if (snapshot.exists()) {
                    Long soLanLong = snapshot.child("so_lan").getValue(Long.class);
                    Long tongTienLong = snapshot.child("tong_tien").getValue(Long.class);
                    soLan = soLanLong != null ? soLanLong.intValue() : 0;
                    tongTienHienTai = tongTienLong != null ? tongTienLong.intValue() : 0;
                }

                // Cập nhật số lần và tổng tiền
                soLan += 1;
                tongTienHienTai += tongTienThanhToan;

                // Lưu lại lên Firebase
                thanhToanRef.child("so_lan").setValue(soLan);
                thanhToanRef.child("tong_tien").setValue(tongTienHienTai);
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });

        // Hiển thị thông báo thành công
        String thongBao = "Thanh toán bằng " + phuongThuc + " hoàn tất!";
        if (maKhachHang != null) {
            thongBao += " Đã cộng " + pointsToAdd + " điểm, bạn có " + currentPoints + " điểm.";
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
