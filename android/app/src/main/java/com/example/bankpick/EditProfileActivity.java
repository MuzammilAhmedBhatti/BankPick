package com.example.bankpick;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditProfileActivity extends AppCompatActivity {
    EditText etFullName, etEmail, etPhone, etDay, etMonth, etYear;
    ImageView ivBack;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        ivBack.setOnClickListener((v) -> finish());

        // TSX Mock State
        etFullName.setText("Tanya Myroniuk");
        etEmail.setText("tanya.myroniuk@gmail.com");
        etPhone.setText("+8801712663389");
        etDay.setText("28");
        etMonth.setText("September");
        etYear.setText("2000");

        if (btnSave != null) {
            btnSave.setOnClickListener((v) -> {
                Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }

    private void init() {
        ivBack = findViewById(R.id.ivBack);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etDay = findViewById(R.id.etDay);
        etMonth = findViewById(R.id.etMonth);
        etYear = findViewById(R.id.etYear);
        btnSave = findViewById(R.id.btnSave); // In EditProfile.tsx there is actually no save button, but if xml has it, we mock it.
    }
}
