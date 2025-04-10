package com.example.qunnbnhyn.QLM;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.qunnbnhyn.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ThemMonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThemMonFragment extends Fragment {
    private   ActivityResultLauncher<Intent> imgpickerlauncher;
    private Uri imageUri;
    private ImageButton imgbt;
    private static final String TAG = "AddFragment";
    String[] options = {"Mi Kay","Tra sua","Tra hoa qua","Nuoc co ga","Do an vat","Combo"};
    private String url;

    private Spinner spinner;
    private EditText editTextTenMon;
    private EditText editTextGiaMon;
    private Button btnThemMon;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ThemMonFragment() {
    }


    public static ThemMonFragment newInstance(String param1, String param2) {
        ThemMonFragment fragment = new ThemMonFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_them_mon, container, false);
        editTextGiaMon = view.findViewById(R.id.edit_giaban);
        editTextTenMon = view.findViewById(R.id.edit_ten);
        btnThemMon = view.findViewById(R.id.btn_them);
        spinner = view.findViewById(R.id.spinneradd);

        myRef = FirebaseDatabase.getInstance().getReference("thuc_don");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, options);
        spinner.setAdapter(adapter);
        imgbt = view.findViewById(R.id.img_bt);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view instanceof TextView) {
                    Typeface myCustomFont = ResourcesCompat.getFont(requireContext(), R.font.comfortaa);
                    ((TextView) view).setTypeface(myCustomFont);
                }
                // Xử lý item được chọn
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì
            }
        });

        imgpickerlauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null){
                        imageUri = result.getData().getData();
                        Glide.with(view.getContext())
                                .load(imageUri)
                                .into(imgbt);
                    }
                });

        imgbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        btnThemMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upLoad(view);
            }
        });
        return view;
    }
    private void themMon(){

        String tenMon = editTextTenMon.getText().toString().trim();
        String giaMonStr = editTextGiaMon.getText().toString().trim();
        String loaiMon = spinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(tenMon)) {
            editTextTenMon.setError("Vui lòng nhập tên món");
            return;
        }
        if (TextUtils.isEmpty(giaMonStr)) {
            editTextGiaMon.setError("Vui lòng nhập giá món");
            return;
        }

        try {
            double giaMon = Double.parseDouble(giaMonStr);
            url = url.replace("http","https");
            MonAn monAn = new MonAn(tenMon, url, giaMon, loaiMon);
            myRef.push().setValue(monAn)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), "Thêm thành công", Toast.LENGTH_LONG).show();
                        editTextTenMon.setText("");
                        editTextGiaMon.setText("");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Thêm thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } catch (NumberFormatException e) {
            editTextGiaMon.setError("Giá món phải là một số");
            Toast.makeText(requireContext(), "Thêm thất bại: Giá món phải là số", Toast.LENGTH_LONG).show();
        }
    }
    private void upLoad(View view){
        MediaManager.get().upload(imageUri)
                .option("folder","BTLON")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Toast.makeText(view.getContext(), "Đang upload...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {

                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        url = (String) resultData.get("url");
                        Log.d("URL: ",url);
                        themMon();
                        imgbt.setImageDrawable(null);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(view.getContext(), "Lỗi: " + error.getDescription(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {

                    }
                })
                .dispatch();
    }
    private void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imgpickerlauncher.launch(intent);
    }
}