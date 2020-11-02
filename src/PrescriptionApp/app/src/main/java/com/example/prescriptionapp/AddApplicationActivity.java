package com.example.prescriptionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.util.Calendar;

public class AddApplicationActivity extends AppCompatActivity {

    Button btn_time, btn_add;
    EditText et_time, et_dosage, et_amount;
    TimePickerDialog timePickerDialog;
    int currentHour, currentMinutes;
    MedicationModel medModel;
    DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_application);

        medModel = (MedicationModel) getIntent().getSerializableExtra("MedModel");
        et_time = findViewById(R.id.et_time);
        et_dosage = findViewById(R.id.et_dosage);
        et_amount = findViewById(R.id.et_amount);
        btn_time = findViewById(R.id.btn_time);
        databaseHelper = new DatabaseHelper(AddApplicationActivity.this);

        btn_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get current time
                final Calendar calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                currentMinutes = calendar.get(Calendar.MINUTE);

                // launch time picker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddApplicationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        et_time.setText(hourOfDay + ":" + minute);
                    }
                }, currentHour, currentMinutes, true);
                timePickerDialog.show();
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ApplicationModel applModel;
                Intent intent;
                String [] days = {"Monday", "Tuesday", "Wednesday", "Thursday",
                                    "Friday", "Saturday", "Sunday"};

                try {
                    int medID = medModel.getMedicationId();
                    String time = et_time.getText().toString();
                    double dosage = Double.parseDouble(et_dosage.getText().toString());
                    int amount = Integer.parseInt(et_amount.getText().toString());

                    // Add an application model for each day of the week
                    for(String day: days){
                        applModel = new ApplicationModel(0, medID, time, dosage, day, amount, false);
                        boolean success = databaseHelper.addApplication(applModel);
                        if (!success) {
                            throw new Exception("Failed to add new application.");
                        }
                    }

                    // Once all applications added, return to the prev activity
                    finish();

                }
                catch (Exception e) {
                    Toast.makeText(AddApplicationActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }


                finish();
            }
        });


    }



}