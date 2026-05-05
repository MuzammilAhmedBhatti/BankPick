package com.example.bankpick;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AdminDashboardActivity extends BaseActivity {

    private TextView tvTotalUsers, tvPendingLoans, tvBlockedUsers, tvApprovedLoans;
    private Button btnAdminUsers, btnAdminLoans, btnAdminBlocked, btnAdminSignOut, btnAdminBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tvTotalUsers   = findViewById(R.id.tvTotalUsers);
        tvPendingLoans = findViewById(R.id.tvPendingLoans);
        tvBlockedUsers = findViewById(R.id.tvBlockedUsers);
        tvApprovedLoans = findViewById(R.id.tvApprovedLoans);

        btnAdminUsers   = findViewById(R.id.btnAdminUsers);
        btnAdminLoans   = findViewById(R.id.btnAdminLoans);
        btnAdminBlocked = findViewById(R.id.btnAdminBlocked);
        btnAdminBroadcast = findViewById(R.id.btnAdminBroadcast);
        btnAdminSignOut = findViewById(R.id.btnAdminSignOut);

        loadStats();

        btnAdminUsers.setOnClickListener(v ->
                startActivity(new Intent(this, AdminUsersListActivity.class)));

        btnAdminLoans.setOnClickListener(v ->
                startActivity(new Intent(this, AdminLoanManagementActivity.class)));

        btnAdminBlocked.setOnClickListener(v ->
                startActivity(new Intent(this, AdminBlockedUsersActivity.class)));

        btnAdminBroadcast.setOnClickListener(v -> showBroadcastDialog());

        btnAdminSignOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStats();
    }

    private void showBroadcastDialog() {
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText titleBox = new EditText(this);
        titleBox.setHint("Notification Title");
        layout.addView(titleBox);

        final EditText messageBox = new EditText(this);
        messageBox.setHint("Notification Message");
        layout.addView(messageBox);

        new AlertDialog.Builder(this)
                .setTitle("Broadcast Notification")
                .setMessage("Send a notification to all active users.")
                .setView(layout)
                .setPositiveButton("Send", (dialog, which) -> {
                    String title = titleBox.getText().toString().trim();
                    String msg = messageBox.getText().toString().trim();
                    if (title.isEmpty() || msg.isEmpty()) {
                        Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DatabaseHelper.getInstance().broadcastNotification(title, msg, (success, resultMsg) -> {
                        Toast.makeText(this, resultMsg, Toast.LENGTH_SHORT).show();
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadStats() {
        // Total (non-deleted) users
        DatabaseHelper.getInstance().usersRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total = 0, blocked = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Boolean deleted = ds.child("isDeleted").getValue(Boolean.class);
                    if (Boolean.TRUE.equals(deleted)) continue;
                    total++;
                    Boolean isBlocked = ds.child("isBlocked").getValue(Boolean.class);
                    if (Boolean.TRUE.equals(isBlocked)) blocked++;
                }
                tvTotalUsers.setText(String.valueOf(total));
                tvBlockedUsers.setText(String.valueOf(blocked));
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Loan stats
        DatabaseHelper.getInstance().loansRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int pending = 0, approved = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String status = ds.child("status").getValue(String.class);
                    if (DatabaseHelper.LOAN_PENDING.equals(status)) pending++;
                    if (DatabaseHelper.LOAN_APPROVED.equals(status)) approved++;
                }
                tvPendingLoans.setText(String.valueOf(pending));
                tvApprovedLoans.setText(String.valueOf(approved));
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
