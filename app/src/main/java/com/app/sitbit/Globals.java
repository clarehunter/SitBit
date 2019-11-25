package com.app.sitbit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

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
import java.util.HashMap;


public class Globals {

    public static final String NOTIF_CHANNEL = "SitBit";
    public static final int NOTIF_REQUEST_CODE = 47;
    public static final int NOTIF_HOUR = 12;
    public static final int NOTIF_MIN = 30;

    public static final int TRANSMISSION_FREQ = 60; // number of seconds between entry transmissions to firebase

    public static final int SECONDS_PER_CLASSIFICATION = 2; // each classification of "active" or "sedentary" represents SECONDS_PER_CLASSIFICATION seconds of activity.

    public static final int MILLISECS_PER_DAY = 86400000;

    private static Globals instance;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private Globals() {}

    /**
     * Globals is a singleton class and getInstance returns the instance.
     * @return singleton instance of Globals
     */
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

    /**
     * Method for safely acquiring the firebase authentication instance.
     * @return FirebaseAuth instance on success, null on failure.
     */
    private FirebaseAuth getAuth() {
        if (firebaseAuth == null)
            firebaseAuth = FirebaseAuth.getInstance();

        return firebaseAuth;
    }

    /**
     * Method for safely acquiring the firebase database instance.
     * @return FirbaseDatabase instance on success, null on failure.
     */
    private FirebaseDatabase getDatabase() {
        if (firebaseDatabase == null)
            firebaseDatabase = FirebaseDatabase.getInstance();

        return firebaseDatabase;
    }

    /**
     * Method for safely checking if a user is already logged into the firebase authentication instance.
     * @return 1 if a user is logged in, 0 if a user is not logged in, -1 on failure.
     */
    public int isLoggedIn() {
        FirebaseAuth auth = getAuth();
        if (auth == null)
            return -1;

        return auth.getCurrentUser() == null ? 0 : 1;
    }

    /**
     * Method for safely signing out of the firebase authentication instance.
     * @return 1 if the user was logged out, 0 if no user was logged in, -1 on failure.
     */
    public int signOut() {
        FirebaseAuth auth = getAuth();
        if (auth == null)
            return -1;

        int ret = isLoggedIn();
        if (ret == 1)
            auth.signOut();

        return ret;
    }

    /**
     * Method for logging a user into the firebase authentication instance.
     * @param email user's account email
     * @param password user's account password
     * @param consumer used for action on finish. Passed 1 on success, 0 on login failure, -1 on firebase failure.
     */
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

    /**
     * Method for registering an account to the firebase authentication instance.
     * @param name user's desired name
     * @param email user's desired account email
     * @param password user's desired account password
     * @param consumer used for action on finish. Passed 1 on success, 0 on register failure, -1 on firebase failure.
     */
    public void register(final String name, final String email, String password, final Consumer<Integer> consumer) {
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
                        userRef.child("Name").setValue(name);
                        userRef.child("Email").setValue(email);
                        userRef.child("ActivityGoal").setValue(0);
                        userRef.child("EnableNotifications").setValue(false);
                        consumer.accept(1);
                    } else {
                        consumer.accept(0);
                    }
                }
            });
        }
    }

    /**
     * Method for updating a user's account password.
     * @param oldPass user's old password
     * @param newPass user's new password
     * @param consumer used for action on finish. Passed 1 on success, 0 on updation failure, -1 on firebase failure.
     */
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

    /**
     * Method for deleting a user's account from the firebase instance.
     * @param password the user's account password
     * @param consumer used for action on finish. Passed 1 on success, 0 on deletion failure, -1 on firebase failure.
     */
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

    /**
     * Method for saving a data entry [time-isActive] pair to the user's remote sedentary data firebase storage.
     * @param time in milliseconds
     * @param isActive
     * @return 0 on success, -1 on failure.
     */
    public int saveDataEntry(long time, boolean isActive) {
        FirebaseAuth auth = getAuth();
        FirebaseDatabase database = getDatabase();
        if (auth == null || database == null)
            return -1;

        FirebaseUser user = auth.getCurrentUser();
        if (user == null)
            return -1;

        long day = getDayInterval(time)[0].getTimeInMillis();
        DatabaseReference dataRef = database.getReference("Users/" + user.getUid() + "/SedentaryData/" + day);
        dataRef.child("" + (time - day)).setValue(isActive ? 1 : 0);

        return 0;
    }

    /**
     * Method for retrieving an interval of data entries from the user's remote sedentary data firebase storage.
     * @param startTime in milliseconds *must be the start of a day
     * @param endTime in milliseconds
     * @param consumer used for action on finish. Passed a hashmap of entries on success, null on failure.
     */
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

        DatabaseReference dataRef = database.getReference("Users/" + user.getUid() + "/SedentaryData");

        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                HashMap<Long, Boolean> entries = new HashMap<>();

                for (DataSnapshot daySnapshot : dataSnapshot.getChildren()) {

                    long dayTime = Long.parseLong(daySnapshot.getKey());

                    for (DataSnapshot entrySnapshot : daySnapshot.getChildren()) {
                        long entryTime = dayTime + Long.parseLong(entrySnapshot.getKey());
                        boolean isActive = (Long) entrySnapshot.getValue() == 1;

                        if (entryTime >= startTime && entryTime <= endTime)
                            entries.put(entryTime, isActive);
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

    /**
     * Method for deleting data entries from the user's remote sedentary data firebase storage that are older than the given time.
     * @param time in milliseconds
     * @return 1 on success, -1 on failure.
     */
    public int deleteOldData(final long time) {
        FirebaseAuth auth = getAuth();
        FirebaseDatabase database = getDatabase();
        if (auth == null || database == null)
            return -1;

        FirebaseUser user = auth.getCurrentUser();
        if (user == null)
            return -1;

        final DatabaseReference dataRef = database.getReference("Users/" + user.getUid() + "/SedentaryData");

        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot daySnapshot : dataSnapshot.getChildren())
                    if (Long.parseLong(daySnapshot.getKey()) < time)
                        dataRef.child(daySnapshot.getKey()).setValue(null);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}

        });

        return 1;
    }

    /**
     * Method for retrieving a user's attribute from the user's remote firebase storage.
     * @param attribute the desired attribute
     * @param consumer used for action on finish. Passed a castable object (String, Long, etc.) depending on the attributes value, or null on failure.
     */
    public void getAttribute(String attribute, final Consumer<Object> consumer) {
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

        DatabaseReference dataRef = database.getReference("Users/" + user.getUid() + "/" + attribute);

        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                consumer.accept(dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { consumer.accept(null); }
        });
    }

    /**
     * Method for setting a user's attribute in the user's remote firebase storage.
     * @param attribute
     * @param value
     * @return 1 on success, -1 on failure.
     */
    public <T> int setAttribute(String attribute, T value) {
        FirebaseAuth auth = getAuth();
        FirebaseDatabase database = getDatabase();
        if (auth == null || database == null)
            return -1;

        FirebaseUser user = auth.getCurrentUser();
        if (user == null)
            return -1;

        database.getReference("Users/" + user.getUid() + "/" + attribute).setValue(value);

        return 1;
    }


    public void registerNotification(Context context) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.set(Calendar.HOUR_OF_DAY, NOTIF_HOUR);
        cal.set(Calendar.MINUTE, NOTIF_MIN);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (cal.getTimeInMillis() < System.currentTimeMillis())
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);

        Intent intent = new Intent(context, NotificationReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIF_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void deregisterNotification(Context context) {
        Intent intent = new Intent(context, NotificationReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIF_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null)
            alarmManager.cancel(pendingIntent);
    }



}