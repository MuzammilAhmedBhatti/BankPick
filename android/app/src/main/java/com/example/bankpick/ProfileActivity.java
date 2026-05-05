package com.example.bankpick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    ImageView ivBack, btnEdit;
    TextView tvUserName, tvUserTitle, tvJoinedDate;
    TextView tvStatBalance, tvStatCards, tvStatTransactions;
    TextView tvNotifBadge;

    private ValueEventListener userListener;
    private ValueEventListener cardListener;
    private ValueEventListener txnListener;
    private ValueEventListener notifListener;
    private String currentUserId;
    private String currentCardId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        init();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivBack.setOnClickListener(v -> finish());
        btnEdit.setOnClickListener(v -> startActivity(new Intent(this, EditProfileActivity.class)));

        findViewById(R.id.btnPersonalInfo).setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class)));
        findViewById(R.id.btnBanksCards).setOnClickListener(v ->
                startActivity(new Intent(this, AllCardsActivity.class)));
        findViewById(R.id.btnNotifications).setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));
        findViewById(R.id.btnSettings).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsFragment.class.getName().equals("") ?
                        NotificationsActivity.class : NotificationsActivity.class)));

        currentUserId = DatabaseHelper.getInstance().getCurrentUserId();
        if (currentUserId != null) {
            currentCardId = currentUserId + "_card_001";
            loadUserData();
            loadCardStats();
            loadTransactionCount();
            loadNotifBadge();
        }
    }

    private void loadUserData() {
        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name   = snapshot.child("fullName").getValue(String.class);
                String email  = snapshot.child("email").getValue(String.class);
                String joined = snapshot.child("joinedDate").getValue(String.class);

                if (name  != null && tvUserName   != null) tvUserName.setText(name);
                if (email != null && tvUserTitle   != null) tvUserTitle.setText(email);
                if (joined != null && tvJoinedDate != null)
                    tvJoinedDate.setText("Member since " + joined);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };
        DatabaseHelper.getInstance().userRef(currentUserId).addValueEventListener(userListener);
    }

    private void loadCardStats() {
        cardListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double balance = snapshot.child("balance").getValue(Double.class);
                if (balance != null && tvStatBalance != null)
                    tvStatBalance.setText(String.format("$%.0f", balance));
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };
        DatabaseHelper.getInstance().cardRef(currentCardId).addValueEventListener(cardListener);

        // Count total cards for this user
        DatabaseHelper.getInstance().cardsRef()
                .orderByChild("userId").equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (tvStatCards != null)
                            tvStatCards.setText(String.valueOf(snapshot.getChildrenCount()));
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void loadTransactionCount() {
        txnListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    String cardId = child.child("cardId").getValue(String.class);
                    if (currentCardId.equals(cardId)) count++;
                }
                if (tvStatTransactions != null)
                    tvStatTransactions.setText(String.valueOf(count));
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };
        DatabaseHelper.getInstance().transactionsRef().addValueEventListener(txnListener);
    }

    private void loadNotifBadge() {
        notifListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long unread = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    Boolean isUnread = child.child("unread").getValue(Boolean.class);
                    if (Boolean.TRUE.equals(isUnread)) unread++;
                }
                if (tvNotifBadge != null) {
                    if (unread > 0) {
                        tvNotifBadge.setText(String.valueOf(Math.min(unread, 99)));
                        tvNotifBadge.setVisibility(View.VISIBLE);
                    } else {
                        tvNotifBadge.setVisibility(View.GONE);
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };
        DatabaseHelper.getInstance()
                .userNotificationsRef(currentUserId)
                .addValueEventListener(notifListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseHelper db = DatabaseHelper.getInstance();
        if (userListener != null && currentUserId != null)
            db.userRef(currentUserId).removeEventListener(userListener);
        if (cardListener != null && currentCardId != null)
            db.cardRef(currentCardId).removeEventListener(cardListener);
        if (txnListener != null)
            db.transactionsRef().removeEventListener(txnListener);
        if (notifListener != null && currentUserId != null)
            db.userNotificationsRef(currentUserId).removeEventListener(notifListener);
    }

    private void init() {
        ivBack              = findViewById(R.id.btnBack);
        btnEdit             = findViewById(R.id.btnEdit);
        tvUserName          = findViewById(R.id.tvUserName);
        tvUserTitle         = findViewById(R.id.tvUserTitle);
        tvJoinedDate        = findViewById(R.id.tvJoinedDate);
        tvStatBalance       = findViewById(R.id.tvStatBalance);
        tvStatCards         = findViewById(R.id.tvStatCards);
        tvStatTransactions  = findViewById(R.id.tvStatTransactions);
        tvNotifBadge        = findViewById(R.id.tvNotifBadge);
    }
}
