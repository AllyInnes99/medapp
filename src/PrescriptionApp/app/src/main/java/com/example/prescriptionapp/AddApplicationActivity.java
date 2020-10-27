package com.example.prescriptionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.Calendar;

public class AddApplicationActivity extends AppCompatActivity {

    EditText et_time, et_dosage, et_amount;
    TimePickerDialog timePickerDialog;
    Calendar calendar;
    int currentHour, currentMinutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_application);

        et_time = findViewById(R.id.et_time);

        et_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                currentMinutes = calendar.get(Calendar.MINUTE);


                timePickerDialog = new TimePickerDialog(AddApplicationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
                        et_time.setText(hourOfDay + ":" + minutes);
                    }
                }, 0, 0, false);
            }
        });
    }



}