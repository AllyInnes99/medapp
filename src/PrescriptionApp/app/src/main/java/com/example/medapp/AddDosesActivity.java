package com.example.medapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
    private static final int ADD_DOSE_REQUEST_CODE = 42;
    private static final int ADD_PATIENT_REQUEST_CODE = 54;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_DOSE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            AddDoseModel doseModel = (AddDoseModel) data.getSerializableExtra("model");
            tempModels.add(doseModel);
            displayRecycler();
        }

        else if( requestCode == ADD_PATIENT_REQUEST_CODE && resultCode == RESULT_OK &&  data != null) {
            returnToMainActivity();
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
                startActivityForResult(intent, ADD_DOSE_REQUEST_CODE);
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
                    else {
                        returnToMainActivity();
                    }

                }
                else {
                    Toast.makeText(AddDosesActivity.this, "Please add at least one dose.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Method that adds the doses to the database
     */
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

        List<DoseModel> addedDoses = databaseHelper.selectDoseFromMedication(medModel);
        for(DoseModel dose: addedDoses) {
            initialiseNotification(dose);
        }

    }

    /**
     * Method that starts a notification for a medication does, if the medication is meant
     * to be taken later in the day
     * @param doseModel the dose that a notification is to be created for
     */
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

        if(Calendar.getInstance().getTimeInMillis() < c.getTimeInMillis()){
            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlertReceiver.class);
            intent.setAction("android.intent.action.NOTIFY");

            intent.putExtra("quantity", doseModel.getAmount());
            intent.putExtra("name", medModel.getName());


            PendingIntent pendingIntent = PendingIntent.getBroadcast(AddDosesActivity.this, doseModel.getDoseId() + 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            //Toast.makeText(AddDosesActivity.this, "Registered alarm.", Toast.LENGTH_SHORT).show();
        }

    }

    private void addToGoogleCal() {
        GoogleCalendarHelper gch = new GoogleCalendarHelper(AddDosesActivity.this);
        medModel = databaseHelper.selectMedicationFromID(medModel.getMedicationId());
        if(databaseHelper.countContacts() > 0) {
            promptToAddContacts(gch);
        }
        else {
            addEvents(gch);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        displayRecycler();
    }

    private void promptToAddContacts(final GoogleCalendarHelper gch) {
        new MaterialAlertDialogBuilder(AddDosesActivity.this)
            .setTitle("Select Patients")
            .setMessage("Would you like to assign a patient to this medication? The details of the medication will be shared with them on Google Calendar.")
            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    medModel = databaseHelper.selectAllMedication().get(0);
                    Intent i = new Intent(AddDosesActivity.this, CarerActivity.class);
                    i.putExtra("medId", medModel.getMedicationId());
                    startActivity(i);
                }
            })
            .setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addEvents(gch);
                }
            })
            .show();
    }

    private void addEvents(GoogleCalendarHelper gch) {
        gch.addDoseReminder(medModel);
        gch.addRefillEvents(medModel);
        returnToMainActivity();
    }

    private void displayRecycler() {
        doseAdapter = new AddDoseAdapter(AddDosesActivity.this, tempModels);
        recyclerView.setAdapter(doseAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(AddDosesActivity.this));
    }

    private void returnToMainActivity() {
        Intent intent = new Intent(AddDosesActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Toast.makeText(AddDosesActivity.this, "Successfully create medication " + medModel.getName(), Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

}
