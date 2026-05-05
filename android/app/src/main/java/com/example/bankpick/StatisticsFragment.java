package com.example.bankpick;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bankpick.adapters.TransactionAdapter;
import com.example.bankpick.models.Transaction;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    View rootView;
    TextView tvBalance, tvSeeAll;
    RecyclerView rvTransactions;
    ArrayList<Transaction> transactions;
    ArrayList<Transaction> allTransactions = new ArrayList<>();
    TransactionAdapter adapter;
    LineChart lineChart;
    TextView[] monthViews;
    String activeCardId;
    String[] dynamicMonths = new String[7];
    String currentMonthName;
    String selectedMonthName;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
        generateDynamicMonths();
        init();
        
        String uid = DatabaseHelper.getInstance().getCurrentUserId();
        if (uid != null) {
            activeCardId = uid + "_card_001";
            loadRealData();
            listenToBalance();
        }

        tvSeeAll.setOnClickListener((v) -> startActivity(new Intent(requireContext(), TransactionHistoryActivity.class)));
        rootView.findViewById(R.id.ivNotifications).setOnClickListener((v) ->
                startActivity(new Intent(requireContext(), NotificationsActivity.class)));

        return rootView;
    }

    private void generateDynamicMonths() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        currentMonthName = sdf.format(cal.getTime());
        selectedMonthName = currentMonthName;
        
        cal.add(Calendar.MONTH, -6); // Start from 6 months ago
        for (int i = 0; i < 7; i++) {
            dynamicMonths[i] = sdf.format(cal.getTime());
            cal.add(Calendar.MONTH, 1);
        }
    }

    private void listenToBalance() {
        DatabaseHelper.getInstance().cardRef(activeCardId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;
                Double balance = snapshot.child("balance").getValue(Double.class);
                if (balance != null) {
                    tvBalance.setText(String.format(Locale.getDefault(), "$%,.2f", balance));
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadRealData() {
        DatabaseHelper.getInstance().transactionsRef()
                .orderByChild("cardId").equalTo(activeCardId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;
                allTransactions.clear();
                Map<String, Double> monthlyTotals = new HashMap<>();
                
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Transaction t = ds.getValue(Transaction.class);
                    if (t != null) {
                        allTransactions.add(t);
                        
                        String date = t.getDate();
                        String monthKey = null;
                        if (date != null) {
                            if (date.equalsIgnoreCase("Today")) {
                                monthKey = currentMonthName;
                            } else if (date.length() >= 3) {
                                monthKey = date.substring(0, 3);
                            }
                        }
                        
                        if (monthKey != null) {
                            double amount = Math.abs(t.getAmount());
                            monthlyTotals.put(monthKey, monthlyTotals.getOrDefault(monthKey, 0.0) + amount);
                        }
                    }
                }
                
                Collections.sort(allTransactions, (t1, t2) -> t2.getTransactionId().compareTo(t1.getTransactionId()));
                
                updateChart(monthlyTotals);
                refreshSelection();
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateChart(Map<String, Double> monthlyTotals) {
        if (!isAdded()) return;
        List<Entry> entries = new ArrayList<>();
        
        for (int i = 0; i < dynamicMonths.length; i++) {
            float val = monthlyTotals.getOrDefault(dynamicMonths[i], 0.0).floatValue();
            entries.add(new Entry(i, val));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Volume");
        dataSet.setColor(ContextCompat.getColor(requireContext(), R.color.primary));
        dataSet.setValueTextColor(Color.TRANSPARENT);
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(4f);
        dataSet.setCircleColor(ContextCompat.getColor(requireContext(), R.color.primary));
        dataSet.setDrawFilled(true);
        try {
            dataSet.setFillDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.chart_gradient));
        } catch (Exception e) {
            dataSet.setFillColor(ContextCompat.getColor(requireContext(), R.color.primary_light));
        }
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.notifyDataSetChanged();
        lineChart.animateX(800);
        lineChart.invalidate();
    }

    private void init() {
        tvBalance = rootView.findViewById(R.id.tvBalance);
        tvSeeAll = rootView.findViewById(R.id.tvSeeAll);
        rvTransactions = rootView.findViewById(R.id.rvTransactions);
        lineChart = rootView.findViewById(R.id.lineChart);
        
        // Setup chart appearance
        lineChart.setNoDataText("Loading statistics...");
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dynamicMonths));
        xAxis.setLabelCount(7);
        xAxis.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_hint));
        
        lineChart.getAxisLeft().setDrawLabels(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawAxisLine(false);
        lineChart.getAxisRight().setEnabled(false);
        
        // Initial empty chart to avoid "No data"
        updateChart(new HashMap<>());
        
        transactions = new ArrayList<>();
        adapter = new TransactionAdapter(requireContext(), transactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTransactions.setAdapter(adapter);

        monthViews = new TextView[]{
                rootView.findViewById(R.id.tvMonth1),
                rootView.findViewById(R.id.tvMonth2),
                rootView.findViewById(R.id.tvMonth3),
                rootView.findViewById(R.id.tvMonth4),
                rootView.findViewById(R.id.tvMonth5),
                rootView.findViewById(R.id.tvMonth6),
                rootView.findViewById(R.id.tvMonth7)
        };

        for (int i = 0; i < monthViews.length; i++) {
            if (monthViews[i] == null) continue;
            monthViews[i].setText(dynamicMonths[i]);
            final int index = i;
            monthViews[i].setOnClickListener(v -> selectMonth(monthViews[index], dynamicMonths[index]));
        }
        
        // Default highlight (Current Month)
        refreshSelection();
    }

    private void refreshSelection() {
        if (selectedMonthName == null) selectedMonthName = currentMonthName;
        for (int i = 0; i < dynamicMonths.length; i++) {
            if (dynamicMonths[i].equals(selectedMonthName)) {
                if (monthViews[i] != null) {
                    selectMonth(monthViews[i], dynamicMonths[i]);
                }
                return;
            }
        }
    }

    private void selectMonth(TextView selected, String monthName) {
        selectedMonthName = monthName;
        if (!isAdded()) return;
        
        for (TextView tv : monthViews) {
            if (tv != null) {
                tv.setBackgroundResource(R.drawable.bg_month_chip);
                tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
            }
        }
        selected.setBackgroundResource(R.drawable.bg_month_chip_selected);
        selected.setTextColor(Color.WHITE);
        
        filterTransactions(monthName);
    }

    private void filterTransactions(String month) {
        ArrayList<Transaction> filtered = new ArrayList<>();
        for (Transaction t : allTransactions) {
            String date = t.getDate();
            boolean matches = false;
            if (date != null) {
                if (date.equalsIgnoreCase("Today") && month.equals(currentMonthName)) {
                    matches = true;
                } else if (date.startsWith(month)) {
                    matches = true;
                }
            }
            if (matches) {
                filtered.add(t);
            }
        }
        transactions.clear();
        transactions.addAll(filtered);
        adapter.notifyDataSetChanged();
    }
}
