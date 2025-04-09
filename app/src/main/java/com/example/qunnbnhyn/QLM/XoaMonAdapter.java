package com.example.qunnbnhyn.QLM;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.qunnbnhyn.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class XoaMonAdapter extends RecyclerView.Adapter<XoaMonAdapter.ViewHolder>{
    List<MonAn> listMA;

    public XoaMonAdapter(List<MonAn> listMA) {
        this.listMA = listMA;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcl_xoa, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MonAn monAn = listMA.get(position);
        holder.editGia.setText(monAn.getGiaban()+"");
        holder.editTen.setText(monAn.getName());
        holder.editLoai.setText(monAn.getLoai());
        Glide.with(holder.img)
                .load(monAn.getImageMonAn())
                .into(holder.img);
        holder.btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("thuc_don");
                myRef.child(monAn.getMaMon()).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            // Xóa thành công
                            Log.d("Firebase", "Đã xóa người dùng user123");
                        })
                        .addOnFailureListener(e -> {
                            // Xóa thất bại
                            Log.e("Firebase", "Lỗi xóa người dùng", e);
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return listMA.size();
    }

    static public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView img;
        private EditText editTen, editGia,editLoai;
        private Button btnXoa;
        private Uri newImageUri;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_monan);
            editTen = itemView.findViewById(R.id.edit_ten_mon);
            editGia = itemView.findViewById(R.id.edit_gia);
            editLoai = itemView.findViewById(R.id.edit_loai);
            btnXoa = itemView.findViewById(R.id.btn_xoa);
        }
    }
}
