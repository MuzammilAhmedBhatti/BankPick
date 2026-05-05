package com.example.bankpick;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText[] otpFields;
    private Button btnVerify;
    private ImageView ivBack;
    private TextView tvResend, tvDescription;
    private String dummyOtp;
    private CountDownTimer resendTimer;
    private boolean canResend = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        initViews();
        setupOtpFields();
        startResendTimer();

        dummyOtp = getIntent().getStringExtra("dummyOtp");
        if (dummyOtp != null) {
            // In a real app, this would be an email/SMS. 
            // We display it in a toast for this dummy implementation.
            Toast.makeText(this, "DEBUG: Your OTP is " + dummyOtp, Toast.LENGTH_LONG).show();
        }

        ivBack.setOnClickListener(v -> finish());

        btnVerify.setOnClickListener(v -> {
            String enteredOtp = getEnteredOtp();
            if (enteredOtp.length() < 4) {
                Toast.makeText(this, "Please enter 4-digit code", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseHelper.getInstance().verifyOtp(enteredOtp, isCorrect -> {
                if (isCorrect) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT).show();
                    clearOtpFields();
                }
            });
        });

        tvResend.setOnClickListener(v -> {
            if (canResend) {
                DatabaseHelper.getInstance().sendOtp(FirebaseAuth.getInstance().getCurrentUser().getEmail(), new DatabaseHelper.OtpCallback() {
                    @Override
                    public void onSuccess(String otp) {
                        dummyOtp = otp;
                        Toast.makeText(OtpVerificationActivity.this, "New OTP sent: " + otp, Toast.LENGTH_LONG).show();
                        startResendTimer();
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(OtpVerificationActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void initViews() {
        otpFields = new EditText[]{
                findViewById(R.id.etOtp1),
                findViewById(R.id.etOtp2),
                findViewById(R.id.etOtp3),
                findViewById(R.id.etOtp4)
        };
        btnVerify = findViewById(R.id.btnVerify);
        ivBack = findViewById(R.id.btnBack);
        tvResend = findViewById(R.id.tvResend);
        tvDescription = findViewById(R.id.tvOtpSubtitle);
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
                public void afterTextChanged(Editable s) {}
            });

            otpFields[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (otpFields[index].getText().toString().isEmpty() && index > 0) {
                        otpFields[index - 1].requestFocus();
                        otpFields[index - 1].setText("");
                        return true;
                    }
                }
                return false;
            });
        }
    }

    private void startResendTimer() {
        canResend = false;
        tvResend.setEnabled(false);
        tvResend.setTextColor(getResources().getColor(R.color.gray_400));
        
        if (resendTimer != null) resendTimer.cancel();
        
        resendTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvResend.setText(String.format(Locale.getDefault(), "Resend code in %02d:%02d", 
                    (millisUntilFinished / 1000) / 60, (millisUntilFinished / 1000) % 60));
            }

            @Override
            public void onFinish() {
                canResend = true;
                tvResend.setEnabled(true);
                tvResend.setText("Resend Code");
                tvResend.setTextColor(getResources().getColor(R.color.primary));
            }
        }.start();
    }

    private String getEnteredOtp() {
        StringBuilder sb = new StringBuilder();
        for (EditText field : otpFields) {
            sb.append(field.getText().toString());
        }
        return sb.toString();
    }

    private void clearOtpFields() {
        for (EditText field : otpFields) {
            field.setText("");
        }
        otpFields[0].requestFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resendTimer != null) resendTimer.cancel();
    }
}


