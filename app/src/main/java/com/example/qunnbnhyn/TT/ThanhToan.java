package com.example.qunnbnhyn.TT;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.qunnbnhyn.R;

public class ThanhToan extends AppCompatActivity implements View.OnClickListener {
    private CardView btnBan1, btnBan2, btnBan3, btnBan4, btnBan5, btnBan6, btnBan7, btnBan8, btnBan9, btnBan10, btnBan11, btnBan12, btnBan13, btnBan14, btnBan15;
    private ImageButton imbBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);

        btnBan1 = findViewById(R.id.btn_ban1);
        btnBan2 = findViewById(R.id.btn_ban2);
        btnBan3 = findViewById(R.id.btn_ban3);
        btnBan4 = findViewById(R.id.btn_ban4);
        btnBan5 = findViewById(R.id.btn_ban5);
        btnBan6 = findViewById(R.id.btn_ban6);
        btnBan7 = findViewById(R.id.btn_ban7);
        btnBan8 = findViewById(R.id.btn_ban8);
        btnBan9 = findViewById(R.id.btn_ban9);
        btnBan10 = findViewById(R.id.btn_ban10);
        btnBan11 = findViewById(R.id.btn_ban11);
        btnBan12 = findViewById(R.id.btn_ban12);
        btnBan13 = findViewById(R.id.btn_ban13);
        btnBan14 = findViewById(R.id.btn_ban14);
        btnBan15 = findViewById(R.id.btn_ban15);
        imbBack = findViewById(R.id.imb_back);
        imbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Thêm sự kiện nhấn cho từng bàn
        btnBan1.setOnClickListener(v -> openThanhToanChiTiet(1));
        btnBan2.setOnClickListener(v -> openThanhToanChiTiet(2));
        btnBan3.setOnClickListener(v -> openThanhToanChiTiet(3));
        btnBan4.setOnClickListener(v -> openThanhToanChiTiet(4));
        btnBan5.setOnClickListener(v -> openThanhToanChiTiet(5));
        btnBan6.setOnClickListener(v -> openThanhToanChiTiet(6));
        btnBan7.setOnClickListener(v -> openThanhToanChiTiet(7));
        btnBan8.setOnClickListener(v -> openThanhToanChiTiet(8));
        btnBan9.setOnClickListener(v -> openThanhToanChiTiet(9));
        btnBan10.setOnClickListener(v -> openThanhToanChiTiet(10));
        btnBan11.setOnClickListener(v -> openThanhToanChiTiet(11));
        btnBan12.setOnClickListener(v -> openThanhToanChiTiet(12));
        btnBan13.setOnClickListener(v -> openThanhToanChiTiet(13));
        btnBan14.setOnClickListener(v -> openThanhToanChiTiet(14));
        btnBan15.setOnClickListener(v -> openThanhToanChiTiet(15));

        // Thêm sự kiện nhấn cho từng bàn
        btnBan1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openThanhToanChiTiet(1); // Truyền số bàn 1
            }
        });

        btnBan2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openThanhToanChiTiet(2); // Truyền số bàn 2
            }
        });

        btnBan3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openThanhToanChiTiet(3); // Truyền số bàn 3
            }
        });

        btnBan4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openThanhToanChiTiet(4);
            }
        });

        btnBan5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openThanhToanChiTiet(5);
            }
        });

        btnBan6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openThanhToanChiTiet(6);
            }
        });

        btnBan7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openThanhToanChiTiet(7);
            }
        });

        btnBan8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openThanhToanChiTiet(8);
            }
        });

        btnBan9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openThanhToanChiTiet(9);
            }
        });

        btnBan10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openThanhToanChiTiet(10);
            }
        });

        btnBan11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openThanhToanChiTiet(11);
            }
        });

        btnBan12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openThanhToanChiTiet(12);
            }
        });

        btnBan13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openThanhToanChiTiet(13);
            }
        });

        btnBan14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openThanhToanChiTiet(14);
            }
        });

        btnBan15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openThanhToanChiTiet(15);
            }
        });
    }

    private void openThanhToanChiTiet(int soBan) {
        Intent intent = new Intent(this, ThanhToanChiTiet.class);
        intent.putExtra("SO_BAN", soBan); // Truyền số bàn qua Intent
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, ThanhToanChiTiet.class);
        int soBan = 0;

        if (v.getId() == R.id.btn_ban1) {
            soBan = 1;
        } else if (v.getId() == R.id.btn_ban2) {
            soBan = 2;
        } else if (v.getId() == R.id.btn_ban3) {
            soBan = 3;
        } else if (v.getId() == R.id.btn_ban4) {
            soBan = 4;
        } else if (v.getId() == R.id.btn_ban5) {
            soBan = 5;
        } else if (v.getId() == R.id.btn_ban6) {
            soBan = 6;
        } else if (v.getId() == R.id.btn_ban7) {
            soBan = 7;
        } else if (v.getId() == R.id.btn_ban8) {
            soBan = 8;
        } else if (v.getId() == R.id.btn_ban9) {
            soBan = 9;
        } else if (v.getId() == R.id.btn_ban10) {
            soBan = 10;
        } else if (v.getId() == R.id.btn_ban11) {
            soBan = 11;
        } else if (v.getId() == R.id.btn_ban12) {
            soBan = 12;
        } else if (v.getId() == R.id.btn_ban13) {
            soBan = 13;
        } else if (v.getId() == R.id.btn_ban14) {
            soBan = 14;
        } else if (v.getId() == R.id.btn_ban15) {
            soBan = 15;
        }

        intent.putExtra("soBan", soBan);
        startActivity(intent);
    }
}