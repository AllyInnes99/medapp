package com.example.medapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;


import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    NavController navController;
    View parentLayout;
    private FirebaseAuth mAuth;

    private static final int DAILY_EVENT_ID = 1;
    private static final int WEEKLY_EVENT_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parentLayout = findViewById(android.R.id.content);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        navController = Navigation.findNavController(this, R.id.navFragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        mAuth = FirebaseAuth.getInstance();
        setDailyEventAlarm();
        setRefreshAlarm();

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
        if (acct != null) {
            PeopleAPIHelper peopleApi = new PeopleAPIHelper(MainActivity.this);
            //Toast.makeText(MainActivity.this, Boolean.toString(peopleApi.getService() == null), Toast.LENGTH_LONG).show();
            peopleApi.getContacts();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Helper function that is called on creation to start the daily cycle of "automatically" taking
     * medication that the user wishes to do so.
     */
    private void setDailyEventAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // set calendar to begin at midnight the next day
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DATE, 1);

        // Setup intent to pass to receiver
        Intent intent = new Intent(MainActivity.this, DailyEventReceiver.class);
        intent.setAction("android.intent.action.NOTIFY");

        // Set up pendingIntent for the broadcast to specify action in the future
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, DAILY_EVENT_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set repeating alarm that calls onReceive() of AutoTakeReceiver at supplied time
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }


    /**
     * Helper function that is called on creation to start the refresh cycle for medication
     * I.e., isTaken in each application will be set to false in every dose in db at the start of
     * a new week
     */
    private void setRefreshAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Setup calendar obj so that it is set to the coming Monday at 00:00
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        // Setup intent to pass to receiver
        Intent intent = new Intent(MainActivity.this, RefreshReceiver.class);
        intent.setAction("android.intent.action.NOTIFY");

        // Set up pendingIntent for the broadcast to specify action in the future
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, WEEKLY_EVENT_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set repeating alarm that calls onReceive() of RefreshReceiver at supplied time
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
    }





}
