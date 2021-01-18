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
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class UpdateMedActivity extends AppCompatActivity {

    Button btn_update, btn_delete, btn_cal, btn_dose;
    EditText et_name, et_quantity, et_refill, et_dosage;
    Spinner medTypeDropdown, measurementDropdown;
    String selectedType, selectedMeasurement;
    MedicationModel model;
    DatabaseHelper databaseHelper = new DatabaseHelper(UpdateMedActivity.this);
    int originalQuantity;
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 42;
    final List<String> medTypes = Arrays.asList("tablet", "pill", "injection", "powder",
                                                "drops", "inhalers", "topical");
    final List<String> measurements = Arrays.asList("g", "mg", "ml", "l");

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String msg = "Updated the doses of " + model.getName();
            Toast.makeText(UpdateMedActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_med);

        btn_update = findViewById(R.id.button_update);
        btn_delete = findViewById(R.id.button_cancel);
        btn_cal = findViewById(R.id.btn_cal);
        btn_dose = findViewById(R.id.btn_dose);
        et_name = findViewById(R.id.edit_name);
        et_quantity = findViewById(R.id.edit_quantity);
        et_refill = findViewById(R.id.edit_refill);
        et_dosage = findViewById(R.id.et_dosage);
        medTypeDropdown = findViewById(R.id.spinner1);
        measurementDropdown = findViewById(R.id.spinner2);

        model = (MedicationModel) getIntent().getSerializableExtra("MedModel");
        originalQuantity = model.getQuantity();

        et_name.setText(model.getName());
        et_quantity.setText(Integer.toString(model.getQuantity()));

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, model.getRefillAt());
        final String date = "" + c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);


        et_refill.setText(date);
        et_dosage.setText(Double.toString(model.getDosage()));

        ArrayAdapter<String> medTypeAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, medTypes);
        medTypeDropdown.setAdapter(medTypeAdapter);
        medTypeDropdown.setSelection(medTypes.indexOf(model.getType()), true);
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
        measurementDropdown.setAdapter(measurementAdapter);
        measurementDropdown.setSelection(measurements.indexOf(model.getMeasurement()), true);
        measurementDropdown.setOnItemSelectedListener((new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMeasurement = measurements.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }));

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String medicationName =  et_name.getText().toString();
                    if(!MedicationModel.validateMedicationName(medicationName)) {
                        throw new Exception("Invalid medication name");
                    }
                    int quantity = Integer.parseInt(et_quantity.getText().toString());


                    model.setName(medicationName);
                    model.setQuantity(quantity);
                    model.setMeasurement(selectedMeasurement);
                    model.setType(selectedType);

                    databaseHelper.updateMedication(model);
                    if(quantity != originalQuantity) {
                        databaseHelper.updateDaysUntilEmpty(model);

                        Toast.makeText(UpdateMedActivity.this, Integer.toString(model.getRefillAt()), Toast.LENGTH_SHORT).show();
                        model.setRefillAt(databaseHelper.daysUntilEmpty(model));
                        Toast.makeText(UpdateMedActivity.this, Integer.toString(model.getRefillAt()), Toast.LENGTH_SHORT).show();

                        GoogleCalendarHelper gch = new GoogleCalendarHelper(UpdateMedActivity.this);
                        gch.updateRefillEvents(model);
                    }

                    Toast.makeText(UpdateMedActivity.this, "Successfully updated medication", Toast.LENGTH_SHORT).show();
                    finish();

                }
                catch(Exception e){
                    Toast.makeText(UpdateMedActivity.this, e.toString(), Toast.LENGTH_SHORT);
                }

            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                GoogleCalendarHelper gac = new GoogleCalendarHelper(UpdateMedActivity.this);
                gac.deleteMedEvents(model);
                databaseHelper.deleteMedication(model);
                Toast.makeText(UpdateMedActivity.this, "Successfully deleted medication", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btn_cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UpdateMedActivity.this, "Adding reminder events to Google Calendar", Toast.LENGTH_SHORT).show();
                GoogleCalendarHelper gac = new GoogleCalendarHelper(UpdateMedActivity.this);
                try {
                    gac.addMedReminder(model);
                    gac.addRefillEvents(model);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btn_dose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UpdateMedActivity.this, EditMedDosesActivity.class);
                i.putExtra("medID", model.getMedicationId());
                startActivityForResult(i, SECOND_ACTIVITY_REQUEST_CODE);
            }
        });


    }

    private void cancelNotification(MedicationModel model){
        List<DoseModel> doseModels = databaseHelper.selectDoseFromMedication(model);
        for(DoseModel doseModel : doseModels){

        }
    }

}
