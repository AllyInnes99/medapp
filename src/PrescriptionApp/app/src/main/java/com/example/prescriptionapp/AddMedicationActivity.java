package com.example.prescriptionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddMedicationActivity extends AppCompatActivity {

    Button btn_add, btn_cancel;
    EditText et_name, et_quantity, et_refill;
    Spinner medTypeDropdown, measurementDropdown, frequencyDropdown;
    String selectedType, selectedMeasurement, selectedFrequency;
    final String[] medTypes = new String[] {"tablet", "pill", "injection", "powder",
                                            "drops", "inhalers", "topical"};
    final String[] measurements = new String[] {"g", "mg", "ml", "l"};
    final String[] frequencies = {"Daily", "Weekly"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);

        btn_add = findViewById(R.id.button_confirm);
        btn_cancel = findViewById(R.id.button_cancel);
        et_name = findViewById(R.id.edit_name);
        et_quantity = findViewById(R.id.edit_quantity);
        et_refill = findViewById(R.id.edit_refill);
        medTypeDropdown = findViewById(R.id.spinner1);
        measurementDropdown = findViewById(R.id.spinner2);
        frequencyDropdown = findViewById(R.id.spinner3);

        ArrayAdapter<String> medTypeAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, medTypes);
        medTypeDropdown.setAdapter(medTypeAdapter);
        medTypeDropdown.setOnItemSelectedListener((new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedType = medTypes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }));

        ArrayAdapter<String> measurementAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, measurements);
        measurementDropdown.setAdapter(measurementAdapter);
        measurementDropdown.setOnItemSelectedListener((new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMeasurement = measurements[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }));

        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, frequencies);
        frequencyDropdown.setAdapter(frequencyAdapter);
        frequencyDropdown.setOnItemSelectedListener((new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFrequency = frequencies[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }));


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                MedicationModel model;
                Intent intent;
                try {
                    DatabaseHelper databaseHelper = new DatabaseHelper(AddMedicationActivity.this);
                    String medicationName =  et_name.getText().toString();
                    if(!validateMedicationName(medicationName)) {
                        throw new Exception("Invalid medication name");
                    }
                    int quantity = Integer.parseInt(et_quantity.getText().toString());
                    int refill = Integer.parseInt(et_refill.getText().toString());
                    int id = databaseHelper.countMedication() + 1;

                    model = new MedicationModel(medicationName, quantity,refill, selectedType,
                                                selectedFrequency, selectedMeasurement, "me");
                    boolean success = databaseHelper.addMedication(model);
                    Toast.makeText(AddMedicationActivity.this, "success = " + success, Toast.LENGTH_SHORT).show();
                    intent = new Intent(AddMedicationActivity.this, DailyActivity.class);
                    intent.putExtra("MedModel", model);
                    startActivity(intent);

                }
                catch (NumberFormatException e) {
                    Toast.makeText(AddMedicationActivity.this, "Invalid number for quantity", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    Toast.makeText(AddMedicationActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * Helper method that validates if the name of the medication is valid
     * @param medicationName - String of the med name
     * @return true if valid, false otherwise
     */
    public boolean validateMedicationName(String medicationName) {
        return !medicationName.isEmpty();
    }
    
}
