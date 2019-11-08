package com.example.sitbit;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class Globals {

    public static final int MILLISECS_PER_DAY = 86400000;

    private static Globals instance;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private Globals() {
    }

    // method for getting the singleton class
    public static Globals getInstance() {
        if (instance == null)
            instance = new Globals();
        return instance;
    }

    public static Calendar[] getDayInterval(long time) {
        Calendar lastDayStart = Calendar.getInstance();
        lastDayStart.setTimeInMillis(time);
        lastDayStart.set(Calendar.AM_PM, Calendar.AM);
        lastDayStart.set(Calendar.HOUR, 0);
        lastDayStart.set(Calendar.MINUTE, 0);
        lastDayStart.set(Calendar.SECOND, 0);
        lastDayStart.set(Calendar.MILLISECOND, 0);

        Calendar lastDayEnd = Calendar.getInstance();
        lastDayEnd.setTimeInMillis(time);
        lastDayEnd.set(Calendar.DAY_OF_MONTH, lastDayEnd.get(Calendar.DAY_OF_MONTH) + 1);
        lastDayEnd.set(Calendar.AM_PM, Calendar.AM);
        lastDayEnd.set(Calendar.HOUR, 0);
        lastDayEnd.set(Calendar.MINUTE, 0);
        lastDayEnd.set(Calendar.SECOND, 0);
        lastDayEnd.set(Calendar.MILLISECOND, 0);

        return new Calendar[] { lastDayStart, lastDayEnd };
    }

    public static Calendar[] getWeekInterval(long time) {
        Calendar lastWeekStart = Calendar.getInstance();
        lastWeekStart.setTimeInMillis(time);
        lastWeekStart.set(Calendar.DAY_OF_MONTH, lastWeekStart.get(Calendar.DAY_OF_MONTH) - 6);
        lastWeekStart.set(Calendar.AM_PM, Calendar.AM);
        lastWeekStart.set(Calendar.HOUR, 0);
        lastWeekStart.set(Calendar.MINUTE, 0);
        lastWeekStart.set(Calendar.SECOND, 0);
        lastWeekStart.set(Calendar.MILLISECOND, 0);

        Calendar lastWeekEnd = Calendar.getInstance();
        lastWeekEnd.setTimeInMillis(time);
        lastWeekEnd.set(Calendar.DAY_OF_MONTH, lastWeekEnd.get(Calendar.DAY_OF_MONTH) + 1);
        lastWeekEnd.set(Calendar.AM_PM, Calendar.AM);
        lastWeekEnd.set(Calendar.HOUR, 0);
        lastWeekEnd.set(Calendar.MINUTE, 0);
        lastWeekEnd.set(Calendar.SECOND, 0);
        lastWeekEnd.set(Calendar.MILLISECOND, 0);

        return new Calendar[] { lastWeekStart, lastWeekEnd };
    }

    public static Calendar[] getMonthInterval(long time) {
        Calendar lastMonthStart = Calendar.getInstance();
        lastMonthStart.setTimeInMillis(time);
        lastMonthStart.set(Calendar.MONTH, lastMonthStart.get(Calendar.MONTH) - 1);
        lastMonthStart.set(Calendar.DAY_OF_MONTH, lastMonthStart.get(Calendar.DAY_OF_MONTH) + 1);
        lastMonthStart.set(Calendar.AM_PM, Calendar.AM);
        lastMonthStart.set(Calendar.HOUR, 0);
        lastMonthStart.set(Calendar.MINUTE, 0);
        lastMonthStart.set(Calendar.SECOND, 0);
        lastMonthStart.set(Calendar.MILLISECOND, 0);

        Calendar lastMonthEnd = Calendar.getInstance();
        lastMonthEnd.setTimeInMillis(time);
        lastMonthEnd.set(Calendar.DAY_OF_MONTH, lastMonthEnd.get(Calendar.DAY_OF_MONTH) + 1);
        lastMonthEnd.set(Calendar.AM_PM, Calendar.AM);
        lastMonthEnd.set(Calendar.HOUR, 0);
        lastMonthEnd.set(Calendar.MINUTE, 0);
        lastMonthEnd.set(Calendar.SECOND, 0);
        lastMonthEnd.set(Calendar.MILLISECOND, 0);

        return new Calendar[] { lastMonthStart, lastMonthEnd };
    }

    // method for getting the firebase authentication instance
    private FirebaseAuth getAuth() {
        if (firebaseAuth == null)
            firebaseAuth = FirebaseAuth.getInstance();

        return firebaseAuth;
    }

    // method for getting the firebase database instance
    private FirebaseDatabase getDatabase() {
        if (firebaseDatabase == null)
            firebaseDatabase = FirebaseDatabase.getInstance();

        return firebaseDatabase;
    }

    // method for checking if a user is logged into firebase
    // returns -1 if could not connect to firebase
    // returns 0 if not logged in
    // returns 1 if user is logged in
    public int isLoggedIn() {
        FirebaseAuth auth = getAuth();
        if (auth == null)
            return -1;

        return auth.getCurrentUser() == null ? 0 : 1;
    }

    // method for signing a user out of firebase
    // returns -1 if could not connect to firebase
    // returns 0 if user was not logged in in the first place
    // returns 1 if a user was successfully logged out
    public int signOut() {
        FirebaseAuth auth = getAuth();
        if (auth == null)
            return -1;

        int ret = isLoggedIn();
        if (ret == 1)
            auth.signOut();

        return ret;
    }

    // method for logging a user into firebase
    // consumer is responsible for handling the result of the login attempt
    // passes -1 if could not connect to firebase
    // passes 0 if login failed
    // passes 1 if login succeeded
    public void login(String email, String password, final Consumer<Integer> consumer) {
        FirebaseAuth auth = getAuth();
        if (auth == null || signOut() == -1)
            consumer.accept(-1);
        else {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        consumer.accept(1);
                    } else {
                        consumer.accept(0);
                    }
                }
            });
        }
    }

    // method for registering an account to the firebase
    // consumer is responsible for handling the result of the register attempt
    // passes -1 if could not connect to firebase
    // passes 0 if register failed
    // passes 1 if register succeeded
    public void register(final String email, String password, final Consumer<Integer> consumer) {
        final FirebaseAuth auth = getAuth();
        final FirebaseDatabase database = getDatabase();
        if (auth == null || database == null || signOut() == -1)
            consumer.accept(-1);
        else {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        DatabaseReference userRef = database.getReference().child("Users").child(auth.getUid());
                        userRef.child("Email").setValue(email);
                        userRef.child("Goal").setValue(0);

                        consumer.accept(1);
                    } else {
                        consumer.accept(0);
                    }
                }
            });
        }
    }

    // method for updating a user's password
    // consumer is responsible for handling the result of the updation attempt
    // passes -1 if could not connect to firebase or a user isn't signed in
    // passes 0 if password updation failed
    // passes 1 if password updation succeeded
    // *IMPORTANT* 'updation' is a word despite what other people may say
    public void updatePassword(String oldPass, final String newPass, final Consumer<Integer> consumer) {
        FirebaseAuth auth = getAuth();
        if (auth == null)
            consumer.accept(-1);
        else {
            final FirebaseUser user = auth.getCurrentUser();
            if (user == null) {
                consumer.accept(-1);
                return;
            }
            AuthCredential cred = EmailAuthProvider.getCredential(user.getEmail(), oldPass);

            user.reauthenticate(cred).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    consumer.accept(1);
                                } else {
                                    consumer.accept(0);
                                }
                            }
                        });
                    } else {
                        consumer.accept(0);
                    }
                }
            });

        }
    }

    // method for deleting a user's account
    // consumer is responsible for handling the result of the deletion attempt
    // passes -1 if could not connect to firebase or user was not signed in
    // passes 0 if deletion failed
    // passes 1 if account was successfully deleted
    public void deregister(String password, final Consumer<Integer> consumer) {
        FirebaseAuth auth = getAuth();
        final FirebaseDatabase database = getDatabase();
        if (auth == null || database == null)
            consumer.accept(-1);
        else {
            final FirebaseUser user = auth.getCurrentUser();
            if (user == null) {
                consumer.accept(-1);
                return;
            }
            AuthCredential cred = EmailAuthProvider.getCredential(user.getEmail(), password);

            user.reauthenticate(cred).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    database.getReference().child("Users").child(user.getUid()).removeValue();
                                    consumer.accept(1);
                                } else {
                                    consumer.accept(0);
                                }
                            }
                        });
                    } else {
                        consumer.accept(0);
                    }
                }
            });
        }
    }

    // method for saving a time (currenttimemillis) and associated sedentary status to the user's sedentary data
    // returns -1 if failed to connect to firebase or no user is logged on
    // returns 0 if successful
    public int saveDataEntry(long time, boolean isActive) {
        FirebaseAuth auth = getAuth();
        FirebaseDatabase database = getDatabase();
        if (auth == null || database == null)
            return -1;

        FirebaseUser user = auth.getCurrentUser();
        if (user == null)
            return -1;

        Calendar[] interval = getDayInterval(time);

        DatabaseReference dataRef = database.getReference().child("Users").child(user.getUid()).child("SedentaryData").child(new Long(interval[0].getTimeInMillis()).toString());

        dataRef.child("Date").setValue(interval[0].getTime().toString());
        dataRef.child("Data").child(new Long(time).toString()).setValue(isActive ? "true" : "false");
        return 0;
    }

    // method for retrieving an interval of sedentary data
    // startTime and endTime are in milliseconds since Jan 1st 1970
    // consumer is responsible for handling the data
    // passes null if failed to get entries
    // passes a hashmap<time, isActive> if successful
    // note* startTime must be on the boundary of a day (e.g. Oct 31st, 12:00 AM)
    public void getDataEntries(final long startTime, final long endTime, final Consumer<HashMap<Long, Boolean>> consumer) {
        FirebaseAuth auth = getAuth();
        FirebaseDatabase database = getDatabase();
        if (auth == null || database == null) {
            consumer.accept(null);
            return;
        }
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            consumer.accept(null);
            return;
        }

        DatabaseReference dataRef = database.getReference().child("Users").child(user.getUid()).child("SedentaryData");

        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                HashMap<Long, Boolean> entries = new HashMap<>();

                for (long curr = startTime; curr <= endTime; curr += MILLISECS_PER_DAY) {

                    DataSnapshot daySnapshot = dataSnapshot.child(new Long(curr).toString()).child("Data");

                    for (DataSnapshot entrySnapshot : daySnapshot.getChildren()) {
                        long time = Long.parseLong(entrySnapshot.getKey());
                        boolean isActive = Boolean.parseBoolean((String) entrySnapshot.getValue());

                        if (time >= startTime && time <= endTime)
                            entries.put(time, isActive);
                    }
                }

                consumer.accept(entries);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                consumer.accept(null);
            }
        });
    }

    public int deleteOldData() {
        FirebaseAuth auth = getAuth();
        FirebaseDatabase database = getDatabase();
        if (auth == null || database == null)
            return -1;

        FirebaseUser user = auth.getCurrentUser();
        if (user == null)
            return -1;

        final DatabaseReference dataRef = database.getReference().child("Users").child(user.getUid()).child("SedentaryData");

        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long deleteBoundary = System.currentTimeMillis() - (32 * 24 * 60 * 60 * 1000L);

                System.out.println("deleting data older than " + deleteBoundary);

                for (DataSnapshot dayData : dataSnapshot.getChildren()) {

                    System.out.println("considering " + Long.parseLong(dayData.getKey()));

                    if (Long.parseLong(dayData.getKey()) < deleteBoundary) {
                        System.out.println("deleting " + dayData.getKey());
                        dataRef.child(dayData.getKey()).removeValue();
                    }
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        return 1;
    }

    public void getGoal(final Consumer<Integer> consumer) {
        FirebaseAuth auth = getAuth();
        FirebaseDatabase database = getDatabase();
        if (auth == null || database == null) {
            consumer.accept(-1);
            return;
        }
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            consumer.accept(-1);
            return;
        }

        final DatabaseReference dataRef = database.getReference().child("Users").child(user.getUid()).child("Goal");

        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                consumer.accept(Integer.parseInt(((Long) dataSnapshot.getValue()).toString()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { consumer.accept(-1); }
        });


    }

    public int setGoal(int i) {
        FirebaseAuth auth = getAuth();
        FirebaseDatabase database = getDatabase();
        if (auth == null || database == null)
            return -1;

        FirebaseUser user = auth.getCurrentUser();
        if (user == null)
            return -1;


        if (i < 0 || i > 24)
            return 0;

        database.getReference().child("Users").child(user.getUid()).child("Goal").setValue(i);

        return 1;
    }

}