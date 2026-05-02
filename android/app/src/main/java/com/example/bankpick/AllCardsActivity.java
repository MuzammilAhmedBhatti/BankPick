package com.example.bankpick;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bankpick.adapters.CardAdapter;
import com.example.bankpick.models.Card;
import java.util.ArrayList;

public class AllCardsActivity extends AppCompatActivity {
    RecyclerView rvCards;
    ImageView ivBack;
    Button btnAddCard;
    ArrayList<Card> cards;
    CardAdapter adapter;

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

        // Mock Cards from AllCards.tsx
        cards.clear();
        cards.add(new Card("1", "4562 1122 4595 7852", "AR Jonson", "24/2000", "6986", "Mastercard", 0));
        cards.add(new Card("2", "4562 1122 4595 7852", "Smith Jonson", "24/2000", "6986", "VISA", 1)); // 1 could mean blue gradient
        adapter.notifyDataSetChanged();
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
