package com.example.bankpick;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends AppCompatActivity {
    ImageView ivBack;
    TextView tvName, tvRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        ivBack.setOnClickListener((v) -> finish());
        findViewById(R.id.btnPersonalInfo).setOnClickListener((v) -> startActivity(new Intent(this, EditProfileActivity.class)));
        findViewById(R.id.btnBanksCards).setOnClickListener((v) -> startActivity(new Intent(this, AllCardsActivity.class)));
        findViewById(R.id.btnNotifications).setOnClickListener((v) -> startActivity(new Intent(this, NotificationsActivity.class)));
        findViewById(R.id.btnSettings).setOnClickListener((v) -> finish());

        tvName.setText("Tanya Myroniuk");
        tvRole.setText("Senior Designer");
    }

    private void init() {
        ivBack = findViewById(R.id.ivBack);
        tvName = findViewById(R.id.tvName);
        tvRole = findViewById(R.id.tvRole);
    }
}
