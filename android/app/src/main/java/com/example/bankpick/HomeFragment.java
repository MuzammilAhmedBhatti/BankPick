package com.example.bankpick;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bankpick.adapters.TransactionAdapter;
import com.example.bankpick.models.Transaction;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    View rootView;
    TextView tvUserName, tvSeeAll, tvCardNumber, tvCardHolder, tvExpiry, tvCvv;
    ImageView ivSearch;
    RecyclerView rvTransactions;
    View btnSent, btnReceive, btnLoan, btnTopup;
    ArrayList<Transaction> transactions;
    TransactionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        init();
        loadData();

        ivSearch.setOnClickListener((v) -> startActivity(new Intent(requireContext(), SearchActivity.class)));
        tvSeeAll.setOnClickListener((v) -> startActivity(new Intent(requireContext(), TransactionHistoryActivity.class)));
        btnSent.setOnClickListener((v) -> startActivity(new Intent(requireContext(), SendMoneyActivity.class)));
        btnReceive.setOnClickListener((v) -> startActivity(new Intent(requireContext(), RequestMoneyActivity.class)));
        btnTopup.setOnClickListener((v) -> startActivity(new Intent(requireContext(), TopupActivity.class)));

        return rootView;
    }

    private void loadData() {
        // Mock User Data
        tvUserName.setText("Tanya Myroniuk");

        // Mock Card Data
        tvCardNumber.setText("4562 1122 4595 7852");
        tvCardHolder.setText("AR Jonson");
        tvExpiry.setText("24/2000");
        tvCvv.setText("6986");

        // Mock Transactions
        transactions.clear();
        transactions.add(new Transaction("1", "Apple Store", "Entertainment", -5.99, "apple", "Today", "10:00 AM"));
        transactions.add(new Transaction("2", "Spotify", "Music", -12.99, "music", "Yesterday", "11:30 AM"));
        transactions.add(new Transaction("3", "Money Transfer", "Transaction", 300, "transfer", "Yesterday", "1:00 PM"));
        transactions.add(new Transaction("4", "Grocery", "Shopping", -88, "grocery", "Yesterday", "4:45 PM"));
        adapter.notifyDataSetChanged();
    }

    private void init() {
        tvUserName = rootView.findViewById(R.id.tvUserName);
        tvSeeAll = rootView.findViewById(R.id.tvSeeAll);
        tvCardNumber = rootView.findViewById(R.id.tvCardNumber);
        tvCardHolder = rootView.findViewById(R.id.tvCardHolder);
        tvExpiry = rootView.findViewById(R.id.tvExpiry);
        tvCvv = rootView.findViewById(R.id.tvCvv);
        ivSearch = rootView.findViewById(R.id.ivSearch);
        rvTransactions = rootView.findViewById(R.id.rvTransactions);
        btnSent = rootView.findViewById(R.id.btnSent);
        btnReceive = rootView.findViewById(R.id.btnReceive);
        btnLoan = rootView.findViewById(R.id.btnLoan);
        btnTopup = rootView.findViewById(R.id.btnTopup);

        transactions = new ArrayList<>();
        adapter = new TransactionAdapter(requireContext(), transactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTransactions.setAdapter(adapter);
    }
}
