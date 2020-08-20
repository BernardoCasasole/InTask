package com.example.chatapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;


public class MainActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            NotificationChannel channel = new NotificationChannel("myNot","myNot", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publication_choice);
        selectedItem = R.id.bottom_nav_home;
        initToolbar();

    }



}