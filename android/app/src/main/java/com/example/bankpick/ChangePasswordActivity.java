package com.example.bankpick;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText etCurrent, etNew, etConfirm;
    Button btnSave;
    ImageView ivTogglePassword;
    ProgressBar progressBar;
    boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);

        ImageView ivBack = findViewById(R.id.btnBack);
        etCurrent = findViewById(R.id.etCurrentPassword);
        etNew = findViewById(R.id.etNewPassword);
        etConfirm = findViewById(R.id.etConfirmPassword);
        btnSave = findViewById(R.id.btnChangePassword);
        ivTogglePassword = findViewById(R.id.ivToggleNewPassword);
        progressBar = findViewById(R.id.progressBar);

        // Apply window insets for soft keyboard
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivBack.setOnClickListener(v -> finish());

        if (ivTogglePassword != null) {
            ivTogglePassword.setOnClickListener(v -> {
                passwordVisible = !passwordVisible;
                if (passwordVisible) {
                    etNew.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    etNew.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                etNew.setSelection(etNew.getText().length());
            });
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> updatePassword());
        }
    }

    private void updatePassword() {
        String currentPwd = etCurrent.getText().toString().trim();
        String newPwd = etNew.getText().toString().trim();
        String confirmPwd = etConfirm.getText().toString().trim();

        if (currentPwd.isEmpty()) { etCurrent.setError("Current password is required"); etCurrent.requestFocus(); return; }
        if (newPwd.isEmpty() || newPwd.length() < 6) { etNew.setError("New password must be at least 6 characters"); etNew.requestFocus(); return; }
        if (!newPwd.equals(confirmPwd)) { etConfirm.setError("Passwords do not match"); etConfirm.requestFocus(); return; }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getEmail() == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPwd);
        
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user.updatePassword(newPwd).addOnCompleteListener(updateTask -> {
                    btnSave.setEnabled(true);
                    if (progressBar != null) progressBar.setVisibility(View.GONE);

                    if (updateTask.isSuccessful()) {
                        Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        String err = updateTask.getException() != null ? updateTask.getException().getMessage() : "Failed to update password";
                        Toast.makeText(this, err, Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                btnSave.setEnabled(true);
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                
                String err = task.getException() != null ? task.getException().getMessage() : "Authentication failed";
                Toast.makeText(this, "Current password incorrect: " + err, Toast.LENGTH_LONG).show();
            }
        });
    }
}
