package com.example.bankpick;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    ImageView ivBack;
    TextView tvName, tvRole;
    private ValueEventListener userListener;
    private String currentUserId;

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

        loadUserFromFirebase();
    }

    private void loadUserFromFirebase() {
        currentUserId = DatabaseHelper.getInstance().getCurrentUserId();
        if (currentUserId == null) return;

        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("fullName").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                if (name != null && tvName != null) tvName.setText(name);
                if (tvRole != null) tvRole.setText(email != null ? email : "BankPick User");
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };
        DatabaseHelper.getInstance().userRef(currentUserId).addValueEventListener(userListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userListener != null && currentUserId != null)
            DatabaseHelper.getInstance().userRef(currentUserId).removeEventListener(userListener);
    }

    private void init() {
        ivBack = findViewById(R.id.ivBack);
        tvName = findViewById(R.id.tvName);
        tvRole = findViewById(R.id.tvRole);
    }
}
