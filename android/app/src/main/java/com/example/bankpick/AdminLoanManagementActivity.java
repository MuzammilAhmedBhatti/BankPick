package com.example.bankpick;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bankpick.adapters.AdminLoanAdapter;
import com.example.bankpick.models.Loan;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminLoanManagementActivity extends BaseActivity {

    private RecyclerView rvLoans;
    private AdminLoanAdapter adapter;
    private final List<Loan> displayedLoans = new ArrayList<>();
    private final List<Loan> allLoans = new ArrayList<>();
    private String currentFilter = DatabaseHelper.LOAN_PENDING;

    private Button btnFilterPending, btnFilterApproved, btnFilterRejected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_loan_management);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btnFilterPending  = findViewById(R.id.btnFilterPending);
        btnFilterApproved = findViewById(R.id.btnFilterApproved);
        btnFilterRejected = findViewById(R.id.btnFilterRejected);

        rvLoans = findViewById(R.id.rvLoans);
        rvLoans.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminLoanAdapter(displayedLoans, new AdminLoanAdapter.LoanActionListener() {
            @Override
            public void onApprove(Loan loan) { approveLoan(loan); }
            @Override
            public void onReject(Loan loan)  { rejectLoan(loan); }
        });
        rvLoans.setAdapter(adapter);

        btnFilterPending.setOnClickListener(v  -> setFilter(DatabaseHelper.LOAN_PENDING));
        btnFilterApproved.setOnClickListener(v -> setFilter(DatabaseHelper.LOAN_APPROVED));
        btnFilterRejected.setOnClickListener(v -> setFilter(DatabaseHelper.LOAN_REJECTED));

        loadLoans();
    }

    private void setFilter(String filter) {
        currentFilter = filter;
        updateFilterButtons();
        applyFilter();
    }

    private void updateFilterButtons() {
        int activeRes  = R.drawable.bg_btn_gradient_blue;
        int defaultRes = R.drawable.bg_quick_amount;
        int activeColor  = getResources().getColor(R.color.white);
        int defaultColor = getResources().getColor(R.color.text_primary);

        btnFilterPending.setBackgroundResource(DatabaseHelper.LOAN_PENDING.equals(currentFilter) ? activeRes : defaultRes);
        btnFilterPending.setTextColor(DatabaseHelper.LOAN_PENDING.equals(currentFilter) ? activeColor : defaultColor);

        btnFilterApproved.setBackgroundResource(DatabaseHelper.LOAN_APPROVED.equals(currentFilter) ? activeRes : defaultRes);
        btnFilterApproved.setTextColor(DatabaseHelper.LOAN_APPROVED.equals(currentFilter) ? activeColor : defaultColor);

        btnFilterRejected.setBackgroundResource(DatabaseHelper.LOAN_REJECTED.equals(currentFilter) ? activeRes : defaultRes);
        btnFilterRejected.setTextColor(DatabaseHelper.LOAN_REJECTED.equals(currentFilter) ? activeColor : defaultColor);
    }

    private void applyFilter() {
        displayedLoans.clear();
        for (Loan loan : allLoans) {
            if (currentFilter.equals(loan.getStatus())) {
                displayedLoans.add(loan);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void loadLoans() {
        DatabaseHelper.getInstance().loansRef()
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allLoans.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Loan loan = ds.getValue(Loan.class);
                    if (loan != null) allLoans.add(loan);
                }
                applyFilter();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminLoanManagementActivity.this, "Failed to load loans", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void approveLoan(Loan loan) {
        DatabaseHelper.getInstance().approveLoan(
                loan.getLoanId(), loan.getCardId(), loan.getUserId(), loan.getAmount(),
                (success, msg) -> Toast.makeText(this,
                        success ? "Loan approved! $" + String.format("%.2f", loan.getAmount()) + " credited." : "Failed: " + msg,
                        Toast.LENGTH_SHORT).show());
    }

    private void rejectLoan(Loan loan) {
        DatabaseHelper.getInstance().rejectLoan(
                loan.getLoanId(), loan.getUserId(),
                (success, msg) -> Toast.makeText(this,
                        success ? "Loan rejected." : "Failed: " + msg,
                        Toast.LENGTH_SHORT).show());
    }
}
