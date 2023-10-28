package com.s362106.mappe_2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import androidx.preference.PreferenceManager;

public class SMSBroadcastReceiver extends BroadcastReceiver {
    private SharedPreferences preferences;
    private final String intentHourExtra = "HOUR_OF_DAY", intentMinuteExtra = "MINUTE";
    private Resources resources;
    public SMSBroadcastReceiver(){};

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("SMSBroadcastReceiver", "Tatt imot broadcast");
        resources = context.getResources();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String timePreferenceKey = resources.getString(R.string.time_preference_key);
        String timeString = preferences.getString(timePreferenceKey, "").trim();
        if (validateTime(timeString)) {
            Intent i = new Intent(context, SetPeriodicService.class);
            String[] parts = timeString.split(":");

            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            intent.putExtra(intentHourExtra, hour);
            intent.putExtra(intentMinuteExtra, minute);
            context.startService(i);
        }
    }

    private boolean validateTime(String inputTime) {
        String timeRegex = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
        return inputTime.matches(timeRegex);
    }
}
