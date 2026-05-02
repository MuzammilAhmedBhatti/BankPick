package com.example.bankpick;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TransactionDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaction_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView ivBack = findViewById(R.id.ivBack);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvCategory = findViewById(R.id.tvCategory);
        TextView tvAmount = findViewById(R.id.tvAmount);
        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvTime = findViewById(R.id.tvTime);
        TextView tvIcon = findViewById(R.id.tvIcon);

        ivBack.setOnClickListener((v) -> finish());

        String name = getIntent().getStringExtra("name");
        String category = getIntent().getStringExtra("category");
        double amount = getIntent().getDoubleExtra("amount", 0);
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");
        String icon = getIntent().getStringExtra("icon");

        tvName.setText(name != null ? name : "");
        tvCategory.setText(category != null ? category : "");
        tvDate.setText(date != null ? date : "");
        tvTime.setText(time != null ? time : "");

        if (amount > 0) {
            tvAmount.setText(String.format("+$%.2f", amount));
            tvAmount.setTextColor(getResources().getColor(R.color.primary, null));
        } else {
            tvAmount.setText(String.format("- $%.2f", Math.abs(amount)));
            tvAmount.setTextColor(getResources().getColor(R.color.red_500, null));
        }

        if (icon != null) {
            switch (icon) {
                case "apple": tvIcon.setText("🍎"); break;
                case "music": tvIcon.setText("🎵"); break;
                case "transfer": tvIcon.setText("💸"); break;
                case "grocery": tvIcon.setText("🛒"); break;
                default: tvIcon.setText("💰"); break;
            }
        }
    }
}
