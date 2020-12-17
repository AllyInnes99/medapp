package com.example.medapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

/**
 * Class that extends Broadcast receiver, to automatically take medication
 */
public class DailyEventReceiver extends BroadcastReceiver {

    DatabaseHelper databaseHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        databaseHelper = new DatabaseHelper(context);
        autoTakeMedication(context);

    }

    private void autoTakeMedication(Context context) {
        Toast.makeText(context, "AutoTake activated", Toast.LENGTH_SHORT).show();
        // Obtain what day it was yesterday
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        int cDay = c.get(Calendar.DAY_OF_WEEK);

        // Get every medication that is set to be auto-taken
        List<MedicationModel> medModels = databaseHelper.selectAutoTakenMeds();

        for(MedicationModel m: medModels) {

            // Get the doses for the med
            Toast.makeText(context, Integer.toString(m.getMedicationId()), Toast.LENGTH_SHORT).show();
            List<DoseModel> doseModels = databaseHelper.selectDoseFromMedication(m);

            for(DoseModel d: doseModels) {
                String day = d.getDay();
                if(App.days.indexOf(day) == cDay && !d.isTaken()){
                    databaseHelper.takeMedication(d, m);
                }
            }
        }
    }

}
