package com.example.prescriptionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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

        // Ensure that reminder is setup for future if currently ahead of new reminder
        Calendar now = Calendar.getInstance();
        if(c.before(now)) {
            c.add(Calendar.DATE, 7);
        }

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("MyModel", model);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, model.getApplicationId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long interval = 7 * 24 * 60 * 60 * 1000;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), interval, pendingIntent);
    }


}
