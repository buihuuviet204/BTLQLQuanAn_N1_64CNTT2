package com.example.qunnbnhyn;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PriorityCustomerAdapter extends RecyclerView.Adapter<PriorityCustomerAdapter.PriorityViewHolder> {

    private List<Customer> customerList;

    public PriorityCustomerAdapter(List<Customer> customerList) {
        this.customerList = customerList;
    }

    @Override
    public PriorityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_priority_customer, parent, false);
        return new PriorityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PriorityViewHolder holder, int position) {
        Customer customer = customerList.get(position);
        holder.tvName.setText(customer.getName());
        holder.tvPhone.setText(customer.getPhoneNumber());
        holder.tvVisitCount.setText(String.valueOf(customer.getVisitCount()));
        holder.tvPoints.setText(String.valueOf(customer.getPoints()));
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    static class PriorityViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvVisitCount, tvPoints;

        public PriorityViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCustomerName);
            tvPhone = itemView.findViewById(R.id.tvPhoneNumber);
            tvVisitCount = itemView.findViewById(R.id.tvVisitCount);
            tvPoints = itemView.findViewById(R.id.tvPoints);
        }
    }
}