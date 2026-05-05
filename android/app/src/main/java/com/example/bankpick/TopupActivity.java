package com.example.bankpick;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import androidx.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TopupActivity extends AppCompatActivity {

    private ImageView btnBack;
    private Spinner spinnerFromCard, spinnerToCard;
    private EditText etAmount;
    private Button btnTransfer;
    
    // New UI elements for the visual cards
    private View cardFromView, cardToView;
    private TextView tvFromCardType, tvFromCardNumber, tvFromBalance;
    private TextView tvToCardType, tvToCardNumber, tvToBalance;
    
    // Quick amount buttons
    private Button btnAmt50, btnAmt100, btnAmt200, btnAmt500;

    private List<Card> allCards = new ArrayList<>();
    private List<Card> toCards = new ArrayList<>();
    private String currentUserId;

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

        currentUserId = DatabaseHelper.getInstance().getCurrentUserId();
        if (currentUserId != null) {
            loadCards();
        }

        btnBack.setOnClickListener(v -> finish());

        btnTransfer.setOnClickListener(v -> handleTransfer());

        // Visual card click handlers -> Open hidden spinners
        cardFromView.setOnClickListener(v -> spinnerFromCard.performClick());
        cardToView.setOnClickListener(v -> spinnerToCard.performClick());

        spinnerFromCard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateToCardSpinner(position);
                updateFromCardUI(position);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerToCard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateToCardUI(position);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        setupQuickAmounts();
    }

    private void init() {
        btnBack = findViewById(R.id.btnBack);
        spinnerFromCard = findViewById(R.id.spinnerFromCard);
        spinnerToCard = findViewById(R.id.spinnerToCard);
        etAmount = findViewById(R.id.etAmount);
        btnTransfer = findViewById(R.id.btnTransfer);

        cardFromView = findViewById(R.id.cardFromView);
        tvFromCardType = findViewById(R.id.tvFromCardType);
        tvFromCardNumber = findViewById(R.id.tvFromCardNumber);
        tvFromBalance = findViewById(R.id.tvFromBalance);

        cardToView = findViewById(R.id.cardToView);
        tvToCardType = findViewById(R.id.tvToCardType);
        tvToCardNumber = findViewById(R.id.tvToCardNumber);
        tvToBalance = findViewById(R.id.tvToBalance);

        btnAmt50 = findViewById(R.id.btnAmt50);
        btnAmt100 = findViewById(R.id.btnAmt100);
        btnAmt200 = findViewById(R.id.btnAmt200);
        btnAmt500 = findViewById(R.id.btnAmt500);
    }

    private void setupQuickAmounts() {
        View.OnClickListener listener = v -> {
            Button b = (Button) v;
            String val = b.getText().toString().replace("$", "") + ".00";
            etAmount.setText(val);
            
            // Highlight selected
            resetQuickAmountStyles();
            b.setBackgroundResource(R.drawable.bg_quick_amount_selected);
            b.setTextColor(getResources().getColor(R.color.blue_600));
        };
        
        btnAmt50.setOnClickListener(listener);
        btnAmt100.setOnClickListener(listener);
        btnAmt200.setOnClickListener(listener);
        btnAmt500.setOnClickListener(listener);
    }

    private void resetQuickAmountStyles() {
        int defaultColor = getResources().getColor(R.color.text_primary);
        btnAmt50.setBackgroundResource(R.drawable.bg_quick_amount);
        btnAmt50.setTextColor(defaultColor);
        btnAmt100.setBackgroundResource(R.drawable.bg_quick_amount);
        btnAmt100.setTextColor(defaultColor);
        btnAmt200.setBackgroundResource(R.drawable.bg_quick_amount);
        btnAmt200.setTextColor(defaultColor);
        btnAmt500.setBackgroundResource(R.drawable.bg_quick_amount);
        btnAmt500.setTextColor(defaultColor);
    }

    private void loadCards() {
        DatabaseHelper.getInstance().cardsRef().orderByChild("userId").equalTo(currentUserId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allCards.clear();
                int primaryIndex = 0;
                int i = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Card card = ds.getValue(Card.class);
                    if (card != null) {
                        allCards.add(card);
                        if (Boolean.TRUE.equals(ds.child("isPrimary").getValue(Boolean.class))) {
                            primaryIndex = i;
                        }
                        i++;
                    }
                }

                ArrayAdapter<Card> adapter = new ArrayAdapter<Card>(TopupActivity.this,
                        R.layout.item_spinner_card, allCards) {
                    @NonNull @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        return createItemView(position, convertView, parent);
                    }
                    @Override
                    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        return createItemView(position, convertView, parent);
                    }
                    private View createItemView(int position, View convertView, ViewGroup parent) {
                        View v = convertView != null ? convertView : getLayoutInflater().inflate(R.layout.item_spinner_card, parent, false);
                        Card c = getItem(position);
                        if (c != null) {
                            ((TextView) v.findViewById(R.id.tvSpinnerCardNumber)).setText(c.getCardNumber());
                            ((TextView) v.findViewById(R.id.tvSpinnerCardBalance)).setText(String.format("$%,.2f", c.getBalance()));
                        }
                        return v;
                    }
                };
                spinnerFromCard.setAdapter(adapter);
                if (allCards.size() > primaryIndex) {
                    spinnerFromCard.setSelection(primaryIndex);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateFromCardUI(int position) {
        if (position < allCards.size()) {
            Card card = allCards.get(position);
            tvFromCardType.setText(card.getType());
            tvFromCardNumber.setText(card.getCardNumber());
            tvFromBalance.setText(String.format(Locale.getDefault(), "$%,.2f", card.getBalance()));
        }
    }

    private void updateToCardUI(int position) {
        if (position < toCards.size()) {
            Card card = toCards.get(position);
            tvToCardType.setText(card.getType());
            tvToCardNumber.setText(card.getCardNumber());
            tvToBalance.setText(String.format(Locale.getDefault(), "$%,.2f", card.getBalance()));
        }
    }

    private void updateToCardSpinner(int fromPosition) {
        toCards.clear();
        for (int i = 0; i < allCards.size(); i++) {
            if (i != fromPosition) {
                toCards.add(allCards.get(i));
            }
        }

        ArrayAdapter<Card> adapter = new ArrayAdapter<Card>(this,
                R.layout.item_spinner_card, toCards) {
            @NonNull @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return createItemView(position, convertView, parent);
            }
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return createItemView(position, convertView, parent);
            }
            private View createItemView(int position, View convertView, ViewGroup parent) {
                View v = convertView != null ? convertView : getLayoutInflater().inflate(R.layout.item_spinner_card, parent, false);
                Card c = getItem(position);
                if (c != null) {
                    ((TextView) v.findViewById(R.id.tvSpinnerCardNumber)).setText(c.getCardNumber());
                    ((TextView) v.findViewById(R.id.tvSpinnerCardBalance)).setText(String.format("$%,.2f", c.getBalance()));
                }
                return v;
            }
        };
        spinnerToCard.setAdapter(adapter);
        
        if (!toCards.isEmpty()) {
            updateToCardUI(0);
        }
    }

    private void handleTransfer() {
        if (currentUserId == null) return;
        DatabaseHelper.getInstance().isUserBlocked(currentUserId, isBlocked -> {
            if (isBlocked) {
                Toast.makeText(this, "Your account is blocked. Transfer failed.", Toast.LENGTH_LONG).show();
                return;
            }

            if (spinnerFromCard.getSelectedItem() == null || spinnerToCard.getSelectedItem() == null) {
                Toast.makeText(this, "Please select both cards", Toast.LENGTH_SHORT).show();
                return;
            }

        String amountStr = etAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            etAmount.setError("Enter amount");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            etAmount.setError("Invalid amount");
            return;
        }

        if (amount <= 0) {
            etAmount.setError("Amount must be greater than 0");
            return;
        }

        Card fromCard = allCards.get(spinnerFromCard.getSelectedItemPosition());
        Card toCard = toCards.get(spinnerToCard.getSelectedItemPosition());

        if (fromCard.getBalance() < amount) {
            Toast.makeText(this, "Insufficient balance in source card", Toast.LENGTH_SHORT).show();
            return;
        }

        btnTransfer.setEnabled(false);
        DatabaseHelper.getInstance().transferBetweenCards(fromCard.getCardId(), toCard.getCardId(), amount, (success, message) -> {
            btnTransfer.setEnabled(true);
            if (success) {
                Toast.makeText(this, "Transfer Successful!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Transfer Failed: " + message, Toast.LENGTH_SHORT).show();
            }
        });
        });
    }
}
