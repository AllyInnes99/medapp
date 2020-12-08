package com.example.medapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class AddDailyApplication extends AppCompatActivity {

    Button btn_time, btn_add;
    EditText et_time, et_dosage, et_amount;
    int currentHour, currentMinutes;
    MedicationModel medModel;
    DatabaseHelper databaseHelper;

    String[] hours = new String[] {
        "00", "01", "02", "03", "04", "05", "06", "07", "08", "09",
        "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
        "20", "21", "22", "23"
    };

    String[] minutes = new String[] {
        "00", "05", "10", "15", "20", "25",
        "30", "35", "40", "45", "50", "55"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_application);

        medModel = (MedicationModel) getIntent().getSerializableExtra("MedModel");
        et_time = findViewById(R.id.et_time);
        et_dosage = findViewById(R.id.et_dosage);
        et_amount = findViewById(R.id.et_amount);
        btn_time = findViewById(R.id.btn_time);
        btn_add = findViewById(R.id.btnAdd);
        databaseHelper = new DatabaseHelper(AddDailyApplication.this);

        btn_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get current time
                final Calendar calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                currentMinutes = calendar.get(Calendar.MINUTE);

                // launch time picker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddDailyApplication.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        et_time.setText(hourOfDay + ":" + minute);
                    }
                }, currentHour, currentMinutes, false);
                timePickerDialog.show();
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DoseModel applModel;
                String [] days = {"Monday", "Tuesday", "Wednesday", "Thursday",
                                    "Friday", "Saturday", "Sunday"};

                try {
                    int medID = medModel.getMedicationId();
                    String time = et_time.getText().toString();
                    int amount = Integer.parseInt(et_amount.getText().toString());

                    // add the medication model created previously
                    //databaseHelper.addMedication(medModel);

                    applModel = new DoseModel(0, medID, time, "Monday", amount, false);


                    /*
                    for(String day: days){
                        applModel = new DoseModel(0, medID, time, day, amount, false);
                        boolean success = databaseHelper.addDose(applModel);
                        if (!success) {
                            throw new Exception("Failed to add new application.");
                        }
                    }
                    */
                    // Once all applications added, return to the prev activity
                    Intent output = new Intent();
                    output.putExtra("applModel", applModel);
                    setResult(RESULT_OK, output);
                    finish();

                }
                catch (Exception e) {
                    Toast.makeText(AddDailyApplication.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
