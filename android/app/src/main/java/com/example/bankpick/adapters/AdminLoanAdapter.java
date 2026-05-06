package com.example.bankpick.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bankpick.DatabaseHelper;
import com.example.bankpick.R;
import com.example.bankpick.models.Loan;

import java.util.List;
import java.util.Locale;

public class AdminLoanAdapter extends RecyclerView.Adapter<AdminLoanAdapter.ViewHolder> {

    public interface LoanActionListener {
        void onApprove(Loan loan);
        void onReject(Loan loan);
    }

    private final List<Loan> loans;
    private final LoanActionListener listener;

    public AdminLoanAdapter(List<Loan> loans, LoanActionListener listener) {
        this.loans = loans;
        this.listener = listener;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_loan, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Loan loan = loans.get(position);

        h.tvUserName.setText(loan.getUserName() != null ? loan.getUserName() : "Unknown");
        h.tvCardNumber.setText("Card: " + (loan.getCardNumber() != null ? loan.getCardNumber() : "—"));
        h.tvAmount.setText(String.format(Locale.getDefault(), "$%.2f", loan.getAmount()));
        h.tvReason.setText("Reason: " + (loan.getReason() != null ? loan.getReason() : "—"));
        h.tvTimestamp.setText(loan.getTimestamp() != null ? loan.getTimestamp() : "—");

        String status = loan.getStatus();
        if (DatabaseHelper.LOAN_PENDING.equals(status)) {
            h.llActions.setVisibility(View.VISIBLE);
            h.tvStatus.setVisibility(View.GONE);
            h.btnApprove.setOnClickListener(v -> listener.onApprove(loan));
            h.btnReject.setOnClickListener(v -> listener.onReject(loan));
        } else {
            h.llActions.setVisibility(View.GONE);
            h.tvStatus.setVisibility(View.VISIBLE);
            h.tvStatus.setText(status != null ? status.toUpperCase() : "—");
            if (DatabaseHelper.LOAN_APPROVED.equals(status)) {
                h.tvStatus.setBackgroundResource(R.drawable.bg_badge_green_pill);
                h.tvStatus.setTextColor(0xFF065f46);
            } else {
                h.tvStatus.setBackgroundResource(R.drawable.bg_badge_red_pill);
                h.tvStatus.setTextColor(0xFF991b1b);
            }
        }
    }

    @Override public int getItemCount() { return loans.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvCardNumber, tvAmount, tvReason, tvTimestamp, tvStatus;
        LinearLayout llActions, btnApprove, btnReject;

        ViewHolder(View v) {
            super(v);
            tvUserName   = v.findViewById(R.id.tvLoanUserName);
            tvCardNumber = v.findViewById(R.id.tvLoanCardNumber);
            tvAmount     = v.findViewById(R.id.tvLoanAmount);
            tvReason     = v.findViewById(R.id.tvLoanReason);
            tvTimestamp  = v.findViewById(R.id.tvLoanTimestamp);
            tvStatus     = v.findViewById(R.id.tvLoanStatus);
            llActions    = v.findViewById(R.id.llLoanActions);
            btnApprove   = v.findViewById(R.id.btnApproveLoan);
            btnReject    = v.findViewById(R.id.btnRejectLoan);
        }
    }
}
