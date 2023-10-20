package com.s362106.mappe_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button preferenceBtn, contactBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferenceBtn = findViewById(R.id.preferenceBtn);
        contactBtn = findViewById(R.id.friendsBtn);
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