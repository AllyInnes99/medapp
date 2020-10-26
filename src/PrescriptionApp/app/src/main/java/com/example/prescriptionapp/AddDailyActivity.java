package com.example.prescriptionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AddDailyActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    ApplicationAdapter applicationAdapter;
    MedicationModel model = (MedicationModel) getIntent().getSerializableExtra("MedModel");
    List<ApplicationModel> temp;

    private static final int LIMIT = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_daily);

        recyclerView = findViewById(R.id.recyclerView);
        floatingActionButton = findViewById(R.id.addApplicationButton);
        temp = new ArrayList<>();
        displayRecycler();


    }

    @Override
    protected void onResume() {
        super.onResume();
        temp = (List<ApplicationModel>) getIntent().getSerializableExtra("Applications");
        displayRecycler();
    }

    private void displayRecycler() {
        applicationAdapter = new ApplicationAdapter(AddDailyActivity.this, temp);
        recyclerView.setAdapter(applicationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(AddDailyActivity.this));
    }



}