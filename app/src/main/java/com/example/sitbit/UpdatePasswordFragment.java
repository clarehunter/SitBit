package com.example.sitbit;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class UpdatePasswordFragment extends Fragment {

    TextView oldPass1;
    TextView oldPass2;
    TextView newPass;
    Button updateButton;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;


    public UpdatePasswordFragment() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_password, container, false);

        oldPass1 = (TextView) view.findViewById(R.id.UPPASS_old_password_field1);
        oldPass2 = (TextView) view.findViewById(R.id.UPPASS_old_password_field2);
        newPass = (TextView) view.findViewById(R.id.UPPASS_new_password_field);
        updateButton = (Button) view.findViewById(R.id.UPPASS_update_button);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String opass1 = oldPass1.getText().toString().trim();
                String opass2 = oldPass2.getText().toString().trim();
                final String npass = newPass.getText().toString().trim();

                if (opass1.length() == 0 || opass2.length() == 0 || npass.length() == 0) {
                    Toast.makeText(getActivity(), R.string.UPPASS_emtpy_pass_toast, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (opass1.equals(opass2)) {
                    AuthCredential cred = EmailAuthProvider.getCredential(firebaseUser.getEmail(), opass1);

                    firebaseUser.reauthenticate(cred).addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                firebaseUser.updatePassword(npass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity(), R.string.UPPASS_pass_updated_toast, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), R.string.UPPASS_diff_pass_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

}
