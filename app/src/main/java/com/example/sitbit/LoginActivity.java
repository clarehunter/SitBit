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
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText passField;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = (EditText) findViewById(R.id.LOGIN_email_field);
        passField = (EditText) findViewById(R.id.LOGIN_pass_field);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null)
            toHomeScreen();
    }

    public void onLoginClick(View view) {
        String email = emailField.getText().toString();
        String pass = passField.getText().toString();

        if (email.trim().length() > 0 && pass.trim().length() > 0) {
            firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        toHomeScreen();
                    } else {
                        Toast.makeText(LoginActivity.this, R.string.LOGIN_login_failed_toast, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(LoginActivity.this, R.string.LOGIN_login_failed_toast, Toast.LENGTH_SHORT).show();
        }
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
