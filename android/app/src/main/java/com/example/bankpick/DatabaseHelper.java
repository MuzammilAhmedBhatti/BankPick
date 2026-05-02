package com.example.bankpick;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * Firebase Realtime Database helper — per-user data.
 *
 * Database structure:
 *   /users/{uid}                → User profile
 *   /cards/{uid}_{seq}          → Card (field "userId" links to owner)
 *   /transactions/{txnId}       → Transaction (field "cardId" links to card)
 *
 * When a new user signs up, createNewUser() provisions:
 *   - a user profile node
 *   - a primary Mastercard with $5,000 balance
 *   - 4 sample transactions
 */
public class DatabaseHelper {

    // ── Node names ────────────────────────────────────────────────────────────
    public static final String NODE_USERS        = "users";
    public static final String NODE_CARDS        = "cards";
    public static final String NODE_TRANSACTIONS = "transactions";

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

    // ── Current user helper ───────────────────────────────────────────────────

    /**
     * Returns the Firebase Auth UID of the currently logged-in user, or null.
     */
    public String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : null;
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

    // ── New user provisioning ─────────────────────────────────────────────────

    /**
     * Creates a full user profile + default card + sample transactions
     * in Firebase Realtime Database.
     *
     * Called once after Firebase Auth sign-up succeeds.
     *
     * @param uid       Firebase Auth UID
     * @param fullName  Display name entered during sign-up
     * @param email     Email used for sign-up
     * @param phone     Phone number entered during sign-up
     */
    public void createNewUser(String uid, String fullName, String email, String phone) {
        // ── User profile ──────────────────────────────────────────────────
        Map<String, Object> user = new HashMap<>();
        user.put("userId",       uid);
        user.put("fullName",     fullName);
        user.put("email",        email);
        user.put("phone",        phone);
        user.put("birthDate",    "");
        user.put("joinedDate",   new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date()));
        user.put("profileImage", "");
        userRef(uid).setValue(user);

        // ── Primary Card (Mastercard, $5,000 starting balance) ────────────
        String cardId = uid + "_card_001";
        String cardNumber = generateCardNumber();

        Map<String, Object> card = new HashMap<>();
        card.put("cardId",     cardId);
        card.put("userId",     uid);
        card.put("cardNumber", cardNumber);
        card.put("holderName", fullName);
        card.put("expiryDate", generateExpiry());
        card.put("cvv",        String.valueOf(100 + new Random().nextInt(900)));
        card.put("type",       "Mastercard");
        card.put("balance",    5000.00);
        card.put("isPrimary",  true);
        cardRef(cardId).setValue(card);

        // ── Sample transactions so the home screen isn't empty ────────────
        writeSeedTransaction("txn_" + uid + "_001", cardId,
                "Welcome Bonus", "Reward", 500.00, "transfer",
                "Today", "12:00 PM");
        writeSeedTransaction("txn_" + uid + "_002", cardId,
                "Spotify", "Music", -12.99, "music",
                "Today", "10:30 AM");
        writeSeedTransaction("txn_" + uid + "_003", cardId,
                "Apple Store", "Entertainment", -5.99, "apple",
                "Today", "9:00 AM");
    }

    // ── Seed helpers ──────────────────────────────────────────────────────────

    /**
     * @deprecated Use createNewUser() instead. Kept only for backward compat
     * with the demo user_001 if it still exists.
     */
    @Deprecated
    public void seedDummyDataIfAbsent() {
        String uid = getCurrentUserId();
        if (uid == null) return;
        userRef(uid).get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) {
                createNewUser(uid, "Demo User", "demo@bankpick.com", "+1 000 000 0000");
            }
        });
    }

    private void writeSeedTransaction(String id, String cardId, String name,
                                      String category, double amount, String icon,
                                      String date, String time) {
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

    // ── Card number generator ─────────────────────────────────────────────────

    private String generateCardNumber() {
        Random r = new Random();
        return String.format("%04d %04d %04d %04d",
                4000 + r.nextInt(999),
                r.nextInt(10000),
                r.nextInt(10000),
                r.nextInt(10000));
    }

    private String generateExpiry() {
        int month = 1 + new Random().nextInt(12);
        int year  = 2027 + new Random().nextInt(4); // 2027-2030
        return String.format("%02d/%d", month, year);
    }

    // ── Send Money ────────────────────────────────────────────────────────────

    /**
     * Atomically deducts amount from card balance and writes a transaction record.
     * Uses Firebase runTransaction to prevent race conditions.
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
                    String msg = (error != null) ? error.getMessage() : "Insufficient funds";
                    callback.onResult(false, msg);
                }
            }
        });
    }

    // ── Callback ──────────────────────────────────────────────────────────────

    public interface SendMoneyCallback {
        void onResult(boolean success, String info);
    }
}
