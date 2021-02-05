package com.example.medapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Class that represents the receiver that is called when the user presses the take action button
 * on the reminder notification to take their medication dose
 */
public class TakeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        int doseID = intent.getIntExtra("doseID", 0);
        int medID = intent.getIntExtra("medID", 0);
        DoseModel doseModel = databaseHelper.selectDoseFromID(doseID);
        MedicationModel medModel = databaseHelper.selectMedicationFromID(medID);
        databaseHelper.takeMedication(doseModel, medModel);
        String msg = "You have taken " + doseModel.getAmount() + " of " + medModel.getName();
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
