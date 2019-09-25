package com.example.sitbit;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;

public class DeleteAccountFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private TextView passField1;
    private TextView passField2;
    private Button deleteButton;

    public DeleteAccountFragment() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_account, container, false);

        passField1 = (TextView) view.findViewById(R.id.DELACC_password_field1);
        passField2 = (TextView) view.findViewById(R.id.DELACC_password_field2);

        deleteButton = (Button) view.findViewById((R.id.DELACC_delete_button));

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass1 = passField1.getText().toString().trim();
                String pass2 = passField2.getText().toString().trim();

                if (pass1.length() == 0 || pass2.length() == 0) {
                    Toast.makeText(getActivity(), R.string.DELACC_emtpy_pass_toast, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pass1.equals(pass2)) {
                    AuthCredential cred = EmailAuthProvider.getCredential(firebaseUser.getEmail(), pass1);

                    firebaseUser.reauthenticate(cred).addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                                            getActivity().finish();
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(getActivity(), R.string.DELACC_diff_pass_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

}
