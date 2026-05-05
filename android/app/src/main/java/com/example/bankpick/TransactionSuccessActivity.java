package com.example.bankpick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TransactionSuccessActivity extends AppCompatActivity {

    FrameLayout checkCircle;
    TextView tvSubtitle, tvAmount, tvRecipient, tvTxnId, tvDate, tvTime;
    Button btnDownload, btnShare, btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaction_success);

        initViews();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        populateData();
        animateCheckCircle();
        setupButtons();
    }

    private void initViews() {
        checkCircle  = findViewById(R.id.checkCircle);
        tvSubtitle   = findViewById(R.id.tvSubtitle);
        tvAmount     = findViewById(R.id.tvAmount);
        tvRecipient  = findViewById(R.id.tvRecipient);
        tvTxnId      = findViewById(R.id.tvTransactionId);
        tvDate       = findViewById(R.id.tvDate);
        tvTime       = findViewById(R.id.tvTime);
        btnDownload  = findViewById(R.id.btnDownload);
        btnShare     = findViewById(R.id.btnShare);
        btnDone      = findViewById(R.id.btnBackToHome);
    }

    private void populateData() {
        Intent i = getIntent();

        String type      = i.getStringExtra("type");
        String amount    = i.getStringExtra("amount");
        String recipient = i.getStringExtra("recipient");
        String date      = i.getStringExtra("date");
        String time      = i.getStringExtra("time");
        String txnId     = i.getStringExtra("transactionId");

        if (type != null && tvSubtitle != null) {
            tvSubtitle.setText("Your " + type.toLowerCase() + " has been completed");
        }

        if (amount    != null && tvAmount    != null) tvAmount.setText("$" + amount);
        if (recipient != null && tvRecipient != null) tvRecipient.setText(recipient);
        if (txnId     != null && tvTxnId     != null) tvTxnId.setText(txnId);
        if (date      != null && tvDate      != null) tvDate.setText(date);
        if (time      != null && tvTime      != null) tvTime.setText(time);
    }

    private void animateCheckCircle() {
        if (checkCircle == null) return;
        checkCircle.setScaleX(0f);
        checkCircle.setScaleY(0f);
        checkCircle.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(500)
                .setStartDelay(200)
                .setInterpolator(new OvershootInterpolator(1.5f))
                .start();
    }

    private void setupButtons() {
        if (btnDownload != null) {
            btnDownload.setOnClickListener((v) ->
                    Toast.makeText(this, "Receipt saved to your device", Toast.LENGTH_SHORT).show());
        }

        if (btnShare != null) {
            btnShare.setOnClickListener((v) -> {
                String amount    = getIntent().getStringExtra("amount");
                String type      = getIntent().getStringExtra("type");
                String recipient = getIntent().getStringExtra("recipient");

                String shareText = String.format("Transaction Receipt\n%s - $%s to %s",
                        type != null ? type : "Transfer",
                        amount != null ? amount : "0.00",
                        recipient != null ? recipient : "");

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Transaction Receipt");
                startActivity(Intent.createChooser(shareIntent, "Share Receipt"));
            });
        }

        if (btnDone != null) {
            btnDone.setOnClickListener((v) -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }
    }
}
