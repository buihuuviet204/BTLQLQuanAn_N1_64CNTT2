package com.example.qunnbnhyn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class ActivityFragmentPopularFood extends Fragment {

    private Spinner spinnerTimePopular;
    private EditText etDatePickerPopular;
    private Button btnConfirmPopular;
    private LinearLayout llPopularList;

    public ActivityFragmentPopularFood() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_popular_food, container, false);

        spinnerTimePopular = view.findViewById(R.id.spinner_time_popular);
        etDatePickerPopular = view.findViewById(R.id.et_date_picker_popular);
        btnConfirmPopular = view.findViewById(R.id.btn_confirm_popular);
        llPopularList = view.findViewById(R.id.ll_popular_list);

        btnConfirmPopular.setOnClickListener(v -> updatePopularList());

        return view;
    }

    private void updatePopularList() {
        String dateInput = etDatePickerPopular.getText().toString().trim();
        int selectedOption = spinnerTimePopular.getSelectedItemPosition();

        if (dateInput.isEmpty()) {
            etDatePickerPopular.setError("Vui lòng nhập ngày/tháng");
            return;
        }

        llPopularList.removeAllViews();

        // Dữ liệu mẫu
        String[][] foods = (selectedOption == 0) ? // Theo ngày
                new String[][]{{"Phở bò", "5", "250,000"}, {"Bún chả", "4", "200,000"}} :
                new String[][]{{"Phở bò", "150", "7,500,000"}, {"Bún chả", "120", "6,000,000"}};

        for (String[] food : foods) {
            View foodView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, null);
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setPadding(8, 8, 8, 8);

            TextView tvName = new TextView(getContext());
            tvName.setText(food[0]);
            tvName.setTextSize(16);
            tvName.setTextColor(0xFF212121);
            tvName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvQuantity = new TextView(getContext());
            tvQuantity.setText(food[1]);
            tvQuantity.setTextSize(16);
            tvQuantity.setTextColor(0xFF757575);
            tvQuantity.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvRevenue = new TextView(getContext());
            tvRevenue.setText(food[2] + " VNĐ");
            tvRevenue.setTextSize(18);
            tvRevenue.setTextColor(0xFFFF5722);
            tvRevenue.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            layout.addView(tvName);
            layout.addView(tvQuantity);
            layout.addView(tvRevenue);
            llPopularList.addView(layout);
        }
    }
}