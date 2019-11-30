package com.app.sitbit;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;

public class DeleteAccountFragment extends Fragment {

    private TextView passField1;
    private TextView passField2;
    private Button deleteButton;

    private Globals globals;


    public DeleteAccountFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_account, container, false);

        passField1 = view.findViewById(R.id.DELACC_password_field1);
        passField2 = view.findViewById(R.id.DELACC_password_field2);

        deleteButton = view.findViewById((R.id.DELACC_delete_button));

        globals = Globals.getInstance();

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
                    globals.deregister(pass1, new Consumer<Integer>() {
                        @Override
                        public void accept(Integer t) {
                            if (t == 1) {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                Activity activity = getActivity();

                                if (activity != null) {
                                    getActivity().finish();
                                    startActivity(intent);
                                }
                            } else if (t == 0) {
                                Toast.makeText(getActivity(), R.string.DELACC_failed_delete_toast, Toast.LENGTH_SHORT).show();
                            } else if (t == -1) {
                                Toast.makeText(getActivity(), R.string.DELACC_failed_firebase_connection_toast, Toast.LENGTH_SHORT).show();
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
