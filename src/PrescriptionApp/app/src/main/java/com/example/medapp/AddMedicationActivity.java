package com.example.medapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

public class AddMedicationActivity extends AppCompatActivity {

    TextInputEditText et_name, et_quantity, et_strength;
    AutoCompleteTextView dropdown_measurement, dropdown_type;
    Button submit_btn;
    RadioButton radio1, radio2;
    SwitchMaterial autoTake;

    final String[] medTypes = new String[] {"tablet", "pill", "injection", "powder",
            "drops", "inhalers", "topical"};
    final String[] measurements = new String[] {"g", "mg", "ml", "l"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        et_name = findViewById(R.id.et_name);
        et_quantity = findViewById(R.id.et_quantity);
        et_strength = findViewById(R.id.et_strength);
        dropdown_measurement = findViewById(R.id.dropdown_measurement);
        dropdown_type = findViewById(R.id.dropdown_type);
        submit_btn = findViewById(R.id.submit_btn);
        radio1 = findViewById(R.id.radio_button_1);
        radio2 = findViewById(R.id.radio_button_2);
        autoTake = findViewById(R.id.autotake);


        ArrayAdapter<String> measurementAdapter =
                new ArrayAdapter<>(AddMedicationActivity.this, R.layout.list_item, measurements);
        dropdown_measurement.setAdapter(measurementAdapter);

        ArrayAdapter<String> typeAdapter =
                new ArrayAdapter<String>(AddMedicationActivity.this, R.layout.list_item, medTypes);
        dropdown_type.setAdapter(typeAdapter);



        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                MedicationModel model;
                Intent intent;

                String selectedType = dropdown_type.getText().toString();
                String selectedMeasurement = dropdown_measurement.getText().toString();
                String selectedFrequency;
                if(radio1.isChecked()){
                    selectedFrequency = "Daily";
                    intent = new Intent(AddMedicationActivity.this, DailyActivity.class);
                }
                else{
                    selectedFrequency = "Weekly";
                    intent = new Intent(AddMedicationActivity.this, WeeklyActivity.class);
                }

                try {
                    String medicationName =  et_name.getText().toString();
                    if(!MedicationModel.validateMedicationName(medicationName)) {
                        throw new Exception("Invalid medication name");
                    }
                    int quantity = Integer.parseInt(et_quantity.getText().toString());
                    int refill = 0;
                    double dosage = Double.parseDouble(et_strength.getText().toString());

                    boolean take = autoTake.isChecked();

                    model = new MedicationModel(medicationName, quantity, refill, selectedType,
                                selectedFrequency, dosage, selectedMeasurement, "me", take );
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
