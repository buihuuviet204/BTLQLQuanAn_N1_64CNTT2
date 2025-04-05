package com.example.qunnbnhyn;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private List<Customer> customerList;
    private OnCustomerClickListener listener;

    public CustomerAdapter(List<Customer> customerList, OnCustomerClickListener listener) {
        this.customerList = customerList;
        this.listener = listener;
    }

    @Override
    public CustomerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomerViewHolder holder, int position) {
        Customer customer = customerList.get(position);
        holder.tvName.setText(customer.getName());
        holder.tvPhone.setText(customer.getPhoneNumber());
        holder.itemView.setOnClickListener(v -> listener.onCustomerClick(customer));
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone;

        public CustomerViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCustomerName);
            tvPhone = itemView.findViewById(R.id.tvPhoneNumber);
        }
    }

    interface OnCustomerClickListener {
        void onCustomerClick(Customer customer);
    }
}