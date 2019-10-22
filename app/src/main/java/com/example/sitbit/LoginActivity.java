package com.example.sitbit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText passField;

    private Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = (EditText) findViewById(R.id.LOGIN_email_field);
        passField = (EditText) findViewById(R.id.LOGIN_pass_field);

        globals = Globals.getInstance();

        globals.setFirebaseAuth(FirebaseAuth.getInstance());

        if (globals.getFirebaseAuth().getCurrentUser() != null)
            toHomeScreen();
    }

    public void onLoginClick(View view) {
        String email = emailField.getText().toString().trim();
        String pass = passField.getText().toString().trim();

        if (email.length() == 0 || pass.length() == 0) {
            Toast.makeText(LoginActivity.this, R.string.LOGIN_empty_field_toast, Toast.LENGTH_SHORT).show();
            return;
        }

        globals.getFirebaseAuth().signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        toHomeScreen();
                    } else {
                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
