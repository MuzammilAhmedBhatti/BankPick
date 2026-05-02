package com.example.bankpick;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bankpick.adapters.CardAdapter;
import com.example.bankpick.models.Card;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class AllCardsActivity extends AppCompatActivity {
    RecyclerView rvCards;
    ImageView ivBack;
    Button btnAddCard;
    ArrayList<Card> cards;
    CardAdapter adapter;

    private String currentUserId;
    private ValueEventListener cardListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_cards);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        ivBack.setOnClickListener((v) -> finish());

        if (btnAddCard != null) {
            btnAddCard.setOnClickListener((v) -> startActivity(new Intent(this, AddNewCardActivity.class)));
        }

        currentUserId = DatabaseHelper.getInstance().getCurrentUserId();
        if (currentUserId != null) {
            loadCards();
        }
    }

    private void loadCards() {
        DatabaseHelper db = DatabaseHelper.getInstance();
        cardListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cards.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String userId = child.child("userId").getValue(String.class);
                    if (currentUserId.equals(userId)) {
                        Card card = child.getValue(Card.class);
                        if (card != null) {
                            cards.add(card);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        db.cardsRef().addValueEventListener(cardListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cardListener != null) {
            DatabaseHelper.getInstance().cardsRef().removeEventListener(cardListener);
        }
    }

    private void init() {
        ivBack = findViewById(R.id.ivBack);
        btnAddCard = findViewById(R.id.btnAddCard);
        
        rvCards = findViewById(R.id.rvCards);
        cards = new ArrayList<>();
        adapter = new CardAdapter(this, cards);
        rvCards.setLayoutManager(new LinearLayoutManager(this));
        rvCards.setAdapter(adapter);
    }
}
