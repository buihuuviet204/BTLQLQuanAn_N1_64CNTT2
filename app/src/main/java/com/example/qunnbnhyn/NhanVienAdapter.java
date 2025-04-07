package com.example.qunnbnhyn;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NhanVienAdapter extends RecyclerView.Adapter<NhanVienAdapter.NhanVienViewHolder> {

    private List<NhanVien> nhanVienList;

    public NhanVienAdapter(List<NhanVien> nhanVienList) {
        this.nhanVienList = nhanVienList;
    }

    @NonNull
    @Override
    public NhanVienViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.danh_sach_nhan_vien, parent, false);
        return new NhanVienViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NhanVienViewHolder holder, int position) {
        NhanVien nhanVien = nhanVienList.get(position);
        holder.bind(nhanVien);

        holder.itemView.setOnClickListener(v -> {
            Log.d("NhanVienAdapter", "Clicked on: " + nhanVien.getMaNhanVien());
            Intent intent = new Intent(v.getContext(), ChinhSuaThongTinChiTiet.class);
            try {
                intent.putExtra("maNhanVien", nhanVien.getMaNhanVien());
                intent.putExtra("name", nhanVien.getName());
                intent.putExtra("birthDate", nhanVien.getBirthDate());
                intent.putExtra("gender", nhanVien.getGender());
                intent.putExtra("email", nhanVien.getEmail());
                intent.putExtra("phone", nhanVien.getPhone());
                intent.putExtra("hometown", nhanVien.getHometown());
                intent.putExtra("position", nhanVien.getPosition());
                intent.putExtra("password", nhanVien.getPassword());
                intent.putExtra("avatarBase64", nhanVien.getAvatarBase64());
                Log.d("NhanVienAdapter", "Intent prepared for ChinhSuaThongTinChiTiet");
                v.getContext().startActivity(intent);
                Log.d("NhanVienAdapter", "Intent started");
            } catch (Exception e) {
                Log.e("NhanVienAdapter", "Error starting ChinhSuaThongTinChiTiet: " + e.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return nhanVienList.size();
    }

    static class NhanVienViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgNhanVien;
        private TextView txtMaNhanVien;
        private TextView txtHoTen;

        public NhanVienViewHolder(@NonNull View itemView) {
            super(itemView);
            imgNhanVien = itemView.findViewById(R.id.img_nhan_vien);
            txtMaNhanVien = itemView.findViewById(R.id.txt_ma_nhan_vien);
            txtHoTen = itemView.findViewById(R.id.txt_ho_ten);
        }

        public void bind(NhanVien nhanVien) {
            txtMaNhanVien.setText(nhanVien.getMaNhanVien());
            txtHoTen.setText(nhanVien.getName());

            if (nhanVien.getAvatarBase64() != null && !nhanVien.getAvatarBase64().isEmpty()) {
                byte[] decodedString = Base64.decode(nhanVien.getAvatarBase64(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imgNhanVien.setImageBitmap(bitmap);
            } else {
                imgNhanVien.setImageResource(R.drawable.ic_launcher_background);
            }
        }
    }
}