package com.example.bankpick;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bankpick.models.Card;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class LoanRequestActivity extends AppCompatActivity {

    private TextView tvLoanCardNumber, tvLoanCardType, tvLoanCardBalance;
    private EditText etLoanAmount, etLoanReason;
    private Button btnSubmitLoan;
    private LinearLayout llExistingLoan;

    private String userId, primaryCardId, primaryCardNumber, userName;
    private boolean hasPendingLoan = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loan_request);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        ImageView btnBack = findViewById(R.id.btnBack);
        tvLoanCardNumber  = findViewById(R.id.tvLoanCardNumber);
        tvLoanCardType    = findViewById(R.id.tvLoanCardType);
        tvLoanCardBalance = findViewById(R.id.tvLoanCardBalance);
        etLoanAmount      = findViewById(R.id.etLoanAmount);
        etLoanReason      = findViewById(R.id.etLoanReason);
        btnSubmitLoan     = findViewById(R.id.btnSubmitLoan);
        llExistingLoan    = findViewById(R.id.llExistingLoan);

        btnBack.setOnClickListener(v -> finish());

        userId = DatabaseHelper.getInstance().getCurrentUserId();
        if (userId == null) { finish(); return; }

        checkBlockedAndLoad();

        btnSubmitLoan.setOnClickListener(v -> submitLoan());
    }

    private void checkBlockedAndLoad() {
        DatabaseHelper.getInstance().isUserBlocked(userId, isBlocked -> {
            if (isBlocked) {
                Toast.makeText(this, "Your account is blocked. Contact support.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            loadUserAndCard();
        });
    }

    private void loadUserAndCard() {
        // Load user name
        DatabaseHelper.getInstance().userRef(userId).child("fullName")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = snapshot.getValue(String.class);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Load primary card
        DatabaseHelper.getInstance().cardsRef()
                .orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Boolean isPrimary = ds.child("isPrimary").getValue(Boolean.class);
                    if (Boolean.TRUE.equals(isPrimary)) {
                        Card card = ds.getValue(Card.class);
                        if (card != null) {
                            primaryCardId     = card.getCardId();
                            primaryCardNumber = card.getCardNumber();
                            tvLoanCardNumber.setText(card.getCardNumber());
                            tvLoanCardType.setText(card.getType());
                            tvLoanCardBalance.setText(
                                    String.format(Locale.getDefault(), "$%,.2f", card.getBalance()));
                        }
                        break;
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Check for existing pending loan
        DatabaseHelper.getInstance().getUserActiveLoan(userId, (loanId, status) -> {
            hasPendingLoan = loanId != null;
            if (hasPendingLoan) {
                llExistingLoan.setVisibility(View.VISIBLE);
                btnSubmitLoan.setEnabled(false);
                btnSubmitLoan.setAlpha(0.5f);
            }
        });
    }

    private void submitLoan() {
        if (primaryCardId == null) {
            Toast.makeText(this, "No active card found", Toast.LENGTH_SHORT).show();
            return;
        }

        String amountStr = etLoanAmount.getText().toString().trim();
        String reason    = etLoanReason.getText().toString().trim();

        if (amountStr.isEmpty()) {
            etLoanAmount.setError("Enter loan amount");
            etLoanAmount.requestFocus();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            etLoanAmount.setError("Invalid amount");
            return;
        }

        if (amount <= 0) {
            etLoanAmount.setError("Amount must be greater than 0");
            return;
        }

        if (amount > 50000) {
            etLoanAmount.setError("Maximum loan amount is $50,000");
            return;
        }

        if (reason.isEmpty()) {
            etLoanReason.setError("Please explain the reason for your loan");
            etLoanReason.requestFocus();
            return;
        }

        if (reason.length() < 10) {
            etLoanReason.setError("Reason must be at least 10 characters");
            return;
        }

        btnSubmitLoan.setEnabled(false);
        btnSubmitLoan.setText("Submitting...");

        DatabaseHelper.getInstance().requestLoan(
                userId, primaryCardId, amount, reason,
                userName != null ? userName : "Unknown",
                primaryCardNumber,
                (success, message) -> {
                    if (success) {
                        Toast.makeText(this, "Loan request submitted! Awaiting admin approval.", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        btnSubmitLoan.setEnabled(true);
                        btnSubmitLoan.setText("Submit Loan Request");
                        Toast.makeText(this, "Failed: " + message, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
