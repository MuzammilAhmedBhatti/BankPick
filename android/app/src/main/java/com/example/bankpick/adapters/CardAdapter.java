package com.example.bankpick.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bankpick.R;
import com.example.bankpick.models.Card;
import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    Context context;
    ArrayList<Card> cards;

    public CardAdapter(Context context, ArrayList<Card> cards) {
        this.context = context;
        this.cards = cards;
    }

    @NonNull @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card card = cards.get(position);
        holder.tvCardNumber.setText(card.getCardNumber());
        holder.tvHolderName.setText(card.getHolderName());
        holder.tvExpiry.setText(card.getExpiryDate());
        holder.tvType.setText(card.getType());
        holder.tvBalance.setText(String.format("$%,.2f", card.getBalance()));
    }

    @Override public int getItemCount() { return cards.size(); }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        TextView tvCardNumber, tvHolderName, tvExpiry, tvType, tvBalance;
        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCardNumber = itemView.findViewById(R.id.tvCardNumber);
            tvHolderName = itemView.findViewById(R.id.tvHolderName);
            tvExpiry = itemView.findViewById(R.id.tvExpiry);
            tvType = itemView.findViewById(R.id.tvType);
            tvBalance = itemView.findViewById(R.id.tvBalance);
        }
    }
}
