package com.example.bankpick;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
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
    private LinearLayout btnAdminUsers, btnAdminLoans, btnAdminBlocked, btnAdminSignOut, btnAdminBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tvTotalUsers   = findViewById(R.id.tvTotalUsers);
        tvPendingLoans = findViewById(R.id.tvPendingLoans);
        tvBlockedUsers = findViewById(R.id.tvBlockedUsers);
        tvApprovedLoans = findViewById(R.id.tvApprovedLoans);

        btnAdminUsers     = (LinearLayout) findViewById(R.id.btnAdminUsers);
        btnAdminLoans     = (LinearLayout) findViewById(R.id.btnAdminLoans);
        btnAdminBlocked   = (LinearLayout) findViewById(R.id.btnAdminBlocked);
        btnAdminBroadcast = (LinearLayout) findViewById(R.id.btnAdminBroadcast);
        btnAdminSignOut   = (LinearLayout) findViewById(R.id.btnAdminSignOut);

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
        android.view.View dialogView = android.view.LayoutInflater.from(this)
                .inflate(R.layout.dialog_broadcast, null);

        final EditText titleBox   = dialogView.findViewById(R.id.etBroadcastTitle);
        final EditText messageBox = dialogView.findViewById(R.id.etBroadcastMessage);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        // Transparent window so the card's rounded corners show
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        dialogView.findViewById(R.id.btnCloseDialog).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnCancelBroadcast).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnSendBroadcast).setOnClickListener(v -> {
            String title = titleBox.getText().toString().trim();
            String msg   = messageBox.getText().toString().trim();
            if (title.isEmpty() || msg.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            DatabaseHelper.getInstance().broadcastNotification(title, msg,
                    (success, resultMsg) -> Toast.makeText(this, resultMsg, Toast.LENGTH_SHORT).show());
            dialog.dismiss();
        });

        dialog.show();
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
