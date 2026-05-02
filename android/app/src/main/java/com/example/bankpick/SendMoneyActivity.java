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

public class SendMoneyActivity extends AppCompatActivity {
    EditText etAmount;
    Button btnSend;
    ImageView ivBack;
    String selectedContact = "Yamilet"; // Default in TSX

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_send_money);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        ivBack.setOnClickListener((v) -> finish());

        btnSend.setOnClickListener((v) -> {
            String amountStr = etAmount.getText().toString().trim();
            if (amountStr.isEmpty()) { amountStr = "36.00"; } // Default TSX state

            Intent intent = new Intent(this, TransactionSuccessActivity.class);
            intent.putExtra("type", "Send Money");
            intent.putExtra("amount", amountStr);
            intent.putExtra("recipient", selectedContact);
            
            // TSX generates these
            intent.putExtra("date", java.text.DateFormat.getDateInstance(java.text.DateFormat.LONG).format(new java.util.Date()));
            intent.putExtra("time", java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT).format(new java.util.Date()));
            intent.putExtra("transactionId", "TRX" + System.currentTimeMillis());

            startActivity(intent);
        });
    }

    private void init() {
        ivBack = findViewById(R.id.ivBack);
        etAmount = findViewById(R.id.etAmount);
        btnSend = findViewById(R.id.btnSend);

        // Match TSX initial state
        etAmount.setText("36.00");
    }
}
