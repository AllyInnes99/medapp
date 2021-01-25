package com.example.medapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditMedDosesActivity extends AppCompatActivity {

    MedicationModel medModel;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton, nextButton;
    AddDoseAdapter doseAdapter;
    GoogleCalendarHelper gch;
    List<String> days = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday",
                                        "Friday", "Saturday", "Sunday");
    Context context = EditMedDosesActivity.this;
    DatabaseHelper databaseHelper = new DatabaseHelper(context);
    List<AddDoseModel> tempModels;
    List<DoseModel> doseModels;
    List<DoseModel> originalDoses;
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
        setContentView(R.layout.activity_edit_med_doses);
        tempModels = new ArrayList<>();
        int id = getIntent().getIntExtra("medID", 0);
        medModel = databaseHelper.selectMedicationFromID(id);
        doseModels = databaseHelper.selectDoseFromMedication(medModel);
        originalDoses = new ArrayList<>(doseModels);
        gch = new GoogleCalendarHelper(context);
        getData();

        recyclerView = findViewById(R.id.recyclerView);
        floatingActionButton = findViewById(R.id.addApplicationButton);
        nextButton = findViewById(R.id.nextButton);

        displayRecycler();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditMedDosesActivity.this, CreateDoseActivity.class);
                intent.putExtra("MedModel", medModel);
                startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tempModels.isEmpty()) {
                    addToDatabase();
                    Intent output = new Intent();
                    output.putExtra("refill", medModel.getDaysUntilEmpty());
                    setResult(RESULT_OK, output);
                    finish();
                }
                else {
                    Toast.makeText(EditMedDosesActivity.this, "Please add at least one dose.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * Method that makes changes to the database to accommodate the newly created/removed doses
     */
    private void addToDatabase(){

        // remove all previous doseModels from db and Google Calendar
        for(DoseModel dm: doseModels) {
            gch.deleteDoseEvent(dm);
            databaseHelper.deleteDose(dm);
        }

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
        medModel = databaseHelper.selectMedicationFromID(medModel.getMedicationId());

        // add the new Doses to Google Calendar
        if(medModel.getCalendarRefill() != null) {
            gch.updateRefillEvent(medModel);
            gch.updateEmptyEvent(medModel);
            gch.addDoseReminder(medModel);
        }
    }



    private void getData() {
        List<String> tempDays = new ArrayList<>();
        String prevTime = "";
        int prevAmount = 0;
        for(DoseModel dm: doseModels) {
            String day = dm.getDay();
            String time = dm.getTime();

            // if the dose is one that is taken every day, then simply just add every day to model
            if(day.equals("Daily")){
                AddDoseModel addDoseModel = new AddDoseModel(dm.getTime(), dm.getAmount());
                addDoseModel.setDays(days);


                if(!tempDays.isEmpty()) {
                    Toast.makeText(context, "here", Toast.LENGTH_SHORT).show();
                    AddDoseModel prevDose = new AddDoseModel(prevTime, prevAmount);
                    prevDose.setDays(new ArrayList<>(tempDays));
                    tempModels.add(prevDose);
                    tempDays.clear();
                }
                tempModels.add(addDoseModel);
                prevTime = "";
            }

            else if(!prevTime.equals(time) && !tempDays.isEmpty()) {
                AddDoseModel addDoseModel = new AddDoseModel(prevTime, dm.getAmount());
                addDoseModel.setDays(new ArrayList<>(tempDays));
                tempModels.add(addDoseModel);
                tempDays.clear();
                prevTime = "";
            }

            // otherwise, we need to identify the days for which the dose is scheduled to take place
            else {
                prevTime = time;
                prevAmount = dm.getAmount();
                tempDays.add(day);
            }
        }

        // if the last dose was not daily, then add it to the list
        if(!tempDays.isEmpty()) {
            DoseModel dm = doseModels.get(doseModels.size() - 1);
            AddDoseModel addDoseModel = new AddDoseModel(dm.getTime(), dm.getAmount());
            addDoseModel.setDays(new ArrayList<>(tempDays));
            tempModels.add(addDoseModel);
        }
    }

    private void displayRecycler() {
        doseAdapter = new AddDoseAdapter(EditMedDosesActivity.this, tempModels);
        recyclerView.setAdapter(doseAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(EditMedDosesActivity.this));
    }

}
