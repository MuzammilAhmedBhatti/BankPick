package com.example.bankpick;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.List;
import java.util.Map;

public class SendMoneyActivity extends AppCompatActivity {

    EditText etAmount, etRecipientEmail;
    Button btnSend, btnAddContact;
    ImageView ivBack;
    TextView tvCardNumber, tvCardHolder, tvCardBalance;
    ProgressBar progressBar;
    LinearLayout llContactsContainer;

    // Resolved from current user
    String activeCardId;
    double currentBalance = 0;

    String selectedRecipientUid = null;
    String selectedRecipientName = null;

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

        String uid = DatabaseHelper.getInstance().getCurrentUserId();
        if (uid != null) {
            activeCardId = uid + "_card_001";
            listenToCard();
            loadContacts(uid);
        }

        ivBack.setOnClickListener((v) -> finish());

        btnAddContact.setOnClickListener(v -> {
            String email = etRecipientEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }
            DatabaseHelper.getInstance().findUserByEmail(email, (foundUid, name) -> {
                if (foundUid != null) {
                    DatabaseHelper.getInstance().addContact(uid, foundUid, name);
                    etRecipientEmail.setText("");
                    loadContacts(uid);
                    Toast.makeText(this, "Added " + name, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnSend.setOnClickListener((v) -> {
            String amountStr = etAmount.getText().toString().trim();
            if (TextUtils.isEmpty(amountStr)) {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedRecipientUid == null) {
                Toast.makeText(this, "Please select a recipient", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Insufficient funds", Toast.LENGTH_LONG).show();
                return;
            }

            btnSend.setEnabled(false);
            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

            DatabaseHelper.getInstance().sendMoney(
                    activeCardId, selectedRecipientUid, selectedRecipientName, amount,
                    (success, info) -> runOnUiThread(() -> {
                        btnSend.setEnabled(true);
                        if (progressBar != null) progressBar.setVisibility(View.GONE);

                        if (success) {
                            Intent intent = new Intent(this, TransactionSuccessActivity.class);
                            intent.putExtra("type",          "Sent to " + selectedRecipientName);
                            intent.putExtra("amount",        String.format("%.2f", amount));
                            intent.putExtra("recipient",     selectedRecipientName);
                            intent.putExtra("transactionId", info);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Transfer failed: " + info, Toast.LENGTH_LONG).show();
                        }
                    })
            );
        });
    }

    private void loadContacts(String uid) {
        DatabaseHelper.getInstance().getContacts(uid, contacts -> {
            llContactsContainer.removeAllViews();
            for (Map<String, String> contact : contacts) {
                addContactToView(contact.get("uid"), contact.get("name"));
            }
        });
    }

    private void addContactToView(String contactUid, String name) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_contact_circle, llContactsContainer, false);
        TextView tvName = view.findViewById(R.id.tvContactName);
        ImageView ivIcon = view.findViewById(R.id.ivContactIcon);
        
        tvName.setText(name);
        
        view.setOnClickListener(v -> {
            selectedRecipientUid = contactUid;
            selectedRecipientName = name;
            
            // Highlight selection (simple logic)
            for (int i = 0; i < llContactsContainer.getChildCount(); i++) {
                llContactsContainer.getChildAt(i).findViewById(R.id.ivContactIcon)
                        .setBackgroundResource(R.drawable.bg_circle);
            }
            ivIcon.setBackgroundResource(R.drawable.bg_contact_selected);
            
            Toast.makeText(this, "Selected: " + name, Toast.LENGTH_SHORT).show();
        });
        
        llContactsContainer.addView(view);
    }

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

    private void init() {
        ivBack            = findViewById(R.id.ivBack);
        etAmount          = findViewById(R.id.etAmount);
        etRecipientEmail  = findViewById(R.id.etRecipientEmail);
        btnAddContact     = findViewById(R.id.btnAddContact);
        btnSend           = findViewById(R.id.btnSend);
        tvCardNumber      = findViewById(R.id.tvCardNumber);
        tvCardHolder      = findViewById(R.id.tvCardHolder);
        tvCardBalance     = findViewById(R.id.tvCardBalance);
        progressBar       = findViewById(R.id.progressBar);
        llContactsContainer = findViewById(R.id.llContactsContainer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cardListener != null && activeCardId != null)
            DatabaseHelper.getInstance().cardRef(activeCardId).removeEventListener(cardListener);
    }
}
