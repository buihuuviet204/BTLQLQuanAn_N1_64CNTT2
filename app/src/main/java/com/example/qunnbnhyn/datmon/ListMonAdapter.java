package com.example.qunnbnhyn.datmon;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.qunnbnhyn.QLM.MonAn;
import com.example.qunnbnhyn.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ListMonAdapter extends RecyclerView.Adapter<ListMonAdapter.ListMonViewHolder> {

    public List<MonAn> listmon;
    public OnTotalChangeListener listener;
    private HashMap<MonAn, EditText> dsMongoi = new HashMap<>();
    private List<EditText> allEditTexts = new ArrayList<>();

    public ListMonAdapter(List<MonAn> listmon, OnTotalChangeListener listener) {
        this.listmon = listmon;
        this.listener = listener;
    }

    public ListMonAdapter(List<MonAn> listmon) {
        this.listmon = listmon;
    }
    @NonNull
    @Override
    public ListMonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from((parent.getContext())).inflate(R.layout.item,parent,false);
        return new ListMonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListMonViewHolder holder, int position) {
        MonAn monAn = listmon.get(position);
        holder.txtGiaBan.setText("Gia ban: " + monAn.getGiaban());
        holder.txtTenMon.setText(monAn.getName());
        Log.d("ListMonAdapter", "Đường dẫn ảnh cho " + monAn.getName() + ": " + monAn.getImageMonAn());
        Glide.with(holder.imgMonAn.getContext())
                .load(monAn.getImageMonAn())
                .into(holder.imgMonAn);
        HashMap<String, Integer> monGoi = new HashMap<>();
        dsMongoi.put(monAn,holder.editSoluong);
        holder.editSoluong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateTotal();
                HashMap<MonAn,Integer> dsmongoii = new HashMap<>();
                HashMap<String, Integer> listMonGoi = new HashMap<>();
                List<EditText> listtext = new LinkedList<>();
                for(MonAn key : dsMongoi.keySet()){
                    String sluong = dsMongoi.get(key).getText().toString();
                    if(!sluong.isEmpty()){
                        dsmongoii.put(key,Integer.parseInt(sluong));
                        listMonGoi.put(key.getMaMon().toString(),Integer.parseInt(sluong));
                    }
                }
                if(listener != null)    listener.onSlgChanged(listMonGoi,holder.editSoluong);

            }
        });
    }
    @Override
    public int getItemCount() {
        return listmon.size();
    }

    public static class ListMonViewHolder extends RecyclerView.ViewHolder{
        public ImageView imgMonAn;
        public TextView txtTenMon;
        public TextView txtGiaBan;
        private EditText editSoluong;
        private TextView txtTongtien;
        public ListMonViewHolder(View itemView){
            super(itemView);
            txtTenMon = itemView.findViewById(R.id.txt_tenmon);
            txtGiaBan = itemView.findViewById(R.id.txt_giaban);
            imgMonAn = itemView.findViewById(R.id.img_monan);
            editSoluong = itemView.findViewById(R.id.edit_soluong);
            txtTongtien = itemView.findViewById(R.id.txt_tongtien);
        }
    }
//    private void updataCTHD(){
//        if(listener != null)    listener.onTotalChanged(dsMongoi);
//    }
    private void updateTotal() {
        double total = 0;
        for (EditText editText : allEditTexts) {
            String text = editText.getText().toString().trim();
            if (!text.isEmpty()) {
                try {
                    total += Double.parseDouble(text);
                } catch (NumberFormatException e) {
                    // Bỏ qua nếu không phải số hợp lệ
                }
            }
        }
        if (listener != null) {
            listener.onTotalChanged(total); // Gửi tổng ra ngoài
        }
    }
    public void clearAllEditText() {

        notifyDataSetChanged();
    }
    private void updateDSMon(){

    }
}