package com.s362106.mappe_2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContactsActivity extends AppCompatActivity {
    private ListView contactListView;
    private ArrayAdapter<Contact> adapter;
     private EditText firstName_edittext, lastName_edittext, phoneNumber_edittext;
     private View contactDialogLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contactListView = findViewById(R.id.contactsListView);
        setupListView();
    }

    private void setupListView() {
        updateListView();

        contactListView.setOnItemClickListener((parent, view, position, id) -> {
            Contact selectedContact = (Contact) parent.getItemAtPosition(position);
            showContactDetailsDialog(selectedContact);
            Toast.makeText(ContactsActivity.this, "Clicked on: " + selectedContact.getFirstName(), Toast.LENGTH_SHORT).show();

        });
    }

    public void showNewContactDialog(View view) {
        contactDialogLayout = getLayoutInflater().inflate(R.layout.contact_dialog_layout, null);
        firstName_edittext = (EditText) contactDialogLayout.findViewById(R.id.dialog_firstName);
        lastName_edittext = (EditText) contactDialogLayout.findViewById(R.id.dialog_lastName);
        phoneNumber_edittext = (EditText) contactDialogLayout.findViewById(R.id.dialog_phoneNumber);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        setupContactDialog(contactDialogLayout, alertBuilder, firstName_edittext, lastName_edittext, phoneNumber_edittext);
    }

    private void setupContactDialog(View customLayout, AlertDialog.Builder alertBuilder, EditText firstName_edittext,
                                    EditText lastName_edittext, EditText phoneNumber_edittext) {
        alertBuilder.setTitle(R.string.newContactTitle)
                .setPositiveButton(R.string.newContactPositiveButtonText, ((dialog, which) -> {

                }))
                .setNegativeButton(R.string.newContactNegativeButtonText, ((dialog, which) -> dialog.dismiss()))
                .setView(customLayout);

        AlertDialog dialog = alertBuilder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields(firstName_edittext, lastName_edittext, phoneNumber_edittext)) {
                    Contact newContact = new Contact();
                    newContact.setFirstName(firstName_edittext.getText().toString().trim());
                    newContact.setLastName(lastName_edittext.getText().toString().trim());
                    newContact.setPhoneNumber(phoneNumber_edittext.getText().toString().toString());

                    createNewContact(newContact);
                    updateListView();
                    dialog.dismiss();
                } else {
                    Toast.makeText(ContactsActivity.this, "Fyll ut alle felt", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateFields(EditText... fields) {
        for(EditText field : fields) {
            if(field.getText().toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void createNewContact(Contact contact) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().contactDao().newContact(contact);

            handler.post(() -> {
                updateListView();
            });
        });
    }

    public void updateListView() {
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

    public void showContactDetailsDialog(Contact contact) {
        final View customLayout = getLayoutInflater().inflate(R.layout.contact_dialog_layout, null);
        final EditText firstName_edittext = customLayout.findViewById(R.id.dialog_firstName);
        final EditText lastName_edittext = customLayout.findViewById(R.id.dialog_lastName);
        final EditText phoneNumber_edittext = customLayout.findViewById(R.id.dialog_phoneNumber);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ContactsActivity.this);
        alertBuilder.setTitle(R.string.contactdetailsTitle);
        alertBuilder.setView(customLayout);

        firstName_edittext.setText(contact.getFirstName());
        lastName_edittext.setText(contact.getLastName());
        phoneNumber_edittext.setText(contact.getPhoneNumber());
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

                DeleteContact(contact);
                updateListView();
            }
        });

        AlertDialog alert = alertBuilder.create();
        alert.show();

        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!firstName_edittext.getText().toString().isEmpty() && !lastName_edittext.getText().toString().isEmpty() && !phoneNumber_edittext.getText().toString().isEmpty()) {
                    contact.setFirstName(firstName_edittext.getText().toString().trim());
                    contact.setLastName(lastName_edittext.getText().toString().trim());
                    contact.setPhoneNumber(phoneNumber_edittext.getText().toString().trim());
                    UpdateContact(contact);

                    alert.dismiss();
                }
                else {
                    Toast.makeText(ContactsActivity.this, "Alle felt må bli fylt ut!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void UpdateContact(Contact contact) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().contactDao().updateContact(contact);
            handler.post(() -> {
                updateListView();
            });
        });
    }

    public void DeleteContact(Contact contact) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().contactDao().deleteContact(contact);
            handler.post(() -> {
                updateListView();
            });
        });
    }
}