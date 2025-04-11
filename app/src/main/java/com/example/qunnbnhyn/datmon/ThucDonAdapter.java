package com.example.qunnbnhyn.datmon;


import android.content.Context;
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
import java.util.HashMap;
import java.util.List;

public class ThucDonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<ItemThucDon> items;
    private OnChangeListener listener;
    private HashMap<String, Integer> ctdh;


    public ThucDonAdapter(List<ItemThucDon> items, OnChangeListener listener, HashMap<String, Integer> ctdh) {
        this.items = items;
        this.listener = listener;
        this.ctdh = ctdh;
    }

    public ThucDonAdapter(List<ItemThucDon> items, OnChangeListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public HashMap<String, Integer> getCtdh() {
        return ctdh;
    }

    public void setCtdh(HashMap<String, Integer> ctdh) {
        this.ctdh = ctdh;
    }

    public ThucDonAdapter(List<ItemThucDon> items) {
        this.items = (items != null) ? items : new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType(); // Xác định loại view
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ItemThucDon.TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_header, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == ItemThucDon.TYPE_LIST) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_mon_an_theo_loai, parent, false);
            return new ListViewHolder(view);
        }
        throw new IllegalArgumentException("Invalid view type");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemThucDon itemThucDon = items.get(position);
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.header.setText(itemThucDon.getLoai());
        } else if (holder instanceof ListViewHolder) {
            ListViewHolder listHolder = (ListViewHolder) holder;
            listHolder.bind(itemThucDon.getListmon(),listener,ctdh);


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
        }

        public void bind(List<MonAn> listmon, OnChangeListener listener, HashMap<String, Integer> ctdh) {
            adapter = new ListMonAdapter(listmon, listener, ctdh);
            ListMon.setAdapter(adapter);
        }
    }
}
