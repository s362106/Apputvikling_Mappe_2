package com.s362106.mappe_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ListView appointmentListView;
    private ArrayAdapter<Appointment> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appointmentListView = findViewById(R.id.appointmentListView);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<Appointment> allAppointment = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().appointmentDao().getAll();

            handler.post(() -> {
                adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, allAppointment);
                appointmentListView.setAdapter(adapter);
            });
        });

        appointmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Appointment selectedAppointment = (Appointment) parent.getItemAtPosition(position);
                Toast.makeText(MainActivity.this, "Clicked on: " + selectedAppointment, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, AppointmentActivity.class);
                intent.putExtra("uid", selectedAppointment.getUid());
                startActivity(intent);
            }
        });
    }

    public void newAppointmentMethod(View view) {
        final String date = "2014";
        final String time = "10:50";

        Appointment newA = new Appointment();
        newA.setDate(date);
        newA.setTime(time);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().appointmentDao().newAppointment(newA);
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
}