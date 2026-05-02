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

public class SignInActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnSignIn;
    TextView tvSignUp;
    ImageView ivBack, ivTogglePassword;
    ProgressBar progressBar;
    boolean passwordVisible = false;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, ime.bottom > 0 ? ime.bottom : systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        init();

        ivBack.setOnClickListener((v) -> {
            startActivity(new Intent(this, OnboardingActivity.class));
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

        btnSignIn.setOnClickListener((v) -> attemptSignIn());

        tvSignUp.setOnClickListener((v) -> {
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        });
    }

    private void attemptSignIn() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // ── Validation ────────────────────────────────────────────────────
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        // ── Show progress ─────────────────────────────────────────────────
        btnSignIn.setEnabled(false);
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        // ── Firebase Auth sign in ─────────────────────────────────────────
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        btnSignIn.setEnabled(true);
                        if (progressBar != null) progressBar.setVisibility(View.GONE);

                        String error = task.getException() != null
                                ? task.getException().getMessage()
                                : "Sign in failed";
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void init() {
        etEmail          = findViewById(R.id.etEmail);
        etPassword       = findViewById(R.id.etPassword);
        btnSignIn        = findViewById(R.id.btnSignIn);
        tvSignUp         = findViewById(R.id.tvSignUp);
        ivBack           = findViewById(R.id.ivBack);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        progressBar      = findViewById(R.id.progressBarSignIn);

        // Clear defaults — user enters their credentials
        etEmail.setText("");
        etPassword.setText("");
    }
}
