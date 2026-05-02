package com.example.bankpick;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bankpick.adapters.TransactionAdapter;
import com.example.bankpick.models.Transaction;
import java.util.ArrayList;

public class TransactionHistoryActivity extends AppCompatActivity {
    RecyclerView rvTransactions;
    ImageView ivBack;
    ArrayList<Transaction> transactions;
    TransactionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaction_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        ivBack.setOnClickListener((v) -> finish());

        // Mock Transactions from TransactionHistory.tsx
        transactions.clear();
        transactions.add(new Transaction("1", "Apple Store", "Entertainment", -5.99, "apple", "Today", "10:00 AM"));
        transactions.add(new Transaction("2", "Spotify", "Music", -12.99, "music", "Today", "11:30 AM"));
        transactions.add(new Transaction("3", "Money Transfer", "Transaction", 300, "transfer", "Today", "1:00 PM"));
        transactions.add(new Transaction("4", "Grocery", "Shopping", -88, "grocery", "Today", "4:45 PM"));
        transactions.add(new Transaction("5", "Apple Store", "Entertainment", -5.99, "apple", "Today", "9:00 AM"));
        transactions.add(new Transaction("6", "Spotify", "Music", -12.99, "music", "Today", "2:00 PM"));
        transactions.add(new Transaction("7", "Money Transfer", "Transaction", 300, "transfer", "Today", "4:00 PM"));
        transactions.add(new Transaction("8", "Spotify", "Music", -12.99, "music", "Today", "8:00 AM"));
        transactions.add(new Transaction("9", "Grocery", "Shopping", -88, "grocery", "Today", "6:00 PM"));
        adapter.notifyDataSetChanged();
    }

    private void init() {
        ivBack = findViewById(R.id.ivBack);
        rvTransactions = findViewById(R.id.rvTransactions);
        transactions = new ArrayList<>();
        adapter = new TransactionAdapter(this, transactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(adapter);
    }
}
