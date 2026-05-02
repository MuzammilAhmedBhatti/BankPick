package com.example.bankpick;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bankpick.adapters.TransactionAdapter;
import com.example.bankpick.models.Transaction;
import java.util.ArrayList;

public class MyCardsFragment extends Fragment {

    View rootView;
    RecyclerView rvTransactions;
    SeekBar seekBar;
    TextView tvSpendingAmount, tvCardNumber, tvCardHolder, tvExpiry, tvCvv;
    ArrayList<Transaction> transactions;
    TransactionAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_cards, container, false);
        init();
        loadData();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int progress, boolean fromUser) {
                tvSpendingAmount.setText("Amount: $" + String.format("%,d", progress) + ".00");
            }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        });

        rootView.findViewById(R.id.ivAddCard).setOnClickListener((v) ->
                startActivity(new Intent(requireContext(), AllCardsActivity.class)));

        return rootView;
    }

    private void loadData() {
        tvCardNumber.setText("4562 1122 4595 7852");
        tvCardHolder.setText("AR Jonson");
        tvExpiry.setText("24/2000");
        tvCvv.setText("6986");

        transactions.clear();
        transactions.add(new Transaction("1", "Apple Store", "Entertainment", -5.99, "apple", "Today", "10:00 AM"));
        transactions.add(new Transaction("2", "Spotify", "Music", -12.99, "music", "Yesterday", "11:30 AM"));
        transactions.add(new Transaction("3", "Grocery", "Shopping", -88, "grocery", "Yesterday", "4:45 PM"));
        adapter.notifyDataSetChanged();
    }

    private void init() {
        tvCardNumber = rootView.findViewById(R.id.tvCardNumber);
        tvCardHolder = rootView.findViewById(R.id.tvCardHolder);
        tvExpiry = rootView.findViewById(R.id.tvExpiry);
        tvCvv = rootView.findViewById(R.id.tvCvv);
        tvSpendingAmount = rootView.findViewById(R.id.tvSpendingAmount);
        seekBar = rootView.findViewById(R.id.seekBar);
        rvTransactions = rootView.findViewById(R.id.rvTransactions);
        
        seekBar.setMax(10000);
        seekBar.setProgress(8545);
        tvSpendingAmount.setText("Amount: $8,545.00");

        transactions = new ArrayList<>();
        adapter = new TransactionAdapter(requireContext(), transactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTransactions.setAdapter(adapter);
    }
}
