package com.s362106.mappe_2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ListView appointmentListView;

    private Spinner contact_spinner;
    private DatePicker date_datePicker;
    private TimePicker time_timePicker;
    private ArrayAdapter<Appointment> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appointmentListView = findViewById(R.id.appointmentListView);
        setupListView();

    }

    private void setupListView() {
        updateListView();

        appointmentListView.setOnItemClickListener((parent, view, position, id) -> {
            Appointment selectedObject = (Appointment) parent.getItemAtPosition(position);
            showAppointmentDetailsDialog(selectedObject);
            Toast.makeText(MainActivity.this, "Clicked on: " + selectedObject, Toast.LENGTH_SHORT).show();

        });
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

    private void newAppointmentMethod(View customLayout, AlertDialog.Builder alertBuilder, Spinner contact_spinner,
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
                if(validateFields(contact_spinner, date_datePicker, time_timePicker)) {
                    Appointment newAppointment = new Appointment();
                    Contact selectedContact = (Contact) contact_spinner.getSelectedItem();
                    newAppointment.setContactId(selectedContact.getContactId());
                    newAppointment.setDate(date_datePicker);
                    newAppointment.setTime(time_timePicker);

                    createNewAppointment(newAppointment);
                    updateListView();
                    dialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, "Fyll ut alle felt", Toast.LENGTH_SHORT).show();
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

    private boolean validateFields(Spinner spinner, DatePicker date, TimePicker time) {
        if (spinner.getSelectedItemPosition() != -1
                && date.getYear() != 0 && date.getMonth() != 0 && date.getDayOfMonth() != 0
                && time.getHour() != 0 && time.getMinute() != 0 ) {
            return true;
        } else {
            return false;
        }
    }

    public void showAppointmentDetailsDialog(Appointment appointment) {
        final View customLayout = getLayoutInflater().inflate(R.layout.appointment_dialog_layout, null);
        final Spinner contact_spinner  = customLayout.findViewById(R.id.dialog_spinner);
        final DatePicker date_datepicker = customLayout.findViewById(R.id.dialog_datePicker);
        final TimePicker time_timepicker = customLayout.findViewById(R.id.dialog_timePicker);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setTitle(R.string.appointmentDetailsTitle);
        alertBuilder.setView(customLayout);

        List<Contact> allContacts = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().contactDao().getAllContacts();
        ArrayAdapter<Contact> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allContacts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contact_spinner.setAdapter(adapter);

        int currentContact = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().contactDao().getPositionByContactId(appointment.getContactId());
        contact_spinner.setSelection(currentContact);

        int[] selectedDate = appointment.getDate();
        date_datepicker.updateDate(selectedDate[0], selectedDate[1], selectedDate[2]);

        int[] selectedTime = appointment.getTime();
        time_timepicker.setHour(selectedTime[0]);
        time_timepicker.setMinute(selectedTime[1]);

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
                if(validateFields(contact_spinner, date_datepicker, time_timepicker)){

                    Contact selectedContact = (Contact) contact_spinner.getSelectedItem();
                    appointment.setContactId(selectedContact.getContactId());

                    appointment.setDate(date_datepicker);
                    appointment.setTime(time_timepicker);
                    UpdateAppointment(appointment);
                    alert.dismiss();
                }
                else {
                    Toast.makeText(MainActivity.this, "Alle felt må bli fylt ut!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void preferenceMethod(View view) {
        Intent iS = new Intent(this, SettingsActivity.class);
        startActivity(iS);
    }

    public void contactMethod(View view) {
        Intent iC = new Intent(this, ContactsActivity.class);
        startActivity(iC);
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

}