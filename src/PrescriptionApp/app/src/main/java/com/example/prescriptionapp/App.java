package com.example.prescriptionapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String MED_TAKING_CHANNEL = "medChannel";
    public static final String REFILL_CHANNEL = "refillChannel";

    @Override
    public void onCreate(){
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels(){

        // First check that we are on Android Oreo or higher
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel medChannel = new NotificationChannel(
                    MED_TAKING_CHANNEL,
                    "Med Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            medChannel.setDescription("Notifications that alert users to take their medication");

            NotificationChannel refillChannel = new NotificationChannel(
                    REFILL_CHANNEL,
                    "Refill Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            refillChannel.setDescription("Notifications that alert user that they need to re-supply certain med");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(medChannel);
            notificationManager.createNotificationChannel(refillChannel);

        }

    }

}
