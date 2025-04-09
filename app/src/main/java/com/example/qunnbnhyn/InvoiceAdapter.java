package com.example.qunnbnhyn;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder> {

    private List<Invoice> invoiceList;
    private SimpleDateFormat dateFormat;

    public InvoiceAdapter(List<Invoice> invoiceList) {
        this.invoiceList = invoiceList;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        this.dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice, parent, false);
        return new InvoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceViewHolder holder, int position) {
        Invoice invoice = invoiceList.get(position);
        holder.tvInvoiceId.setText("Mã hóa đơn: " + invoice.getId());
        holder.tvCustomerId.setText("Mã khách: " + (invoice.getMaKhach() != null && !invoice.getMaKhach().equals("NULL") ? invoice.getMaKhach() : "N/A"));
        holder.tvInvoiceDate.setText("Ngày: " + (invoice.getTimestamp() != null ? dateFormat.format(invoice.getTimestamp()) : "N/A"));
        holder.tvInvoiceAmount.setText("Tổng tiền: " + (invoice.getTongTien() != null ? String.format("%,d VNĐ", invoice.getTongTien()) : "N/A"));
    }

    @Override
    public int getItemCount() {
        return invoiceList.size();
    }

    static class InvoiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvInvoiceId, tvCustomerId, tvInvoiceDate, tvInvoiceAmount;

        public InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInvoiceId = itemView.findViewById(R.id.tv_invoice_id);
            tvCustomerId = itemView.findViewById(R.id.tv_customer_id);
            tvInvoiceDate = itemView.findViewById(R.id.tv_invoice_date);
            tvInvoiceAmount = itemView.findViewById(R.id.tv_invoice_amount);
        }
    }
}