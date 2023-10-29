package com.s362106.mappe_2;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        EditTextPreference timePreference = findPreference(getString(R.string.time_preference_key));

        if (timePreference != null) {
            timePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                String timeRegex = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
                if (newValue.toString().matches(timeRegex)) {
                    return true;
                } else {
                    Toast.makeText(getContext(), "Ugyldig tidsformat", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }
    }
}
