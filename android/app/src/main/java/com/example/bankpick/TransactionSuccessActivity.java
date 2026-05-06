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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TransactionSuccessActivity extends BaseActivity {

    FrameLayout checkCircle;
    TextView tvSubtitle, tvAmount, tvRecipient, tvTxnId, tvDate, tvTime, tvPaymentMethod;
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
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
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

        String paymentMethod = i.getStringExtra("paymentMethod");
        if (paymentMethod != null && tvPaymentMethod != null) {
            tvPaymentMethod.setText(paymentMethod);
        }

        // Date and Time
        if (date != null && !date.isEmpty()) {
            tvDate.setText(date);
        } else {
            String currentDate = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(new Date());
            tvDate.setText(currentDate);
        }

        if (time != null && !time.isEmpty()) {
            tvTime.setText(time);
        } else {
            String currentTime = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
            tvTime.setText(currentTime);
        }
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

    private void saveReceiptAsImage(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        String filename = "BankPick_Receipt_" + System.currentTimeMillis() + ".png";
        OutputStream fos;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/BankPick");
                Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                fos = getContentResolver().openOutputStream(imageUri);
            } else {
                String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/BankPick";
                java.io.File file = new java.io.File(imagesDir);
                if (!file.exists()) file.mkdir();
                java.io.File image = new java.io.File(imagesDir, filename);
                fos = new java.io.FileOutputStream(image);
            }

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            if (fos != null) fos.close();
            Toast.makeText(this, "Receipt saved to Pictures/BankPick", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving receipt: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupButtons() {
        if (btnDownload != null) {
            btnDownload.setOnClickListener((v) -> {
                View receiptCard = findViewById(R.id.receiptCard);
                if (receiptCard != null) {
                    saveReceiptAsImage(receiptCard);
                } else {
                    Toast.makeText(this, "Could not find receipt to download", Toast.LENGTH_SHORT).show();
                }
            });
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
