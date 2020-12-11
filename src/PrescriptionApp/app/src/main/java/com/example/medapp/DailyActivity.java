package com.example.medapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class DailyActivity extends AppCompatActivity {

    MedicationModel medModel;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton, nextButton;
    AddApplicationAdapter applicationAdapter;
    DatabaseHelper databaseHelper = new DatabaseHelper(DailyActivity.this);
    List<DoseModel> temp = new ArrayList<>();
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 42;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            temp.clear();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            DoseModel m = (DoseModel) data.getSerializableExtra("applModel");
            temp.add(m);
            displayRecycler();
        }
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
                startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                databaseHelper.addMedication(medModel);
                //Toast.makeText(DailyActivity.this, medModel.getName(), Toast.LENGTH_SHORT).show();

                List<MedicationModel> mModels = databaseHelper.selectAllMedication();
                medModel = mModels.get(0);

                Intent intent = new Intent(DailyActivity.this, MainActivity.class);

                String [] days = {"Monday", "Tuesday", "Wednesday", "Thursday",
                        "Friday", "Saturday", "Sunday"};

                for(DoseModel m: temp){
                    m.setMedicationId(medModel.getMedicationId());
                    for(int i = 0; i < days.length; i++){
                        m.setDoseId((int) Calendar.getInstance().getTimeInMillis());
                        m.setDay(days[i]);
                        databaseHelper.addDose(m);
                    }
                    initialiseNotification(m);
                }

                // update days until refill for med
                databaseHelper.updateDaysUntilEmpty(medModel);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    /**
     * Helper method that creates a new notification channel for a newly added medication
     */
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            String id = medModel.getName();
            String label = medModel.getName() + " reminder";
            NotificationChannel channel = new NotificationChannel(
                    id, label, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications to remind user to take "+ medModel.getName() + " at appointed time");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    private void initialiseNotification(DoseModel doseModel){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());

        String[] time = doseModel.timeToHourAndMin();
        int hour = Integer.parseInt(time[0]);
        int mins = Integer.parseInt(time[1]);

        // Set calendar to represent the day
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, mins);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.setAction("android.intent.action.NOTIFY");

        intent.putExtra("quantity", doseModel.getAmount());
        intent.putExtra("name", medModel.getName());

        // Register receiver
        DailyActivity.this.registerReceiver(new AlertReceiver(), new IntentFilter());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(DailyActivity.this, doseModel.getDoseId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(DailyActivity.this, "Registered alarm.", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        displayRecycler();
    }

    private void displayRecycler() {
        //List<DoseModel> applModels = databaseHelper.selectDoseFromMedicationAndDay(medModel);
        applicationAdapter = new AddApplicationAdapter(DailyActivity.this, temp);
        recyclerView.setAdapter(applicationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(DailyActivity.this));
    }

}
