package com.example.prescriptionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class AddMedicationActivity extends AppCompatActivity {

    Button btn_add, btn_cancel;
    EditText et_name, et_quantity;
    Switch sw_isTaken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);

        btn_add = findViewById(R.id.button_confirm);
        btn_cancel = findViewById(R.id.button_cancel);
        et_name = findViewById(R.id.edit_name);
        et_quantity = findViewById(R.id.edit_quantity);
        sw_isTaken = findViewById(R.id.sw_isTaken);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                MedicationModel model;
                try {
                    String medicationName =  et_name.getText().toString();
                    model = new MedicationModel(0,  medicationName, Integer.parseInt(et_quantity.getText().toString()), sw_isTaken.isChecked());
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

    }

    public boolean validateMedicationName(String medicationName) {
        return !medicationName.isEmpty();
    }


}