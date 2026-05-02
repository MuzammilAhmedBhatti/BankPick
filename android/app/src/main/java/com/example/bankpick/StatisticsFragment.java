package com.example.bankpick;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bankpick.adapters.TransactionAdapter;
import com.example.bankpick.models.Transaction;
import java.util.ArrayList;

public class StatisticsFragment extends Fragment {

    View rootView;
    TextView tvBalance, tvSeeAll;
    RecyclerView rvTransactions;
    ArrayList<Transaction> transactions;
    TransactionAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
        init();
        loadData();

        tvSeeAll.setOnClickListener((v) -> startActivity(new Intent(requireContext(), TransactionHistoryActivity.class)));
        rootView.findViewById(R.id.ivNotifications).setOnClickListener((v) ->
                startActivity(new Intent(requireContext(), NotificationsActivity.class)));

        return rootView;
    }

    private void loadData() {
        tvBalance.setText("$8,545.00");

        transactions.clear();
        transactions.add(new Transaction("1", "Apple Store", "Entertainment", -5.99, "apple", "Today", "10:00 AM"));
        transactions.add(new Transaction("2", "Spotify", "Music", -12.99, "music", "Today", "11:30 AM"));
        transactions.add(new Transaction("3", "Money Transfer", "Transaction", 300, "transfer", "Today", "1:00 PM"));
        adapter.notifyDataSetChanged();
    }

    private void init() {
        tvBalance = rootView.findViewById(R.id.tvBalance);
        tvSeeAll = rootView.findViewById(R.id.tvSeeAll);
        rvTransactions = rootView.findViewById(R.id.rvTransactions);
        
        transactions = new ArrayList<>();
        adapter = new TransactionAdapter(requireContext(), transactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTransactions.setAdapter(adapter);
    }
}
