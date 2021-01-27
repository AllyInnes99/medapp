package com.example.medapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;

public class MedRefill extends AppCompatActivity {


    Button btn_add, btn_remove, btn_request, btn_update;
    MaterialButtonToggleGroup toggleGroup;
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
        btn_request = findViewById(R.id.btn_request);
        btn_update = findViewById(R.id.btn_update);
        toggleGroup = findViewById(R.id.toggleGroup);
        et_current = findViewById(R.id.et_current);
        et_new = findViewById(R.id.et_new);

        int medID = getIntent().getIntExtra("medID", 0);
        medModel = databaseHelper.selectMedicationFromID(medID);
        prevQty = medModel.getQuantity();

        et_current.setText(Integer.toString(prevQty));

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int btnID = toggleGroup.getCheckedButtonId();
                switch(btnID) {
                    case R.id.btn_add:
                        addToQuantity();
                        break;
                    case R.id.btn_remove:
                        removeFromQuantity();
                        break;
                    default:
                        Toast.makeText(context, "Please select a toggle option", Toast.LENGTH_SHORT).show();

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

    private void addToQuantity() {
        try {
            String v = et_new.getText().toString();
            if(v.equals("")) throw new NullPointerException("Empty input for new quantity.");
            inputVal = Integer.parseInt(v);
            int newQuantity = prevQty + inputVal;
            medModel.setQuantity(newQuantity);

            // Refill has been received, so disable the refill request
            medModel.setRefillRequested(false);
            databaseHelper.updateMedication(medModel);
            databaseHelper.updateDaysUntilEmpty(medModel);
            updateGoogleCal();
            Toast.makeText(context, "Updated quantity of medication", Toast.LENGTH_SHORT).show();
            closeActivity();
        }
        catch (NullPointerException e) {
            Toast.makeText(context, "Input value is empty.", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeFromQuantity() {
        try {
            String v = et_new.getText().toString();
            if(v.equals("")) throw new NullPointerException("Empty input for new quantity.");
            inputVal = Integer.parseInt(v);
            int newQuantity = prevQty - inputVal;
            if(newQuantity < 0){
                newQuantity = 0;
            }
            medModel.setQuantity(newQuantity);
            databaseHelper.updateMedication(medModel);
            databaseHelper.updateDaysUntilEmpty(medModel);
            updateGoogleCal();
            Toast.makeText(context, "Updated quantity of medication", Toast.LENGTH_SHORT).show();
            closeActivity();
        }
        catch (NullPointerException e) {
            Toast.makeText(context, "Input value is empty.", Toast.LENGTH_SHORT).show();
        }
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