package com.example.bankpick;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.bankpick.models.Card;
import com.example.bankpick.models.Transaction;
import com.example.bankpick.models.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Firebase Realtime Database helper.
 *
 * Database structure:
 *  /users/{userId}         → User object
 *  /cards/{cardId}         → Card object  (field userId links to user)
 *  /transactions/{txnId}   → Transaction object (field cardId links to card)
 *
 * Demo user: user_001
 * Demo primary card: card_001
 */
public class DatabaseHelper {

    // ── Node names ────────────────────────────────────────────────────────────
    public static final String NODE_USERS        = "users";
    public static final String NODE_CARDS        = "cards";
    public static final String NODE_TRANSACTIONS = "transactions";

    // ── Demo IDs ──────────────────────────────────────────────────────────────
    public static final String DEMO_USER_ID    = "user_001";
    public static final String DEMO_CARD_ID    = "card_001";
    public static final String DEMO_CARD_ID_2  = "card_002";

    private static DatabaseHelper instance;
    private final FirebaseDatabase firebaseDb;

    // ── Singleton ─────────────────────────────────────────────────────────────
    public static synchronized DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    private DatabaseHelper() {
        firebaseDb = FirebaseDatabase.getInstance();
    }

    // ── Reference helpers ─────────────────────────────────────────────────────

    public DatabaseReference usersRef() {
        return firebaseDb.getReference(NODE_USERS);
    }

    public DatabaseReference cardsRef() {
        return firebaseDb.getReference(NODE_CARDS);
    }

    public DatabaseReference transactionsRef() {
        return firebaseDb.getReference(NODE_TRANSACTIONS);
    }

    public DatabaseReference userRef(String userId) {
        return usersRef().child(userId);
    }

    public DatabaseReference cardRef(String cardId) {
        return cardsRef().child(cardId);
    }

    public DatabaseReference transactionRef(String txnId) {
        return transactionsRef().child(txnId);
    }

    // ── Seed dummy data ───────────────────────────────────────────────────────

    /**
     * Writes demo data to Firebase if it doesn't already exist.
     * Safe to call every app launch (uses setValue only once via runTransaction
     * or checks existence via a listener in the calling activity).
     *
     * Call this from SplashActivity or Application class.
     */
    public void seedDummyDataIfAbsent() {
        // Check if demo user exists; if not, seed everything
        userRef(DEMO_USER_ID).get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) {
                writeSeedData();
            }
        });
    }

    private void writeSeedData() {
        // ── User ──────────────────────────────────────────────────────────
        Map<String, Object> user = new HashMap<>();
        user.put("userId",      DEMO_USER_ID);
        user.put("fullName",    "Tanya Myroniuk");
        user.put("email",       "tanya@bankpick.com");
        user.put("phone",       "+1 (555) 123-4567");
        user.put("birthDate",   "1995-06-15");
        user.put("joinedDate",  "January 2023");
        user.put("profileImage","");
        userRef(DEMO_USER_ID).setValue(user);

        // ── Primary Card ──────────────────────────────────────────────────
        Map<String, Object> card1 = new HashMap<>();
        card1.put("cardId",     DEMO_CARD_ID);
        card1.put("userId",     DEMO_USER_ID);
        card1.put("cardNumber", "4562 1122 4595 7852");
        card1.put("holderName", "AR Jonson");
        card1.put("expiryDate", "24/2000");
        card1.put("cvv",        "6986");
        card1.put("type",       "Mastercard");
        card1.put("balance",    5000.00);
        card1.put("isPrimary",  true);
        cardRef(DEMO_CARD_ID).setValue(card1);

        // ── Second Card ───────────────────────────────────────────────────
        Map<String, Object> card2 = new HashMap<>();
        card2.put("cardId",     DEMO_CARD_ID_2);
        card2.put("userId",     DEMO_USER_ID);
        card2.put("cardNumber", "5132 0000 1234 5678");
        card2.put("holderName", "Tanya Myroniuk");
        card2.put("expiryDate", "12/2026");
        card2.put("cvv",        "321");
        card2.put("type",       "Visa");
        card2.put("balance",    1200.00);
        card2.put("isPrimary",  false);
        cardRef(DEMO_CARD_ID_2).setValue(card2);

        // ── Seed Transactions ─────────────────────────────────────────────
        writeSeedTransaction("txn_001", DEMO_CARD_ID, "Apple Store",  "Entertainment", -5.99,  "apple",    "Today",     "10:00 AM");
        writeSeedTransaction("txn_002", DEMO_CARD_ID, "Spotify",       "Music",         -12.99, "music",    "Yesterday", "11:30 AM");
        writeSeedTransaction("txn_003", DEMO_CARD_ID, "Money Transfer","Transaction",    300.00, "transfer", "Yesterday", "1:00 PM");
        writeSeedTransaction("txn_004", DEMO_CARD_ID, "Grocery",       "Shopping",       -88.00, "grocery",  "Yesterday", "4:45 PM");
    }

    private void writeSeedTransaction(String id, String cardId, String name, String category,
                                      double amount, String icon, String date, String time) {
        Map<String, Object> txn = new HashMap<>();
        txn.put("transactionId", id);
        txn.put("cardId",        cardId);
        txn.put("name",          name);
        txn.put("category",      category);
        txn.put("amount",        amount);
        txn.put("icon",          icon);
        txn.put("date",          date);
        txn.put("time",          time);
        transactionRef(id).setValue(txn);
    }

    // ── Send Money ────────────────────────────────────────────────────────────

    /**
     * Atomically deducts amount from card balance and writes a transaction record.
     *
     * Uses Firebase runTransaction to prevent race conditions.
     *
     * @param cardId        ID of the source card
     * @param recipientName Name of the recipient
     * @param amount        Positive amount to send
     * @param callback      Called with success=true/false on completion
     */
    public void sendMoney(String cardId, String recipientName, double amount,
                          SendMoneyCallback callback) {

        DatabaseReference balanceRef = cardRef(cardId).child("balance");

        balanceRef.runTransaction(new com.google.firebase.database.Transaction.Handler() {
            @Override
            public com.google.firebase.database.Transaction.Result doTransaction(
                    com.google.firebase.database.MutableData currentData) {

                Double balance = currentData.getValue(Double.class);
                if (balance == null) {
                    return com.google.firebase.database.Transaction.abort();
                }
                if (balance < amount) {
                    return com.google.firebase.database.Transaction.abort();
                }
                currentData.setValue(balance - amount);
                return com.google.firebase.database.Transaction.success(currentData);
            }

            @Override
            public void onComplete(com.google.firebase.database.DatabaseError error,
                                   boolean committed,
                                   com.google.firebase.database.DataSnapshot currentData) {
                if (committed && error == null) {
                    // Write transaction record
                    String txnId = "TXN" + System.currentTimeMillis();
                    String now  = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(new Date());
                    String time = new SimpleDateFormat("h:mm a",      Locale.getDefault()).format(new Date());

                    Map<String, Object> txn = new HashMap<>();
                    txn.put("transactionId", txnId);
                    txn.put("cardId",        cardId);
                    txn.put("name",          recipientName);
                    txn.put("category",      "Transfer");
                    txn.put("amount",        -amount);
                    txn.put("icon",          "transfer");
                    txn.put("date",          now);
                    txn.put("time",          time);

                    transactionRef(txnId).setValue(txn)
                            .addOnSuccessListener(unused -> callback.onResult(true,  txnId))
                            .addOnFailureListener(e     -> callback.onResult(false, null));
                } else {
                    // Either insufficient funds or DB error
                    String msg = (error != null) ? error.getMessage() : "Insufficient funds";
                    callback.onResult(false, msg);
                }
            }
        });
    }

    // ── Callback interfaces ───────────────────────────────────────────────────

    public interface SendMoneyCallback {
        /** @param success true if money was sent; false otherwise
         *  @param info    txnId on success, error message on failure */
        void onResult(boolean success, String info);
    }
}
