package com.example.bankpick;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;

public class TransactionDetailActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaction_detail);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView ivBack = findViewById(R.id.btnBack);
        TextView tvName = findViewById(R.id.tvMerchant);
        TextView tvCategory = findViewById(R.id.tvCategory);
        TextView tvAmount = findViewById(R.id.tvAmount);
        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvTime = findViewById(R.id.tvTime);
        TextView tvTransactionId = findViewById(R.id.tvTransactionId);
        TextView tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        android.widget.Button btnDownload = findViewById(R.id.btnDownload);
        android.widget.Button btnShare = findViewById(R.id.btnShare);

        ivBack.setOnClickListener((v) -> finish());

        String tempName = getIntent().getStringExtra("name");
        String tempCategory = getIntent().getStringExtra("category");
        double tempAmount = getIntent().getDoubleExtra("amount", 0);
        String tempDate = getIntent().getStringExtra("date");
        String tempTime = getIntent().getStringExtra("time");
        String cardId = getIntent().getStringExtra("cardId");
        
        final String name = (tempName == null) ? "Apple Store" : tempName;
        final String category = (tempCategory == null) ? "Entertainment" : tempCategory;
        final double amount = (tempAmount == 0) ? -5.99 : tempAmount;
        final String date = (tempDate == null) ? "May 2, 2026" : tempDate;
        final String time = (tempTime == null) ? "2:30 PM" : tempTime;

        tvName.setText(name);
        tvCategory.setText(category);
        tvDate.setText(date);
        tvTime.setText(time);
        tvTransactionId.setText("TRX123456789");

        if (amount > 0) {
            tvAmount.setText(String.format("+$%.2f", amount));
            tvAmount.setTextColor(getResources().getColor(R.color.primary, null));
        } else {
            tvAmount.setText(String.format("-$%.2f", Math.abs(amount)));
            tvAmount.setTextColor(getResources().getColor(R.color.text_primary, null));
        }

        if (cardId != null) {
            DatabaseHelper.getInstance().cardRef(cardId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String cardNumber = snapshot.child("cardNumber").getValue(String.class);
                    String type = snapshot.child("type").getValue(String.class);
                    if (cardNumber != null) {
                        String lastFour = cardNumber.substring(Math.max(0, cardNumber.length() - 4));
                        tvPaymentMethod.setText((type != null ? type : "Card") + " •••• " + lastFour);
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {}
            });
        }

        btnDownload.setOnClickListener(v -> {
            android.widget.Toast.makeText(this, "Receipt downloaded to your device", android.widget.Toast.LENGTH_SHORT).show();
        });

        btnShare.setOnClickListener(v -> {
            android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Transaction Receipt");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, name + " - $" + Math.abs(amount));
            startActivity(android.content.Intent.createChooser(shareIntent, "Share via"));
        });
    }
}
