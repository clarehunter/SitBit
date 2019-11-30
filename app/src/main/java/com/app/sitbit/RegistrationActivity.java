package com.app.sitbit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity {

    private EditText nameField;
    private EditText emailField;
    private EditText passField;

    private Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        nameField = findViewById(R.id.REG_name_field);
        emailField = findViewById(R.id.REG_email_field);
        passField = findViewById(R.id.REG_pass_field);

        globals = Globals.getInstance();

        if (globals.signOut() == -1)
            Toast.makeText(this, R.string.REG_failed_firebase_connection_toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        toLoginScreen();
    }

    public void onRegisterClick(View view) {
        String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passField.getText().toString().trim();

        if (name.length() == 0 || email.length() == 0 || password.length() == 0) {
            Toast.makeText(this, R.string.REG_empty_field_toast, Toast.LENGTH_SHORT).show();
            return;
        }

        globals.register(name, email, password, new Consumer<Integer>() {
            @Override
            public void accept(Integer t) {
                if (t == 1) {
                    toHomeScreen();
                } else if (t == 0) {
                    Toast.makeText(RegistrationActivity.this, R.string.REG_invalid_credentials_toast, Toast.LENGTH_SHORT).show();
                } else if (t == -1) {
                    Toast.makeText(RegistrationActivity.this, R.string.LOGIN_failed_firebase_connection_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onLoginClick(View view) {
        toLoginScreen();
    }

    public void toHomeScreen() {
        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
        finish();
        startActivity(intent);
    }

    public void toLoginScreen() {
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        finish();
        startActivity(intent);
    }
}
