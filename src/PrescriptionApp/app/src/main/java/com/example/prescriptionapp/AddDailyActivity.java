package com.example.prescriptionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class AddDailyActivity extends AppCompatActivity {

    MedicationModel model;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    ApplicationAdapter applicationAdapter;
    List<ApplicationModel> temp;

    private static final int LIMIT = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_daily);

        model = (MedicationModel) getIntent().getSerializableExtra("MedModel");
        recyclerView = findViewById(R.id.recyclerView);
        floatingActionButton = findViewById(R.id.addApplicationButton);
        temp = new ArrayList<>();
        //displayRecycler();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddDailyActivity.this, AddApplicationActivity.class);
                intent.putExtra("MedModel", model);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //temp = (List<ApplicationModel>) getIntent().getSerializableExtra("Applications");
        //displayRecycler();
    }

    private void displayRecycler() {
        applicationAdapter = new ApplicationAdapter(AddDailyActivity.this, temp);
        recyclerView.setAdapter(applicationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(AddDailyActivity.this));
    }



}