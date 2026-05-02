package com.example.bankpick;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
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

public class SearchActivity extends AppCompatActivity {
    EditText etSearch;
    ImageView ivBack, ivClearSearch;
    RecyclerView rvTransactions;
    ArrayList<Transaction> transactions;
    ArrayList<Transaction> filteredTransactions;
    TransactionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        ivBack.setOnClickListener((v) -> finish());

        // Mock Transactions from SearchScreen.tsx
        transactions.add(new Transaction("1", "Apple Store", "Entertainment", -5.99, "apple", "Today", "10:00 AM"));
        transactions.add(new Transaction("2", "Spotify", "Music", -12.99, "music", "Yesterday", "11:30 AM"));
        transactions.add(new Transaction("3", "Money Transfer", "Transaction", 300, "transfer", "Yesterday", "1:00 PM"));
        transactions.add(new Transaction("4", "Grocery", "Shopping", -88, "grocery", "Yesterday", "4:45 PM"));
        transactions.add(new Transaction("5", "Apple Store", "Entertainment", -5.99, "apple", "2 Days Ago", "9:00 AM"));
        transactions.add(new Transaction("6", "Money Transfer", "Transaction", 300, "transfer", "3 Days Ago", "2:00 PM"));
        transactions.add(new Transaction("7", "Apple Store", "Entertainment", -5.99, "apple", "3 Days Ago", "4:00 PM"));
        transactions.add(new Transaction("8", "Spotify", "Music", -12.99, "music", "4 Days Ago", "8:00 AM"));

        filteredTransactions.addAll(transactions);
        adapter.notifyDataSetChanged();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
                if (ivClearSearch != null) {
                    ivClearSearch.setVisibility(s.length() > 0 ? android.view.View.VISIBLE : android.view.View.GONE);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        if (ivClearSearch != null) {
            ivClearSearch.setOnClickListener(v -> {
                etSearch.setText("");
            });
        }
    }

    private void filter(String query) {
        filteredTransactions.clear();
        for (Transaction t : transactions) {
            if (t.getName().toLowerCase().contains(query.toLowerCase()) || 
                t.getCategory().toLowerCase().contains(query.toLowerCase())) {
                filteredTransactions.add(t);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void init() {
        ivBack = findViewById(R.id.ivBack);
        etSearch = findViewById(R.id.etSearch);
        rvTransactions = findViewById(R.id.rvTransactions);

        transactions = new ArrayList<>();
        filteredTransactions = new ArrayList<>();
        adapter = new TransactionAdapter(this, filteredTransactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(adapter);
    }
}
