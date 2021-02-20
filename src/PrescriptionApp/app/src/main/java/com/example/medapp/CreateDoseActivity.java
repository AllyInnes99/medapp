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

/**
 * Activity that is called the create a dose of a medication
 */
public class CreateDoseActivity extends AppCompatActivity {

    Button btn_time, btn_add;
    EditText et_time, et_amount;
    int currentHour, currentMinutes;
    MedicationModel medModel;
    DatabaseHelper databaseHelper;
    MaterialCheckBox monday, tuesday, wednesday, thursday, friday, saturday, sunday, select_all;
    List<MaterialCheckBox> checkBoxes;
    List<String> daysToBeTakenOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_dose);

        medModel = (MedicationModel) getIntent().getSerializableExtra("MedModel");
        et_time = findViewById(R.id.et_time);
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

        databaseHelper = new DatabaseHelper(CreateDoseActivity.this);

        btn_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get current time
                final Calendar calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                currentMinutes = calendar.get(Calendar.MINUTE);

                // launch time picker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateDoseActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                for (MaterialCheckBox checkBox : checkBoxes) {
                    checkBox.setChecked(checked);
                }
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddDoseModel addDoseModel;
                daysToBeTakenOn.clear();
                try {
                    String time = et_time.getText().toString();
                    if (time.trim().isEmpty()) {
                        throw new Exception("Please select a time");
                    }
                    String amountStr = et_amount.getText().toString();
                    if (amountStr.trim().isEmpty()) {
                        throw new Exception("Please select the amount of medication to be taken at this time");
                    }
                    int amount = Integer.parseInt(amountStr);
                    addDoseModel = new AddDoseModel(time, amount);
                    checkSelectedBoxes();
                    if (daysToBeTakenOn.isEmpty()) {
                        throw new Exception("Please select the days where this medication is to be taken on");
                    }
                    addDoseModel.setDays(daysToBeTakenOn);
                    Intent output = new Intent();
                    output.putExtra("model", addDoseModel);
                    setResult(RESULT_OK, output);
                    finish();

                } catch (Exception e) {
                    Toast.makeText(CreateDoseActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String padString(int target) {
        String output = Integer.toString(target);
        if (target < 10) {
            return "0" + output;
        }
        return output;
    }

    /**
     * Helper method that checks which of the day checkboxes are selected, and adds their String
     * representation to the list maintaining which days the dose is to be taken on
     */
    private void checkSelectedBoxes() {
        for (MaterialCheckBox checkBox : checkBoxes) {
            if (checkBox.isChecked()) {
                daysToBeTakenOn.add(checkBox.getTag().toString());
            }
            if (daysToBeTakenOn.size() == 7) {
                daysToBeTakenOn.clear();
                daysToBeTakenOn.add("Daily");
            }
        }
    }

}
