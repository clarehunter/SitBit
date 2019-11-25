package com.app.sitbit;


import android.os.Bundle;

import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsAccountFragment extends Fragment {

    private TextView nameField;
    private TextView emailField;

    private Globals globals;

    public SettingsAccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_account, container, false);

        nameField = view.findViewById(R.id.SETTINGS_ACC_name_field);
        emailField = view.findViewById(R.id.SETTINGS_ACC_email_field);

        globals = Globals.getInstance();

        globals.getAttribute("Name", new Consumer<Object>() {
            @Override
            public void accept(Object o) {

                if (o == null) {
                    Toast.makeText(getContext(), R.string.SETTINGS_ACC_failed_firebase_connection_toast, Toast.LENGTH_SHORT).show();
                    return;
                }

                nameField.setText("Name: " + o);
            }
        });

        globals.getAttribute("Email", new Consumer<Object>() {
            @Override
            public void accept(Object o) {

                if (o == null) {
                    Toast.makeText(getContext(), R.string.SETTINGS_ACC_failed_firebase_connection_toast, Toast.LENGTH_SHORT).show();
                    return;
                }
                
                emailField.setText("Email: " + o);
            }
        });

        (view.findViewById(R.id.SETTINGS_ACC_update_password_field)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.update_password_fragment);
            }
        });

        (view.findViewById(R.id.SETTINGS_ACC_delete_account_field)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.delete_account_fragment);
            }
        });

        return view;
    }

}
