package com.example.bankpick;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.graphics.Color;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends BaseActivity {

    FrameLayout fragmentContainer;
    DrawerLayout drawerLayout;

    // Custom Bottom Nav
    LinearLayout navItemHome, navItemCards, navItemStats, navItemProfile;
    ImageView navIconHome, navIconCards, navIconStats, navIconProfile;
    TextView navTextHome, navTextCards, navTextStats, navTextProfile;

    // Drawer views
    TextView tvDrawerName, tvDrawerEmail, tvDrawerBalance, tvDrawerTransactions, tvDrawerNotifBadge;

    private String currentUserId;
    private String currentCardId;
    private ValueEventListener userListener, cardListener, txnListener, notifListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        init();

        // Apply insets
        ViewCompat.setOnApplyWindowInsetsListener(fragmentContainer, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });
        // Apply insets to the container of the floating nav
        View navContainer = findViewById(R.id.bottom_nav_container);
        ViewCompat.setOnApplyWindowInsetsListener(navContainer, (v, insets) -> {
            Insets navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            int extraPadding = (int) (16 * v.getResources().getDisplayMetrics().density); // 16dp
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), navBar.bottom + extraPadding);
            return insets;
        });

        // Apply insets to drawer content so it doesn't get hidden behind the navigation
        // bar
        View drawerContent = drawerLayout.findViewById(R.id.drawerContent);
        if (drawerContent != null) {
            ViewCompat.setOnApplyWindowInsetsListener(drawerContent, (v, insets) -> {
                Insets navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
                v.setPadding(0, 0, 0, navBar.bottom);
                return insets;
            });
        }

        // Load default fragment
        if (savedInstanceState == null) {
            if (getIntent() != null && "settings".equals(getIntent().getStringExtra("navigate_to"))) {
                loadFragment(new SettingsFragment());
                setActiveTab(-1);
            } else {
                loadFragment(new HomeFragment());
                setActiveTab(0);
            }
        }

        // Custom Nav listeners
        navItemHome.setOnClickListener(v -> {
            loadFragment(new HomeFragment());
            setActiveTab(0);
        });
        navItemCards.setOnClickListener(v -> {
            loadFragment(new MyCardsFragment());
            setActiveTab(1);
        });
        navItemStats.setOnClickListener(v -> {
            loadFragment(new StatisticsFragment());
            setActiveTab(2);
        });
        navItemProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        // Blocked overlay sign-out
        View btnBlockedSignOut = findViewById(R.id.btnBlockedSignOut);
        if (btnBlockedSignOut != null) {
            btnBlockedSignOut.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        // Drawer close button
        View ivClose = drawerLayout.findViewById(R.id.ivDrawerClose);
        if (ivClose != null)
            ivClose.setOnClickListener(v -> closeDrawer());

        // Wire drawer nav items
        wireDrawerItem(R.id.drawerHome, () -> {
            loadFragment(new HomeFragment());
            setActiveTab(0);
        });
        wireDrawerItem(R.id.drawerMyCards, () -> {
            loadFragment(new MyCardsFragment());
            setActiveTab(1);
        });
        wireDrawerItem(R.id.drawerStatistics, () -> {
            loadFragment(new StatisticsFragment());
            setActiveTab(2);
        });
        wireDrawerItem(R.id.drawerSendMoney, () -> startActivity(new Intent(this, SendMoneyActivity.class)));
        wireDrawerItem(R.id.drawerTopup, () -> startActivity(new Intent(this, TopupActivity.class)));
        wireDrawerItem(R.id.drawerNotifications, () -> startActivity(new Intent(this, NotificationsActivity.class)));
        wireDrawerItem(R.id.drawerProfile, () -> startActivity(new Intent(this, ProfileActivity.class)));
        wireDrawerItem(R.id.drawerSettings, () -> {
            loadFragment(new SettingsFragment(), true);
            setActiveTab(-1);
        });

        // Logout
        View drawerLogout = drawerLayout.findViewById(R.id.drawerLogout);
        if (drawerLogout != null) {
            drawerLogout.setOnClickListener(v -> {
                closeDrawer();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        // Load Firebase data into drawer header
        currentUserId = DatabaseHelper.getInstance().getCurrentUserId();
        if (currentUserId != null) {
            loadDrawerUserData();
            loadDrawerPrimaryCardStats();
            loadDrawerNotifBadge();
            listenForBlockStatus();
        }
    }

    /**
     * Monitors the user's isBlocked flag in real-time and shows an overlay if
     * blocked.
     */
    private void listenForBlockStatus() {
        DatabaseHelper.getInstance().userRef(currentUserId).child("isBlocked")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Boolean blocked = snapshot.getValue(Boolean.class);
                        View blockedOverlay = findViewById(R.id.blockedOverlay);
                        if (blockedOverlay != null) {
                            blockedOverlay.setVisibility(Boolean.TRUE.equals(blocked) ? View.VISIBLE : View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void wireDrawerItem(int viewId, Runnable action) {
        View v = drawerLayout.findViewById(viewId);
        if (v != null)
            v.setOnClickListener(view -> {
                closeDrawer();
                action.run();
            });
    }

    /** Called by HomeFragment's hamburger button */
    public void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void loadDrawerUserData() {
        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("fullName").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                if (tvDrawerName != null && name != null)
                    tvDrawerName.setText(name);
                if (tvDrawerEmail != null && email != null)
                    tvDrawerEmail.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        DatabaseHelper.getInstance().userRef(currentUserId).addValueEventListener(userListener);
    }

    private void loadDrawerPrimaryCardStats() {
        DatabaseHelper db = DatabaseHelper.getInstance();
        cardListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot primaryCardSnap = null;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    primaryCardSnap = ds;
                    if (Boolean.TRUE.equals(ds.child("isPrimary").getValue(Boolean.class))) {
                        break;
                    }
                }

                if (primaryCardSnap != null) {
                    String newCardId = primaryCardSnap.getKey();
                    Double balance = primaryCardSnap.child("balance").getValue(Double.class);
                    if (tvDrawerBalance != null && balance != null)
                        tvDrawerBalance.setText(String.format("$%.0f", balance));

                    if (newCardId != null && !newCardId.equals(currentCardId)) {
                        currentCardId = newCardId;
                        loadDrawerTransactionCountForCard(currentCardId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        db.cardsRef().orderByChild("userId").equalTo(currentUserId).addValueEventListener(cardListener);
    }

    private void loadDrawerTransactionCountForCard(String cardId) {
        if (txnListener != null) {
            DatabaseHelper.getInstance().transactionsRef().removeEventListener(txnListener);
        }

        txnListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    String cId = child.child("cardId").getValue(String.class);
                    if (cardId.equals(cId))
                        count++;
                }
                if (tvDrawerTransactions != null)
                    tvDrawerTransactions.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        DatabaseHelper.getInstance().transactionsRef().addValueEventListener(txnListener);
    }

    private void loadDrawerNotifBadge() {
        notifListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long unread = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    Boolean isUnread = child.child("unread").getValue(Boolean.class);
                    if (Boolean.TRUE.equals(isUnread))
                        unread++;
                }
                if (tvDrawerNotifBadge != null) {
                    if (unread > 0) {
                        tvDrawerNotifBadge.setText(String.valueOf(Math.min(unread, 99)));
                        tvDrawerNotifBadge.setVisibility(View.VISIBLE);
                    } else {
                        tvDrawerNotifBadge.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        DatabaseHelper.getInstance()
                .userNotificationsRef(currentUserId)
                .addValueEventListener(notifListener);
    }

    public void loadFragment(Fragment fragment) {
        loadFragment(fragment, false);
    }

    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        androidx.fragment.app.FragmentTransaction tx = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment);
        if (addToBackStack) {
            tx.addToBackStack(null);
        }
        tx.commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent != null && "settings".equals(intent.getStringExtra("navigate_to"))) {
            loadFragment(new SettingsFragment());
            setActiveTab(-1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseHelper db = DatabaseHelper.getInstance();
        if (userListener != null && currentUserId != null)
            db.userRef(currentUserId).removeEventListener(userListener);
        if (cardListener != null && currentUserId != null)
            db.cardsRef().orderByChild("userId").equalTo(currentUserId).removeEventListener(cardListener);
        if (txnListener != null)
            db.transactionsRef().removeEventListener(txnListener);
        if (notifListener != null && currentUserId != null)
            db.userNotificationsRef(currentUserId).removeEventListener(notifListener);
    }

    private void setActiveTab(int index) {
        // Reset all to inactive
        navItemHome.setBackgroundColor(Color.TRANSPARENT);
        navIconHome.setColorFilter(Color.parseColor("#9CA3AF"));
        navTextHome.setTextColor(Color.parseColor("#6B7280"));

        navItemCards.setBackgroundColor(Color.TRANSPARENT);
        navIconCards.setColorFilter(Color.parseColor("#9CA3AF"));
        navTextCards.setTextColor(Color.parseColor("#6B7280"));

        navItemStats.setBackgroundColor(Color.TRANSPARENT);
        navIconStats.setColorFilter(Color.parseColor("#9CA3AF"));
        navTextStats.setTextColor(Color.parseColor("#6B7280"));

        navItemProfile.setBackgroundColor(Color.TRANSPARENT);
        navIconProfile.setColorFilter(Color.parseColor("#9CA3AF"));
        navTextProfile.setTextColor(Color.parseColor("#6B7280"));

        // Set active
        if (index == 0) {
            navItemHome.setBackgroundResource(R.drawable.bg_nav_active);
            navIconHome.setColorFilter(Color.WHITE);
            navTextHome.setTextColor(Color.WHITE);
        } else if (index == 1) {
            navItemCards.setBackgroundResource(R.drawable.bg_nav_active);
            navIconCards.setColorFilter(Color.WHITE);
            navTextCards.setTextColor(Color.WHITE);
        } else if (index == 2) {
            navItemStats.setBackgroundResource(R.drawable.bg_nav_active);
            navIconStats.setColorFilter(Color.WHITE);
            navTextStats.setTextColor(Color.WHITE);
        }
    }

    private void init() {
        fragmentContainer = findViewById(R.id.fragment_container);
        drawerLayout = findViewById(R.id.drawerLayout);

        // Bind custom nav views
        navItemHome = findViewById(R.id.navItemHome);
        navItemCards = findViewById(R.id.navItemCards);
        navItemStats = findViewById(R.id.navItemStats);
        navItemProfile = findViewById(R.id.navItemProfile);

        navIconHome = findViewById(R.id.navIconHome);
        navIconCards = findViewById(R.id.navIconCards);
        navIconStats = findViewById(R.id.navIconStats);
        navIconProfile = findViewById(R.id.navIconProfile);

        navTextHome = findViewById(R.id.navTextHome);
        navTextCards = findViewById(R.id.navTextCards);
        navTextStats = findViewById(R.id.navTextStats);
        navTextProfile = findViewById(R.id.navTextProfile);

        tvDrawerName = drawerLayout.findViewById(R.id.tvDrawerName);
        tvDrawerEmail = drawerLayout.findViewById(R.id.tvDrawerEmail);
        tvDrawerBalance = drawerLayout.findViewById(R.id.tvDrawerBalance);
        tvDrawerTransactions = drawerLayout.findViewById(R.id.tvDrawerTransactions);
        tvDrawerNotifBadge = drawerLayout.findViewById(R.id.tvDrawerNotifBadge);
    }
}
