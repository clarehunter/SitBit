package com.example.sitbit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText passField;

    private Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.LOGIN_email_field);
        passField = findViewById(R.id.LOGIN_pass_field);

        globals = Globals.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Globals.NOTIF_CHANNEL, "Notification", importance);
            channel.setDescription("A Simple Notification");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        int ret = globals.isLoggedIn();

        if (ret == 1)
            toHomeScreen();
        else if (ret == -1)
            Toast.makeText(this, R.string.LOGIN_failed_firebase_connection_toast, Toast.LENGTH_SHORT).show();
    }

    public void onLoginClick(View view) {
        String email = emailField.getText().toString().trim();
        String password = passField.getText().toString().trim();

        if (email.length() == 0 || password.length() == 0) {
            Toast.makeText(LoginActivity.this, R.string.LOGIN_empty_field_toast, Toast.LENGTH_SHORT).show();
            return;
        }

        globals.login(email, password, new Consumer<Integer>() {
            @Override
            public void accept(Integer t) {
                if (t == 1) {
                    toHomeScreen();
                } else if (t == 0) {
                    Toast.makeText(LoginActivity.this, R.string.LOGIN_invalid_credentials_toast, Toast.LENGTH_SHORT).show();
                } else if (t == -1) {
                    Toast.makeText(LoginActivity.this, R.string.LOGIN_failed_firebase_connection_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onRegisterClick(View view) {
        toRegisterScreen();
    }

    public void toHomeScreen() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        finish();
        startActivity(intent);
    }

    public void toRegisterScreen() {
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        finish();
        startActivity(intent);
    }

}
