package com.s362106.mappe_2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContactsActivity extends AppCompatActivity {
    private ListView contactListView;
    private ArrayAdapter<Contact> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contactListView = findViewById(R.id.contactsListView);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<Contact> allContacts = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().contactDao().getAllContacts();

            handler.post(() -> {
                adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, allContacts);
                contactListView.setAdapter(adapter);
            });
        });

        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact selectedContact = (Contact) parent.getItemAtPosition(position);
                Toast.makeText(ContactsActivity.this, "Clicked on: " + selectedContact, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void newContactMethod(View view) {
        final String firstName = "Fa";
        final String lastName = "Af";
        final String phoneNumber = "12345";

        Contact newC = new Contact();
        newC.setFirstName(firstName);
        newC.setLastName(lastName);
        newC.setPhoneNumber(phoneNumber);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().contactDao().newContact(newC);
        });
    }

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
}