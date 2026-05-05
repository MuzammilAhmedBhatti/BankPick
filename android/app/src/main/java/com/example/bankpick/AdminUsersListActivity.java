package com.example.bankpick;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bankpick.adapters.AdminUserAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminUsersListActivity extends AppCompatActivity {

    private RecyclerView rvUsers;
    private AdminUserAdapter adapter;
    private final List<DataSnapshot> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users_list);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvUsers = findViewById(R.id.rvUsers);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminUserAdapter(userList, ds -> {
            // Pass user id to detail screen
            String uid = ds.getKey();
            if (uid == null) return;
            Intent intent = new Intent(this, AdminUserDetailActivity.class);
            intent.putExtra("userId", uid);
            startActivity(intent);
        });
        rvUsers.setAdapter(adapter);

        loadUsers();
    }

    private void loadUsers() {
        DatabaseHelper.getInstance().usersRef()
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    // Skip admin account and soft-deleted users
                    String email = ds.child("email").getValue(String.class);
                    Boolean deleted = ds.child("isDeleted").getValue(Boolean.class);
                    if (DatabaseHelper.ADMIN_EMAIL.equalsIgnoreCase(email)) continue;
                    if (Boolean.TRUE.equals(deleted)) continue;
                    userList.add(ds);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminUsersListActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
