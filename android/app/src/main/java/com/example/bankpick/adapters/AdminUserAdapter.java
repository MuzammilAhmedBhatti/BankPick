package com.example.bankpick.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bankpick.R;
import com.google.firebase.database.DataSnapshot;

import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.ViewHolder> {

    public interface OnUserClickListener {
        void onUserClick(DataSnapshot userSnapshot);
    }

    private final List<DataSnapshot> users;
    private final OnUserClickListener listener;

    public AdminUserAdapter(List<DataSnapshot> users, OnUserClickListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_user, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataSnapshot ds = users.get(position);

        String name  = ds.child("fullName").getValue(String.class);
        String email = ds.child("email").getValue(String.class);
        Boolean blocked = ds.child("isBlocked").getValue(Boolean.class);

        holder.tvName.setText(name != null ? name : "Unknown");
        holder.tvEmail.setText(email != null ? email : "—");

        if (Boolean.TRUE.equals(blocked)) {
            holder.tvStatus.setText("Blocked");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_red);
        } else {
            holder.tvStatus.setText("Active");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_circle_green);
        }

        holder.itemView.setOnClickListener(v -> listener.onUserClick(ds));
    }

    @Override public int getItemCount() { return users.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvStatus;
        ViewHolder(View v) {
            super(v);
            tvName   = v.findViewById(R.id.tvUserName);
            tvEmail  = v.findViewById(R.id.tvUserEmail);
            tvStatus = v.findViewById(R.id.tvUserStatus);
        }
    }
}
