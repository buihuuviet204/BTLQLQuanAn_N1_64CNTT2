package com.example.qunnbnhyn.datmon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.qunnbnhyn.QLM.MonAn;
import com.example.qunnbnhyn.R;

import java.util.List;

public class ItemSearchAdapter extends RecyclerView.Adapter<ItemSearchAdapter.ViewHolder> {
    private List<MonAn> listMon;
    private OnChangeListener listener;

    public ItemSearchAdapter(List<MonAn> listMon, OnChangeListener listener) {
        this.listMon = listMon;
        this.listener = listener;
    }

    public ItemSearchAdapter(List<MonAn> listMon) {
        this.listMon = listMon;
    }

    @NonNull
    @Override
    public ItemSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemSearchAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemSearchAdapter.ViewHolder holder, int position) {
        MonAn item = listMon.get(position);
        holder.txtTenSearch.setText(item.getName());
        holder.txtGiaSearch.setText(item.getGiaban() + "");
        Glide.with(holder.imgSearch)
                .load(item.getImageMonAn())
                .into(holder.imgSearch);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.monAnSearched(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listMon.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtTenSearch, txtGiaSearch;
        private LinearLayout linearLayout;
        private ImageView imgSearch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtGiaSearch = itemView.findViewById(R.id.txt_gia_search);
            txtTenSearch = itemView.findViewById(R.id.txt_tenmon_search);
            linearLayout = itemView.findViewById(R.id.ll_search);
            imgSearch = itemView.findViewById(R.id.img_anh_search);
        }
    }
}
