package com.example.bankpick;

import com.example.bankpick.services.MailService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

    public static final String NODE_USERS          = "users";
    public static final String NODE_CARDS          = "cards";
    public static final String NODE_TRANSACTIONS   = "transactions";
    public static final String NODE_NOTIFICATIONS  = "notifications";
    public static final String NODE_LOANS          = "loans";

    public static final String LOAN_PENDING  = "pending";
    public static final String LOAN_APPROVED = "approved";
    public static final String LOAN_REJECTED = "rejected";

    public static final String ADMIN_EMAIL = "admin@gmail.com";

    private static DatabaseHelper instance;
    private final FirebaseDatabase firebaseDb;
    private final FirebaseStorage firebaseStorage;
    private final MailService mailService;

    public static synchronized DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    private DatabaseHelper() {
        firebaseDb = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        mailService = new MailService();
    }

    // ─── References ────────────────────────────────────────────────────────────
    public String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public DatabaseReference usersRef()            { return firebaseDb.getReference(NODE_USERS); }
    public DatabaseReference cardsRef()            { return firebaseDb.getReference(NODE_CARDS); }
    public DatabaseReference transactionsRef()     { return firebaseDb.getReference(NODE_TRANSACTIONS); }
    public DatabaseReference notificationsRef()    { return firebaseDb.getReference(NODE_NOTIFICATIONS); }
    public DatabaseReference loansRef()            { return firebaseDb.getReference(NODE_LOANS); }
    public DatabaseReference userNotificationsRef(String uid) { return notificationsRef().child(uid); }
    public DatabaseReference userRef(String userId)   { return usersRef().child(userId); }
    public DatabaseReference cardRef(String cardId)   { return cardsRef().child(cardId); }
    public DatabaseReference transactionRef(String txnId) { return transactionsRef().child(txnId); }
    public DatabaseReference loanRef(String loanId)   { return loansRef().child(loanId); }
    public StorageReference storageRef()           { return firebaseStorage.getReference(); }
    public StorageReference profileStorageRef()    { return storageRef().child("profile_images"); }

    // ─── User Creation ─────────────────────────────────────────────────────────
    public void createNewUser(String uid, String fullName, String email, String phone) {
        Map<String, Object> user = new HashMap<>();
        user.put("userId",       uid);
        user.put("fullName",     fullName);
        user.put("email",        email.toLowerCase().trim());
        user.put("phone",        phone);
        user.put("birthDate",    "");
        user.put("joinedDate",   new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date()));
        user.put("profileImage", "");
        user.put("isBlocked",    false);
        user.put("isDeleted",    false);
        userRef(uid).setValue(user);

        // Initial card: $5000
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

    // ─── User Status ───────────────────────────────────────────────────────────
    public void isUserBlocked(String userId, BooleanCallback callback) {
        userRef(userId).child("isBlocked").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean blocked = snapshot.getValue(Boolean.class);
                callback.onResult(Boolean.TRUE.equals(blocked));
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { callback.onResult(false); }
        });
    }

    public void blockUser(String userId, SimpleCallback callback) {
        userRef(userId).child("isBlocked").setValue(true)
                .addOnSuccessListener(v -> callback.onResult(true, "User blocked"))
                .addOnFailureListener(e -> callback.onResult(false, e.getMessage()));
    }

    public void unblockUser(String userId, SimpleCallback callback) {
        userRef(userId).child("isBlocked").setValue(false)
                .addOnSuccessListener(v -> callback.onResult(true, "User unblocked"))
                .addOnFailureListener(e -> callback.onResult(false, e.getMessage()));
    }

    public void softDeleteUser(String userId, SimpleCallback callback) {
        userRef(userId).child("isDeleted").setValue(true)
                .addOnSuccessListener(v -> callback.onResult(true, "User deleted"))
                .addOnFailureListener(e -> callback.onResult(false, e.getMessage()));
    }

    // ─── Loan Management ───────────────────────────────────────────────────────
    /**
     * Checks if user already has a pending loan. Returns null if none found.
     */
    public void getUserActiveLoan(String userId, LoanCallback callback) {
        loansRef().orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String status = ds.child("status").getValue(String.class);
                    if (LOAN_PENDING.equals(status)) {
                        // Return the pending loan
                        String loanId = ds.getKey();
                        callback.onResult(loanId, status);
                        return;
                    }
                }
                callback.onResult(null, null);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { callback.onResult(null, null); }
        });
    }

    /**
     * Creates a new loan request linked to the user's active card.
     */
    public void requestLoan(String userId, String cardId, double amount, String reason,
                            String userName, String cardNumber, SimpleCallback callback) {
        String loanId = "LOAN_" + System.currentTimeMillis();
        String timestamp = new SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault()).format(new Date());

        Map<String, Object> loan = new HashMap<>();
        loan.put("loanId",     loanId);
        loan.put("userId",     userId);
        loan.put("cardId",     cardId);
        loan.put("amount",     amount);
        loan.put("reason",     reason);
        loan.put("status",     LOAN_PENDING);
        loan.put("timestamp",  timestamp);
        loan.put("userName",   userName);
        loan.put("cardNumber", cardNumber);

        loanRef(loanId).setValue(loan)
                .addOnSuccessListener(v -> {
                    writeNotification(userId, "Loan Request Submitted",
                            "Your loan request of $" + String.format(Locale.getDefault(), "%.2f", amount) + " is under review.",
                            "💰", "bg-blue-100");
                    callback.onResult(true, loanId);
                })
                .addOnFailureListener(e -> callback.onResult(false, e.getMessage()));
    }

    /**
     * Admin: Approve loan — atomically credits card balance and marks status approved.
     */
    public void approveLoan(String loanId, String cardId, String userId, double amount, SimpleCallback callback) {
        DatabaseReference balanceRef = cardRef(cardId).child("balance");
        balanceRef.runTransaction(new Transaction.Handler() {
            @NonNull @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Double bal = currentData.getValue(Double.class);
                currentData.setValue((bal == null ? 0 : bal) + amount);
                return Transaction.success(currentData);
            }
            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                if (committed) {
                    loanRef(loanId).child("status").setValue(LOAN_APPROVED)
                            .addOnSuccessListener(v -> {
                                // Record loan credit as transaction
                                String id = "TXN_LOAN_" + System.currentTimeMillis();
                                String date = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(new Date());
                                String time = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
                                writeSeedTransaction(id, cardId, "Loan Disbursement", "Loan", amount, "loan", date, time);
                                writeNotification(userId, "Loan Approved! 🎉",
                                        "$" + String.format(Locale.getDefault(), "%.2f", amount) + " has been credited to your card.",
                                        "✅", "bg-green-100");
                                callback.onResult(true, "Loan approved");
                            })
                            .addOnFailureListener(e -> callback.onResult(false, e.getMessage()));
                } else {
                    callback.onResult(false, "Balance update failed");
                }
            }
        });
    }

    /**
     * Admin: Reject loan — marks status rejected, no balance change.
     */
    public void rejectLoan(String loanId, String userId, SimpleCallback callback) {
        loanRef(loanId).child("status").setValue(LOAN_REJECTED)
                .addOnSuccessListener(v -> {
                    writeNotification(userId, "Loan Request Rejected",
                            "Your loan request has been reviewed and rejected.",
                            "❌", "bg-red-100");
                    callback.onResult(true, "Loan rejected");
                })
                .addOnFailureListener(e -> callback.onResult(false, e.getMessage()));
    }

    // ─── Contacts ─────────────────────────────────────────────────────────────
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

    public void setPrimaryCard(String userId, String cardId) {
        cardsRef().orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    boolean isTargetCard = ds.getKey().equals(cardId);
                    ds.getRef().child("isPrimary").setValue(isTargetCard);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
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

    // ─── Money Transfer ────────────────────────────────────────────────────────
    public void sendMoney(String senderCardId, String recipientUid, String recipientName, double amount, SendMoneyCallback callback) {
        String currentUid = getCurrentUserId();
        if (currentUid == null) {
            callback.onResult(false, "User not authenticated");
            return;
        }

        userRef(currentUid).child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot senderSnapshot) {
                String rawName = senderSnapshot.getValue(String.class);
                final String finalSenderName = (rawName == null) ? "Unknown" : rawName;

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
                                            writeSeedTransaction(id + "_R", finalRCardId, finalSenderName, "Transfer", amount, "transfer", date, time);

                                            String senderUid = senderCardId.replace("_card_001", "");
                                            writeNotification(senderUid, "Transfer Sent",
                                                    "You sent $" + String.format(Locale.getDefault(), "%.2f", amount) + " to " + recipientName,
                                                    "💸", "bg-blue-100");
                                            writeNotification(recipientUid, "Money Received",
                                                    "You received $" + String.format(Locale.getDefault(), "%.2f", amount) + " from " + finalSenderName,
                                                    "💰", "bg-green-100");

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
            @Override public void onCancelled(@NonNull DatabaseError error) { callback.onResult(false, "Network error"); }
        });
    }

    // ─── Card-to-Card Transfer ─────────────────────────────────────────────────
    public void transferBetweenCards(String fromCardId, String toCardId, double amount, SimpleCallback callback) {
        DatabaseReference fromRef = cardRef(fromCardId).child("balance");
        DatabaseReference toRef = cardRef(toCardId).child("balance");

        fromRef.runTransaction(new Transaction.Handler() {
            @NonNull @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Double bal = currentData.getValue(Double.class);
                if (bal == null || bal < amount) return Transaction.abort();
                currentData.setValue(bal - amount);
                return Transaction.success(currentData);
            }
            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                if (committed) {
                    toRef.runTransaction(new Transaction.Handler() {
                        @NonNull @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                            Double bal = currentData.getValue(Double.class);
                            currentData.setValue((bal == null ? 0 : bal) + amount);
                            return Transaction.success(currentData);
                        }
                        @Override
                        public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                            if (committed) {
                                String id = "TXN" + System.currentTimeMillis();
                                String date = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(new Date());
                                String time = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
                                writeSeedTransaction(id + "_D", fromCardId, "Self Transfer Out", "Transfer", -amount, "transfer", date, time);
                                writeSeedTransaction(id + "_C", toCardId, "Self Transfer In", "Transfer", amount, "transfer", date, time);
                                callback.onResult(true, "Transfer successful");
                            } else {
                                callback.onResult(false, "Credit failed");
                            }
                        }
                    });
                } else {
                    callback.onResult(false, "Insufficient balance or transaction failed");
                }
            }
        });
    }

    // ─── Shared Utilities ─────────────────────────────────────────────────────
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

    public void writeNotification(String uid, String title, String message, String icon, String color) {
        String notifId = "notif_" + System.currentTimeMillis();
        String timestamp = new SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault()).format(new Date());
        Map<String, Object> notif = new HashMap<>();
        notif.put("id",      notifId);
        notif.put("uid",     uid);
        notif.put("title",   title);
        notif.put("message", message);
        notif.put("time",    timestamp);
        notif.put("unread",  true);
        notif.put("icon",    icon);
        notif.put("color",   color);
        userNotificationsRef(uid).child(notifId).setValue(notif);
    }

    public void broadcastNotification(String title, String message, SimpleCallback callback) {
        usersRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Boolean deleted = ds.child("isDeleted").getValue(Boolean.class);
                    if (Boolean.TRUE.equals(deleted)) continue;
                    
                    String uid = ds.getKey();
                    if (uid != null) {
                        writeNotification(uid, title, message, "📢", "bg-blue-100");
                        count++;
                    }
                }
                callback.onResult(true, "Broadcast sent to " + count + " users");
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {
                callback.onResult(false, error.getMessage());
            }
        });
    }

    public void deleteCard(String cardId, SimpleCallback callback) {
        cardRef(cardId).removeValue()
                .addOnSuccessListener(v -> callback.onResult(true, "Card deleted successfully"))
                .addOnFailureListener(e -> callback.onResult(false, e.getMessage()));
    }

    // ─── OTP ──────────────────────────────────────────────────────────────────
    public void sendOtp(String email, OtpCallback callback) {
        String otp = String.format(Locale.getDefault(), "%04d", new Random().nextInt(10000));
        String uid = getCurrentUserId();
        if (uid == null) { callback.onFailure("User not logged in"); return; }
        userRef(uid).child("currentOtp").setValue(otp)
                .addOnSuccessListener(aVoid -> callback.onSuccess(otp))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
        mailService.sendEmail(email, "OTP Verification", "Your OTP is: " + otp);
    }

    public void verifyOtp(String enteredOtp, OtpVerifyCallback callback) {
        String uid = getCurrentUserId();
        if (uid == null) { callback.onResult(false); return; }
        userRef(uid).child("currentOtp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String storedOtp = snapshot.getValue(String.class);
                if (storedOtp != null && storedOtp.equals(enteredOtp)) {
                    snapshot.getRef().removeValue();
                    callback.onResult(true);
                } else {
                    callback.onResult(false);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { callback.onResult(false); }
        });
    }

    private String generateCardNumber() {
        Random r = new Random();
        return String.format("%04d %04d %04d %04d", 4000 + r.nextInt(999), r.nextInt(10000), r.nextInt(10000), r.nextInt(10000));
    }

    private String generateExpiry() {
        return String.format("%02d/%d", 1 + new Random().nextInt(12), 2027 + new Random().nextInt(4));
    }

    // ─── Callbacks ────────────────────────────────────────────────────────────
    public interface SimpleCallback    { void onResult(boolean success, String message); }
    public interface OtpCallback       { void onSuccess(String otp); void onFailure(String error); }
    public interface OtpVerifyCallback { void onResult(boolean isCorrect); }
    public interface SendMoneyCallback { void onResult(boolean success, String info); }
    public interface FindUserCallback  { void onResult(String uid, String name); }
    public interface ContactsCallback  { void onResult(List<Map<String, String>> contacts); }
    public interface LoanCallback      { void onResult(String loanId, String status); }
    public interface BooleanCallback   { void onResult(boolean value); }
}
