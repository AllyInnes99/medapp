package com.example.prescriptionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;


public class DailyActivity extends AppCompatActivity {

    MedicationModel medModel;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton, nextButton;
    AddApplicationAdapter applicationAdapter;
    DatabaseHelper databaseHelper = new DatabaseHelper(DailyActivity.this);

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            List<ApplicationModel> applModels = databaseHelper.selectApplFromMedication(medModel);
            for(ApplicationModel m: applModels){
                databaseHelper.deleteApplication(m);
            }
            databaseHelper.deleteMedication(medModel);
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        medModel = (MedicationModel) getIntent().getSerializableExtra("MedModel");
        recyclerView = findViewById(R.id.recyclerView);
        floatingActionButton = findViewById(R.id.addApplicationButton);
        nextButton = findViewById(R.id.nextButton);

        displayRecycler();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DailyActivity.this, AddDailyApplication.class);
                intent.putExtra("MedModel", medModel);
                startActivity(intent);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(DailyActivity.this, MainActivity.class);

                // For each application set-up, initialise a notification for taking the medication
                List<ApplicationModel> applModels = databaseHelper.selectApplFromMedication(medModel);
                Toast.makeText(DailyActivity.this, medModel.getName(), Toast.LENGTH_SHORT).show();
                for(ApplicationModel model: applModels){
                    intialiseNotification(model);
                }
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
    }

    private void intialiseNotification(ApplicationModel applicationModel){
        Calendar c = Calendar.getInstance();
        String[] time = applicationModel.timeToHourAndMin();
        int hour = Integer.parseInt(time[0]);
        int mins = Integer.parseInt(time[1]);

        // Set calendar to represent the day
        c.set(Calendar.DAY_OF_WEEK, applicationModel.dayToInt());
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, mins);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);


        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.setAction("android.intent.action.NOTIFY");

        intent.putExtra("quantity", applicationModel.getAmount());
        intent.putExtra("name", medModel.getName());

        // Register receiver
        DailyActivity.this.registerReceiver(new AlertReceiver(), new IntentFilter());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(DailyActivity.this, applicationModel.getApplicationId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        displayRecycler();
    }

    private void displayRecycler() {
        List<ApplicationModel> applModels = databaseHelper.selectApplFromMedicationAndDay(medModel);
        applicationAdapter = new AddApplicationAdapter(DailyActivity.this, applModels);
        recyclerView.setAdapter(applicationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(DailyActivity.this));
    }

}
