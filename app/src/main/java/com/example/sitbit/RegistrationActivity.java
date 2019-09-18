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

public class RegistrationActivity extends AppCompatActivity {

    private EditText nameField;
    private EditText emailField;
    private EditText passField;

    private Globals globals;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        nameField = (EditText) findViewById(R.id.REG_name_field);
        emailField = (EditText) findViewById(R.id.REG_email_field);
        passField = (EditText) findViewById(R.id.REG_pass_field);

        globals = (Globals) getApplication();

        firebaseAuth = globals.getFirebaseAuth();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null)       // this should obviously never happen
            firebaseAuth.signOut();     // ... hopefully?
    }

    public void onRegisterClick(View view) {
        String name = nameField.getText().toString();
        String email = emailField.getText().toString();
        String pass = passField.getText().toString();

        if (name.trim().length() > 0 && email.trim().length() > 0 && pass.trim().length() > 0) {
            firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        toHomeScreen();
                    } else {
                        Toast.makeText(RegistrationActivity.this, R.string.REG_reg_failed_toast, Toast.LENGTH_SHORT);
                    }
                }
            });
        } else {
            Toast.makeText(RegistrationActivity.this, R.string.REG_reg_failed_toast, Toast.LENGTH_SHORT);
        }
    }

    public void onLoginClick(View view) {
        toLoginScreen();
    }

    public void toHomeScreen() {
        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void toLoginScreen() {
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
