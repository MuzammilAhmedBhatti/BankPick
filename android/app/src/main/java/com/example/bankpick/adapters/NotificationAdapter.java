package com.example.bankpick.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bankpick.R;
import com.example.bankpick.models.BankNotification;
import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    Context context;
    ArrayList<BankNotification> notifications;

    public NotificationAdapter(Context context, ArrayList<BankNotification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        BankNotification n = notifications.get(position);
        holder.tvTitle.setText(n.getTitle());
        holder.tvMessage.setText(n.getMessage());
        holder.tvTime.setText(n.getTime());
        holder.tvIcon.setText(n.getIcon());

        if (n.isUnread()) {
            holder.ivUnreadDot.setVisibility(View.VISIBLE);
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.text_primary, null));
            holder.itemView.setBackgroundResource(R.drawable.bg_rounded_rect_blue_light); // Assuming we have this
        } else {
            holder.ivUnreadDot.setVisibility(View.GONE);
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.text_secondary, null));
            holder.itemView.setBackgroundResource(R.drawable.bg_rounded_rect_gray);
        }

        // Set dynamic circle background color
        GradientDrawable bgShape = (GradientDrawable) context.getResources().getDrawable(R.drawable.bg_circle_white).mutate();
        switch (n.getColor()) {
            case "bg-green-100": bgShape.setColor(Color.parseColor("#DCFCE7")); break;
            case "bg-blue-100": bgShape.setColor(Color.parseColor("#DBEAFE")); break;
            case "bg-orange-100": bgShape.setColor(Color.parseColor("#FFEDD5")); break;
            case "bg-purple-100": bgShape.setColor(Color.parseColor("#F3E8FF")); break;
            case "bg-yellow-100": bgShape.setColor(Color.parseColor("#FEF9C3")); break;
            default: bgShape.setColor(Color.WHITE); break;
        }
        holder.tvIcon.setBackground(bgShape);

        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(context, com.example.bankpick.NotificationDetailActivity.class);
            intent.putExtra("id", n.getId());
            intent.putExtra("title", n.getTitle());
            intent.putExtra("message", n.getMessage());
            intent.putExtra("time", n.getTime());
            intent.putExtra("icon", n.getIcon());
            intent.putExtra("color", n.getColor());
            context.startActivity(intent);
        });
    }

    @Override public int getItemCount() { return notifications.size(); }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTime, tvIcon;
        ImageView ivUnreadDot;
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvIcon = itemView.findViewById(R.id.tvIcon);
            ivUnreadDot = itemView.findViewById(R.id.ivUnreadDot);
        }
    }
}
