package com.example.bankpick;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import android.widget.FrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        init();

        // Apply insets correctly:
        // - status bar top padding → fragmentContainer (so content isn't behind status bar)
        // - navigation bar bottom padding → bottomNav (so it isn't hidden behind gesture bar)
        ViewCompat.setOnApplyWindowInsetsListener(fragmentContainer, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(bottomNav, (v, insets) -> {
            Insets navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            v.setPadding(0, 0, 0, navBar.bottom);
            return insets;
        });

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            bottomNav.setSelectedItemId(R.id.nav_home);
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            int id = item.getItemId();
            if (id == R.id.nav_home) { selected = new HomeFragment(); }
            else if (id == R.id.nav_my_cards) { selected = new MyCardsFragment(); }
            else if (id == R.id.nav_statistics) { selected = new StatisticsFragment(); }
            else if (id == R.id.nav_settings) { selected = new SettingsFragment(); }

            if (selected != null) { loadFragment(selected); }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void init() {
        bottomNav = findViewById(R.id.bottomNav);
        fragmentContainer = findViewById(R.id.fragmentContainer);
    }
}
