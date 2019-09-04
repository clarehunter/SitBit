package com.example.sit_bit;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class HealthSettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.health_settings_preferences, rootKey);
    }
}
