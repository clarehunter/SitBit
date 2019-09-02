package com.example.sit_bit;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.os.Bundle;

public class RegistrationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        TextView login = (TextView)findViewById(R.id.lnkLogin);
        login.setMovementMethod(LinkMovementMethod.getInstance());
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, loginActivity.class);
                startActivity(intent);
            }
        });
    }
}