package com.example.bankpick;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;

public class DatabaseHelper {

    public static final String NODE_USERS        = "users";
    public static final String NODE_CARDS        = "cards";
    public static final String NODE_TRANSACTIONS = "transactions";

    private static DatabaseHelper instance;
    private final FirebaseDatabase firebaseDb;

    public static synchronized DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    private DatabaseHelper() {
        firebaseDb = FirebaseDatabase.getInstance();
    }

    public String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public DatabaseReference usersRef() { return firebaseDb.getReference(NODE_USERS); }
    public DatabaseReference cardsRef() { return firebaseDb.getReference(NODE_CARDS); }
    public DatabaseReference transactionsRef() { return firebaseDb.getReference(NODE_TRANSACTIONS); }
    public DatabaseReference userRef(String userId) { return usersRef().child(userId); }
    public DatabaseReference cardRef(String cardId) { return cardsRef().child(cardId); }
    public DatabaseReference transactionRef(String txnId) { return transactionsRef().child(txnId); }

    public void createNewUser(String uid, String fullName, String email, String phone) {
        Map<String, Object> user = new HashMap<>();
        user.put("userId",       uid);
        user.put("fullName",     fullName);
        user.put("email",        email.toLowerCase().trim()); // Unified case
        user.put("phone",        phone);
        user.put("birthDate",    "");
        user.put("joinedDate",   new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date()));
        user.put("profileImage", "");
        userRef(uid).setValue(user);

        // Initial Balance: $5000
        String cardId = uid + "_card_001";
        Map<String, Object> card = new HashMap<>();
        card.put("cardId",     cardId);
        card.put("userId",     uid);
        card.put("cardNumber", generateCardNumber());
        card.put("holderName", fullName);
        card.put("expiryDate", generateExpiry());
        card.put("cvv",        String.valueOf(100 + new Random().nextInt(900)));
        card.put("type",       "Mastercard");
        card.put("balance",    5000.0);
        card.put("isPrimary",  true);
        cardRef(cardId).setValue(card);

        writeSeedTransaction("txn_" + uid + "_001", cardId, "Welcome Bonus", "Reward", 500.0, "transfer", "Today", "12:00 PM");
    }

    public void findUserByEmail(String email, FindUserCallback callback) {
        String searchEmail = email.toLowerCase().trim();
        usersRef().orderByChild("email").equalTo(searchEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String uid = ds.getKey();
                        String name = ds.child("fullName").getValue(String.class);
                        callback.onResult(uid, name);
                        return;
                    }
                }
                callback.onResult(null, null);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { callback.onResult(null, null); }
        });
    }

    public void addContact(String currentUid, String contactUid, String contactName) {
        Map<String, Object> contact = new HashMap<>();
        contact.put("uid", contactUid);
        contact.put("name", contactName);
        userRef(currentUid).child("contacts").child(contactUid).setValue(contact);
    }

    public void getContacts(String currentUid, ContactsCallback callback) {
        userRef(currentUid).child("contacts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Map<String, String>> contacts = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, String> c = new HashMap<>();
                    c.put("uid", ds.child("uid").getValue(String.class));
                    c.put("name", ds.child("name").getValue(String.class));
                    contacts.add(c);
                }
                callback.onResult(contacts);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { callback.onResult(new ArrayList<>()); }
        });
    }

    public void sendMoney(String senderCardId, String recipientUid, String recipientName, double amount, SendMoneyCallback callback) {
        // Find recipient's card
        cardsRef().orderByChild("userId").equalTo(recipientUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String rCardId = null;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    rCardId = ds.getKey();
                    if (Boolean.TRUE.equals(ds.child("isPrimary").getValue(Boolean.class))) break;
                }
                if (rCardId == null) { callback.onResult(false, "Recipient card not found"); return; }

                final String finalRCardId = rCardId;
                DatabaseReference sRef = cardRef(senderCardId).child("balance");
                sRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData currentData) {
                        Double bal = currentData.getValue(Double.class);
                        if (bal == null || bal < amount) return Transaction.abort();
                        currentData.setValue(bal - amount);
                        return Transaction.success(currentData);
                    }
                    @Override
                    public void onComplete(DatabaseError e, boolean committed, DataSnapshot snap) {
                        if (committed) {
                            DatabaseReference rRef = cardRef(finalRCardId).child("balance");
                            rRef.runTransaction(new Transaction.Handler() {
                                @Override
                                public Transaction.Result doTransaction(MutableData data) {
                                    Double b = data.getValue(Double.class);
                                    data.setValue((b == null ? 0 : b) + amount);
                                    return Transaction.success(data);
                                }
                                @Override
                                public void onComplete(DatabaseError e, boolean committed, DataSnapshot s) {
                                    String id = "TXN" + System.currentTimeMillis();
                                    String date = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(new Date());
                                    String time = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
                                    writeSeedTransaction(id + "_S", senderCardId, recipientName, "Transfer", -amount, "transfer", date, time);
                                    writeSeedTransaction(id + "_R", finalRCardId, "Received", "Transfer", amount, "transfer", date, time);
                                    callback.onResult(true, id + "_S");
                                }
                            });
                        } else {
                            callback.onResult(false, "Transaction failed or insufficient funds");
                        }
                    }
                });
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { callback.onResult(false, "Network error"); }
        });
    }

    private void writeSeedTransaction(String id, String cardId, String name, String category, double amount, String icon, String date, String time) {
        Map<String, Object> txn = new HashMap<>();
        txn.put("transactionId", id);
        txn.put("cardId", cardId);
        txn.put("name", name);
        txn.put("category", category);
        txn.put("amount", amount);
        txn.put("icon", icon);
        txn.put("date", date);
        txn.put("time", time);
        transactionRef(id).setValue(txn);
    }

    private String generateCardNumber() {
        Random r = new Random();
        return String.format("%04d %04d %04d %04d", 4000 + r.nextInt(999), r.nextInt(10000), r.nextInt(10000), r.nextInt(10000));
    }

    private String generateExpiry() {
        return String.format("%02d/%d", 1 + new Random().nextInt(12), 2027 + new Random().nextInt(4));
    }

    // --- OTP Feature Implementation ---

    /**
     * Sends a dummy OTP to the specified email (for now just stores in DB).
     */
    public void sendOtp(String email, OtpCallback callback) {
        // Generating a dummy 6-digit OTP
        String otp = String.format(Locale.getDefault(), "%06d", new Random().nextInt(1000000));
        
        // Storing it under the current user for verification
        String uid = getCurrentUserId();
        if (uid == null) {
            callback.onFailure("User not logged in");
            return;
        }

        userRef(uid).child("currentOtp").setValue(otp)
            .addOnSuccessListener(aVoid -> callback.onSuccess(otp))
            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    /**
     * Verifies the entered OTP against the one stored in the database.
     */
    public void verifyOtp(String enteredOtp, OtpVerifyCallback callback) {
        String uid = getCurrentUserId();
        if (uid == null) {
            callback.onResult(false);
            return;
        }

        userRef(uid).child("currentOtp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String storedOtp = snapshot.getValue(String.class);
                if (storedOtp != null && storedOtp.equals(enteredOtp)) {
                    // Correct OTP, clear it from database
                    snapshot.getRef().removeValue();
                    callback.onResult(true);
                } else {
                    callback.onResult(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onResult(false);
            }
        });
    }

    public interface OtpCallback { void onSuccess(String otp); void onFailure(String error); }
    public interface OtpVerifyCallback { void onResult(boolean isCorrect); }
    public interface SendMoneyCallback { void onResult(boolean success, String info); }
    public interface FindUserCallback { void onResult(String uid, String name); }
    public interface ContactsCallback { void onResult(List<Map<String, String>> contacts); }
}
