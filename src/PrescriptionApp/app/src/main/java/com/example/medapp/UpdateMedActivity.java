package com.example.medapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class UpdateMedActivity extends AppCompatActivity {

    Button btn_update, btn_delete, btn_dose, btn_refill;
    TextInputEditText et_name, et_quantity, et_refill, et_dosage;
    Spinner dropdown_measurement, dropdown_type;
    MedicationModel medModel;
    SwitchMaterial autoTake;

    DatabaseHelper databaseHelper = new DatabaseHelper(UpdateMedActivity.this);
    int originalQuantity;
    private static final int DOSE_ACTIVITY_REQUEST_CODE = 42;
    final List<String> medTypes = Arrays.asList("tablet", "pill", "injection", "powder",
            "drops", "inhalers", "topical");
    final List<String> measurements = Arrays.asList("g", "mg", "ml", "l");

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DOSE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //int refill = data.getIntExtra("refill", 0);
            //displayRefillDate(refill);
            String msg = "Updated the doses of " + medModel.getName();
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
        medModel = databaseHelper.selectMedicationFromID(id);
        originalQuantity = medModel.getQuantity();

        setTitle(String.format("MedApp - Edit %s", medModel.getName()));


        // set the values of the texts
        et_name.setText(medModel.getName());
        et_quantity.setText(String.format(Locale.UK, "%d", medModel.getQuantity()));
        displayRefillDate(medModel.getDaysUntilEmpty());
        et_dosage.setText(Double.toString(medModel.getDosage()));
        autoTake.setChecked(medModel.isAutoTake());

        ArrayAdapter<String> measurementAdapter =
                new ArrayAdapter<>(UpdateMedActivity.this, R.layout.list_item, measurements);
        dropdown_measurement.setAdapter(measurementAdapter);

        ArrayAdapter<String> typeAdapter =
                new ArrayAdapter<String>(UpdateMedActivity.this, R.layout.list_item, medTypes);
        dropdown_type.setAdapter(typeAdapter);

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String medicationName = et_name.getText().toString();
                    if (!MedicationModel.validateMedicationName(medicationName)) {
                        throw new Exception("Invalid medication name");
                    }
                    int quantity = Integer.parseInt(et_quantity.getText().toString());


                    String selectedMeasurement = dropdown_measurement.getSelectedItem().toString();
                    String selectedType = dropdown_type.getSelectedItem().toString();

                    medModel.setName(medicationName);
                    medModel.setQuantity(quantity);
                    medModel.setMeasurement(selectedMeasurement);
                    medModel.setType(selectedType);

                    medModel.setAutoTake(autoTake.isChecked());

                    databaseHelper.updateMedication(medModel);
                    if (quantity != originalQuantity) {
                        databaseHelper.updateDaysUntilEmpty(medModel);
                        medModel = databaseHelper.selectMedicationFromID(medModel.getMedicationId());
                        GoogleCalendarHelper gch = new GoogleCalendarHelper(UpdateMedActivity.this);
                        gch.updateMedEvents(medModel);
                    }

                    Toast.makeText(UpdateMedActivity.this, "Successfully updated medication", Toast.LENGTH_SHORT).show();
                    finish();

                } catch (Exception e) {
                    Toast.makeText(UpdateMedActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    List<DoseModel> doses = databaseHelper.selectDoseFromMedication(medModel);
                    if (GoogleSignIn.getLastSignedInAccount(UpdateMedActivity.this) != null) {
                        GoogleCalendarHelper gac = new GoogleCalendarHelper(UpdateMedActivity.this);
                        gac.deleteMedEvents(medModel, doses);
                    }
                    cancelNotifications();
                    databaseHelper.deleteMedication(medModel);
                    Toast.makeText(UpdateMedActivity.this, "Successfully deleted medication", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        btn_refill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateMedActivity.this, MedRefill.class);
                intent.putExtra("medID", medModel.getMedicationId());
                startActivity(intent);
            }
        });


        btn_dose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UpdateMedActivity.this, EditMedDosesActivity.class);
                i.putExtra("medID", medModel.getMedicationId());
                startActivityForResult(i, DOSE_ACTIVITY_REQUEST_CODE);
            }
        });


    }

    private void displayRefillDate(int daysUntilRefill) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, daysUntilRefill);
        final String date = "" + c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
        et_refill.setText(date);
    }

    /**
     * Helper method that cancels all of the pending notifications for a medicine upon removal
     */
    private void cancelNotifications() {
        List<DoseModel> doseModels = databaseHelper.selectDoseFromMedication(medModel);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(UpdateMedActivity.this);
        for (DoseModel doseModel : doseModels) {
            notificationManager.cancel(doseModel.getDoseId());
        }
    }

}
