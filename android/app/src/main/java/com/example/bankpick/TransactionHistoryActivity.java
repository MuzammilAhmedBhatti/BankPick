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

public class TransactionHistoryActivity extends BaseActivity {
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
            listenToPrimaryCard(uid);
        }
    }

    private void listenToPrimaryCard(String uid) {
        DatabaseHelper db = DatabaseHelper.getInstance();
        db.cardsRef().orderByChild("userId").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot primaryCardSnap = null;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    primaryCardSnap = ds;
                    if (Boolean.TRUE.equals(ds.child("isPrimary").getValue(Boolean.class))) {
                        break;
                    }
                }

                if (primaryCardSnap != null) {
                    String newCardId = primaryCardSnap.getKey();
                    if (newCardId != null && !newCardId.equals(currentCardId)) {
                        currentCardId = newCardId;
                        loadTransactions();
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadTransactions() {
        if (txnListener != null) {
            DatabaseHelper.getInstance().transactionsRef().removeEventListener(txnListener);
        }

        DatabaseHelper db = DatabaseHelper.getInstance();
        txnListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactions.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String cardId = child.child("cardId").getValue(String.class);
                    if (cardId == null || !currentCardId.equals(cardId)) continue;

                    Transaction t = child.getValue(Transaction.class);
                    if (t != null) {
                        transactions.add(0, t);
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
