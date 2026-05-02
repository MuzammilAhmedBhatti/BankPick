package com.example.bankpick;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    View rootView;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        rootView.findViewById(R.id.btnMyProfile).setOnClickListener((v) -> startActivity(new Intent(requireContext(), ProfileActivity.class)));
        rootView.findViewById(R.id.btnContactUs).setOnClickListener((v) -> startActivity(new Intent(requireContext(), ContactUsActivity.class)));
        rootView.findViewById(R.id.btnChangePassword).setOnClickListener((v) -> startActivity(new Intent(requireContext(), ChangePasswordActivity.class)));
        rootView.findViewById(R.id.btnTerms).setOnClickListener((v) -> startActivity(new Intent(requireContext(), TermsConditionActivity.class)));

        return rootView;
    }
}
