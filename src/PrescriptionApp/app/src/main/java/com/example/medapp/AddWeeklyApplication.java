package com.example.medapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class AddWeeklyApplication extends AppCompatActivity {

    EditText et_amount, et_time;
    Button btn_time, btn_add;
    Spinner spinner;
    int currentHour, currentMinutes;
    MedicationModel medModel;
    DatabaseHelper databaseHelper;


    final String[] days = new String[] {"Monday", "Tuesday", "Wednesday",
                                        "Thursday", "Friday", "Saturday", "Sunday"};
    String selectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weekly_application);

        medModel = (MedicationModel) getIntent().getSerializableExtra("MedModel");
        et_time = findViewById(R.id.et_time);
        spinner = findViewById(R.id.spinner);
        et_amount = findViewById(R.id.et_amount);
        btn_time = findViewById(R.id.btn_time);
        btn_add = findViewById(R.id.btnAdd);
        databaseHelper = new DatabaseHelper(AddWeeklyApplication.this);

        btn_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get current time
                final Calendar calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                currentMinutes = calendar.get(Calendar.MINUTE);

                // launch time picker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddWeeklyApplication.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        et_time.setText(hourOfDay + ":" + minute);
                    }
                }, currentHour, currentMinutes, false);
                timePickerDialog.show();
            }
        });

        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, days);
        spinner.setAdapter(dayAdapter);
        spinner.setOnItemSelectedListener((new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDay = days[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }));

        btn_add.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                DoseModel applModel;
                try {
                    int medID = medModel.getMedicationId();
                    String time = et_time.getText().toString();
                    double dosage = 0.1;
                    int amount = Integer.parseInt(et_amount.getText().toString());

                    applModel = new DoseModel(medID, time, selectedDay, amount, false);
                    boolean success = databaseHelper.addDose(applModel);
                    if(!success) throw new Exception("Failed to add new application");

                    finish();

                } catch (Exception e) {
                    Toast.makeText(AddWeeklyApplication.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}