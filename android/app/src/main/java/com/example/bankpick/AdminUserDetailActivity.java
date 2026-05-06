package com.example.bankpick;

import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
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
        if (userId == null) {
            finish();
            return;
        }

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailEmail = findViewById(R.id.tvDetailEmail);
        tvDetailPhone = findViewById(R.id.tvDetailPhone);
        tvDetailJoined = findViewById(R.id.tvDetailJoined);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);
        llCards = findViewById(R.id.llCards);
        llLoans = findViewById(R.id.llLoans);
        btnBlockUnblock = findViewById(R.id.btnBlockUnblock);
        btnDeleteUser = findViewById(R.id.btnDeleteUser);

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
                        String name = snapshot.child("fullName").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String phone = snapshot.child("phone").getValue(String.class);
                        String joined = snapshot.child("joinedDate").getValue(String.class);
                        Boolean blocked = snapshot.child("isBlocked").getValue(Boolean.class);

                        isBlocked = Boolean.TRUE.equals(blocked);

                        tvDetailName.setText(name != null ? name : "—");
                        tvDetailEmail.setText(email != null ? email : "—");
                        tvDetailPhone.setText(phone != null ? phone : "—");
                        tvDetailJoined.setText("Joined: " + (joined != null ? joined : "—"));

                        if (isBlocked) {
                            tvDetailStatus.setText("Blocked");
                            tvDetailStatus.setBackgroundResource(R.drawable.bg_status_red_pill);
                            tvDetailStatus.setTextColor(Color.parseColor("#991B1B"));
                            btnBlockUnblock.setText("Unblock User");
                            btnBlockUnblock.setBackgroundResource(R.drawable.bg_btn_unblock);
                        } else {
                            tvDetailStatus.setText("Active");
                            tvDetailStatus.setBackgroundResource(R.drawable.bg_status_green_pill);
                            tvDetailStatus.setTextColor(Color.parseColor("#065F46"));
                            btnBlockUnblock.setText("Block User");
                            btnBlockUnblock.setBackgroundResource(R.drawable.bg_btn_block);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
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
                            String line1 = (type != null ? type : "Card") + "  •  " +
                                    (num != null ? num : "—");
                            String line2 = "Balance: $" + String.format("%.2f", bal != null ? bal : 0);
                            SpannableStringBuilder content = new SpannableStringBuilder()
                                    .append(line1)
                                    .append("\n")
                                    .append(line2);
                            content.setSpan(new StyleSpan(Typeface.BOLD), 0, line1.length(),
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            content.setSpan(new ForegroundColorSpan(Color.parseColor("#1A1A2E")),
                                    0, line1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            int line2Start = line1.length() + 1;
                            content.setSpan(new ForegroundColorSpan(Color.parseColor("#6B7280")),
                                    line2Start, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            content.setSpan(new AbsoluteSizeSpan(12, true),
                                    line2Start, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            tv.setText(content);
                            tv.setTextSize(14f);
                            tv.setPadding(20, 16, 20, 16);
                            tv.setBackground(null);
                            tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close, 0);
                            tv.setCompoundDrawablePadding(12);
                            tv.setCompoundDrawableTintList(android.content.res.ColorStateList.valueOf(
                                    androidx.core.content.ContextCompat.getColor(AdminUserDetailActivity.this,
                                            R.color.text_EF4444)));
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            lp.setMargins(0, 0, 0, 0);
                            tv.setLayoutParams(lp);

                            String finalCardId = ds.getKey();
                            tv.setOnClickListener(v -> {
                                new AlertDialog.Builder(AdminUserDetailActivity.this)
                                        .setTitle("Delete Card")
                                        .setMessage("Are you sure you want to delete this card ("
                                                + (num != null ? num : "—") + ")? This action cannot be undone.")
                                        .setPositiveButton("Delete", (dialog, which) -> {
                                            DatabaseHelper.getInstance().deleteCard(finalCardId, (success, msg) -> {
                                                Toast.makeText(AdminUserDetailActivity.this, msg, Toast.LENGTH_SHORT)
                                                        .show();
                                                if (success) {
                                                    loadUserCards(); // Refresh cards
                                                }
                                            });
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .show();
                            });

                            llCards.addView(tv);
                        }
                        if (llCards.getChildCount() == 0) {
                            TextView empty = new TextView(AdminUserDetailActivity.this);
                            empty.setText("No cards found.");
                            empty.setGravity(Gravity.CENTER);
                            empty.setTextColor(Color.parseColor("#9CA3AF"));
                            empty.setPadding(0, 32, 0, 32);
                            llCards.addView(empty);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
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
                            String header = "$" + String.format("%.2f", amount != null ? amount : 0) +
                                    "  |  " + (status != null ? status.toUpperCase() : "—");
                            String body = (reason != null ? reason : "") +
                                    "\n" + (ts != null ? ts : "");
                            SpannableStringBuilder content = new SpannableStringBuilder()
                                    .append(header)
                                    .append("\n")
                                    .append(body);
                            content.setSpan(new StyleSpan(Typeface.BOLD), 0, header.length(),
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            content.setSpan(new ForegroundColorSpan(Color.parseColor("#1A1A2E")),
                                    0, header.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            int bodyStart = header.length() + 1;
                            content.setSpan(new ForegroundColorSpan(Color.parseColor("#6B7280")),
                                    bodyStart, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            content.setSpan(new AbsoluteSizeSpan(12, true),
                                    bodyStart, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            tv.setText(content);
                            tv.setTextSize(13f);
                            tv.setPadding(20, 16, 20, 16);
                            tv.setBackground(null);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            lp.setMargins(0, 0, 0, 0);
                            tv.setLayoutParams(lp);
                            llLoans.addView(tv);
                        }
                        if (llLoans.getChildCount() == 0) {
                            TextView empty = new TextView(AdminUserDetailActivity.this);
                            empty.setText("No loans found.");
                            empty.setGravity(Gravity.CENTER);
                            empty.setTextColor(Color.parseColor("#9CA3AF"));
                            empty.setPadding(0, 32, 0, 32);
                            llLoans.addView(empty);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void toggleBlock() {
        if (isBlocked) {
            DatabaseHelper.getInstance().unblockUser(userId, (success, msg) -> Toast
                    .makeText(this, success ? "User unblocked" : msg, Toast.LENGTH_SHORT).show());
        } else {
            DatabaseHelper.getInstance().blockUser(userId,
                    (success, msg) -> Toast.makeText(this, success ? "User blocked" : msg, Toast.LENGTH_SHORT).show());
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to soft-delete this user? They will be hidden from all lists.")
                .setPositiveButton("Delete",
                        (d, w) -> DatabaseHelper.getInstance().softDeleteUser(userId, (success, msg) -> {
                            Toast.makeText(this, success ? "User deleted" : msg, Toast.LENGTH_SHORT).show();
                            if (success)
                                finish();
                        }))
                .setNegativeButton("Cancel", null)
                .show();
    }
}
