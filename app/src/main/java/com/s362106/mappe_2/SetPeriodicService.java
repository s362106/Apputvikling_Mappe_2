package com.s362106.mappe_2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.List;

public class SetPeriodicService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, intent.getIntExtra("HOUR_OF_DAY", 7));
        cal.set(Calendar.MINUTE, intent.getIntExtra("MINUTE", 7));
        Intent i = new Intent(this, SMSService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, i, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        return super.onStartCommand(intent, flags, startId);
    }
}
