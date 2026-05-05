package com.example.bankpick;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RequestMoneyActivity extends BaseActivity {
    EditText etAmount, etPayerName, etEmail, etDescription, etDay, etMonth, etYear;
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
            if (amountStr.isEmpty()) { amountStr = "26.00"; }
            
            String payerName = etPayerName.getText().toString().trim();
            if (payerName.isEmpty()) payerName = "Unknown";

            Intent intent = new Intent(this, TransactionSuccessActivity.class);
            intent.putExtra("type", "Request Money");
            intent.putExtra("amount", amountStr);
            intent.putExtra("recipient", payerName);

            intent.putExtra("date", java.text.DateFormat.getDateInstance(java.text.DateFormat.LONG).format(new java.util.Date()));
            intent.putExtra("time", java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT).format(new java.util.Date()));
            intent.putExtra("transactionId", "TRX" + System.currentTimeMillis());

            startActivity(intent);
        });
    }

    private void init() {
        ivBack = findViewById(R.id.btnBack);
        etAmount = findViewById(R.id.etAmount);
        etPayerName = findViewById(R.id.etPayerName);
        etEmail = findViewById(R.id.etEmail);
        etDescription = findViewById(R.id.etDescription);
        etDay = findViewById(R.id.etDay);
        etMonth = findViewById(R.id.etMonth);
        etYear = findViewById(R.id.etYear);
        btnRequest = findViewById(R.id.btnRequestMoney);

        etAmount.setText("26.00");
    }
}
