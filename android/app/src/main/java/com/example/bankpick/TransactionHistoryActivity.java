package com.example.bankpick;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bankpick.adapters.TransactionAdapter;
import com.example.bankpick.models.Transaction;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class TransactionHistoryActivity extends AppCompatActivity {
    RecyclerView rvTransactions;
    ImageView ivBack;
    ArrayList<Transaction> transactions;
    TransactionAdapter adapter;

    private String currentCardId;
    private ValueEventListener txnListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaction_history);
        
        init();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivBack.setOnClickListener((v) -> finish());

        // Resolve current user's primary card
        String uid = DatabaseHelper.getInstance().getCurrentUserId();
        if (uid != null) {
            currentCardId = uid + "_card_001";
            loadTransactions();
        }
    }

    private void loadTransactions() {
        DatabaseHelper db = DatabaseHelper.getInstance();

        txnListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactions.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String cardId = child.child("cardId").getValue(String.class);
                    if (cardId == null || !currentCardId.equals(cardId)) continue;

                    String id       = child.child("transactionId").getValue(String.class);
                    String name     = child.child("name").getValue(String.class);
                    String category = child.child("category").getValue(String.class);
                    Double amount   = child.child("amount").getValue(Double.class);
                    String icon     = child.child("icon").getValue(String.class);
                    String date     = child.child("date").getValue(String.class);
                    String time     = child.child("time").getValue(String.class);

                    if (id != null) {
                        transactions.add(0, new Transaction(id, name, category,
                                amount != null ? amount : 0, icon, date, time));
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };
        db.transactionsRef().addValueEventListener(txnListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (txnListener != null)
            DatabaseHelper.getInstance().transactionsRef().removeEventListener(txnListener);
    }

    private void init() {
        ivBack = findViewById(R.id.btnBack);
        rvTransactions = findViewById(R.id.rvTransactions);
        transactions = new ArrayList<>();
        adapter = new TransactionAdapter(this, transactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(adapter);
    }
}
