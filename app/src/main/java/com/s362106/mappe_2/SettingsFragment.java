package com.s362106.mappe_2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        EditTextPreference timepreference = findPreference(getString(R.string.time_preference_key));

        if (timepreference != null) {
            timepreference.setOnPreferenceChangeListener(((preference, newValue) -> {
                String timeRegex = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
                if (newValue.toString().matches(timeRegex)) {
                    return true;
                }
                else {
                    Toast.makeText(getContext(), "Ugyldig tidsformat", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }));
        }
    }

    public void accessSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        boolean isSMSEnabled = sharedPreferences.getBoolean("sms-service", false);
        String defaultSmsMessage = sharedPreferences.getString("sms-message", "Hei");
    }
}
