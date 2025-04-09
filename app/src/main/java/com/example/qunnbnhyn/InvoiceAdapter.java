package com.example.qunnbnhyn;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class InvoiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_INVOICE = 1;

    private List<Object> invoiceList;
    private SimpleDateFormat dateFormat;

    public InvoiceAdapter(List<Object> invoiceList) {
        this.invoiceList = invoiceList;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
    }

    @Override
    public int getItemViewType(int position) {
        if (invoiceList.get(position) instanceof String) {
            return TYPE_HEADER;
        } else {
            return TYPE_INVOICE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice, parent, false);
            return new InvoiceViewHolder(view);
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            String date = (String) invoiceList.get(position);
            headerHolder.tvDate.setText(date);
        } else {
            InvoiceViewHolder invoiceHolder = (InvoiceViewHolder) holder;
            Invoice invoice = (Invoice) invoiceList.get(position);
            invoiceHolder.tvInvoiceId.setText("Mã hóa đơn: " + invoice.getId());
            invoiceHolder.tvMaKhach.setText("Mã khách hàng: "+invoice.getMaKhach());
            invoiceHolder.tvTongTien.setText("Tổng tiền: "+(String.format("%,d VNĐ", invoice.getTongTien())));
            invoiceHolder.tvPaymentMethod.setText("Phương thức thanh toán: "+invoice.getPaymentMethod());
            Long timestamp = invoice.getTimestamp();
            invoiceHolder.tvDate.setText("Thời gian: " + (timestamp != null ? dateFormat.format(timestamp) : "N/A"));
        }
    }

    @Override
    public int getItemCount() {
        return invoiceList.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date_header);
        }
    }

    static class InvoiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvInvoiceId, tvMaKhach, tvTongTien, tvPaymentMethod, tvDate;

        public InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInvoiceId = itemView.findViewById(R.id.tv_invoice_id);
            tvMaKhach = itemView.findViewById(R.id.tv_ma_khach);
            tvTongTien = itemView.findViewById(R.id.tv_tong_tien);
            tvPaymentMethod = itemView.findViewById(R.id.tv_payment_method);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }
}