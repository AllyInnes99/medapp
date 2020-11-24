package com.example.prescriptionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;


import android.app.AlarmManager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        navController = Navigation.findNavController(this, R.id.navFragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        setRefreshAlarm();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setMedicationAlarm(){
        // get every application in the db
        DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
        List<ApplicationModel> applications = databaseHelper.selectAllApplications();
        for(ApplicationModel applicationModel: applications){
            setApplicationAlarm(applicationModel);
        }
    }

    /**
     * Method that creates a reminder notification for user to take certain medication
     * @param model - the application to be notified
     */
    private void setApplicationAlarm(ApplicationModel model){
        Calendar c = Calendar.getInstance();
        String[] time = model.timeToHourAndMin();
        int hour = Integer.parseInt(time[0]);
        int mins = Integer.parseInt(time[1]);

        // Set calendar to represent the day
        c.set(Calendar.DAY_OF_WEEK, model.dayToInt());
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, mins);


        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.setAction("android.intent.action.NOTIFY");

        // Register receiver
        MainActivity.this.registerReceiver(new AlertReceiver(), new IntentFilter());

        intent.putExtra("MyModel", model);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, model.getApplicationId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
    }

    /**
     * Helper function that is called on creation to start the refresh cycle for medication
     * I.e., isTaken in each application will be set to false in every appl in db at the start of
     * a new week
     */
    private void setRefreshAlarm() {
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        // Setup calendar obj so that it is set to the coming Monday at 00:00
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        // Use time as a unique ID for the pending intent
        int id = (int) System.currentTimeMillis();

        // Setup intent to pass to receiver
        Intent intent = new Intent(MainActivity.this, RefreshReceiver.class);
        intent.setAction("android.intent.action.NOTIFY");

        // Register the custom broadcast receiver
        MainActivity.this.registerReceiver(new RefreshReceiver(), new IntentFilter());

        // Set up pendingIntent for the broadcast to specify action in the future
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set repeating alarm that calls onReceive() of RefreshReceiver at supplied time
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
    }

}
