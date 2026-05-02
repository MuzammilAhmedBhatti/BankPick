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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class MyCardsFragment extends Fragment {

    View rootView;
    RecyclerView rvTransactions;
    SeekBar seekBar;
    TextView tvSpendingAmount, tvCardNumber, tvCardHolder, tvExpiry, tvCvv;
    ArrayList<Transaction> transactions;
    TransactionAdapter adapter;

    private String currentUserId;
    private String currentCardId;
    private ValueEventListener cardListener;
    private ValueEventListener txnListener;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_cards, container, false);
        init();

        currentUserId = DatabaseHelper.getInstance().getCurrentUserId();
        if (currentUserId != null) {
            currentCardId = currentUserId + "_card_001";
            attachFirebaseListeners();
        }

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

    private void attachFirebaseListeners() {
        DatabaseHelper db = DatabaseHelper.getInstance();

        // Load primary card info
        cardListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String number = snapshot.child("cardNumber").getValue(String.class);
                String holder = snapshot.child("holderName").getValue(String.class);
                String expiry = snapshot.child("expiryDate").getValue(String.class);
                String cvv    = snapshot.child("cvv").getValue(String.class);

                if (tvCardNumber != null && number != null) tvCardNumber.setText(number);
                if (tvCardHolder != null && holder != null) tvCardHolder.setText(holder);
                if (tvExpiry     != null && expiry != null) tvExpiry.setText(expiry);
                if (tvCvv        != null && cvv    != null) tvCvv.setText(cvv);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };
        db.cardRef(currentCardId).addValueEventListener(cardListener);

        // Load transactions for this card
        txnListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactions.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String cardId = child.child("cardId").getValue(String.class);
                    if (!currentCardId.equals(cardId)) continue;

                    Transaction txn = child.getValue(Transaction.class);
                    if (txn != null) {
                        transactions.add(0, txn); // Most recent first
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };
        db.transactionsRef().addValueEventListener(txnListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DatabaseHelper db = DatabaseHelper.getInstance();
        if (cardListener != null && currentCardId != null)
            db.cardRef(currentCardId).removeEventListener(cardListener);
        if (txnListener != null)
            db.transactionsRef().removeEventListener(txnListener);
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
