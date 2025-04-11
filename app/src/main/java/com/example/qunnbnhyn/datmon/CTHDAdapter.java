package com.example.qunnbnhyn.datmon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qunnbnhyn.R;

import java.util.List;

public class CTHDAdapter extends RecyclerView.Adapter<CTHDAdapter.ViewHolder> {
    private List<ItemCTHD> cthd;

    public CTHDAdapter(List<ItemCTHD> cthd) {
        this.cthd = cthd;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcl_cthd,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemCTHD item = cthd.get(position);
        holder.txtTen.setText(item.getTenMon());
        holder.txtGia.setText(item.getGiaBan());
        holder.txtTTien.setText(item.getThanhTien());
        holder.txtSLg.setText(item.getSoLuong());
    }

    @Override
    public int getItemCount() {
        return cthd.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView txtTen;
        private TextView txtGia;
        private TextView txtSLg;
        private TextView txtTTien;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtGia = itemView.findViewById(R.id.txt_giaban);
            txtSLg = itemView.findViewById(R.id.txt_soluong);
            txtTen = itemView.findViewById(R.id.txt_tenmon);
            txtTTien = itemView.findViewById(R.id.txt_ttien);
        }
    }
}
