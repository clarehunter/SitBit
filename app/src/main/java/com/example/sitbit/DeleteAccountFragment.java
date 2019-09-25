package com.example.sitbit;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DeleteAccountFragment extends Fragment {
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public DeleteAccountFragment() {
        user.delete();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_delete_account, container, false);

        ((Button) view.findViewById(R.id.DELACC_delete_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass1 = ((TextView) view.findViewById(R.id.DELACC_password_field1)).getText().toString();
                String pass2 = ((TextView) view.findViewById(R.id.DELACC_password_field2)).getText().toString();

                if (pass1.equals(pass2)) {
                } else {
                    Toast.makeText(getActivity(), R.string.DELACC_diff_pass_toast, Toast.LENGTH_SHORT).show();
                }
                firebaseAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().finish();
                startActivity(intent);
            }
        });
    }

}
