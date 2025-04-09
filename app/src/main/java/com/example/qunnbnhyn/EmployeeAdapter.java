package com.example.qunnbnhyn;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {
    private List<Employee> employeeList; //danh sach nv hien thi
    private OnNhanVienClickListener listener;
    private Employee nhanVien;
    //private Context context;

    public EmployeeAdapter(List<Employee> employeeList, OnNhanVienClickListener listener) {

        this.employeeList = employeeList;
        this.listener = listener;
    }
    //tao view cho moi item trong danh sach
    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employee, parent, false);
        return new EmployeeViewHolder(view);//tra ve viewholder chua view vua tao
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = employeeList.get(position);
        //hien thi thong tin nhan vien
        holder.tvEmployeeId.setText("Mã NV: " + employee.getId());
        holder.tvEmployeeName.setText("Tên: " + employee.getName());
        holder.tvShift.setText("Ca làm: " + (employee.getShift() != null ? employee.getShift() : "Chưa gán"));

        holder.itemView.setOnClickListener(v -> listener.onNhanVienClick(nhanVien));
    }

    @Override
    public int getItemCount() {
        return employeeList.size(); //so luong nhan vien
    }
    
    interface OnNhanVienClickListener {
        void onNhanVienClick(Employee nhanVien); // Hàm được gọi khi nhấn, truyền vào nhân viên được chọn
    }
    
    static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmployeeName, tvEmployeeId, tvShift;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);
            tvEmployeeId = itemView.findViewById(R.id.tvEmployeeId);
            tvShift = itemView.findViewById(R.id.tvShift);
        }
    }
}
