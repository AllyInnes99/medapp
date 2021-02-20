package com.example.medapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.provider.CalendarContract;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class App extends Application {
    public static final String MED_TAKING_CHANNEL = "medChannel";
    public static final String REFILL_CHANNEL = "refillChannel";
    public static List<String> days = Arrays.asList("", "Sunday", "Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday");

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    /**
     * Method that creates the notification channels for taking medication doses and refill reminders
     */
    private void createNotificationChannels() {

        // First check that we are on Android Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel medChannel = new NotificationChannel(
                    MED_TAKING_CHANNEL,
                    "Dose Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            medChannel.setDescription("Notifications that alert users to take doses of medication at appointed time.");

            NotificationChannel refillChannel = new NotificationChannel(
                    REFILL_CHANNEL,
                    "Refill Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            refillChannel.setDescription("Notifications that alert user that they need to re-supply certain med");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(medChannel);
            notificationManager.createNotificationChannel(refillChannel);
        }
    }

}
