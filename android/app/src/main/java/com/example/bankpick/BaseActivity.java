package com.example.bankpick;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Apply the theme BEFORE super.onCreate()
        SettingsManager.applyTheme(this);
        super.onCreate(savedInstanceState);
    }
}
