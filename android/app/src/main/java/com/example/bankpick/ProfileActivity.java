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

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends BaseActivity {

    ImageView ivBack, btnEdit, ivProfilePhoto;
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
        findViewById(R.id.btnSettings).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("navigate_to", "settings");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        currentUserId = DatabaseHelper.getInstance().getCurrentUserId();
        if (currentUserId != null) {
            currentCardId = currentUserId + "_card_001";
            loadUserData();
            loadTotalStats();
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

                String imageUrl = snapshot.child("profileImage").getValue(String.class);
                if (imageUrl != null && !imageUrl.isEmpty() && ivProfilePhoto != null) {
                    com.bumptech.glide.Glide.with(ProfileActivity.this)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .into(ivProfilePhoto);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };
        DatabaseHelper.getInstance().userRef(currentUserId).addValueEventListener(userListener);
    }

    private void loadTotalStats() {
        DatabaseHelper db = DatabaseHelper.getInstance();

        // 1. Total Balance and Total Cards
        cardListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double totalBalance = 0;
                long cardCount = 0;
                List<String> userCardIds = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Double bal = ds.child("balance").getValue(Double.class);
                    if (bal != null) totalBalance += bal;
                    cardCount++;
                    userCardIds.add(ds.getKey());
                }

                if (tvStatBalance != null)
                    tvStatBalance.setText(String.format("$%.0f", totalBalance));
                if (tvStatCards != null)
                    tvStatCards.setText(String.valueOf(cardCount));

                // 2. Total Transactions (for all user's cards)
                loadTotalTransactionCount(userCardIds);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };
        db.cardsRef().orderByChild("userId").equalTo(currentUserId).addValueEventListener(cardListener);
    }

    private void loadTotalTransactionCount(List<String> cardIds) {
        if (txnListener != null) {
            DatabaseHelper.getInstance().transactionsRef().removeEventListener(txnListener);
        }

        txnListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long totalCount = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    String cId = child.child("cardId").getValue(String.class);
                    if (cardIds.contains(cId)) {
                        totalCount++;
                    }
                }
                if (tvStatTransactions != null)
                    tvStatTransactions.setText(String.valueOf(totalCount));
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
        if (cardListener != null && currentUserId != null)
            db.cardsRef().orderByChild("userId").equalTo(currentUserId).removeEventListener(cardListener);
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
        ivProfilePhoto      = findViewById(R.id.ivProfilePhoto);
    }
}
