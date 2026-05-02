package com.example.bankpick;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bankpick.adapters.NotificationAdapter;
import com.example.bankpick.models.BankNotification;
import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity {
    RecyclerView rvNotifications;
    ImageView ivBack;
    ArrayList<BankNotification> notifications;
    NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifications);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivBack = findViewById(R.id.ivBack);
        rvNotifications = findViewById(R.id.rvNotifications);
        ivBack.setOnClickListener(v -> finish());

        notifications = new ArrayList<>();
        notifications.add(new BankNotification("1", "Payment Received", "You received $300 from John Doe", "2 hours ago", true, "💰", "bg-green-100"));
        notifications.add(new BankNotification("2", "Card Payment Successful", "Your payment of $5.99 to Apple Store was successful", "5 hours ago", true, "✅", "bg-blue-100"));
        notifications.add(new BankNotification("3", "Security Alert", "New login detected from Chrome on Windows", "1 day ago", false, "🔒", "bg-orange-100"));
        notifications.add(new BankNotification("4", "Monthly Statement Ready", "Your April statement is now available", "2 days ago", false, "📄", "bg-purple-100"));
        notifications.add(new BankNotification("5", "Spending Limit Alert", "You've reached 80% of your monthly spending limit", "3 days ago", false, "⚠️", "bg-yellow-100"));

        adapter = new NotificationAdapter(this, notifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);
    }
}
