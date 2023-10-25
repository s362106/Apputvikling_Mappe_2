package com.s362106.mappe_2;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private SharedPreferences preferences;
    String smsContent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("CustomService", "Service Made");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        smsContent = preferences.getString("sms-message", "");
        checkAppointments();
        Log.d("Service", "Startet Service");
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
                    Log.d("sendSMSForAppointment", "sendSMSForAppointment Melding sent til kontakt: " + retreivedContact.getFirstName());
                }
                else {
                    Log.d("sendSMSForAppointment", "sendSMSForAppointment fått tilbake null kontakt");
                }
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

    /*private void sendSMSForAppointment(Appointment appointment) {
        int contactId = appointment.getContactId();
        smsContent = preferences.getString("sms-message", "");

        class sendSMSForAppointment extends AsyncTask<Void, Void, Contact> {
            @Override
            protected Contact doInBackground(Void... voids) {
                Contact contact = DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .contactDao()
                        .getContact(contactId);

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(contact.getPhoneNumber(), null, smsContent, null, null);

                return contact;
            }

            @Override
            protected void onPostExecute(Contact contact) {
                super.onPostExecute(contact);

                Toast.makeText(getApplicationContext(), "SMS-MESSAGE is sent", Toast.LENGTH_SHORT).show();
            }
        }
    }

     */

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
