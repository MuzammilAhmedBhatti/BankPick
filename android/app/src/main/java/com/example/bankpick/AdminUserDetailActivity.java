package com.example.bankpick;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AdminUserDetailActivity extends BaseActivity {

    private String userId;
    private boolean isBlocked = false;

    private TextView tvDetailName, tvDetailEmail, tvDetailPhone, tvDetailJoined, tvDetailStatus;
    private LinearLayout llCards, llLoans;
    private Button btnBlockUnblock, btnDeleteUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_detail);

        userId = getIntent().getStringExtra("userId");
        if (userId == null) { finish(); return; }

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        tvDetailName   = findViewById(R.id.tvDetailName);
        tvDetailEmail  = findViewById(R.id.tvDetailEmail);
        tvDetailPhone  = findViewById(R.id.tvDetailPhone);
        tvDetailJoined = findViewById(R.id.tvDetailJoined);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);
        llCards        = findViewById(R.id.llCards);
        llLoans        = findViewById(R.id.llLoans);
        btnBlockUnblock = findViewById(R.id.btnBlockUnblock);
        btnDeleteUser   = findViewById(R.id.btnDeleteUser);

        loadUserData();
        loadUserCards();
        loadUserLoans();

        btnBlockUnblock.setOnClickListener(v -> toggleBlock());
        btnDeleteUser.setOnClickListener(v -> confirmDelete());
    }

    private void loadUserData() {
        DatabaseHelper.getInstance().userRef(userId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name   = snapshot.child("fullName").getValue(String.class);
                String email  = snapshot.child("email").getValue(String.class);
                String phone  = snapshot.child("phone").getValue(String.class);
                String joined = snapshot.child("joinedDate").getValue(String.class);
                Boolean blocked = snapshot.child("isBlocked").getValue(Boolean.class);

                isBlocked = Boolean.TRUE.equals(blocked);

                tvDetailName.setText(name != null ? name : "—");
                tvDetailEmail.setText(email != null ? email : "—");
                tvDetailPhone.setText(phone != null ? phone : "—");
                tvDetailJoined.setText("Joined: " + (joined != null ? joined : "—"));

                if (isBlocked) {
                    tvDetailStatus.setText("Blocked");
                    tvDetailStatus.setBackgroundResource(R.drawable.bg_badge_red);
                    btnBlockUnblock.setText("Unblock User");
                    btnBlockUnblock.setBackgroundResource(R.drawable.bg_icon_green);
                } else {
                    tvDetailStatus.setText("Active");
                    tvDetailStatus.setBackgroundResource(R.drawable.bg_circle_green);
                    btnBlockUnblock.setText("Block User");
                    btnBlockUnblock.setBackgroundResource(R.drawable.bg_btn_gradient_blue);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadUserCards() {
        DatabaseHelper.getInstance().cardsRef()
                .orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                llCards.removeAllViews();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String num = ds.child("cardNumber").getValue(String.class);
                    Double bal = ds.child("balance").getValue(Double.class);
                    String type = ds.child("type").getValue(String.class);

                    TextView tv = new TextView(AdminUserDetailActivity.this);
                    tv.setText((type != null ? type : "Card") + "  •  " +
                            (num != null ? num : "—") + "\n" +
                            "Balance: $" + String.format("%.2f", bal != null ? bal : 0));
                    tv.setTextSize(14f);
                    tv.setPadding(20, 16, 20, 16);
                    tv.setBackgroundResource(R.drawable.bg_surface_rounded);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 0, 0, 8);
                    tv.setLayoutParams(lp);
                    llCards.addView(tv);
                }
                if (llCards.getChildCount() == 0) {
                    TextView empty = new TextView(AdminUserDetailActivity.this);
                    empty.setText("No cards found.");
                    empty.setGravity(Gravity.CENTER);
                    llCards.addView(empty);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadUserLoans() {
        DatabaseHelper.getInstance().loansRef()
                .orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                llLoans.removeAllViews();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Double amount = ds.child("amount").getValue(Double.class);
                    String status = ds.child("status").getValue(String.class);
                    String reason = ds.child("reason").getValue(String.class);
                    String ts = ds.child("timestamp").getValue(String.class);

                    TextView tv = new TextView(AdminUserDetailActivity.this);
                    tv.setText("$" + String.format("%.2f", amount != null ? amount : 0) +
                            "  |  " + (status != null ? status.toUpperCase() : "—") +
                            "\n" + (reason != null ? reason : "") +
                            "\n" + (ts != null ? ts : ""));
                    tv.setTextSize(13f);
                    tv.setPadding(20, 16, 20, 16);
                    tv.setBackgroundResource(R.drawable.bg_surface_rounded);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 0, 0, 8);
                    tv.setLayoutParams(lp);
                    llLoans.addView(tv);
                }
                if (llLoans.getChildCount() == 0) {
                    TextView empty = new TextView(AdminUserDetailActivity.this);
                    empty.setText("No loans found.");
                    empty.setGravity(Gravity.CENTER);
                    llLoans.addView(empty);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void toggleBlock() {
        if (isBlocked) {
            DatabaseHelper.getInstance().unblockUser(userId, (success, msg) ->
                    Toast.makeText(this, success ? "User unblocked" : msg, Toast.LENGTH_SHORT).show());
        } else {
            DatabaseHelper.getInstance().blockUser(userId, (success, msg) ->
                    Toast.makeText(this, success ? "User blocked" : msg, Toast.LENGTH_SHORT).show());
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to soft-delete this user? They will be hidden from all lists.")
                .setPositiveButton("Delete", (d, w) ->
                        DatabaseHelper.getInstance().softDeleteUser(userId, (success, msg) -> {
                            Toast.makeText(this, success ? "User deleted" : msg, Toast.LENGTH_SHORT).show();
                            if (success) finish();
                        }))
                .setNegativeButton("Cancel", null)
                .show();
    }
}
