package com.example.sitbit;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class SettingsAccountFragment extends Fragment {


    public SettingsAccountFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_account, container, false);

        ((TextView) view.findViewById(R.id.SETTINGS_ACC_update_password_field)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.update_password_fragment);
            }
        });

        ((TextView) view.findViewById(R.id.SETTINGS_ACC_delete_account_field)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.delete_account_fragment);
            }
        });

        return view;
    }

}
