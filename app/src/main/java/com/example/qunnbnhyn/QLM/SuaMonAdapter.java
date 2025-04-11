package com.example.qunnbnhyn.QLM;

import static android.app.Activity.RESULT_OK;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.utils.ObjectUtils;
import com.example.qunnbnhyn.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SuaMonAdapter extends RecyclerView.Adapter<SuaMonAdapter.ViewHolder> {
    private List<MonAn> listMon;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(); // Luồng xử lý mạng

    public SuaMonAdapter(List<MonAn> listMon) {
        this.listMon = listMon;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rcl_sua, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MonAn monAn = listMon.get(position);
        holder.editGia.setText(String.valueOf(monAn.getGiaban()));
        holder.editTen.setText(monAn.getName());

        String[] options = {"Mi Kay", "Tra sua", "Tra hoa qua", "Nuoc co ga", "Do an vat", "Combo"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(holder.itemView.getContext(),android.R.layout.simple_spinner_dropdown_item, options);
        holder.spinner.setAdapter(spinnerAdapter);
        holder.spinner.setSelection(getSpinnerIndex(monAn.getLoai(), options));
        holder.spinner.setEnabled(false);
        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view instanceof TextView) {
                    Typeface myCustomFont = ResourcesCompat.getFont(holder.itemView.getContext(), R.font.comfortaa);
                    ((TextView) view).setTypeface(myCustomFont);
                    ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Glide.with(holder.img)
                .load(monAn.getImageMonAn())
                .into(holder.img);

        ActivityResultLauncher<Intent> imgPickerLauncher = holder.getImagePickerLauncher();

        holder.btnSua.setOnClickListener(v -> {
            holder.btnLuu.setVisibility(View.VISIBLE);
            holder.editTen.setEnabled(true);
            holder.editGia.setEnabled(true);
            holder.btnAnh.setVisibility(View.VISIBLE);
            holder.spinner.setEnabled(true);
        });

        holder.btnAnh.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imgPickerLauncher.launch(intent);
        });

        holder.btnLuu.setOnClickListener(v -> {
            String tenMoi = holder.editTen.getText().toString().trim();
            String giaMoiStr = holder.editGia.getText().toString().trim();
            String loaiMoi = holder.spinner.getSelectedItem().toString();

            if (tenMoi.isEmpty() || giaMoiStr.isEmpty()) {
                Toast.makeText(holder.itemView.getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            double giaMoi;
            try {
                giaMoi = Double.parseDouble(giaMoiStr);
            } catch (NumberFormatException e) {
                Toast.makeText(holder.itemView.getContext(), "Giá phải là số", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("thuc_don");

            executorService.execute(() -> {
                try {
                    String publicId = extractPublicId(monAn.getImageMonAn());
                    if (publicId != null && holder.newImageUri != null) {
                        Map result = MediaManager.get().getCloudinary().uploader().destroy(publicId, ObjectUtils.emptyMap());
                        if (!"ok".equals(result.get("result"))) {
                            throw new IOException("Xóa ảnh thất bại: " + result);
                        }
                        Log.d("Cloudinary", "Xóa ảnh thành công: " + publicId);
                    }

                    if (holder.newImageUri == null) {
                        MonAn updatedMonAn = new MonAn(tenMoi, monAn.getImageMonAn(), giaMoi, loaiMoi);
                        updateFirebase(myRef, monAn.getMaMon(), updatedMonAn, holder);
                    } else {
                        // Tải ảnh mới lên Cloudinary
                        MediaManager.get().upload(holder.newImageUri)
                                .option("folder", "BTLON")
                                .callback(new UploadCallback() {
                                    @Override
                                    public void onStart(String requestId) {
                                        Log.d("Cloudinary", "Bắt đầu tải ảnh lên...");
                                    }

                                    @Override
                                    public void onProgress(String requestId, long bytes, long totalBytes) {
                                        // Có thể hiển thị tiến trình nếu cần
                                    }

                                    @Override
                                    public void onSuccess(String requestId, Map resultData) {
                                        String url = (String) resultData.get("url").toString().replace("http","https");
                                        MonAn updatedMonAn = new MonAn(tenMoi, url, giaMoi, loaiMoi);
                                        updateFirebase(myRef, monAn.getMaMon(), updatedMonAn, holder);
                                    }

                                    @Override
                                    public void onError(String requestId, ErrorInfo error) {
                                        runOnUiThread(holder, () -> Toast.makeText(holder.itemView.getContext(),
                                                "Lỗi tải ảnh: " + error.getDescription(), Toast.LENGTH_LONG).show());
                                    }

                                    @Override
                                    public void onReschedule(String requestId, ErrorInfo error) {
                                        Log.w("Cloudinary", "Tải ảnh bị hoãn lại: " + error.getDescription());
                                    }
                                }).dispatch();
                    }
                } catch (IOException e) {
                    Log.e("Cloudinary", "Lỗi khi xóa ảnh: " + e.getMessage());
                    runOnUiThread(holder, () -> Toast.makeText(holder.itemView.getContext(),
                            "Lỗi xóa ảnh: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return listMon.size();
    }

    // Cập nhật dữ liệu lên Firebase và chạy trên UI thread
    private void updateFirebase(DatabaseReference myRef, String maMon, MonAn updatedMonAn, ViewHolder holder) {
        myRef.child(maMon).setValue(updatedMonAn)
                .addOnSuccessListener(aVoid -> runOnUiThread(holder, () -> {
                    Toast.makeText(holder.itemView.getContext(), "Sửa thành công", Toast.LENGTH_LONG).show();
                    holder.btnLuu.setVisibility(View.GONE);
                    holder.editTen.setEnabled(false);
                    holder.editGia.setEnabled(false);
                    holder.btnAnh.setVisibility(View.GONE);
                    holder.spinner.setEnabled(false);
                    holder.newImageUri = null;
                }))
                .addOnFailureListener(e -> runOnUiThread(holder, () ->
                        Toast.makeText(holder.itemView.getContext(), "Sửa thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show()));
    }

    // Chạy code trên UI thread
    private void runOnUiThread(ViewHolder holder, Runnable action) {
        ((FragmentActivity) holder.itemView.getContext()).runOnUiThread(action);
    }

    private int getSpinnerIndex(String loai, String[] options) {
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(loai)) {
                return i;
            }
        }
        return 0;
    }

    private String extractPublicId(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) return null;

        String prefix = "https://res.cloudinary.com/dr94s8psw/image/upload/";
        if (!imageUrl.startsWith(prefix)) return null;

        String path = imageUrl.replace(prefix, "");

        int versionIndex = path.indexOf("v");
        if (versionIndex == 0) {
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private EditText editTen, editGia;
        private Spinner spinner;
        private Button btnSua, btnLuu, btnAnh;
        private Uri newImageUri;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_monan);
            editTen = itemView.findViewById(R.id.edit_ten_mon);
            editGia = itemView.findViewById(R.id.edit_gia);
            spinner = itemView.findViewById(R.id.spinner_loai);
            btnLuu = itemView.findViewById(R.id.btn_luu);
            btnSua = itemView.findViewById(R.id.btn_sua);
            btnAnh = itemView.findViewById(R.id.btn_anh);
        }

        private ActivityResultLauncher<Intent> getImagePickerLauncher() {
            return ((FragmentActivity) itemView.getContext()).getActivityResultRegistry()
                    .register("imagePicker_" + hashCode(), new ActivityResultContracts.StartActivityForResult(),
                            result -> {
                                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                    newImageUri = result.getData().getData();
                                    Glide.with(itemView.getContext())
                                            .load(newImageUri)
                                            .into(img);
                                }
                            });
        }
    }
}