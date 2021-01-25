package com.example.medapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

/**
 * Class that extends Broadcast receiver, events that are to occur at the "end of day"
 * within the app
 */
public class DailyEventReceiver extends BroadcastReceiver {

    DatabaseHelper databaseHelper;
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        databaseHelper = new DatabaseHelper(context);
        mContext = context;
        setNotificationsForToday();
        autoTakeMedication();
        resetDailyMed();
    }

    private void setNotificationsForToday() {
        List<DoseModel> doses = databaseHelper.selectTodaysDoseAndNotTaken();
        for(DoseModel dose: doses) {
            MedicationModel m = databaseHelper.selectMedicationFromDose(dose);
            if(!m.isAutoTake()){
                createNotification(m, dose);
            }
        }
    }

    private void createNotification(MedicationModel medModel, DoseModel doseModel) {


        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());

        String[] time = doseModel.timeToHourAndMin();
        int hour = Integer.parseInt(time[0]);
        int mins = Integer.parseInt(time[1]);

        // Set calendar to represent the day
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, mins);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, AlertReceiver.class);
        intent.setAction("android.intent.action.NOTIFY");

        intent.putExtra("quantity", doseModel.getAmount());
        intent.putExtra("name", medModel.getName());

        intent.putExtra("medID", medModel.getMedicationId());
        intent.putExtra("doseID", doseModel.getDoseId());

        // Register receiver
        mContext.getApplicationContext().registerReceiver(new AlertReceiver(), new IntentFilter());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, doseModel.getDoseId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    /**
     * Method that performs the automatic taking of medication for the medications that have
     * been selected for this for the previous day
     */
    private void autoTakeMedication() {
        // Obtain what day it was yesterday
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        int cDay = c.get(Calendar.DAY_OF_WEEK);

        // Get every medication that is set to be auto-taken
        List<MedicationModel> medModels = databaseHelper.selectAutoTakenMeds();

        for(MedicationModel m: medModels) {

            // Get the doses for the med
            List<DoseModel> doseModels = databaseHelper.selectDoseFromMedication(m);

            for(DoseModel d: doseModels) {
                String day = d.getDay();
                if((App.days.indexOf(day) == cDay || day.equals("Daily"))&& !d.isTaken()){
                    databaseHelper.takeMedication(d, m);
                }
            }
        }
    }

    /**
     * Method that decreases the daysUntilEmpty field for each medication by one.
     */
    private void decrementDaysUntilEmpty(){
        List<MedicationModel> medModels = databaseHelper.selectAllMedication();
        for(MedicationModel med: medModels) {
            int dec = med.getDaysUntilEmpty() - 1;
            med.setDaysUntilEmpty(dec);
            databaseHelper.updateMedication(med);
        }
    }


    private void resetDailyMed() {
        databaseHelper.refreshDailyDoses();
    }

}
