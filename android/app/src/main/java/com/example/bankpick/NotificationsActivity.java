package com.example.bankpick;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bankpick.adapters.NotificationAdapter;
import com.example.bankpick.models.BankNotification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class NotificationsActivity extends BaseActivity {
    RecyclerView rvNotifications;
    ImageView ivBack;
    TextView tvMarkAllRead, tvEmpty;
    ArrayList<BankNotification> notifications;
    NotificationAdapter adapter;

    private String currentUserId;
    private ValueEventListener notifListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifications);

        ivBack          = findViewById(R.id.btnBack);
        rvNotifications = findViewById(R.id.rvNotifications);
        tvMarkAllRead   = findViewById(R.id.tvMarkAllRead);
        tvEmpty         = findViewById(R.id.tvEmpty);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivBack.setOnClickListener(v -> finish());

        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(this, notifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);

        currentUserId = DatabaseHelper.getInstance().getCurrentUserId();
        if (currentUserId != null) {
            listenToNotifications();
        }

        // Mark all as read when tapped
        tvMarkAllRead.setOnClickListener(v -> markAllRead());
    }

    private void listenToNotifications() {
        notifListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notifications.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String id      = child.child("id").getValue(String.class);
                    String title   = child.child("title").getValue(String.class);
                    String message = child.child("message").getValue(String.class);
                    String time    = child.child("time").getValue(String.class);
                    Boolean unread = child.child("unread").getValue(Boolean.class);
                    String icon    = child.child("icon").getValue(String.class);
                    String color   = child.child("color").getValue(String.class);
                    if (id != null) {
                        notifications.add(0, new BankNotification(
                                id,
                                title != null ? title : "",
                                message != null ? message : "",
                                time != null ? time : "",
                                Boolean.TRUE.equals(unread),
                                icon != null ? icon : "🔔",
                                color != null ? color : "bg-blue-100"
                        ));
                    }
                }
                adapter.notifyDataSetChanged();
                if (tvEmpty != null) {
                    tvEmpty.setVisibility(notifications.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        DatabaseHelper.getInstance()
                .userNotificationsRef(currentUserId)
                .addValueEventListener(notifListener);
    }

    private void markAllRead() {
        if (currentUserId == null) return;
        DatabaseHelper.getInstance()
                .userNotificationsRef(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            child.getRef().child("unread").setValue(false);
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (notifListener != null && currentUserId != null) {
            DatabaseHelper.getInstance()
                    .userNotificationsRef(currentUserId)
                    .removeEventListener(notifListener);
        }
    }
}
