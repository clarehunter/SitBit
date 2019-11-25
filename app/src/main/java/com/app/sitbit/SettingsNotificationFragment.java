package com.app.sitbit;


import android.os.Bundle;

import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsNotificationFragment extends Fragment {

    private Switch notificationSwitch;

    private Globals globals;

    public SettingsNotificationFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_settings_notification, container, false);


        notificationSwitch = view.findViewById(R.id.NOTIF_notifications_switch);

        globals = Globals.getInstance();

        globals.getAttribute("EnableNotifications", new Consumer<Object>() {
            @Override
            public void accept(Object o) {

                if (o == null) {
                    Toast.makeText(getContext(), R.string.NOTIF_failed_firebase_connection_toast, Toast.LENGTH_SHORT).show();
                    return;
                }

                notificationSwitch.setChecked(((Boolean) o).booleanValue());


                notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        globals.setAttribute("EnableNotifications", b);

                        if (b) {
                            globals.registerNotification(view.getContext());
                        } else {
                            globals.deregisterNotification(view.getContext());
                        }
                    }
                });
            }
        });

        return view;
    }

}
