package com.example.bankpick;

import android.content.Intent;
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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;

import java.util.Map;

public class SendMoneyActivity extends AppCompatActivity {

    EditText etAmount, etRecipientEmail;
    Button btnSend;
    ImageView ivBack;
    TextView tvCardNumber, tvCardHolder, tvCardBalance;
    ProgressBar progressBar;
    LinearLayout llContactsContainer;

    String activeCardId;
    double currentBalance = 0;

    String selectedRecipientUid = null;
    String selectedRecipientName = null;
    double pendingAmount = 0;

    private ValueEventListener cardListener;

    private final ActivityResultLauncher<Intent> otpLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    processTransfer();
                } else {
                    Toast.makeText(this, "Verification failed", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_send_money);

        init();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String uid = DatabaseHelper.getInstance().getCurrentUserId();
        if (uid != null) {
            listenToPrimaryCard(uid);
            loadContacts(uid);
        }

        ivBack.setOnClickListener((v) -> finish());

        btnSend.setOnClickListener((v) -> {
            if (uid == null) return;
            DatabaseHelper.getInstance().isUserBlocked(uid, isBlocked -> {
                if (isBlocked) {
                    Toast.makeText(this, "Your account is blocked. Transfer failed.", Toast.LENGTH_LONG).show();
                    return;
                }

                String amountStr = etAmount.getText().toString().trim();
                if (TextUtils.isEmpty(amountStr)) {
                    Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    pendingAmount = Double.parseDouble(amountStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pendingAmount <= 0) {
                    Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pendingAmount > currentBalance) {
                    Toast.makeText(this, "Insufficient funds", Toast.LENGTH_LONG).show();
                    return;
                }

                if (selectedRecipientUid != null) {
                    startOtpProcess();
                } else {
                    String email = etRecipientEmail.getText().toString().trim();
                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(this, "Select a beneficiary or enter an email", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
                    DatabaseHelper.getInstance().findUserByEmail(email, (foundUid, name) -> {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        if (foundUid != null) {
                            selectedRecipientUid = foundUid;
                            selectedRecipientName = name;
                            startOtpProcess();
                        } else {
                            Toast.makeText(this, "Recipient email not found in BankPick", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        });
    }

    private void listenToPrimaryCard(String uid) {
        DatabaseHelper db = DatabaseHelper.getInstance();
        cardListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot primaryCardSnap = null;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    primaryCardSnap = ds;
                    if (Boolean.TRUE.equals(ds.child("isPrimary").getValue(Boolean.class))) {
                        break;
                    }
                }

                if (primaryCardSnap != null) {
                    activeCardId = primaryCardSnap.getKey();
                    String number  = primaryCardSnap.child("cardNumber").getValue(String.class);
                    String holder  = primaryCardSnap.child("holderName").getValue(String.class);
                    Double balance = primaryCardSnap.child("balance").getValue(Double.class);

                    if (number  != null && tvCardNumber  != null) tvCardNumber.setText(number);
                    if (holder  != null && tvCardHolder  != null) tvCardHolder.setText(holder);
                    if (balance != null) {
                        currentBalance = balance;
                        if (tvCardBalance != null)
                            tvCardBalance.setText(String.format("Balance: $%.2f", balance));
                    }
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };
        db.cardsRef().orderByChild("userId").equalTo(uid).addValueEventListener(cardListener);
    }

    private void startOtpProcess() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        DatabaseHelper.getInstance().sendOtp(FirebaseAuth.getInstance().getCurrentUser().getEmail(), new DatabaseHelper.OtpCallback() {
            @Override
            public void onSuccess(String otp) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Intent intent = new Intent(SendMoneyActivity.this, OtpVerificationActivity.class);
                intent.putExtra("dummyOtp", otp);
                otpLauncher.launch(intent);
            }

            @Override
            public void onFailure(String error) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(SendMoneyActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processTransfer() {
        btnSend.setEnabled(false);
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        DatabaseHelper.getInstance().sendMoney(
                activeCardId, selectedRecipientUid, selectedRecipientName, pendingAmount,
                (success, info) -> runOnUiThread(() -> {
                    btnSend.setEnabled(true);
                    if (progressBar != null) progressBar.setVisibility(View.GONE);

                    if (success) {
                        // Automatically add to beneficiary list
                        String currentUid = DatabaseHelper.getInstance().getCurrentUserId();
                        DatabaseHelper.getInstance().addContact(currentUid, selectedRecipientUid, selectedRecipientName);

                        Intent intent = new Intent(this, TransactionSuccessActivity.class);
                        intent.putExtra("type",          "Sent to " + selectedRecipientName);
                        intent.putExtra("amount",        String.format("%.2f", pendingAmount));
                        intent.putExtra("recipient",     selectedRecipientName);
                        intent.putExtra("transactionId", info);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Transfer failed: " + info, Toast.LENGTH_LONG).show();
                    }
                })
        );
    }

    private void loadContacts(String uid) {
        if (llContactsContainer == null) return;
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
            etRecipientEmail.setText(""); // Clear email if contact is selected
            
            for (int i = 0; i < llContactsContainer.getChildCount(); i++) {
                llContactsContainer.getChildAt(i).findViewById(R.id.ivContactIcon)
                        .setBackgroundResource(R.drawable.bg_circle);
            }
            ivIcon.setBackgroundResource(R.drawable.bg_contact_selected);
            
            Toast.makeText(this, "Selected: " + name, Toast.LENGTH_SHORT).show();
        });
        
        llContactsContainer.addView(view);
    }

    private void init() {
        ivBack            = findViewById(R.id.btnBack);
        etAmount          = findViewById(R.id.etAmount);
        etRecipientEmail  = findViewById(R.id.etRecipientEmail);
        btnSend           = findViewById(R.id.btnSendMoney);
        llContactsContainer = findViewById(R.id.llContacts);
        tvCardNumber      = findViewById(R.id.tvCardNumber);
        tvCardHolder      = findViewById(R.id.tvCardHolder);
        tvCardBalance     = findViewById(R.id.tvCardBalance);
        progressBar       = findViewById(R.id.progressBar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cardListener != null) {
             String uid = DatabaseHelper.getInstance().getCurrentUserId();
             if (uid != null)
                DatabaseHelper.getInstance().cardsRef().orderByChild("userId").equalTo(uid).removeEventListener(cardListener);
        }
    }
}
