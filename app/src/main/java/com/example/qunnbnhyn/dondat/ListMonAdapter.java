package com.example.qunnbnhyn.dondat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qunnbnhyn.R;

import java.util.List;

public class ListMonAdapter extends RecyclerView.Adapter<ListMonAdapter.ViewHolder> {

    List<ItemMonGoi> listMon;

    public ListMonAdapter(List<ItemMonGoi> listMon) {
        this.listMon = listMon;
    }

    @NonNull
    @Override
    public ListMonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ListMonAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_mon_order,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListMonAdapter.ViewHolder holder, int position) {
        ItemMonGoi item = listMon.get(position);
        Log.d("ListMonAdapter", "Binding item: " + item.getTenMon() + ", So luong: " + item.getSoLuong());
        Log.d("ListMonAdapter", "holder.txtTen: " + holder.txtTen + ", holder.txtSoLuong: " + holder.txtSoLuong);
        holder.txtSoLuong.setText(item.getSoLuong());
        holder.txtTen.setText(item.getTenMon());
    }

    @Override
    public int getItemCount() {
        return listMon.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtTen,txtSoLuong;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTen = itemView.findViewById(R.id.txt_tenmongoi);
            txtSoLuong = itemView.findViewById(R.id.txt_slggoi);
        }
    }
}
