package com.example.medapp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

/**
 * Class that represents the receiver that is called when the user presses the take action button
 * on the reminder notification to take their medication dose
 */
public class TakeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        int doseID = intent.getIntExtra("doseID", 0);
        int medID = intent.getIntExtra("medID", 0);
        DoseModel doseModel = databaseHelper.selectDoseFromID(doseID);
        MedicationModel medModel = databaseHelper.selectMedicationFromID(medID);
        databaseHelper.takeMedication(doseModel, medModel, true);
        String msg = "You have taken " + doseModel.getAmount() + " of " + medModel.getName();
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        notificationManager.cancel(doseModel.getDoseId());
    }
}
