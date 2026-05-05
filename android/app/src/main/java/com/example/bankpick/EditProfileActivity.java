package com.example.bankpick;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends BaseActivity {
    private EditText etFullName, etEmail, etPhone, etDay, etMonth, etYear;
    private android.widget.TextView tvProfileName;
    private ImageView ivBack;
    private Button btnSave;

    private String currentUserId;
    private ValueEventListener userListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        init();

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets
                        .getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        if (ivBack != null) {
            ivBack.setOnClickListener((v) -> finish());
        }

        // Load current user data from Firebase
        loadUserData();

        if (btnSave != null) {
            btnSave.setOnClickListener((v) -> saveProfile());
        }
    }

    private void loadUserData() {
        currentUserId = DatabaseHelper.getInstance().getCurrentUserId();
        if (currentUserId == null)
            return;

        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("fullName").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String phone = snapshot.child("phone").getValue(String.class);

                if (name != null && etFullName != null) {
                    etFullName.setText(name);
                    if (tvProfileName != null)
                        tvProfileName.setText(name);
                }
                if (email != null && etEmail != null)
                    etEmail.setText(email);
                if (phone != null && etPhone != null)
                    etPhone.setText(phone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        DatabaseHelper.getInstance().userRef(currentUserId).addListenerForSingleValueEvent(userListener);
    }

    private void saveProfile() {
        if (currentUserId == null)
            return;

        String name = etFullName.getText().toString().trim();
        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", name);
        updates.put("email", etEmail.getText().toString().trim());
        updates.put("phone", etPhone.getText().toString().trim());

        String cardId = currentUserId + "_card_001";
        DatabaseHelper.getInstance().cardRef(cardId).child("holderName").setValue(name);

        DatabaseHelper.getInstance().userRef(currentUserId).updateChildren(updates)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(
                        e -> Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userListener != null && currentUserId != null)
            DatabaseHelper.getInstance().userRef(currentUserId).removeEventListener(userListener);
    }

    private void init() {
        ivBack = findViewById(R.id.btnBack);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etDay = findViewById(R.id.etDay);
        etMonth = findViewById(R.id.etMonth);
        etYear = findViewById(R.id.etYear);
        tvProfileName = findViewById(R.id.tvProfileName);
        btnSave = findViewById(R.id.btnSave);
    }
}
