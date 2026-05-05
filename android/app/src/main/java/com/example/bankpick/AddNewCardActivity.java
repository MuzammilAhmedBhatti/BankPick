package com.example.bankpick;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AddNewCardActivity extends BaseActivity {
    EditText etCardNumber, etHolderName, etExpiry, etCvv;
    TextView tvPreviewCardNumber, tvPreviewHolder, tvPreviewExpiry, tvPreviewCvv;
    Button btnAdd;
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_card);
        
        init();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupTextWatchers();

        ivBack.setOnClickListener((v) -> finish());

        btnAdd.setOnClickListener((v) -> {
            String num = etCardNumber.getText().toString().trim();
            String name = etHolderName.getText().toString().trim();
            String exp = etExpiry.getText().toString().trim();
            String cvv = etCvv.getText().toString().trim();

            if (num.isEmpty() || exp.isEmpty() || cvv.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1. Validate Card Number (Expects 16 digits + 3 spaces = 19 chars)
            if (num.replaceAll("\\s", "").length() != 16) {
                etCardNumber.setError("Card number must be 16 digits");
                etCardNumber.requestFocus();
                return;
            }

            // 2. Validate Expiry Date (Expects MM/YY)
            if (exp.length() != 5 || !exp.contains("/")) {
                etExpiry.setError("Use MM/YY format");
                etExpiry.requestFocus();
                return;
            }

            try {
                String[] parts = exp.split("/");
                int month = Integer.parseInt(parts[0]);
                int yearShort = Integer.parseInt(parts[1]);

                if (month < 1 || month > 12) {
                    etExpiry.setError("Month must be 01-12");
                    etExpiry.requestFocus();
                    return;
                }

                // Get current date for validation
                java.util.Calendar cal = java.util.Calendar.getInstance();
                int currentYearLong = cal.get(java.util.Calendar.YEAR);
                int currentYearShort = currentYearLong % 100;
                int currentMonth = cal.get(java.util.Calendar.MONTH) + 1; // Calendar months are 0-indexed

                if (yearShort < currentYearShort || (yearShort == currentYearShort && month < currentMonth)) {
                    etExpiry.setError("Card has expired");
                    etExpiry.requestFocus();
                    return;
                }
                
                // Allow only reasonable future years (e.g., next 20 years)
                if (yearShort > currentYearShort + 20) {
                    etExpiry.setError("Invalid year");
                    etExpiry.requestFocus();
                    return;
                }

            } catch (NumberFormatException e) {
                etExpiry.setError("Invalid expiry date");
                etExpiry.requestFocus();
                return;
            }

            // 3. Validate CVV (Expects 3 digits)
            if (cvv.length() != 3) {
                etCvv.setError("CVV must be 3 digits");
                etCvv.requestFocus();
                return;
            }

            String uid = DatabaseHelper.getInstance().getCurrentUserId();
            if (uid != null) {
                String cardId = uid + "_card_" + System.currentTimeMillis();
                java.util.Map<String, Object> card = new java.util.HashMap<>();
                card.put("cardId", cardId);
                card.put("userId", uid);
                card.put("cardNumber", num);
                card.put("holderName", name);
                card.put("expiryDate", exp);
                card.put("cvv", cvv);
                card.put("type", "Mastercard");
                card.put("balance", 0.0);
                card.put("isPrimary", false);

                DatabaseHelper.getInstance().cardRef(cardId).setValue(card).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Card Added Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to add card.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setupTextWatchers() {
        android.text.TextWatcher generalWatcher = new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePreview();
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        };
        etHolderName.addTextChangedListener(generalWatcher);
        etCvv.addTextChangedListener(generalWatcher);

        etCardNumber.addTextChangedListener(new android.text.TextWatcher() {
            private String current = "";
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d]", "");
                    if (clean.length() > 16) {
                        clean = clean.substring(0, 16);
                    }

                    StringBuilder formatted = new StringBuilder();
                    for (int i = 0; i < clean.length(); i++) {
                        if (i > 0 && i % 4 == 0) formatted.append(" ");
                        formatted.append(clean.charAt(i));
                    }
                    current = formatted.toString();
                    etCardNumber.setText(current);
                    etCardNumber.setSelection(current.length());
                }
                updatePreview();
            }
        });

        etExpiry.addTextChangedListener(new android.text.TextWatcher() {
            private String current = "";
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(android.text.Editable s) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    if (clean.length() > 4) {
                        clean = clean.substring(0, 4);
                    }
                    
                    StringBuilder formatted = new StringBuilder();
                    for (int i = 0; i < clean.length(); i++) {
                        if (i == 2) formatted.append("/");
                        formatted.append(clean.charAt(i));
                    }
                    current = formatted.toString();
                    etExpiry.setText(current);
                    etExpiry.setSelection(current.length());
                }
                updatePreview();
            }
        });
    }

    private void updatePreview() {
        String num = etCardNumber.getText().toString();
        String name = etHolderName.getText().toString();
        String exp = etExpiry.getText().toString();
        String cvv = etCvv.getText().toString();

        tvPreviewCardNumber.setText(num.isEmpty() ? "**** **** **** ****" : num);
        tvPreviewHolder.setText(name.isEmpty() ? "NAME" : name);
        tvPreviewExpiry.setText(exp.isEmpty() ? "MM/YY" : exp);
        tvPreviewCvv.setText(cvv.isEmpty() ? "***" : cvv);
    }

    private void init() {
        ivBack = findViewById(R.id.btnBack);
        etCardNumber = findViewById(R.id.etCardNumber);
        etHolderName = findViewById(R.id.etCardholderName);
        etExpiry = findViewById(R.id.etExpiryDate);
        etCvv = findViewById(R.id.etCvv);
        btnAdd = findViewById(R.id.btnAddCard);

        tvPreviewCardNumber = findViewById(R.id.tvPreviewCardNumber);
        tvPreviewHolder = findViewById(R.id.tvPreviewHolderName);
        tvPreviewExpiry = findViewById(R.id.tvPreviewExpiry);
        tvPreviewCvv = findViewById(R.id.tvPreviewCvv);

        // Pre-fill holder name from Firebase user profile
        String uid = DatabaseHelper.getInstance().getCurrentUserId();
        if (uid != null) {
            DatabaseHelper.getInstance().userRef(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.child("fullName").getValue(String.class);
                    if (name != null && etHolderName != null) {
                        etHolderName.setText(name);
                        updatePreview();
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {}
            });
        }

        updatePreview();
    }
}
