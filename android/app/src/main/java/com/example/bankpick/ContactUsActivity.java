package com.example.bankpick;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ContactUsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_us);
        

        ImageView ivBack = findViewById(R.id.btnBack);
        Button btnSend = findViewById(R.id.btnCallNow);

        ivBack.setOnClickListener(v -> finish());
        btnSend.setOnClickListener(v -> {
            Toast.makeText(this, "Message sent successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}

