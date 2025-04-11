package com.example.qunnbnhyn.dondat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qunnbnhyn.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ListDonDatAdapter extends RecyclerView.Adapter<ListDonDatAdapter.ViewHolder> {
    List<ItemDonDat> listDonDat;

    public ListDonDatAdapter(List<ItemDonDat> listDonDat) {
        this.listDonDat = listDonDat;
    }

    public List<ItemDonDat> getListDonDat() {
        return listDonDat;
    }

    public void setListDonDat(List<ItemDonDat> listDonDat) {
        this.listDonDat = listDonDat;
    }

    @NonNull
    @Override
    public ListDonDatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ListDonDatAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcl_dondat,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListDonDatAdapter.ViewHolder holder, int position) {
        ItemDonDat item = listDonDat.get(position);
        ListMonAdapter listMonAdapter = new ListMonAdapter(item.getListMon());
        holder.rclListMon.setAdapter(listMonAdapter);
        holder.txtSoBan.setText("So ban: "+item.getSoBan());
        holder.btnPVu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("ban_an").child(item.getSoBan()).child("chi tiet order").removeValue()
                        .addOnSuccessListener(avoid -> {
                            Log.d("TB","Xoa thanh cong");
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return listDonDat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerView rclListMon;
       private TextView txtSoBan;
        private Button btnPVu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rclListMon = itemView.findViewById(R.id.rcl_listmon);
            txtSoBan = itemView.findViewById(R.id.txt_soban);
            btnPVu = itemView.findViewById(R.id.btn_phucvu);
            rclListMon.setLayoutManager(new LinearLayoutManager(itemView.getContext(),LinearLayoutManager.VERTICAL,false));
        }
    }
}
