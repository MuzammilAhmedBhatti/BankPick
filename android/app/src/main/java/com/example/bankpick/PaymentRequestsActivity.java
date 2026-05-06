package com.example.bankpick;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bankpick.adapters.PaymentRequestAdapter;
import com.example.bankpick.models.PaymentRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;

public class PaymentRequestsActivity extends BaseActivity {
    private RecyclerView rvRequests;
    private TextView tvEmpty;
    private ImageView ivBack;

    private ArrayList<PaymentRequest> requests;
    private PaymentRequestAdapter adapter;

    private String currentUserId;
    private ValueEventListener requestsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_requests);

        init();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivBack.setOnClickListener(v -> finish());

        currentUserId = DatabaseHelper.getInstance().getCurrentUserId();
        if (currentUserId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        adapter = new PaymentRequestAdapter(this, requests, new PaymentRequestAdapter.PaymentRequestActionListener() {
            @Override
            public void onAccept(PaymentRequest request) {
                handleAccept(request);
            }

            @Override
            public void onDecline(PaymentRequest request) {
                handleDecline(request);
            }
        });
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        rvRequests.setAdapter(adapter);

        listenToRequests();
    }

    private void listenToRequests() {
        requestsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requests.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String status = child.child("status").getValue(String.class);
                    if (!DatabaseHelper.PAYMENT_PENDING.equals(status))
                        continue;

                    String requestId = child.child("requestId").getValue(String.class);
                    String senderUid = child.child("senderUid").getValue(String.class);
                    String receiverUid = child.child("receiverUid").getValue(String.class);
                    String senderName = child.child("senderName").getValue(String.class);
                    String receiverName = child.child("receiverName").getValue(String.class);
                    Double amount = child.child("amount").getValue(Double.class);
                    String description = child.child("description").getValue(String.class);
                    String timestamp = child.child("timestamp").getValue(String.class);
                    Long timestampMs = child.child("timestampMs").getValue(Long.class);

                    if (requestId != null) {
                        PaymentRequest request = new PaymentRequest(
                                requestId,
                                senderUid,
                                receiverUid,
                                senderName,
                                receiverName,
                                amount != null ? amount : 0,
                                description,
                                status != null ? status : DatabaseHelper.PAYMENT_PENDING,
                                timestamp,
                                timestampMs != null ? timestampMs : 0);
                        requests.add(request);
                    }
                }

                Collections.sort(requests, (a, b) -> Long.compare(b.getTimestampMs(), a.getTimestampMs()));
                adapter.notifyDataSetChanged();
                tvEmpty.setVisibility(requests.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        DatabaseHelper.getInstance()
                .paymentRequestsRef()
                .orderByChild("receiverUid")
                .equalTo(currentUserId)
                .addValueEventListener(requestsListener);
    }

    private void handleAccept(PaymentRequest request) {
        if (request.getSenderUid() == null) {
            Toast.makeText(this, "Request is missing sender details", Toast.LENGTH_LONG).show();
            return;
        }

        request.setProcessing(true);
        adapter.notifyDataSetChanged();

        DatabaseHelper.getInstance().getPrimaryCardId(currentUserId, cardId -> {
            if (cardId == null) {
                request.setProcessing(false);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "No active card found", Toast.LENGTH_LONG).show();
                return;
            }

            DatabaseHelper.getInstance().sendMoney(cardId, request.getSenderUid(),
                    request.getSenderName() != null ? request.getSenderName() : "Requester",
                    request.getAmount(),
                    (success, info) -> runOnUiThread(() -> {
                        if (!success) {
                            request.setProcessing(false);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(this, "Transfer failed: " + info, Toast.LENGTH_LONG).show();
                            return;
                        }

                        DatabaseHelper.getInstance().updatePaymentRequestStatus(request.getRequestId(),
                                DatabaseHelper.PAYMENT_APPROVED, (updated, message) -> {
                                    request.setProcessing(false);
                                    adapter.notifyDataSetChanged();

                                    if (updated) {
                                        String receiverName = request.getReceiverName() != null
                                                ? request.getReceiverName()
                                                : "The recipient";
                                        DatabaseHelper.getInstance().writeNotification(
                                                request.getSenderUid(),
                                                "Payment Request Approved",
                                                receiverName + " approved your request for $"
                                                        + String.format(java.util.Locale.getDefault(), "%.2f",
                                                                request.getAmount()),
                                                "✅",
                                                "bg-green-100");
                                        Toast.makeText(this, "Request approved", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(this, "Failed to update request", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }));
        });
    }

    private void handleDecline(PaymentRequest request) {
        request.setProcessing(true);
        adapter.notifyDataSetChanged();

        DatabaseHelper.getInstance().updatePaymentRequestStatus(request.getRequestId(),
                DatabaseHelper.PAYMENT_DECLINED, (updated, message) -> {
                    request.setProcessing(false);
                    adapter.notifyDataSetChanged();

                    if (updated) {
                        String receiverName = request.getReceiverName() != null ? request.getReceiverName()
                                : "The recipient";
                        DatabaseHelper.getInstance().writeNotification(
                                request.getSenderUid(),
                                "Payment Request Declined",
                                receiverName + " declined your request for $"
                                        + String.format(java.util.Locale.getDefault(), "%.2f", request.getAmount()),
                                "❌",
                                "bg-red-100");
                        Toast.makeText(this, "Request declined", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to update request", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void init() {
        rvRequests = findViewById(R.id.rvPaymentRequests);
        tvEmpty = findViewById(R.id.tvEmpty);
        ivBack = findViewById(R.id.btnBack);

        requests = new ArrayList<>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestsListener != null && currentUserId != null) {
            DatabaseHelper.getInstance()
                    .paymentRequestsRef()
                    .orderByChild("receiverUid")
                    .equalTo(currentUserId)
                    .removeEventListener(requestsListener);
        }
    }
}
