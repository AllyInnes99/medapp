package com.example.prescriptionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class AddMedication2Activity extends AppCompatActivity {

    Spinner freqDropdown;
    final String[] frequencies = {"Daily", "Weekly", "Custom"};
    String freq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication2);

        // Get model from previous activity
        MedicationModel model = (MedicationModel) getIntent().getSerializableExtra("MedModel");

        freqDropdown = findViewById(R.id.freqDropdown);
        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item);
        freqDropdown.setAdapter(frequencyAdapter);
        freqDropdown.setOnItemSelectedListener((new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }));
    }
}