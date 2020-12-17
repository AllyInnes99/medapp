package com.example.medapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

public class AddMedicationActivity extends AppCompatActivity {

    Button btn_add;
    EditText et_name, et_quantity, et_dosage;
    Spinner medTypeDropdown, measurementDropdown, frequencyDropdown;
    Switch autoTake;
    String selectedType, selectedMeasurement, selectedFrequency;
    final String[] medTypes = new String[] {"tablet", "pill", "injection", "powder",
                                            "drops", "inhalers", "topical"};
    final String[] measurements = new String[] {"g", "mg", "ml", "l"};
    final String[] frequencies = {"Daily", "Weekly"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);

        btn_add = findViewById(R.id.button_update);
        et_name = findViewById(R.id.edit_name);
        et_quantity = findViewById(R.id.edit_quantity);
        et_dosage = findViewById(R.id.et_dosage);
        medTypeDropdown = findViewById(R.id.spinner1);
        measurementDropdown = findViewById(R.id.spinner2);
        frequencyDropdown = findViewById(R.id.spinner3);
        autoTake = findViewById(R.id.autotake);

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
                    String medicationName =  et_name.getText().toString();
                    if(!MedicationModel.validateMedicationName(medicationName)) {
                        throw new Exception("Invalid medication name");
                    }
                    int quantity = Integer.parseInt(et_quantity.getText().toString());
                    int refill = 0;
                    double dosage = Double.parseDouble(et_dosage.getText().toString());

                    boolean take = autoTake.isChecked();

                    model = new MedicationModel(medicationName, quantity, refill, selectedType,
                                selectedFrequency, dosage, selectedMeasurement, "me", take );



                    // Determine the next activity based off the frequency selected
                    switch(selectedFrequency){
                        case "Daily":
                            intent = new Intent(AddMedicationActivity.this, DailyActivity.class);
                            break;
                        case "Weekly":
                            intent = new Intent(AddMedicationActivity.this, WeeklyActivity.class);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + selectedFrequency);
                    }

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

    }

}
