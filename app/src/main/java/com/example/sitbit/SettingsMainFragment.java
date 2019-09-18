package com.example.sitbit;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettingsMainFragment extends Fragment {

    Globals globals;

    public SettingsMainFragment() {
        System.out.println("SETTINGS FRAGMENT CREATED");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_main, container, false);

        ((TextView) view.findViewById(R.id.SETTINGS_MAIN_account_field)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.settings_account_fragment);
            }
        });

        ((TextView) view.findViewById(R.id.SETTINGS_MAIN_health_field)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.settings_health_fragment);
            }
        });

        ((TextView) view.findViewById(R.id.SETTINGS_MAIN_notification_field)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.settings_notification_fragment);
            }
        });

        ((TextView) view.findViewById(R.id.SETTINGS_MAIN_logout_field)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // LOGOUT
            }
        });

        System.out.println("SETTINGS FRAGMENT CREATED");

        return view;
    }

}
