package com.example.qunnbnhyn;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NhanVienAdapter extends RecyclerView.Adapter<NhanVienAdapter.NhanVienViewHolder> {

    private List<NhanVien> nhanVienList; // Danh sách nhân viên để hiển thị
    private OnNhanVienClickListener listener; // Đối tượng để xử lý khi người dùng nhấn vào nhân viên

    // Constructor: Khởi tạo adapter với danh sách nhân viên và listener
    public NhanVienAdapter(List<NhanVien> nhanVienList, OnNhanVienClickListener listener) {
        this.nhanVienList = nhanVienList; // Gán danh sách nhân viên
        this.listener = listener; // Gán listener để xử lý sự kiện click
    }

    // Tạo view cho mỗi item trong danh sách (gọi khi cần hiển thị item mới)
    @NonNull
    @Override
    public NhanVienViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view từ file layout danh_sach_nhan_vien.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.danh_sach_nhan_vien, parent, false);
        return new NhanVienViewHolder(view); // Trả về ViewHolder chứa view vừa tạo
    }

    // Gán dữ liệu cho từng item trong danh sách
    @Override
    public void onBindViewHolder(@NonNull NhanVienViewHolder holder, int position) {
        NhanVien nhanVien = nhanVienList.get(position); // Lấy nhân viên tại vị trí position

        // Hiển thị thông tin cơ bản của nhân viên
        holder.txtMaNhanVien.setText(nhanVien.getEmail()); // Gán mã nhân viên vào TextView
        holder.txtHoTen.setText(nhanVien.getName()); // Gán tên nhân viên vào TextView

        // Xử lý hiển thị ảnh đại diện
        if (nhanVien.getAvatarBase64() != null && !nhanVien.getAvatarBase64().isEmpty()) {
            // Nếu có ảnh (chuỗi Base64 không rỗng)
            byte[] decodedString = Base64.decode(nhanVien.getAvatarBase64(), Base64.DEFAULT); // Giải mã chuỗi Base64 thành mảng byte
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); // Chuyển mảng byte thành ảnh Bitmap
            holder.imgNhanVien.setImageBitmap(bitmap); // Hiển thị ảnh lên ImageView
        } else {
            // Nếu không có ảnh, dùng ảnh mặc định
            holder.imgNhanVien.setImageResource(R.drawable.ic_launcher_background);
        }

        // Xử lý sự kiện khi người dùng nhấn vào item
        holder.itemView.setOnClickListener(v -> listener.onNhanVienClick(nhanVien)); // Gọi hàm trong listener khi nhấn
    }

    // Trả về số lượng item trong danh sách
    @Override
    public int getItemCount() {
        return nhanVienList.size(); // Số lượng nhân viên trong danh sách
    }

    // Lớp ViewHolder: Giữ các thành phần giao diện của một item
    static class NhanVienViewHolder extends RecyclerView.ViewHolder {
        ImageView imgNhanVien;   // Nơi hiển thị ảnh nhân viên
        TextView txtMaNhanVien;  // Nơi hiển thị mã nhân viên
        TextView txtHoTen;       // Nơi hiển thị tên nhân viên

        // Constructor: Liên kết các thành phần giao diện với ID trong layout
        public NhanVienViewHolder(@NonNull View itemView) {
            super(itemView);
            imgNhanVien = itemView.findViewById(R.id.img_nhan_vien);     // Gắn ImageView với ID
            txtMaNhanVien = itemView.findViewById(R.id.txt_ma_nhan_vien); // Gắn TextView mã nhân viên với ID
            txtHoTen = itemView.findViewById(R.id.txt_ho_ten);           // Gắn TextView tên nhân viên với ID
        }
    }

    // Interface: Định nghĩa hàm để xử lý khi nhấn vào nhân viên
    interface OnNhanVienClickListener {
        void onNhanVienClick(NhanVien nhanVien); // Hàm được gọi khi nhấn, truyền vào nhân viên được chọn
    }
}