package com.example.sitbit;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsMainFragment extends Fragment {

    private FirebaseAuth firebaseAuth;

    public SettingsMainFragment() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_main, container, false);

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
                firebaseAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().finish();
                startActivity(intent);
            }
        });

        return view;
    }

}
