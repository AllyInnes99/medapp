package com.example.medapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.lang.reflect.Array;
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
    boolean originalAutoTake;
    private static final int DOSE_ACTIVITY_REQUEST_CODE = 42;
    final List<String> medTypes = Arrays.asList("pill(s)", "sachet(s)", "ml(s)", "scoop(s)", "drop(s)");
    final List<String> measurements = Arrays.asList("g", "mg", "ml", "l");

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DOSE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
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
        originalAutoTake = medModel.isAutoTake();

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
        dropdown_measurement.setSelection(measurements.indexOf(medModel.getMeasurement()));

        ArrayAdapter<String> typeAdapter =
                new ArrayAdapter<String>(UpdateMedActivity.this, R.layout.list_item, medTypes);
        dropdown_type.setAdapter(typeAdapter);
        dropdown_type.setSelection(medTypes.indexOf(medModel.getType()));

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMed();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMed();
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

    private void deleteMed() {
        List<DoseModel> doses = databaseHelper.selectDoseFromMedication(medModel);
        if (GoogleSignIn.getLastSignedInAccount(UpdateMedActivity.this) != null) {
            GoogleCalendarHelper gac = new GoogleCalendarHelper(UpdateMedActivity.this);
            gac.deleteMedEvents(medModel, doses);
        }
        cancelNotifications();
        databaseHelper.deleteMedication(medModel);
        Toast.makeText(UpdateMedActivity.this, "Successfully deleted medication", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void updateMed() {
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

            if(medModel.isAutoTake() != originalAutoTake) {
                // if user has changes autotake, cancel notifications
                if(!medModel.isAutoTake()) {
                    cancelNotifications();
                    List<DoseModel> doses = databaseHelper.selectDoseFromMedication(medModel);
                    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(UpdateMedActivity.this);


                    if(acct != null) {
                        GoogleCalendarHelper gch = new GoogleCalendarHelper(UpdateMedActivity.this);
                        gch.addDoseReminder(medModel);
                    }

                }
                // otherwise, add notifications
                else if (medModel.isAutoTake()) {
                    List<DoseModel> doses = databaseHelper.selectDoseFromMedication(medModel);
                    for(DoseModel dose: doses) {
                        initialiseNotification(dose);
                        if(GoogleSignIn.getLastSignedInAccount(UpdateMedActivity.this) != null) {
                            GoogleCalendarHelper gch = new GoogleCalendarHelper(UpdateMedActivity.this);
                            gch.deleteDoseEvent(dose);

                        }
                    }
                }
            }

            Toast.makeText(UpdateMedActivity.this, "Successfully updated medication", Toast.LENGTH_SHORT).show();
            finish();

        } catch (Exception e) {
            Toast.makeText(UpdateMedActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void displayRefillDate(int daysUntilRefill) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, daysUntilRefill);
        final String date = "" + c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
        et_refill.setText(date);
    }

    /**
     * Helper method that cancels all of the pending notifications for a medicine
     */
    private void cancelNotifications() {
        List<DoseModel> doseModels = databaseHelper.selectDoseFromMedication(medModel);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(UpdateMedActivity.this);
        for (DoseModel doseModel : doseModels) {
            notificationManager.cancel(doseModel.getDoseId());
        }
    }

    /**
     * Method that creates notifications for the doses of a medication
     */
    private void initialiseNotification(DoseModel doseModel){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());

        String[] time = doseModel.timeToHourAndMin();
        int hour = Integer.parseInt(time[0]);
        int mins = Integer.parseInt(time[1]);

        // Set calendar to represent the day
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, mins);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        if(Calendar.getInstance().getTimeInMillis() < c.getTimeInMillis()){
            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlertReceiver.class);
            intent.setAction("android.intent.action.NOTIFY");
            intent.putExtra("medID", medModel.getMedicationId());
            intent.putExtra("doseID", doseModel.getDoseId());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(UpdateMedActivity.this, doseModel.getDoseId() + 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }
}
