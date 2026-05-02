package com.example.bankpick;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.bankpick.adapters.OnboardingAdapter;

public class OnboardingActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    LinearLayout dotsLayout;
    Button btnNext;
    TextView tvSkip;
    OnboardingAdapter adapter;

    String[] titles;
    String[] descriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        tvSkip.setOnClickListener((v) -> {
            startActivity(new Intent(OnboardingActivity.this, SignInActivity.class));
            finish();
        });

        btnNext.setOnClickListener((v) -> {
            int current = viewPager.getCurrentItem();
            if (current < 2) {
                viewPager.setCurrentItem(current + 1);
            } else {
                startActivity(new Intent(OnboardingActivity.this, SignInActivity.class));
                finish();
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateDots(position);
                if (position == 2) {
                    btnNext.setText(R.string.get_started);
                } else {
                    btnNext.setText(R.string.next);
                }
            }
        });
    }

    private void init() {
        viewPager = findViewById(R.id.viewPager);
        dotsLayout = findViewById(R.id.dotsLayout);
        btnNext = findViewById(R.id.btnNext);
        tvSkip = findViewById(R.id.tvSkip);

        titles = new String[]{
                getString(R.string.onboarding_title_1),
                getString(R.string.onboarding_title_2),
                getString(R.string.onboarding_title_3)
        };

        descriptions = new String[]{
                getString(R.string.onboarding_desc_1),
                getString(R.string.onboarding_desc_2),
                getString(R.string.onboarding_desc_3)
        };

        int[] images = new int[]{
                R.drawable.ic_onboarding_1,
                R.drawable.ic_onboarding_2,
                R.drawable.ic_onboarding_3
        };

        adapter = new OnboardingAdapter(this, titles, descriptions, images);
        viewPager.setAdapter(adapter);
        updateDots(0);
    }

    private void updateDots(int position) {
        dotsLayout.removeAllViews();
        for (int i = 0; i < 3; i++) {
            ImageView dot = new ImageView(this);
            LinearLayout.LayoutParams params;
            if (i == position) {
                params = new LinearLayout.LayoutParams(32, 8);
                dot.setBackgroundResource(R.drawable.bg_dot_active);
            } else {
                params = new LinearLayout.LayoutParams(8, 8);
                dot.setBackgroundResource(R.drawable.bg_dot_inactive);
            }
            params.setMargins(4, 0, 4, 0);
            dot.setLayoutParams(params);
            dotsLayout.addView(dot);
        }
    }
}
