package com.example.bankpick.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bankpick.R;
import com.example.bankpick.models.PaymentRequest;
import java.util.ArrayList;
import java.util.Locale;

public class PaymentRequestAdapter extends RecyclerView.Adapter<PaymentRequestAdapter.PaymentRequestViewHolder> {
    public interface PaymentRequestActionListener {
        void onAccept(PaymentRequest request);

        void onDecline(PaymentRequest request);
    }

    private final Context context;
    private final ArrayList<PaymentRequest> requests;
    private final PaymentRequestActionListener listener;

    public PaymentRequestAdapter(Context context, ArrayList<PaymentRequest> requests,
            PaymentRequestActionListener listener) {
        this.context = context;
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PaymentRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_payment_request, parent, false);
        return new PaymentRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentRequestViewHolder holder, int position) {
        PaymentRequest request = requests.get(position);

        String senderName = request.getSenderName() != null ? request.getSenderName() : "Unknown";
        holder.tvTitle.setText(senderName + " requested money");
        holder.tvAmount.setText(String.format(Locale.getDefault(), "$%.2f", request.getAmount()));
        String description = request.getDescription() != null ? request.getDescription() : "";
        holder.tvDescription.setText(description);
        holder.tvDescription.setVisibility(description.isEmpty() ? View.GONE : View.VISIBLE);
        holder.tvTime.setText(request.getTimestamp() != null ? request.getTimestamp() : "");

        boolean processing = request.isProcessing();
        holder.btnAccept.setEnabled(!processing);
        holder.btnDecline.setEnabled(!processing);

        holder.btnAccept.setOnClickListener(v -> {
            if (listener != null && !request.isProcessing()) {
                listener.onAccept(request);
            }
        });

        holder.btnDecline.setOnClickListener(v -> {
            if (listener != null && !request.isProcessing()) {
                listener.onDecline(request);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public static class PaymentRequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAmount, tvDescription, tvTime;
        Button btnAccept, btnDecline;

        public PaymentRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvRequestTitle);
            tvAmount = itemView.findViewById(R.id.tvRequestAmount);
            tvDescription = itemView.findViewById(R.id.tvRequestDescription);
            tvTime = itemView.findViewById(R.id.tvRequestTime);
            btnAccept = itemView.findViewById(R.id.btnAcceptRequest);
            btnDecline = itemView.findViewById(R.id.btnDeclineRequest);
        }
    }
}
