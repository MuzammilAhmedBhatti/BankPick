package com.example.bankpick;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText[] otpFields;
    private Button btnVerify;
    private ImageView ivBack;
    private TextView tvResend;
    private String dummyOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        initViews();
        setupOtpFields();

        dummyOtp = getIntent().getStringExtra("dummyOtp");
        if (dummyOtp != null) {
            Toast.makeText(this, "Dummy OTP: " + dummyOtp, Toast.LENGTH_LONG).show();
        }

        ivBack.setOnClickListener(v -> finish());

        btnVerify.setOnClickListener(v -> {
            String enteredOtp = getEnteredOtp();
            if (enteredOtp.length() < 6) {
                Toast.makeText(this, "Please enter 6-digit code", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseHelper.getInstance().verifyOtp(enteredOtp, isCorrect -> {
                if (isCorrect) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvResend.setOnClickListener(v -> {
            DatabaseHelper.getInstance().sendOtp("user@example.com", new DatabaseHelper.OtpCallback() {
                @Override
                public void onSuccess(String otp) {
                    dummyOtp = otp;
                    Toast.makeText(OtpVerificationActivity.this, "New Dummy OTP: " + otp, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(OtpVerificationActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void initViews() {
        otpFields = new EditText[]{
                findViewById(R.id.etOtp1),
                findViewById(R.id.etOtp2),
                findViewById(R.id.etOtp3),
                findViewById(R.id.etOtp4),
                findViewById(R.id.etOtp5),
                findViewById(R.id.etOtp6)
        };
        btnVerify = findViewById(R.id.btnVerify);
        ivBack = findViewById(R.id.ivBack);
        tvResend = findViewById(R.id.tvResend);
    }

    private void setupOtpFields() {
        for (int i = 0; i < otpFields.length; i++) {
            final int index = i;
            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpFields.length - 1) {
                        otpFields[index + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0 && index > 0) {
                        otpFields[index - 1].requestFocus();
                    }
                }
            });
        }
    }

    private String getEnteredOtp() {
        StringBuilder sb = new StringBuilder();
        for (EditText field : otpFields) {
            sb.append(field.getText().toString());
        }
        return sb.toString();
    }
}
