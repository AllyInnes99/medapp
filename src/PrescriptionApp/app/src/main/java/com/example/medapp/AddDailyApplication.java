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

import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AddDailyApplication extends AppCompatActivity {

    Button btn_time, btn_add;
    EditText et_time, et_dosage, et_amount;
    int currentHour, currentMinutes;
    MedicationModel medModel;
    DatabaseHelper databaseHelper;
    MaterialCheckBox monday, tuesday, wednesday, thursday, friday, saturday, sunday, select_all;
    List<MaterialCheckBox> checkBoxes;
    List<String> daysToBeTakenOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dose);

        medModel = (MedicationModel) getIntent().getSerializableExtra("MedModel");
        et_time = findViewById(R.id.et_time);
        et_dosage = findViewById(R.id.et_dosage);
        et_amount = findViewById(R.id.et_amount);
        btn_time = findViewById(R.id.btn_time);
        btn_add = findViewById(R.id.btnAdd);

        select_all = findViewById(R.id.select_all);
        monday = findViewById(R.id.monday);
        tuesday = findViewById(R.id.tuesday);
        wednesday = findViewById(R.id.wednesday);
        thursday = findViewById(R.id.thursday);
        friday = findViewById(R.id.friday);
        saturday = findViewById(R.id.saturday);
        sunday = findViewById(R.id.sunday);


        daysToBeTakenOn = new ArrayList<>();
        checkBoxes = Arrays.asList(monday, tuesday, wednesday, thursday,
                                    friday, saturday, sunday);

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
                        String s = padString(hourOfDay) + ":" + padString(minute);
                        et_time.setText(s);
                    }
                }, currentHour, currentMinutes, false);
                timePickerDialog.show();
            }
        });


        // When the select all box is checked, check all of the other checkboxes
        select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = select_all.isChecked();
                for(MaterialCheckBox checkBox: checkBoxes) {
                    checkBox.setChecked(checked);
                }
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DoseModel doseModel;
                AddDoseModel addDoseModel;
                daysToBeTakenOn.clear();

                try {
                    int medID = medModel.getMedicationId();
                    String time = et_time.getText().toString();
                    int amount = Integer.parseInt(et_amount.getText().toString());
                    doseModel = new DoseModel(medID, time, "Monday", amount, false);

                    addDoseModel = new AddDoseModel(time, amount);
                    checkSelectedBoxes();
                    addDoseModel.setDays(daysToBeTakenOn);

                    Intent output = new Intent();
                    output.putExtra("applModel", doseModel);
                    output.putExtra("model", addDoseModel);
                    setResult(RESULT_OK, output);
                    finish();

                }
                catch (Exception e) {
                    Toast.makeText(AddDailyApplication.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String padString(int target) {
        String output = Integer.toString(target);
        if(target < 10){
            return "0" + output;
        }
        return output;
    }

    /**
     * Helper method that checks which of the day checkboxes are selected, and adds their String
     * representation to the list maintaining which days the dose is to be taken on
     */
    private void checkSelectedBoxes() {
        for(MaterialCheckBox checkBox: checkBoxes) {
            if(checkBox.isChecked()) {
                daysToBeTakenOn.add(checkBox.getTag().toString());
            }
        }
    }

}
