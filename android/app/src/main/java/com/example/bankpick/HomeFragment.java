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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    View rootView;
    TextView tvUserName, tvSeeAll, tvCardNumber, tvCardHolder, tvExpiry, tvCvv, tvBalance;
    ImageView ivNotification, ivMenuHamburger;
    RecyclerView rvTransactions;
    View btnSent, btnReceive, btnLoan, btnTopup;

    ArrayList<Transaction> transactions;
    TransactionAdapter adapter;

    // Current user's IDs (resolved dynamically)
    private String currentUserId;
    private String currentCardId;

    // Listener references for cleanup
    private ValueEventListener userListener;
    private ValueEventListener cardListener;
    private ValueEventListener txnListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        init();

        currentUserId = DatabaseHelper.getInstance().getCurrentUserId();
        if (currentUserId == null) return rootView; // shouldn't happen if auth gate works

        // currentCardId will be resolved dynamically in attachFirebaseListeners


        attachFirebaseListeners();

        ivNotification.setOnClickListener((v) ->
                startActivity(new Intent(requireContext(), NotificationsActivity.class)));
        tvSeeAll.setOnClickListener((v) -> startActivity(new Intent(requireContext(), TransactionHistoryActivity.class)));
        btnSent.setOnClickListener((v) -> startActivity(new Intent(requireContext(), SendMoneyActivity.class)));
        btnReceive.setOnClickListener((v) -> startActivity(new Intent(requireContext(), RequestMoneyActivity.class)));
        btnLoan.setOnClickListener((v) -> startActivity(new Intent(requireContext(), LoanRequestActivity.class)));
        btnTopup.setOnClickListener((v) -> startActivity(new Intent(requireContext(), TopupActivity.class)));

        if (ivMenuHamburger != null) {
            ivMenuHamburger.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).openDrawer();
                }
            });
        }

        return rootView;
    }

    private void attachFirebaseListeners() {
        DatabaseHelper db = DatabaseHelper.getInstance();

        // ── User name (real-time) ──────────────────────────────────────────
        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("fullName").getValue(String.class);
                if (name != null && tvUserName != null) tvUserName.setText(name);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };
        db.userRef(currentUserId).addValueEventListener(userListener);

        // ── Primary card (live balance) ────────────────────────────────────
        cardListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot primaryCardSnap = null;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    primaryCardSnap = ds; // fallback to last if none is primary
                    if (Boolean.TRUE.equals(ds.child("isPrimary").getValue(Boolean.class))) {
                        break;
                    }
                }

                if (primaryCardSnap != null) {
                    String newCardId = primaryCardSnap.getKey();
                    
                    String number  = primaryCardSnap.child("cardNumber").getValue(String.class);
                    String holder  = primaryCardSnap.child("holderName").getValue(String.class);
                    String expiry  = primaryCardSnap.child("expiryDate").getValue(String.class);
                    String cvv     = primaryCardSnap.child("cvv").getValue(String.class);
                    Double balance = primaryCardSnap.child("balance").getValue(Double.class);

                    if (tvCardNumber != null && number != null)  tvCardNumber.setText(number);
                    if (tvCardHolder != null && holder != null)  tvCardHolder.setText(holder);
                    if (tvExpiry     != null && expiry != null)  tvExpiry.setText(expiry);
                    if (tvCvv        != null && cvv    != null)  tvCvv.setText(cvv);
                    if (tvBalance != null && balance != null)
                        tvBalance.setText(String.format("$%,.2f", balance));

                    // If card changed, reload transactions
                    if (newCardId != null && !newCardId.equals(currentCardId)) {
                        currentCardId = newCardId;
                        loadTransactionsForCard(currentCardId);
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };
        db.cardsRef().orderByChild("userId").equalTo(currentUserId).addValueEventListener(cardListener);
    }

    private void loadTransactionsForCard(String cardId) {
        DatabaseHelper db = DatabaseHelper.getInstance();
        if (txnListener != null) {
            db.transactionsRef().removeEventListener(txnListener);
        }

        txnListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactions.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String cId = child.child("cardId").getValue(String.class);
                    if (!cardId.equals(cId)) continue;

                    String id       = child.child("transactionId").getValue(String.class);
                    String name     = child.child("name").getValue(String.class);
                    String category = child.child("category").getValue(String.class);
                    Double amount   = child.child("amount").getValue(Double.class);
                    String icon     = child.child("icon").getValue(String.class);
                    String date     = child.child("date").getValue(String.class);
                    String time     = child.child("time").getValue(String.class);

                    if (name != null && (name.contains("Welcome Bonus") || name.contains("Apple Store") || name.contains("Netflix") || name.contains("Spotify") || name.contains("Dribbble") || name.contains("Figma"))) {
                        child.getRef().removeValue();
                        continue;
                    }

                    if (id != null) {
                        transactions.add(0, new Transaction(id, name, category, amount != null ? amount : 0, icon, date, time));
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
        if (userListener != null && currentUserId != null)
            db.userRef(currentUserId).removeEventListener(userListener);
        if (cardListener != null && currentUserId != null)
            db.cardsRef().orderByChild("userId").equalTo(currentUserId).removeEventListener(cardListener);
        if (txnListener != null)
            db.transactionsRef().removeEventListener(txnListener);
    }

    private void init() {
        tvUserName     = rootView.findViewById(R.id.tvUserName);
        tvSeeAll       = rootView.findViewById(R.id.tvSeeAll);
        tvCardNumber   = rootView.findViewById(R.id.tvCardNumber);
        tvCardHolder   = rootView.findViewById(R.id.tvCardHolder);
        tvExpiry       = rootView.findViewById(R.id.tvExpiry);
        tvCvv          = rootView.findViewById(R.id.tvCvv);
        tvBalance      = rootView.findViewById(R.id.tvTotalBalanceProminent);
        ivNotification   = rootView.findViewById(R.id.ivNotification);
        ivMenuHamburger  = rootView.findViewById(R.id.ivMenuHamburger);
        rvTransactions = rootView.findViewById(R.id.rvTransactions);
        btnSent        = rootView.findViewById(R.id.btnSent);
        btnReceive     = rootView.findViewById(R.id.btnReceive);
        btnLoan        = rootView.findViewById(R.id.btnLoan);
        btnTopup       = rootView.findViewById(R.id.btnTopup);

        transactions = new ArrayList<>();
        adapter = new TransactionAdapter(requireContext(), transactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTransactions.setAdapter(adapter);
    }
}
