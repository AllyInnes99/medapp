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
    public static final int CALLBACK_ID = 42;
    public static final String CREDENTIALS_FILE_PATH = "../credentials.json";
    public static Map<MedicationModel, NotificationChannel> channels = new HashMap<>();
    public static Map<MedicationModel, String> channelIDs = new HashMap<>();
    public static List<String> days = Arrays.asList("", "Sunday", "Monday", "Tuesday", "Wednesday",
                                                    "Thursday", "Friday", "Saturday");

    @Override
    public void onCreate(){
        super.onCreate();
        Resources res = getResources();
        createNotificationChannels();
    }

    private void newNotificationChannel(MedicationModel m) {

        // First check that we are on Android Oreo or higher
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            // create ID for notif. channel
            String channelID = createChannelID(m);

            String channelName = m.getName() + " Reminder";
            NotificationChannel channel = new NotificationChannel(
                channelID,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications that remind user to take their " + m.getName() +
                                    " medication.");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            channels.put(m, channel);
        }

    }

    private String createChannelID(MedicationModel m) {
        String channelID = m.getName().toUpperCase(Locale.ROOT) + "_CHANNEL";
        channelIDs.put(m, channelID);
        return channelID;
    }


    private void removeNotificationChannel(MedicationModel m){
        NotificationChannel channel = channels.get(m);
        if(m != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.deleteNotificationChannel(channelIDs.get(m));
        }
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
