package com.example.prescriptionapp;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ApplicationModel applModel = (ApplicationModel)intent.getSerializableExtra("MyModel");
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        MedicationModel medModel = databaseHelper.selectMedicationFromApplication(applModel);

        String title = "Medication Reminder";
        String msg = "Please take " + applModel.getAmount() + " of " + medModel.getName();


        Notification notification = new NotificationCompat.Builder(context, App.MED_TAKING_CHANNEL)
                .setSmallIcon(R.drawable.ic_healing)
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER).build();
    }
}
