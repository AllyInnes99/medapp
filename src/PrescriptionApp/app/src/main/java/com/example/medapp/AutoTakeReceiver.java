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
public class AutoTakeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        Toast.makeText(context, "AutoTake activated", Toast.LENGTH_SHORT).show();

        // Firstly get the day of prev. day
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        int cDay = c.get(Calendar.DAY_OF_WEEK);

        // Get every medication that is set to be auto-taken
        List<MedicationModel> medModels = databaseHelper.selectAutoTakenMeds();

        for(MedicationModel m: medModels) {

            // Get the doses for the med
            List<DoseModel> doseModels = databaseHelper.selectDoseFromMedication(m);
            for(DoseModel d: doseModels) {

                // if the dose is to be taken on target day, take the med
                String day = d.getDay();
                if(App.days.indexOf(day) == cDay){
                    databaseHelper.takeMedication(d, m);
                }

            }
        }

    }
}
