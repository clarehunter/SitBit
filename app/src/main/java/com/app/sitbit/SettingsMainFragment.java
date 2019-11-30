package com.app.sitbit;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsMainFragment extends Fragment {

    private Globals globals;

    public SettingsMainFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_main, container, false);

        globals = Globals.getInstance();

        (view.findViewById(R.id.SETTINGS_MAIN_account_field)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.settings_account_fragment);
            }
        });

        (view.findViewById(R.id.SETTINGS_MAIN_notification_field)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.settings_notification_fragment);
            }
        });

        (view.findViewById(R.id.SETTINGS_MAIN_logout_field)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (globals.signOut() == 1) {
                    globals.deregisterNotification(view.getContext());
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    getActivity().finish();
                    startActivity(intent);
                }
            }
        });

        return view;
    }

}
