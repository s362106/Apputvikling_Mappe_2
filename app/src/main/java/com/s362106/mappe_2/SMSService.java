package com.s362106.mappe_2;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SMSService extends Service {
    private NotificationManager mNM;
    private Resources resources;
    private final String intentHourExtra = "HOUR_OF_DAY", intentMinuteExtra = "MINUTE";
    private String smsContent, smsMessageKey;
    private int hour, minute;
    private SharedPreferences preferences;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("CustomService", "Service Made");
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        resources = getResources();
        String hourMinute = preferences.getString(resources.getString(R.string.time_preference_key), "06:00");
        String[] parts = hourMinute.split(":");

        hour = Integer.parseInt(parts[0]);
        minute = Integer.parseInt(parts[1]);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        checkAppointments();
        Log.d("Service", "Startet Service");
        resources = getResources();
        smsContent = resources.getString(R.string.sms_message_default);
        return START_NOT_STICKY;
    }

    private void checkAppointments() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        Log.d("checkAppointments", "I checkAppointments");
        executor.execute(() -> {
            Message message = new Message();

            message.obj = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .appointmentDao()
                    .getAll();

            Log.d("checkAppointments", "checkAppointments execute er ferdig");

            handler.post(() -> {
                List<Appointment> receivedlist = (List<Appointment>) message.obj;
                if (receivedlist != null) {

                    for (Appointment appointment : receivedlist) {
                        Calendar now = Calendar.getInstance();
                        if(isAppointmentToday(appointment, now)){
                            sendSMSForAppointment(appointment);
                            Log.d("checkAppointments", "Message sent for " + String.valueOf(appointment.getUid()));
                        } else if (isAppointmentOld(appointment, now)) {
                            deleteOldAppointmet(appointment);
                        }
                    }
                }
                else {
                    Log.d("checkAppointments", "receivedList er tom");
                }
                Log.d("checkAppointments", "checkAppointments post er ferdig");
            });
        });
    }

    private void sendSMSForAppointment(Appointment appointment) {
        int contactId = appointment.getContactId();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Message dbContact = new Message();

            dbContact.obj = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .contactDao()
                    .getContact(contactId);

            Log.d("sendSMSForAppointment", "sendSMSForAppointment Ferdig i execute");
            handler.post(() -> {
                Contact retreivedContact = (Contact) dbContact.obj;

                if (retreivedContact != null) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(retreivedContact.getPhoneNumber(), null, smsContent, null, null);
                    mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    // Create a notification to inform the user
                    Intent i = new Intent(this, SetPeriodicService.class);
                    i.putExtra(intentHourExtra, hour);
                    i.putExtra(intentMinuteExtra, minute);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                    Notification notification = new NotificationCompat.Builder(this, "MyChannel")
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentTitle("SMS påminnelse sendt")
                            .setContentText("SMS sendt til " + retreivedContact.getFirstName())
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(pendingIntent).build();
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                    mNM.notify(88, notification);
                    Log.d("sendSMSForAppointment", "sendSMSForAppointment Melding sent til kontakt: " + retreivedContact.getFirstName());
                }
                else {
                    Log.d("sendSMSForAppointment", "sendSMSForAppointment fått tilbake null kontakt");
                }
            });
        });
    }

    private void deleteOldAppointmet(Appointment appointment) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().appointmentDao().delete(appointment);


            handler.post(() -> {
                Log.d("deleteOldAppointment", "Slettet gammel avtale");
            });
        });
    }

    private boolean isAppointmentToday(Appointment appointment, Calendar now) {
        int day, month, year;
        int[] dayMonthYear = appointment.getDate();
        day = dayMonthYear[0];
        month = dayMonthYear[1];
        year = dayMonthYear[2];

        return now.get(Calendar.YEAR) == year &&
                now.get(Calendar.DAY_OF_MONTH) == day &&
                now.get(Calendar.MONTH) == month;
    }

    private boolean isAppointmentOld(Appointment appointment, Calendar now) {
        int day, month, year;
        int[] dayMonthYear = appointment.getDate();
        day = dayMonthYear[0];
        month = dayMonthYear[1];
        year = dayMonthYear[2];

        return now.get(Calendar.YEAR) >= year ||
                now.get(Calendar.DAY_OF_MONTH) > day ||
                now.get(Calendar.MONTH) >= month;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
