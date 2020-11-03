package com.example.prescriptionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class DailyActivity extends AppCompatActivity {

    MedicationModel model;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton, nextButton;
    ApplicationAdapter applicationAdapter;
    List<ApplicationModel> temp;
    DatabaseHelper databaseHelper = new DatabaseHelper(DailyActivity.this);

    private static final int LIMIT = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        model = (MedicationModel) getIntent().getSerializableExtra("MedModel");
        recyclerView = findViewById(R.id.recyclerView);
        floatingActionButton = findViewById(R.id.addApplicationButton);
        nextButton = findViewById(R.id.nextButton);

        displayRecycler();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DailyActivity.this, AddDailyApplication.class);
                intent.putExtra("MedModel", model);
                startActivity(intent);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(DailyActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayRecycler();
    }

    private void displayRecycler() {
        List<ApplicationModel> applModels = databaseHelper.selectApplFromMedicationAndDay(model);
        applicationAdapter = new ApplicationAdapter(DailyActivity.this, applModels);
        recyclerView.setAdapter(applicationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(DailyActivity.this));
    }

}
