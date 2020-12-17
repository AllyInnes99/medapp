package com.example.medapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WeeklyActivity extends AppCompatActivity {

    MedicationModel medModel;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton, nextButton;
    AddWeeklyAdapter applicationAdapter;
    DatabaseHelper databaseHelper = new DatabaseHelper(WeeklyActivity.this);
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
        setContentView(R.layout.activity_weekly);

        medModel = (MedicationModel) getIntent().getSerializableExtra("MedModel");
        recyclerView = findViewById(R.id.recyclerView);
        floatingActionButton = findViewById(R.id.addApplicationButton);
        nextButton = findViewById(R.id.nextButton);
        displayRecycler();

        floatingActionButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeeklyActivity.this, AddWeeklyApplication.class);
                intent.putExtra("MedModel", medModel);
                startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                databaseHelper.addMedication(medModel);
                List<MedicationModel> models = databaseHelper.selectAllMedication();
                medModel = models.get(0);

                String [] days = {"Monday", "Tuesday", "Wednesday", "Thursday",
                        "Friday", "Saturday", "Sunday"};

                //List<DoseModel> dosesToAddToDb = new ArrayList<>();
                for(DoseModel doseModel: temp) {
                    doseModel.setMedicationId(medModel.getMedicationId());
                    if(doseModel.getDay().equals("Daily")){
                        for(int i=0; i< days.length; i++){
                            doseModel.setDay(days[i]);
                            databaseHelper.addDose(doseModel);
                        }
                    }
                    else{
                        databaseHelper.addDose(doseModel);
                    }
                }

                databaseHelper.updateDaysUntilEmpty(medModel);
                Intent i = new Intent(WeeklyActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
    }

    private void displayRecycler() {
        applicationAdapter = new AddWeeklyAdapter(WeeklyActivity.this, temp);
        recyclerView.setAdapter(applicationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(WeeklyActivity.this));
    }

}