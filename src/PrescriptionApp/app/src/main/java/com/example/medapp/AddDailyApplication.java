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
import java.util.Calendar;
import java.util.List;

public class AddDailyApplication extends AppCompatActivity {

    Button btn_time, btn_add;
    EditText et_time, et_dosage, et_amount;
    int currentHour, currentMinutes;
    MedicationModel medModel;
    DatabaseHelper databaseHelper;
    MaterialCheckBox monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    List<String> temp;

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

        monday = findViewById(R.id.monday);
        tuesday = findViewById(R.id.tuesday);
        wednesday = findViewById(R.id.wednesday);
        thursday = findViewById(R.id.thursday);
        friday = findViewById(R.id.friday);
        saturday = findViewById(R.id.saturday);
        sunday = findViewById(R.id.sunday);

        temp = new ArrayList<>();

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

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DoseModel doseModel;
                AddDoseModel addDoseModel;
                temp.clear();

                try {
                    int medID = medModel.getMedicationId();
                    String time = et_time.getText().toString();
                    int amount = Integer.parseInt(et_amount.getText().toString());
                    doseModel = new DoseModel(medID, time, "Monday", amount, false);

                    addDoseModel = new AddDoseModel(time, amount);
                    checkSelectedBoxes();
                    addDoseModel.setDays(temp);

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

    private void checkSelectedBoxes() {
        checkToList(monday);
        checkToList(tuesday);
        checkToList(wednesday);
        checkToList(thursday);
        checkToList(friday);
        checkToList(saturday);
        checkToList(sunday);
    }


    private void checkToList(MaterialCheckBox box) {
        if(box.isChecked()) {
            temp.add(box.getTag().toString());
        }
    }

}
