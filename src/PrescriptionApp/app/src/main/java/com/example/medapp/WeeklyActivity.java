package com.example.medapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class WeeklyActivity extends AppCompatActivity {

    MedicationModel model;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton, nextButton;
    AddApplicationAdapter applicationAdapter;
    DatabaseHelper databaseHelper = new DatabaseHelper(WeeklyActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toast.makeText(WeeklyActivity.this, "Hello", Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly);

        model = (MedicationModel) getIntent().getSerializableExtra("MedModel");
        recyclerView = findViewById(R.id.recyclerView);
        floatingActionButton = findViewById(R.id.addApplicationButton);
        nextButton = findViewById(R.id.nextButton);
        displayRecycler();

        floatingActionButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeeklyActivity.this, AddWeeklyApplication.class);
                intent.putExtra("MedModel", model);
                startActivity(intent);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(WeeklyActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
    }

    private void displayRecycler() {
        List<DoseModel> applModels = databaseHelper.selectDoseFromMedication(model);
        applicationAdapter = new AddApplicationAdapter(WeeklyActivity.this, applModels);
        recyclerView.setAdapter(applicationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(WeeklyActivity.this));
    }

}