package com.example.medapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class TakeReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "test", Toast.LENGTH_SHORT).show();
        /*
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        int doseID = intent.getIntExtra("doseID", 0);
        int medID = intent.getIntExtra("medID", 0);
        DoseModel doseModel = databaseHelper.selectDoseFromID(doseID);
        MedicationModel medModel = databaseHelper.selectMedicationFromID(medID);

        //databaseHelper.takeMedication(doseModel, medModel);
        */
    }
}
