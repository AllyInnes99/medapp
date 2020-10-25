package com.example.prescriptionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AddDailyActivity extends AppCompatActivity {


    private static final int LIMIT = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_daily);

        // Get model from previous activity
        MedicationModel model = (MedicationModel) getIntent().getSerializableExtra("MedModel");

    }
}