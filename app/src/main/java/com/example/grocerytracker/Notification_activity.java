package com.example.grocerytracker;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Notification_activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Redirects to the page after user clicks the Notification message
        setContentView(R.layout.activity_add_grocery);
    }
}
