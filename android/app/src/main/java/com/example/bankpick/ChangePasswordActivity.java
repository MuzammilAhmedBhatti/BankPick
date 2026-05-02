package com.example.bankpick;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText etCurrent, etNew, etConfirm;
    Button btnSave;
    ImageView ivTogglePassword;
    boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView ivBack = findViewById(R.id.ivBack);
        etCurrent = findViewById(R.id.etCurrent);
        etNew = findViewById(R.id.etNew);
        etConfirm = findViewById(R.id.etConfirm);
        btnSave = findViewById(R.id.btnSave);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);

        // Match TSX initial state
        etCurrent.setText("........");
        etNew.setText("........");
        etConfirm.setText("........");

        ivBack.setOnClickListener(v -> finish());

        if (ivTogglePassword != null) {
            ivTogglePassword.setOnClickListener(v -> {
                passwordVisible = !passwordVisible;
                if (passwordVisible) {
                    etNew.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    etNew.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                etNew.setSelection(etNew.getText().length());
            });
        }

        btnSave.setOnClickListener(v -> {
            String newPwd = etNew.getText().toString();
            String confirmPwd = etConfirm.getText().toString();

            if (newPwd.equals(confirmPwd)) {
                finish();
            }
        });
    }
}
