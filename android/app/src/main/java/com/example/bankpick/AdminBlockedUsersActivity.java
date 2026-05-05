package com.example.bankpick;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminBlockedUsersActivity extends AppCompatActivity {

    private RecyclerView rvBlockedUsers;
    private BlockedAdapter adapter;
    private final List<DataSnapshot> blockedUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_blocked_users);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvBlockedUsers = findViewById(R.id.rvBlockedUsers);
        rvBlockedUsers.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BlockedAdapter(blockedUsers, ds -> {
            String uid = ds.getKey();
            if (uid == null) return;
            DatabaseHelper.getInstance().unblockUser(uid, (success, msg) ->
                    Toast.makeText(this, success ? "User unblocked" : msg, Toast.LENGTH_SHORT).show());
        });
        rvBlockedUsers.setAdapter(adapter);

        DatabaseHelper.getInstance().usersRef()
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                blockedUsers.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Boolean blocked = ds.child("isBlocked").getValue(Boolean.class);
                    Boolean deleted = ds.child("isDeleted").getValue(Boolean.class);
                    if (Boolean.TRUE.equals(blocked) && !Boolean.TRUE.equals(deleted)) {
                        blockedUsers.add(ds);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Inline adapter for blocked users
    private static class BlockedAdapter extends RecyclerView.Adapter<BlockedAdapter.VH> {
        interface OnUnblock { void onUnblock(DataSnapshot ds); }
        private final List<DataSnapshot> list;
        private final OnUnblock cb;
        BlockedAdapter(List<DataSnapshot> list, OnUnblock cb) { this.list = list; this.cb = cb; }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_admin_blocked_user, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            DataSnapshot ds = list.get(pos);
            String name  = ds.child("fullName").getValue(String.class);
            String email = ds.child("email").getValue(String.class);
            h.tvName.setText(name != null ? name : "Unknown");
            h.tvEmail.setText(email != null ? email : "—");
            h.btnUnblock.setOnClickListener(v -> cb.onUnblock(ds));
        }

        @Override public int getItemCount() { return list.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvEmail;
            Button btnUnblock;
            VH(View v) {
                super(v);
                tvName    = v.findViewById(R.id.tvBlockedUserName);
                tvEmail   = v.findViewById(R.id.tvBlockedUserEmail);
                btnUnblock = v.findViewById(R.id.btnUnblockUser);
            }
        }
    }
}
