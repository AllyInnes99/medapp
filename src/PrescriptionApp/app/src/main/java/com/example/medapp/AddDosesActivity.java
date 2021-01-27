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
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddDosesActivity extends AppCompatActivity {
    MedicationModel medModel;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton, nextButton;
    AddDoseAdapter doseAdapter;
    DatabaseHelper databaseHelper = new DatabaseHelper(AddDosesActivity.this);
    List<AddDoseModel> tempModels = new ArrayList<>();
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 42;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            AddDoseModel doseModel = (AddDoseModel) data.getSerializableExtra("model");
            tempModels.add(doseModel);
            displayRecycler();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doses);

        medModel = (MedicationModel) getIntent().getSerializableExtra("MedModel");
        recyclerView = findViewById(R.id.recyclerView);
        floatingActionButton = findViewById(R.id.addApplicationButton);
        nextButton = findViewById(R.id.nextButton);
        displayRecycler();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddDosesActivity.this, CreateDoseActivity.class);
                intent.putExtra("MedModel", medModel);
                startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tempModels.isEmpty()) {
                    addToDatabase();
                    if(GoogleSignIn.getLastSignedInAccount(AddDosesActivity.this) != null){
                        addToGoogleCal();
                    }
                    Intent intent = new Intent(AddDosesActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(AddDosesActivity.this, "Please add at least one dose.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addToDatabase() {
        databaseHelper.addMedication(medModel);
        List<MedicationModel> mModels = databaseHelper.selectAllMedication();
        medModel = mModels.get(0);
        String daily = "Daily";
        for(AddDoseModel dm: tempModels){
            if(dm.isDoseDaily()){
                DoseModel m = new DoseModel(medModel.getMedicationId(), dm.getTime(),
                                            daily, dm.getQuantity());
                databaseHelper.addDose(m);
            }
            else {
                for(String day: dm.getDays()){
                    DoseModel m = new DoseModel(medModel.getMedicationId(), dm.getTime(),
                                                day, dm.getQuantity());
                    databaseHelper.addDose(m);
                }
            }
        }

        // update days until refill for med
        databaseHelper.updateDaysUntilEmpty(medModel);
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
        AddDosesActivity.this.registerReceiver(new AlertReceiver(), new IntentFilter());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(AddDosesActivity.this, doseModel.getDoseId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(AddDosesActivity.this, "Registered alarm.", Toast.LENGTH_SHORT).show();
    }

    private void addToGoogleCal() {
        Toast.makeText(AddDosesActivity.this, "Adding reminder events to Google Calendar", Toast.LENGTH_SHORT).show();
        GoogleCalendarHelper gac = new GoogleCalendarHelper(AddDosesActivity.this);
        medModel = databaseHelper.selectMedicationFromID(medModel.getMedicationId());
        try {
            gac.addDoseReminder(medModel);
            gac.addRefillEvents(medModel);
            gac.updateMedEvents(medModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        displayRecycler();
    }

    private void displayRecycler() {
        doseAdapter = new AddDoseAdapter(AddDosesActivity.this, tempModels);
        recyclerView.setAdapter(doseAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(AddDosesActivity.this));
    }

}
