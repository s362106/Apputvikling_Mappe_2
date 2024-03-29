package com.s362106.mappe_2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.Calendar;

public class SetPeriodicService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Calendar cal = Calendar.getInstance();
        String intentHourExtra = "HOUR_OF_DAY";
        cal.set(Calendar.HOUR_OF_DAY, intent.getIntExtra(intentHourExtra, 6));
        String intentMinuteExtra = "MINUTE";
        cal.set(Calendar.MINUTE, intent.getIntExtra(intentMinuteExtra, 0));
        Intent i = new Intent(this, SMSService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, i, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 60 * 1000, pendingIntent);

        return super.onStartCommand(intent, flags, startId);
    }
}
