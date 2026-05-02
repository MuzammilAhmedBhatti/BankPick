package com.example.bankpick.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bankpick.R;
import com.example.bankpick.TransactionDetailActivity;
import com.example.bankpick.models.Transaction;
import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    Context context;
    ArrayList<Transaction> transactions;

    public TransactionAdapter(Context context, ArrayList<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.tvName.setText(transaction.getName());
        holder.tvCategory.setText(transaction.getCategory());

        double amount = transaction.getAmount();
        if (amount > 0) {
            holder.tvAmount.setText(String.format("+$%.2f", amount));
            holder.tvAmount.setTextColor(context.getResources().getColor(R.color.primary, null));
        } else {
            holder.tvAmount.setText(String.format("- $%.2f", Math.abs(amount)));
            holder.tvAmount.setTextColor(context.getResources().getColor(R.color.text_primary, null));
        }

        String icon = transaction.getIcon();
        if (icon != null) {
            switch (icon) {
                case "apple": 
                    holder.tvIcon.setText("🍎"); 
                    holder.tvIcon.setBackgroundResource(R.drawable.bg_rounded_rect_gray);
                    break;
                case "music": 
                    holder.tvIcon.setText("🎵"); 
                    holder.tvIcon.setBackgroundResource(R.drawable.bg_circle_blue); // Close enough for green-100 without a custom shape
                    break;
                case "transfer": 
                    holder.tvIcon.setText("💸"); 
                    holder.tvIcon.setBackgroundResource(R.drawable.bg_rounded_rect_gray);
                    break;
                case "grocery": 
                    holder.tvIcon.setText("🛒"); 
                    holder.tvIcon.setBackgroundResource(R.drawable.bg_circle_white); // Use circle
                    break;
                default: 
                    holder.tvIcon.setText("💰"); 
                    holder.tvIcon.setBackgroundResource(R.drawable.bg_circle);
                    break;
            }
        }

        holder.itemView.setOnClickListener((v) -> {
            Intent intent = new Intent(context, TransactionDetailActivity.class);
            intent.putExtra("name", transaction.getName());
            intent.putExtra("category", transaction.getCategory());
            intent.putExtra("amount", transaction.getAmount());
            intent.putExtra("date", transaction.getDate());
            intent.putExtra("time", transaction.getTime());
            intent.putExtra("icon", transaction.getIcon());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvName, tvCategory, tvAmount;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tvIcon);
            tvName = itemView.findViewById(R.id.tvName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}
