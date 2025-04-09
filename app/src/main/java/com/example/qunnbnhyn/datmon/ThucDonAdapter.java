package com.example.qunnbnhyn.datmon;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qunnbnhyn.QLM.MonAn;
import com.example.qunnbnhyn.R;

import java.util.ArrayList;
import java.util.List;

public class ThucDonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<ListMon> items;
    private OnTotalChangeListener listener;

    public ThucDonAdapter(List<ListMon> items, OnTotalChangeListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public ThucDonAdapter(List<ListMon> items) {
        this.items = (items != null) ? items : new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType(); // Xác định loại view
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ListMon.TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_header, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == ListMon.TYPE_LIST) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_mon_an_theo_loai, parent, false);
            return new ListViewHolder(view);
        }
        throw new IllegalArgumentException("Invalid view type");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListMon listMon = items.get(position);
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.header.setText(listMon.getLoai());
        } else if (holder instanceof ListViewHolder) {
            ListViewHolder listHolder = (ListViewHolder) holder;
            listHolder.bind(listMon.getListmon(),listener);


        }
    }

    @Override
    public int getItemCount() {
        return (items != null) ? items.size() : 0;
    }


    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView header;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            this.header = itemView.findViewById(R.id.headerText);
        }
    }


    public static class ListViewHolder extends RecyclerView.ViewHolder {
        public RecyclerView ListMon;
        private ListMonAdapter adapter;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            ListMon = itemView.findViewById(R.id.subRecyclerView);
            ListMon.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            adapter = new ListMonAdapter(null);
            ListMon.setAdapter(adapter);
        }


        public void bind(List<MonAn> listmon, OnTotalChangeListener listener) {
            adapter.listmon = listmon;
            adapter.listener = listener;
        }
    }
}
