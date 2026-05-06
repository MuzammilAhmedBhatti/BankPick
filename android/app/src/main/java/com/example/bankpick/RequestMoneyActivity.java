package com.example.bankpick;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RequestMoneyActivity extends BaseActivity {
    EditText etAmount, etEmail, etDescription;
    Button btnRequest;
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_request_money);

        init();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivBack.setOnClickListener((v) -> finish());

        btnRequest.setOnClickListener((v) -> {
            String amountStr = etAmount.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (email.isEmpty()) {
                etEmail.setError("Email is required");
                return;
            }

            if (amountStr.isEmpty()) {
                etAmount.setError("Amount is required");
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                etAmount.setError("Enter a valid amount");
                return;
            }

            if (amount <= 0) {
                etAmount.setError("Amount must be greater than 0");
                return;
            }

            btnRequest.setEnabled(false);
            DatabaseHelper.getInstance().createPaymentRequestByEmail(email, amount, description,
                    (success, requestId, receiverName, errorMessage) -> runOnUiThread(() -> {
                        btnRequest.setEnabled(true);
                        if (success) {
                            Intent intent = new Intent(this, TransactionSuccessActivity.class);
                            intent.putExtra("type", "Request Money");
                            intent.putExtra("amount", String.format(java.util.Locale.getDefault(), "%.2f", amount));
                            intent.putExtra("recipient", receiverName != null ? receiverName : email);
                            intent.putExtra("transactionId",
                                    requestId != null ? requestId : "REQ" + System.currentTimeMillis());
                            intent.putExtra("date", java.text.DateFormat.getDateInstance(java.text.DateFormat.LONG)
                                    .format(new java.util.Date()));
                            intent.putExtra("time", java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT)
                                    .format(new java.util.Date()));
                            startActivity(intent);
                            finish();
                        } else {
                            if (errorMessage != null && !errorMessage.isEmpty()) {
                                if (errorMessage.toLowerCase().contains("email")
                                        || errorMessage.toLowerCase().contains("user")) {
                                    etEmail.setError(errorMessage);
                                }
                                android.widget.Toast.makeText(this, errorMessage, android.widget.Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    }));
        });
    }

    private void init() {
        ivBack = findViewById(R.id.btnBack);
        etAmount = findViewById(R.id.etAmount);
        etEmail = findViewById(R.id.etEmail);
        etDescription = findViewById(R.id.etDescription);
        btnRequest = findViewById(R.id.btnRequestMoney);

        etAmount.setText("26.00");
    }
}
