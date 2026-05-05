package com.example.bankpick;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NotificationDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView btnBack = findViewById(R.id.btnBack);
        TextView tvIcon = findViewById(R.id.tvDetailIcon);
        View viewIconBg = findViewById(R.id.viewIconBg);
        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvTime = findViewById(R.id.tvDetailTime);
        TextView tvMessage = findViewById(R.id.tvDetailMessage);
        Button btnDone = findViewById(R.id.btnDone);

        // Get Data
        String title = getIntent().getStringExtra("title");
        String message = getIntent().getStringExtra("message");
        String time = getIntent().getStringExtra("time");
        String icon = getIntent().getStringExtra("icon");
        String colorStr = getIntent().getStringExtra("color");
        String notifId = getIntent().getStringExtra("id");

        tvTitle.setText(title);
        tvMessage.setText(message);
        tvTime.setText(time);
        tvIcon.setText(icon);

        // Set Icon Background Color
        GradientDrawable bgShape = (GradientDrawable) getResources().getDrawable(R.drawable.bg_circle_white_solid, null).mutate();
        if (colorStr != null) {
            switch (colorStr) {
                case "bg-green-100": bgShape.setColor(Color.parseColor("#DCFCE7")); break;
                case "bg-blue-100": bgShape.setColor(Color.parseColor("#DBEAFE")); break;
                case "bg-orange-100": bgShape.setColor(Color.parseColor("#FFEDD5")); break;
                case "bg-purple-100": bgShape.setColor(Color.parseColor("#F3E8FF")); break;
                case "bg-yellow-100": bgShape.setColor(Color.parseColor("#FEF9C3")); break;
                default: bgShape.setColor(Color.WHITE); break;
            }
        }
        viewIconBg.setBackground(bgShape);

        btnBack.setOnClickListener(v -> finish());
        btnDone.setOnClickListener(v -> finish());

        // Mark as read in DB if needed (optional since we have mark all read)
        if (notifId != null) {
            String uid = DatabaseHelper.getInstance().getCurrentUserId();
            if (uid != null) {
                DatabaseHelper.getInstance().userNotificationsRef(uid).child(notifId).child("unread").setValue(false);
            }
        }
    }
}
