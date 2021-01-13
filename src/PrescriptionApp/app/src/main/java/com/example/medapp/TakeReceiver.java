package com.example.medapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TakeReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        int medID = intent.getIntExtra("medID", 0);
        int doseID = intent.getIntExtra("doseID", 0);
        MedicationModel medicationModel = databaseHelper.selectMedicationFromID(medID);
        DoseModel doseModel = databaseHelper.selectDoseFromID(doseID);


    }
}
