package com.example.bankpick;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddNewCardActivity extends AppCompatActivity {
    EditText etCardNumber, etHolderName, etExpiry, etCvv;
    TextView tvPreviewCardNumber, tvPreviewHolder, tvPreviewExpiry, tvPreviewCvv;
    Button btnAdd;
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_card);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        setupTextWatchers();

        ivBack.setOnClickListener((v) -> finish());

        btnAdd.setOnClickListener((v) -> {
            Toast.makeText(this, "Card Added", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void setupTextWatchers() {
        android.text.TextWatcher watcher = new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePreview();
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        };
        etCardNumber.addTextChangedListener(watcher);
        etHolderName.addTextChangedListener(watcher);
        etExpiry.addTextChangedListener(watcher);
        etCvv.addTextChangedListener(watcher);
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
        ivBack = findViewById(R.id.ivBack);
        etCardNumber = findViewById(R.id.etCardNumber);
        etHolderName = findViewById(R.id.etHolderName);
        etExpiry = findViewById(R.id.etExpiry);
        etCvv = findViewById(R.id.etCvv);
        btnAdd = findViewById(R.id.btnAdd);

        tvPreviewCardNumber = findViewById(R.id.tvPreviewCardNumber);
        tvPreviewHolder = findViewById(R.id.tvPreviewHolder);
        tvPreviewExpiry = findViewById(R.id.tvPreviewExpiry);
        tvPreviewCvv = findViewById(R.id.tvPreviewCvv);

        // TSX Mock State
        etHolderName.setText("Tanya Myroniuk");
        etExpiry.setText("09/06/2024");
        etCvv.setText("6986");
        etCardNumber.setText("4562 1122 4595 7852");
        updatePreview();
    }
}
