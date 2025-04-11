package com.example.qunnbnhyn.QLM;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.utils.ObjectUtils;
import com.example.qunnbnhyn.MenuNVActivity;
import com.example.qunnbnhyn.R;
import com.example.qunnbnhyn.login.Login;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XoaMonAdapter extends RecyclerView.Adapter<XoaMonAdapter.ViewHolder>{
    List<MonAn> listMA;
    private Context context;

    public XoaMonAdapter(List<MonAn> listMA) {
        this.listMA = listMA;
    }
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(); // Luồng xử lý mạng

    public XoaMonAdapter(List<MonAn> listMA, Context context) {
        this.listMA = listMA;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcl_xoa, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MonAn monAn = listMA.get(position);
        holder.editGia.setText(monAn.getGiaban()+"");
        holder.editTen.setText(monAn.getName());
        holder.editLoai.setText(monAn.getLoai());
        Glide.with(holder.img)
                .load(monAn.getImageMonAn())
                .into(holder.img);
        holder.btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa món "+monAn.getName())
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String publicId = extractPublicId(monAn.getImageMonAn());
                                if (publicId != null) {
                                    executorService.execute(new Runnable() {
                                        @Override
                                        public void run() {

                                            try {


                                                Map deleteParams = ObjectUtils.asMap("invalidate", true );
                                                MediaManager.get().getCloudinary().uploader().destroy(publicId, deleteParams);
                                                Log.d("Cloudinary", "Đã xóa ảnh với public ID: " + publicId + " (executor)");
                                                // Nếu cần cập nhật UI, hãy sử dụng runOnUiThread
                                                // v.post(() -> { /* Cập nhật UI */ });
                                            } catch (IOException e) {
                                                Log.e("Cloudinary", "Lỗi xóa ảnh (executor): " + e.getMessage(), e);
                                                // Xử lý lỗi nếu cần
                                            }
                                        }
                                    });
                                }


                                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("thuc_don");
                                myRef.child(monAn.getMaMon()).removeValue()
                                        .addOnSuccessListener(aVoid -> {
                                            // Xóa thành công
                                            Log.d("Firebase", "Đã xóa người dùng user123");
                                        })
                                        .addOnFailureListener(e -> {
                                            // Xóa thất bại
                                            Log.e("Firebase", "Lỗi xóa người dùng", e);
                                        });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss(); // Đóng dialog, không làm gì
                            }
                        })
                        .show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return listMA.size();
    }

    static public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView img;
        private EditText editTen, editGia,editLoai;
        private Button btnXoa;
        private Uri newImageUri;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_monan);
            editTen = itemView.findViewById(R.id.edit_ten_mon);
            editGia = itemView.findViewById(R.id.edit_gia);
            editLoai = itemView.findViewById(R.id.edit_loai);
            btnXoa = itemView.findViewById(R.id.btn_xoa);
        }
    }
    private String extractPublicId(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) return null;

        String prefix = "https://res.cloudinary.com/dr94s8psw/image/upload/";
        if (!imageUrl.startsWith(prefix)) return null;

        // Loại bỏ prefix
        String path = imageUrl.replace(prefix, "");

        // Tách phần version (nếu có) và lấy publicId
        int versionIndex = path.indexOf("v");
        if (versionIndex == 0) {
            // Nếu có version, bỏ qua đến dấu "/"
            int slashIndex = path.indexOf("/");
            if (slashIndex != -1) {
                path = path.substring(slashIndex + 1);
            }
        }

        // Loại bỏ phần đuôi file (ví dụ: .jpg, .png)
        int lastDotIndex = path.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return path.substring(0, lastDotIndex);
        }

        return path; // Trả về public parejasId nếu không có đuôi file
    }
}
