package com.example.bankpick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TopupActivity extends AppCompatActivity {
    EditText etAmount;
    Button btnTopup;
    ImageView ivBack;
    
    // TSX Default
    String selectedMethod = "Credit/Debit Card";

    TextView btnQuick50, btnQuick100, btnQuick200, btnQuick500;
    LinearLayout llMethodCard, llMethodBank, llMethodWallet, llMethodPaypal;
    ImageView ivCheckCard, ivCheckBank, ivCheckWallet, ivCheckPaypal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_topup);
        
        init();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivBack.setOnClickListener((v) -> finish());

        setupQuickSelect();
        setupPaymentMethods();

        btnTopup.setOnClickListener((v) -> {
            String amountStr = etAmount.getText().toString().trim();
            if (amountStr.isEmpty()) { amountStr = "100.00"; } // TSX Default

            Intent intent = new Intent(this, TransactionSuccessActivity.class);
            intent.putExtra("type", "Top Up");
            intent.putExtra("amount", amountStr);
            intent.putExtra("recipient", selectedMethod);

            intent.putExtra("date", java.text.DateFormat.getDateInstance(java.text.DateFormat.LONG).format(new java.util.Date()));
            intent.putExtra("time", java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT).format(new java.util.Date()));
            intent.putExtra("transactionId", "TRX" + System.currentTimeMillis());

            startActivity(intent);
        });
    }

    private void setupQuickSelect() {
        View.OnClickListener listener = v -> {
            resetQuickSelect();
            v.setBackgroundResource(R.drawable.bg_rounded_rect_blue_light);
            ((TextView) v).setTextColor(getResources().getColor(R.color.primary, null));
            if (v == btnQuick50) etAmount.setText("50.00");
            else if (v == btnQuick100) etAmount.setText("100.00");
            else if (v == btnQuick200) etAmount.setText("200.00");
            else if (v == btnQuick500) etAmount.setText("500.00");
        };
        btnQuick50.setOnClickListener(listener);
        btnQuick100.setOnClickListener(listener);
        btnQuick200.setOnClickListener(listener);
        btnQuick500.setOnClickListener(listener);
    }

    private void resetQuickSelect() {
        btnQuick50.setBackgroundResource(R.drawable.bg_rounded_rect); btnQuick50.setTextColor(getResources().getColor(R.color.text_primary, null));
        btnQuick100.setBackgroundResource(R.drawable.bg_rounded_rect); btnQuick100.setTextColor(getResources().getColor(R.color.text_primary, null));
        btnQuick200.setBackgroundResource(R.drawable.bg_rounded_rect); btnQuick200.setTextColor(getResources().getColor(R.color.text_primary, null));
        btnQuick500.setBackgroundResource(R.drawable.bg_rounded_rect); btnQuick500.setTextColor(getResources().getColor(R.color.text_primary, null));
    }

    private void setupPaymentMethods() {
        View.OnClickListener listener = v -> {
            resetPaymentMethods();
            v.setBackgroundResource(R.drawable.bg_rounded_rect_blue_light);
            if (v == llMethodCard) { ivCheckCard.setImageResource(R.drawable.bg_circle_blue); selectedMethod = "Credit/Debit Card"; }
            else if (v == llMethodBank) { ivCheckBank.setImageResource(R.drawable.bg_circle_blue); selectedMethod = "Bank Transfer"; }
            else if (v == llMethodWallet) { ivCheckWallet.setImageResource(R.drawable.bg_circle_blue); selectedMethod = "Digital Wallet"; }
            else if (v == llMethodPaypal) { ivCheckPaypal.setImageResource(R.drawable.bg_circle_blue); selectedMethod = "PayPal"; }
        };
        llMethodCard.setOnClickListener(listener);
        llMethodBank.setOnClickListener(listener);
        llMethodWallet.setOnClickListener(listener);
        llMethodPaypal.setOnClickListener(listener);
    }

    private void resetPaymentMethods() {
        llMethodCard.setBackgroundResource(R.drawable.bg_rounded_rect); ivCheckCard.setImageResource(R.drawable.bg_circle);
        llMethodBank.setBackgroundResource(R.drawable.bg_rounded_rect); ivCheckBank.setImageResource(R.drawable.bg_circle);
        llMethodWallet.setBackgroundResource(R.drawable.bg_rounded_rect); ivCheckWallet.setImageResource(R.drawable.bg_circle);
        llMethodPaypal.setBackgroundResource(R.drawable.bg_rounded_rect); ivCheckPaypal.setImageResource(R.drawable.bg_circle);
    }

    private void init() {
        ivBack = findViewById(R.id.btnBack);
        etAmount = findViewById(R.id.etAmount);
        btnTopup = findViewById(R.id.btnTopup);

        btnQuick50 = findViewById(R.id.btn50);
        btnQuick100 = findViewById(R.id.btn100);
        btnQuick200 = findViewById(R.id.btn200);
        btnQuick500 = findViewById(R.id.btn500);

        llMethodCard = findViewById(R.id.llMethodCard);
        llMethodBank = findViewById(R.id.llMethodBank);
        llMethodWallet = findViewById(R.id.llMethodWallet);
        llMethodPaypal = findViewById(R.id.llMethodPaypal);

        ivCheckCard = findViewById(R.id.ivRadioCard);
        ivCheckBank = findViewById(R.id.ivRadioBank); // Added if exists
        ivCheckWallet = findViewById(R.id.ivRadioWallet); // Added if exists
        ivCheckPaypal = findViewById(R.id.ivRadioPaypal); // Added if exists

        etAmount.setText("100.00");
    }
}
