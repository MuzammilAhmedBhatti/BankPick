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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;

public class SendMoneyActivity extends AppCompatActivity {

    EditText etAmount;
    Button btnSend;
    ImageView ivBack;
    TextView tvCardNumber, tvCardHolder, tvCardBalance;
    ProgressBar progressBar;

    // Active card
    String activeCardId = DatabaseHelper.DEMO_CARD_ID;
    double currentBalance = 0;

    // Selected recipient (default = Yamilet, first in list)
    String selectedContact = "Yamilet";

    private ValueEventListener cardListener;

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
        listenToCard();
        setupContactSelection();

        ivBack.setOnClickListener((v) -> finish());

        btnSend.setOnClickListener((v) -> {
            String amountStr = etAmount.getText().toString().trim();
            if (TextUtils.isEmpty(amountStr)) {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount <= 0) {
                Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount > currentBalance) {
                Toast.makeText(this, "Insufficient funds (balance: $" +
                        String.format("%.2f", currentBalance) + ")", Toast.LENGTH_LONG).show();
                return;
            }

            // Disable button & show progress while Firebase processes
            btnSend.setEnabled(false);
            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

            DatabaseHelper.getInstance().sendMoney(
                    activeCardId, selectedContact, amount,
                    (success, info) -> runOnUiThread(() -> {
                        btnSend.setEnabled(true);
                        if (progressBar != null) progressBar.setVisibility(View.GONE);

                        if (success) {
                            Intent intent = new Intent(this, TransactionSuccessActivity.class);
                            intent.putExtra("type",          "Sent to " + selectedContact);
                            intent.putExtra("amount",        String.format("%.2f", amount));
                            intent.putExtra("recipient",     selectedContact);
                            intent.putExtra("transactionId", info);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Transfer failed: " + info,
                                    Toast.LENGTH_LONG).show();
                        }
                    })
            );
        });
    }

    /** Listen to Firebase card node for real-time balance updates */
    private void listenToCard() {
        cardListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String number  = snapshot.child("cardNumber").getValue(String.class);
                String holder  = snapshot.child("holderName").getValue(String.class);
                Double balance = snapshot.child("balance").getValue(Double.class);

                if (number  != null && tvCardNumber  != null) tvCardNumber.setText(number);
                if (holder  != null && tvCardHolder  != null) tvCardHolder.setText(holder);
                if (balance != null) {
                    currentBalance = balance;
                    if (tvCardBalance != null)
                        tvCardBalance.setText(String.format("Balance: $%.2f", balance));
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };
        DatabaseHelper.getInstance().cardRef(activeCardId).addValueEventListener(cardListener);
    }

    private void setupContactSelection() {
        int[] contactIds     = { R.id.contactYamilet, R.id.contactAlexa, R.id.contactYakub };
        String[] contactNames = { "Yamilet", "Alexa", "Yakub" };

        for (int i = 0; i < contactIds.length; i++) {
            View contactView = findViewById(contactIds[i]);
            if (contactView == null) continue;
            final String name = contactNames[i];
            contactView.setOnClickListener((v) -> {
                selectedContact = name;
                Toast.makeText(this, "Sending to " + name, Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void init() {
        ivBack        = findViewById(R.id.ivBack);
        etAmount      = findViewById(R.id.etAmount);
        btnSend       = findViewById(R.id.btnSend);
        tvCardNumber  = findViewById(R.id.tvCardNumber);
        tvCardHolder  = findViewById(R.id.tvCardHolder);
        tvCardBalance = findViewById(R.id.tvCardBalance);
        progressBar   = findViewById(R.id.progressBar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cardListener != null)
            DatabaseHelper.getInstance().cardRef(activeCardId).removeEventListener(cardListener);
    }
}
