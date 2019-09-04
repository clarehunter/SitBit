package com.example.sit_bit;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class NotificationSettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.notification_settings_preferences, rootKey);
    }
}
