package com.example.medapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class UpdateMedActivity extends AppCompatActivity {

    Button btn_update, btn_delete, btn_cal, btn_dose, btn_refill;
    TextInputEditText et_name, et_quantity, et_refill, et_dosage;
    AutoCompleteTextView dropdown_measurement, dropdown_type;
    MedicationModel model;
    SwitchMaterial autoTake;

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
            //int refill = data.getIntExtra("refill", 0);
            //displayRefillDate(refill);
            String msg = "Updated the doses of " + model.getName();
            Toast.makeText(UpdateMedActivity.this, msg, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_med);

        btn_update = findViewById(R.id.btn_update);
        btn_delete = findViewById(R.id.btn_del);
        //btn_cal = findViewById(R.id.btn_cal);
        btn_dose = findViewById(R.id.btn_dose);
        btn_refill = findViewById(R.id.btn_refill);

        et_name = findViewById(R.id.et_name);
        et_quantity = findViewById(R.id.et_quantity);
        et_refill = findViewById(R.id.et_refill);
        et_dosage = findViewById(R.id.et_strength);

        dropdown_measurement = findViewById(R.id.dropdown_measurement);
        dropdown_type = findViewById(R.id.dropdown_type);

        autoTake = findViewById(R.id.autotake);

        // get the medication via ID passed by intent
        int id = getIntent().getIntExtra("medID", 0);
        model = databaseHelper.selectMedicationFromID(id);
        originalQuantity = model.getQuantity();

        // set the values of the texts
        et_name.setText(model.getName());
        et_quantity.setText(String.format(Locale.UK, "%d", model.getQuantity()));
        displayRefillDate(model.getDaysUntilEmpty());
        et_dosage.setText(String.format(Locale.UK, "%f", model.getDosage()));
        autoTake.setChecked(model.isAutoTake());

        ArrayAdapter<String> measurementAdapter =
                new ArrayAdapter<>(UpdateMedActivity.this, R.layout.list_item, measurements);
        dropdown_measurement.setAdapter(measurementAdapter);

        ArrayAdapter<String> typeAdapter =
                new ArrayAdapter<String>(UpdateMedActivity.this, R.layout.list_item, medTypes);
        dropdown_type.setAdapter(typeAdapter);

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String medicationName =  et_name.getText().toString();
                    if(!MedicationModel.validateMedicationName(medicationName)) {
                        throw new Exception("Invalid medication name");
                    }
                    int quantity = Integer.parseInt(et_quantity.getText().toString());


                    String selectedMeasurement = dropdown_measurement.getText().toString();
                    String selectedType = dropdown_type.getText().toString();

                    model.setName(medicationName);
                    model.setQuantity(quantity);
                    model.setMeasurement(selectedMeasurement);
                    model.setType(selectedType);

                    Toast.makeText(UpdateMedActivity.this, Boolean.toString(autoTake.isChecked()), Toast.LENGTH_SHORT).show();
                    model.setAutoTake(autoTake.isChecked());

                    databaseHelper.updateMedication(model);
                    if(quantity != originalQuantity) {
                        databaseHelper.updateDaysUntilEmpty(model);
                        model = databaseHelper.selectMedicationFromID(model.getMedicationId());
                        GoogleCalendarHelper gch = new GoogleCalendarHelper(UpdateMedActivity.this);
                        gch.updateMedEvents(model);
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
                try {
                    List<DoseModel> doses = databaseHelper.selectDoseFromMedication(model);
                    gac.deleteMedEvents(model, doses);
                    databaseHelper.deleteMedication(model);
                    Toast.makeText(UpdateMedActivity.this, "Successfully deleted medication", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        /*
        btn_cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UpdateMedActivity.this, "Adding reminder events to Google Calendar", Toast.LENGTH_SHORT).show();
                GoogleCalendarHelper gac = new GoogleCalendarHelper(UpdateMedActivity.this);
                model = databaseHelper.selectMedicationFromID(model.getMedicationId());
                try {
                    gac.addDoseReminder(model);
                    gac.addRefillEvents(model);
                    gac.updateMedEvents(model);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                model = databaseHelper.selectMedicationFromID(model.getMedicationId());
                finish();
            }
        });
        */

        btn_dose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UpdateMedActivity.this, EditMedDosesActivity.class);
                i.putExtra("medID", model.getMedicationId());
                startActivityForResult(i, SECOND_ACTIVITY_REQUEST_CODE);
            }
        });


    }

    private void displayRefillDate(int daysUntilRefill) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, daysUntilRefill);
        final String date = "" + c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
        et_refill.setText(date);
    }


    private void cancelNotification(MedicationModel model){
        List<DoseModel> doseModels = databaseHelper.selectDoseFromMedication(model);
        for(DoseModel doseModel : doseModels){

        }
    }

}
