package com.app.sitbit;


import android.os.Bundle;

import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class UpdatePasswordFragment extends Fragment {

    private TextView oldPass1;
    private TextView oldPass2;
    private TextView newPass;
    private Button updateButton;

    private Globals globals;

    public UpdatePasswordFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_password, container, false);

        oldPass1 = view.findViewById(R.id.UPPASS_old_password_field1);
        oldPass2 = view.findViewById(R.id.UPPASS_old_password_field2);
        newPass = view.findViewById(R.id.UPPASS_new_password_field);
        updateButton = view.findViewById(R.id.UPPASS_update_button);

        globals = Globals.getInstance();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String opass1 = oldPass1.getText().toString().trim();
                String opass2 = oldPass2.getText().toString().trim();
                final String npass = newPass.getText().toString().trim();

                if (opass1.length() == 0 || opass2.length() == 0 || npass.length() == 0) {
                    Toast.makeText(getActivity(), R.string.UPPASS_empty_pass_toast, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (opass1.equals(opass2)) {
                    globals.updatePassword(opass1, npass, new Consumer<Integer>() {
                        @Override
                        public void accept(Integer t) {
                            if (t == 1) {
                                Toast.makeText(getActivity(), R.string.UPPASS_pass_updated_toast, Toast.LENGTH_SHORT).show();
                            } else if (t == 0) {
                                Toast.makeText(getActivity(), R.string.UPPASS_incorrect_pass_toast, Toast.LENGTH_SHORT).show();
                            } else if (t == -1) {
                                Toast.makeText(getActivity(), R.string.UPPASS_failed_firebase_connection_toast, Toast.LENGTH_SHORT).show();
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
