package com.example.medapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class CarerActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carer);

        MedicationModel medModel = (MedicationModel) getIntent().getSerializableExtra("medModel");
        recyclerView = findViewById(R.id.recyclerView);


    }
}