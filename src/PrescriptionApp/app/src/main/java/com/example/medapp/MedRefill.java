package com.example.medapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class MedRefill extends AppCompatActivity {


    Button btn_add, btn_remove, btn_request;
    TextInputEditText et_current, et_new;
    Context context;
    DatabaseHelper databaseHelper;
    MedicationModel medModel;
    int prevQty, inputVal;
    GoogleCalendarHelper gch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_refill);
        context = MedRefill.this;
        databaseHelper = new DatabaseHelper(context);
        gch = new GoogleCalendarHelper(context);

        btn_add = findViewById(R.id.btn_add);
        btn_remove = findViewById(R.id.btn_remove);
        et_current = findViewById(R.id.et_current);
        et_new = findViewById(R.id.et_new);

        int medID = getIntent().getIntExtra("medID", 0);
        medModel = databaseHelper.selectMedicationFromID(medID);
        prevQty = medModel.getQuantity();

        et_current.setText(Integer.toString(prevQty));

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    inputVal = Integer.parseInt(et_new.getText().toString());
                    int newQuantity = prevQty + inputVal;
                    medModel.setQuantity(newQuantity);
                    medModel.setRefillRequested(false);
                    databaseHelper.updateMedication(medModel);
                    databaseHelper.updateDaysUntilEmpty(medModel);
                    Toast.makeText(context, "Updated quantity of medication", Toast.LENGTH_SHORT).show();
                    closeActivity();
                }
                catch (NullPointerException e) {
                    Toast.makeText(context, "Input value is empty.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    inputVal = Integer.parseInt(et_new.getText().toString());
                    int newQuantity = prevQty - inputVal;
                    if(newQuantity < 0){
                        newQuantity = 0;
                    }

                    medModel.setQuantity(newQuantity);
                    databaseHelper.updateMedication(medModel);
                    databaseHelper.updateDaysUntilEmpty(medModel);
                    Toast.makeText(context, "Updated quantity of medication", Toast.LENGTH_SHORT).show();
                    closeActivity();
                }
                catch (NullPointerException e) {
                    Toast.makeText(context, "Input value is empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medModel.setRefillRequested(true);
                databaseHelper.updateMedication(medModel);
                closeActivity();
            }
        });
    }

    private void updateGoogleCal() {
        medModel = databaseHelper.selectMedicationFromID(medModel.getMedicationId());
        gch.updateMedEvents(medModel);
    }


    private void closeActivity() {
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }


}