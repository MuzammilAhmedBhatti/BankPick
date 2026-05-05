package com.example.bankpick;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.widget.Switch;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {

    View rootView;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        SharedPreferences prefs = requireContext().getSharedPreferences("BankPickPrefs", android.content.Context.MODE_PRIVATE);

        // Switches
        Switch switchDarkMode = rootView.findViewById(R.id.switchDarkMode);
        Switch switchNotifications = rootView.findViewById(R.id.switchNotifications);
        Switch switchSound = rootView.findViewById(R.id.switchSound);

        // Load saved states
        switchDarkMode.setChecked(prefs.getBoolean("dark_mode", false));
        switchNotifications.setChecked(prefs.getBoolean("notifications", true));
        switchSound.setChecked(prefs.getBoolean("sound_effects", true));

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("notifications", isChecked).apply();
        });

        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("sound_effects", isChecked).apply();
        });

        // Navigation
        View btnBack = rootView.findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        rootView.findViewById(R.id.btnMyProfile).setOnClickListener(v -> startActivity(new Intent(requireContext(), ProfileActivity.class)));
        rootView.findViewById(R.id.btnContactUs).setOnClickListener(v -> startActivity(new Intent(requireContext(), ContactUsActivity.class)));
        rootView.findViewById(R.id.btnChangePassword).setOnClickListener(v -> startActivity(new Intent(requireContext(), ChangePasswordActivity.class)));
        rootView.findViewById(R.id.btnTerms).setOnClickListener(v -> startActivity(new Intent(requireContext(), TermsConditionActivity.class)));

        // Logout
        rootView.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(requireContext(), SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return rootView;
    }
}
