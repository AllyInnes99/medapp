package com.example.prescriptionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.List;

public class UpdateMedActivity extends AppCompatActivity {

    Button btn_add, btn_cancel;
    EditText et_name, et_quantity, et_refill;
    Spinner medTypeDropdown, measurementDropdown;
    String selectedType, selectedMeasurement;
    MedicationModel model;
    final List<String> medTypes = Arrays.asList(new String[] {"tablet", "pill", "injection", "powder",
                                                                "drops", "inhalers", "topical"});
    final List<String> measurements = Arrays.asList(new String[] {"g", "mg", "ml", "l"});

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_med);

        btn_add = findViewById(R.id.button_confirm);
        btn_cancel = findViewById(R.id.button_cancel);
        et_name = findViewById(R.id.edit_name);
        et_quantity = findViewById(R.id.edit_quantity);
        et_refill = findViewById(R.id.edit_refill);
        medTypeDropdown = findViewById(R.id.spinner1);
        measurementDropdown = findViewById(R.id.spinner2);

        model = (MedicationModel) getIntent().getSerializableExtra("MedModel");

        et_name.setText(model.getName());
        et_quantity.setText(Integer.toString(model.getQuantity()));
        et_refill.setText(Integer.toString(model.getRefillAt()));

        ArrayAdapter<String> medTypeAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, medTypes);
        medTypeDropdown.setSelection(medTypes.indexOf(model.getType()));
        medTypeDropdown.setAdapter(medTypeAdapter);
        medTypeDropdown.setOnItemSelectedListener((new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedType = medTypes.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }));

        ArrayAdapter<String> measurementAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, measurements);
        measurementDropdown.setSelection(measurements.indexOf(model.getMeasurement()));
        measurementDropdown.setAdapter(measurementAdapter);
        measurementDropdown.setOnItemSelectedListener((new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMeasurement = measurements.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }));


    }
}