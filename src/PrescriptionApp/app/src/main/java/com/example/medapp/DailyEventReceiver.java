package com.example.medapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import java.util.Calendar;
import java.util.List;

/**
 * Class that extends Broadcast receiver, events that are to occur at the "end of day"
 * within the app
 */
public class DailyEventReceiver extends BroadcastReceiver {

    DatabaseHelper databaseHelper;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.NOTIFY")) {
            //Toast.makeText(context, "MedApp: Syncing medication", Toast.LENGTH_SHORT).show();
            databaseHelper = new DatabaseHelper(context);
            this.context = context;
            autoTakeMedication();
            logMedicationNotTaken();
            databaseHelper.refreshDailyDoses();
            setNotificationsForToday();
            decrementDaysUntilEmpty();
        }
    }

    /**
     * Method that sets all the notifications that are required for a given day
     */
    public void setNotificationsForToday() {
        List<DoseModel> doses = databaseHelper.selectTodaysDoseAndNotTaken();
        for (DoseModel dose : doses) {
            MedicationModel m = databaseHelper.selectMedicationFromDose(dose);
            if (!m.isAutoTake()) {
                createNotification(m, dose);
            }
        }
    }

    /**
     * Method that sets a refill reminder for a given medication
     * @param med medication that needs a refill reminder
     */
    private void setRefillReminder(MedicationModel med) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, 9);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, RefillReminder.class);
        intent.setAction("android.intent.action.NOTIFY");
        intent.putExtra("medId", med.getMedicationId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, med.getMedicationId() + 2, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }


    /**
     * Method that creates a notification for a given dose of medication
     *
     * @param medModel  the medication that is to be taken
     * @param doseModel the dose of the medication
     */
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

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.setAction("android.intent.action.NOTIFY");
        intent.putExtra("medID", medModel.getMedicationId());
        intent.putExtra("doseID", doseModel.getDoseId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, doseModel.getDoseId() + 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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

        for (MedicationModel m : medModels) {

            // Get the doses for the med
            List<DoseModel> doseModels = databaseHelper.selectDoseFromMedication(m);

            for (DoseModel d : doseModels) {
                String day = d.getDay();
                if ((App.days.indexOf(day) == cDay || day.equals("Daily")) && !d.isTaken()) {
                    databaseHelper.takeMedication(d, m);
                }
            }
        }
    }

    /**
     * Method that decreases the daysUntilEmpty field for each medication by one.
     */
    public void decrementDaysUntilEmpty() {
        List<MedicationModel> medModels = databaseHelper.selectAllMedication();

        String i = PreferenceManager.getDefaultSharedPreferences(context).
                getString("reminderDay", "7");
        int refill;
        if(i == null) {
            refill = 7;
        }
        else {
            refill = Integer.parseInt(i);
        }
        for (MedicationModel med : medModels) {
            int dec = med.getDaysUntilEmpty() - 1;
            med.setDaysUntilEmpty(dec);
            if (!med.isRefillRequested() && med.getDaysUntilEmpty() < refill) {
                setRefillReminder(med);
            }
            databaseHelper.updateMedication(med);
        }
    }

    /**
     * Method that adds medication that has not been taken from the previous day to the log
     */
    private void logMedicationNotTaken() {

        List<DoseModel> notTaken = databaseHelper.selectYesterdaysDoseAndNotTaken();

        if (!notTaken.isEmpty()) {
            String msg = "Medication not taken";
            Calendar c = Calendar.getInstance();
            for (DoseModel dose : notTaken) {
                MedicationLog log = new MedicationLog(dose.getMedicationId(), msg, dose.getAmount(),
                        c.getTimeInMillis(), false, false);
                databaseHelper.addLog(log);
            }
        }
    }

}
