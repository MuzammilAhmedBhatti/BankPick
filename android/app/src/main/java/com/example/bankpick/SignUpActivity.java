package com.example.bankpick;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    EditText etFullName, etPhone, etEmail, etPassword, etConfirmPassword;
    Button btnSignUp;
    TextView tvSignIn;
    ImageView ivBack, ivTogglePassword;
    ProgressBar progressBar;
    boolean passwordVisible = false;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, ime.bottom > 0 ? ime.bottom : systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        init();

        ivBack.setOnClickListener((v) -> {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        });

        ivTogglePassword.setOnClickListener((v) -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                etPassword.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        btnSignUp.setOnClickListener((v) -> attemptSignUp());

        tvSignIn.setOnClickListener((v) -> {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        });
    }

    private void attemptSignUp() {
        String fullName = etFullName.getText().toString().trim();
        String phone    = etPhone.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // ── Validation ────────────────────────────────────────────────────
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Name is required");
            etFullName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        // ── Show progress ─────────────────────────────────────────────────
        btnSignUp.setEnabled(false);
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        // ── Firebase Auth create user ─────────────────────────────────────
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();

                            // Provision user data in Realtime Database
                            DatabaseHelper.getInstance()
                                    .createNewUser(uid, fullName, email, phone);

                            Toast.makeText(this,
                                    "Welcome, " + fullName + "!",
                                    Toast.LENGTH_SHORT).show();

                            // Go to main screen
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        btnSignUp.setEnabled(true);
                        if (progressBar != null) progressBar.setVisibility(View.GONE);

                        String error = task.getException() != null
                                ? task.getException().getMessage()
                                : "Sign up failed";
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void init() {
        etFullName       = findViewById(R.id.etFullName);
        etPhone          = findViewById(R.id.etPhone);
        etEmail          = findViewById(R.id.etEmail);
        etPassword       = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp        = findViewById(R.id.btnSignUp);
        tvSignIn         = findViewById(R.id.tvSignIn);
        ivBack           = findViewById(R.id.ivBack);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        progressBar      = findViewById(R.id.progressBarSignUp);

        // Clear defaults — let user enter their own details
        etFullName.setText("");
        etPhone.setText("");
        etEmail.setText("");
        etPassword.setText("");
        etConfirmPassword.setText("");
    }
}
