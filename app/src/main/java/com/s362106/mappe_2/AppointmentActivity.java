package com.s362106.mappe_2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Database;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppointmentActivity extends AppCompatActivity {
    private Spinner spinner;
    private ArrayAdapter<Contact> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        int position = getIntent().getIntExtra("item_position", -1);

        spinner = findViewById(R.id.contactSpinner);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);





    }
    /*
    public void getAllContacts(View view) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<Contact> allContacts = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().contactDao().getAllContacts();

            handler.post(() -> {
                adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, allContacts);
                contactListView.setAdapter(adapter);
            });
        });
    }

     */
}