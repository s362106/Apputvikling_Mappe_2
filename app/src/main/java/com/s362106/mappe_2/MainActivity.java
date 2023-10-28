package com.s362106.mappe_2;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;
    private ListView appointmentListView;
    private Resources resources;
    private String smsServiceKey, timePreferenceKey, serviceToastMessage, permissionToastMessage,
            timeFormatToastMessage, validateFieldsToastMessage, notificationDescription;
    private final String intentHourExtra = "HOUR_OF_DAY", intentMinuteExtra = "MINUTE",
            intentContactId = "contactId", CHANNEL_ID = "MyChannel",  customSignal = "OurSignal";
    private Spinner contact_spinner;
    private DatePicker date_datePicker;
    private TimePicker time_timePicker;
    private boolean isSmsEnabled;
    private SharedPreferences preferences;
    private ArrayAdapter<Appointment> adapter;
    private String HOUR_OF_DAY, MINUTE;
    private View appointmentDialogLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resources = getResources();
        smsServiceKey = resources.getString(R.string.sms_service_preference_key);
        timePreferenceKey = resources.getString(R.string.time_preference_key);
        validateFieldsToastMessage = resources.getString(R.string.empty_fields_toast);

        serviceToastMessage = resources.getString(R.string.sms_service_toast_message);
        permissionToastMessage = resources.getString(R.string.sms_permission_message);
        timeFormatToastMessage = resources.getString(R.string.invalid_format_message);

        appointmentListView = findViewById(R.id.appointmentListView);
        setupListView();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        isSmsEnabled = preferences.getBoolean(smsServiceKey, false);
        BroadcastReceiver smsBroadcastReceiver = new SMSBroadcastReceiver();
        IntentFilter filter = new IntentFilter(customSignal);
        filter.addAction(customSignal);
        this.registerReceiver(smsBroadcastReceiver, filter);
        sendBroadcast();
        createNotificationChannel();
        Toast.makeText(this, serviceToastMessage, Toast.LENGTH_SHORT).show();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},
                    SEND_SMS_PERMISSION_REQUEST_CODE);
        }
    }

    public void preferenceMethod(View view) {
        Intent iS = new Intent(this, SettingsActivity.class);
        startActivity(iS);
    }

    public void contactMethod(View view) {
        Intent iC = new Intent(this, ContactsActivity.class);
        startActivity(iC);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendBroadcast();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SEND_SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, permissionToastMessage, Toast.LENGTH_SHORT).show();
                isSmsEnabled = false;
            }
        }
    }

    private void createNotificationChannel() {
        CharSequence name = resources.getString(R.string.notification_channel_name);
        String description = resources.getString(R.string.notification_channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void sendBroadcast() {
        Intent intent = new Intent();
        intent.setAction(customSignal);
        sendBroadcast(intent);
    }

    private void setupListView() {
        updateListView();

        appointmentListView.setOnItemClickListener((parent, view, position, id) -> {
            Appointment selectedObject = (Appointment) parent.getItemAtPosition(position);
            showAppointmentDetailsDialog(selectedObject);
            Toast.makeText(MainActivity.this, "Klikket på: " + selectedObject, Toast.LENGTH_SHORT).show();

        });
    }

    public void showAppointmentDetailsDialog(Appointment appointment) {
        final View customLayout = getLayoutInflater().inflate(R.layout.appointment_dialog_layout, null);
        contact_spinner = customLayout.findViewById(R.id.dialog_spinner);
        final DatePicker date_datepicker = customLayout.findViewById(R.id.dialog_datePicker);
        final TimePicker time_timepicker = customLayout.findViewById(R.id.dialog_timePicker);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setTitle(R.string.appointmentDetailsTitle);
        alertBuilder.setView(customLayout);

        new LoadContactsTask().execute();

        int[] date = appointment.getDate();
        int day = date[0];
        int month = date[1];
        int year = date[2];
        date_datepicker.updateDate(year,month,day);

        int[] time = appointment.getTime();
        int hour = time[0];
        int minute = time[1];
        time_timepicker.setHour(hour);
        time_timepicker.setMinute(minute);


        alertBuilder.setView(customLayout);

        alertBuilder.setPositiveButton(R.string.contactDetailsUpdateText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertBuilder.setNegativeButton(R.string.contactDetailsCancelText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertBuilder.setNeutralButton(R.string.contactdetailsDeleteText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                DeleteAppointment(appointment);
                updateListView();
            }
        });

        AlertDialog alert = alertBuilder.create();
        alert.show();

        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields(contact_spinner, date_datepicker, time_timepicker)) {

                    Contact selectedContact = (Contact) contact_spinner.getSelectedItem();
                    appointment.setContactId(selectedContact.getContactId());

                    appointment.setDate(date_datepicker);
                    appointment.setTime(time_timepicker);
                    UpdateAppointment(appointment);
                    Toast.makeText(MainActivity.this, " "+ date_datepicker.getDayOfMonth(), Toast.LENGTH_SHORT).show();
                    alert.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, validateFieldsToastMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class LoadContactsTask extends AsyncTask<Void, Void, List<Contact>> {
        @Override
        protected List<Contact> doInBackground(Void... voids) {
            return DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().contactDao().getAllContacts();
        }

        @Override
        protected void onPostExecute(List<Contact> contacts) {
            ContactAdapter adapter = new ContactAdapter(MainActivity.this, contacts);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            contact_spinner.setAdapter(adapter);
        }
    }

    public void setupAppointmentMethod(View customLayout, AlertDialog.Builder alertBuilder, Spinner contact_spinner,
                                       DatePicker date_datePicker, TimePicker time_timePicker) {
        alertBuilder.setTitle(R.string.newAppointmentTitle)
                .setPositiveButton(R.string.newContactPositiveButtonText, ((dialog, which) -> {

                }))
                .setNegativeButton(R.string.newContactNegativeButtonText, ((dialog, which) -> dialog.dismiss()))
                .setView(customLayout);

        AlertDialog dialog = alertBuilder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields(contact_spinner, date_datePicker, time_timePicker)) {
                    Appointment newAppointment = new Appointment();
                    Contact selectedContact = (Contact) contact_spinner.getSelectedItem();
                    newAppointment.setContactId(selectedContact.getContactId());
                    newAppointment.setDate(date_datePicker);
                    newAppointment.setTime(time_timePicker);

                    createNewAppointment(newAppointment);
                    updateListView();
                    dialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, validateFieldsToastMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void createNewAppointment(Appointment appointment) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().appointmentDao().newAppointment(appointment);

            handler.post(() -> {
                updateListView();
            });
        });
    }
    public void showNewAppointmentDialog(View view) {
        appointmentDialogLayout = getLayoutInflater().inflate(R.layout.appointment_dialog_layout, null);
        date_datePicker = (DatePicker) appointmentDialogLayout.findViewById(R.id.dialog_datePicker);
        time_timePicker = (TimePicker) appointmentDialogLayout.findViewById(R.id.dialog_timePicker);
        contact_spinner = (Spinner) appointmentDialogLayout.findViewById(R.id.dialog_spinner);

        new LoadContactsTask().execute();


        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        setupAppointmentMethod(appointmentDialogLayout, alertBuilder, contact_spinner, date_datePicker, time_timePicker);
    }

    public void updateListView() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<Appointment> allAppointment = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().appointmentDao().getAll();

            handler.post(() -> {
                adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, allAppointment);
                appointmentListView.setAdapter(adapter);
            });
        });
    }

    public void UpdateAppointment(Appointment appointment) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().appointmentDao().update(appointment);
            handler.post(() -> {
                updateListView();
            });
        });
    }

    public void DeleteAppointment(Appointment appointment) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().appointmentDao().delete(appointment);
            handler.post(() -> {
                updateListView();
            });
        });
    }

    private boolean validateFields(Spinner spinner, DatePicker date, TimePicker time) {
        if (spinner.getSelectedItemPosition() != -1
                && date.getYear() != 0 && date.getMonth() != 0 && date.getDayOfMonth() != 0
                && time.getHour() != 0 && time.getMinute() != 0) {
            return true;
        } else {
            return false;
        }
    }
}