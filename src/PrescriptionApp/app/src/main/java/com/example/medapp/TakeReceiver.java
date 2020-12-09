package com.example.medapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TakeReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        DoseModel doseModel = (DoseModel)intent.getSerializableExtra("dose");
        MedicationModel medModel = (MedicationModel) intent.getSerializableExtra("dose");
        databaseHelper.takeMedication(doseModel, medModel);


    }
}
