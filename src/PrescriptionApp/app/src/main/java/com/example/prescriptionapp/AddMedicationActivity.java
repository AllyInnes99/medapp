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
import android.widget.Switch;
import android.widget.Toast;

public class AddMedicationActivity extends AppCompatActivity {

    Button btn_add, btn_cancel;
    EditText et_name, et_quantity;
    Spinner dropdown;
    final String[] medTypes = new String[] {"tablet", "pill", "injection", "powder", "suppository",
                                            "drops", "inhalers", "topical"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);

        btn_add = findViewById(R.id.button_confirm);
        btn_cancel = findViewById(R.id.button_cancel);
        et_name = findViewById(R.id.edit_name);
        et_quantity = findViewById(R.id.edit_quantity);
        dropdown = findViewById(R.id.spinner1);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, medTypes);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener((new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(AddMedicationActivity.this, medTypes[position], Toast.LENGTH_SHORT ).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }));


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                MedicationModel model;
                try {
                    String medicationName =  et_name.getText().toString();
                    model = new MedicationModel(0,  medicationName, Integer.parseInt(et_quantity.getText().toString()), true);
                    Toast.makeText(AddMedicationActivity.this, model.toString(), Toast.LENGTH_SHORT).show();
                    DatabaseHelper databaseHelper = new DatabaseHelper(AddMedicationActivity.this);
                    boolean success = databaseHelper.addMedication(model);
                    Toast.makeText(AddMedicationActivity.this, "success = " + success, Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    Toast.makeText(AddMedicationActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    model = new MedicationModel(-1, "error", 0, false);
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

    public boolean validateMedicationName(String medicationName) {
        return !medicationName.isEmpty();
    }
    
}