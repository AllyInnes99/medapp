package com.example.prescriptionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class AddMedication2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication2);

        MedicationModel model = (MedicationModel) getIntent().getSerializableExtra("MedModel");
        Toast.makeText(AddMedication2Activity.this, model.getName(), Toast.LENGTH_SHORT).show();
    }
}