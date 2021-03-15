package com.example.medapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that represents a medication that a user is to take
 */
public class MedicationModel implements Serializable {

    private int medicationId;
    private String name;
    private int quantity;
    private int daysUntilEmpty;
    private String type;
    private double dosage;
    private String measurement;
    private String profile;
    private boolean autoTake;
    private boolean refillRequested;
    private String calendarRefill;
    private String calendarEmpty;

    /**
     * Constructor that is used when first creating a medication
     *
     * @param name        String for the name of the medication
     * @param quantity    int for the current quantity of the medication
     * @param type        string for the type of the medication, e.g. it is a tablet
     * @param dosage      double for the strength of a medication
     * @param measurement string for what the medication is measured in, e.g. grams, milliliters
     * @param autoTake    boolean for if the user has selected this medication to be taken automatically
     */
    public MedicationModel(String name, int quantity, String type, double dosage, String measurement, boolean autoTake) {
        this.medicationId = (int) System.currentTimeMillis();
        this.name = name;
        this.quantity = quantity;
        this.daysUntilEmpty = 0;
        this.type = type;
        this.dosage = dosage;
        this.measurement = measurement;
        this.autoTake = autoTake;
        this.refillRequested = false;
        this.profile = null;
        this.calendarRefill = null;
        this.calendarEmpty = null;
    }

    /**
     * Constructor that creates medication object when retrieving from database
     *
     * @param medicationId    int for the unique ID that identifies the medication in the db
     * @param name            String for the name of the medication
     * @param quantity        int for the current quantity of the medication
     * @param daysUntilEmpty  int for the no. of days until the medication will run out of supply
     * @param type            String for the type of the medication, e.g. it is a tablet
     * @param dosage          double for the strength of a medication
     * @param measurement     String for measurement what the medication is measured in, e.g. grams, milliliters
     * @param profile         String for who the medication is for
     * @param autoTake        boolean for if the user has selected this medication to be taken automatically
     * @param refillRequested boolean for if the user has requested a refill for the medication
     * @param calendarRefill  String ID of the Google Calendar event for the refill reminder
     * @param calendarEmpty   String ID of the Google Calendar event for when the medication becomes empty
     */
    public MedicationModel(int medicationId, String name, int quantity, int daysUntilEmpty, String type, double dosage,
                           String measurement, String profile, boolean autoTake, boolean refillRequested, String calendarRefill, String calendarEmpty) {
        this.medicationId = medicationId;
        this.name = name;
        this.quantity = quantity;
        this.daysUntilEmpty = daysUntilEmpty;
        this.type = type;
        this.dosage = dosage;
        this.measurement = measurement;
        this.autoTake = autoTake;
        this.refillRequested = refillRequested;
        this.profile = profile;
        this.calendarRefill = calendarRefill;
        this.calendarEmpty = calendarEmpty;
    }

    /**
     * Helper method that validates if the name of the medication is valid
     *
     * @param medicationName - String of the med name
     * @return true if valid, false otherwise
     */
    public static boolean validateMedicationName(String medicationName) {
        return !medicationName.isEmpty();
    }

    /**
     * Function that calcs. the no. of days until a medication runs out of supply
     * @return the no. of days until the med is empty
     */
    public int daysUntilEmpty(DatabaseHelper databaseHelper) {
        Calendar c = Calendar.getInstance();
        if(databaseHelper.countDosesFromMed(this) == 0) {
            return 0;
        }
        List<DoseModel> doses = databaseHelper.selectDoseFromMedication(this);
        Map<String, Integer> takenPerDay = new HashMap<>();
        takenPerDay.put("Monday", 0);
        takenPerDay.put("Tuesday", 0);
        takenPerDay.put("Wednesday", 0);
        takenPerDay.put("Thursday", 0);
        takenPerDay.put("Friday", 0);
        takenPerDay.put("Saturday", 0);
        takenPerDay.put("Sunday", 0);

        for(DoseModel doseModel: doses) {
            String d = doseModel.getDay();
            int count = doseModel.getAmount();

            if(d.equals("Daily")){
                for(String key: takenPerDay.keySet()){
                    int original = takenPerDay.get(key);
                    takenPerDay.put(key, original + count);
                }
            }
            else {
                int original = takenPerDay.get(d);
                takenPerDay.put(d, original + count);
            }
        }

        Map<Integer, Integer> m = new HashMap<>();
        m.put(Calendar.SUNDAY, mapFiller(takenPerDay, "Sunday"));
        m.put(Calendar.MONDAY, mapFiller(takenPerDay, "Monday"));
        m.put(Calendar.TUESDAY, mapFiller(takenPerDay, "Tuesday"));
        m.put(Calendar.WEDNESDAY, mapFiller(takenPerDay, "Wednesday"));
        m.put(Calendar.THURSDAY, mapFiller(takenPerDay, "Thursday"));
        m.put(Calendar.FRIDAY, mapFiller(takenPerDay, "Friday"));
        m.put(Calendar.SATURDAY, mapFiller(takenPerDay, "Saturday"));
        takenPerDay.clear();

        int current = this.getQuantity();
        int dayCount = 0;

        // from today's date, continuously subtract from the current qty for each day and then
        while(current > 0) {
            current -= m.get(c.get(Calendar.DAY_OF_WEEK));
            c.add(Calendar.DATE, 1);
            dayCount++;
        }
        return dayCount;
    }

    public void createRefillNotificiation(Context context) {

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        Intent tapIntent = new Intent(context, MedRefill.class);
        tapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        tapIntent.putExtra("medID", this.getMedicationId());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, tapIntent, 0);


        String medName = this.getName();
        String title = medName + " supply low";
        String msg = String.format("Your supply of %s will run out soon. Please order new prescription.", medName);

        Notification notification = new NotificationCompat.Builder(context, App.REFILL_CHANNEL)
                .setSmallIcon(R.drawable.ic_healing)
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(-this.getMedicationId(), notification);
    }

    private int mapFiller(Map<String, Integer> takenMap, String target){
        if(takenMap.get(target) == null){
            return 0;
        }
        return takenMap.get(target);
    }



    /**
     * @return a String that prints the contents of a medication object
     */
    @Override
    public String toString() {
        return "Name: " + getName();
    }

    // Getters and setters

    /**
     * @return the id of the medication
     */
    public int getMedicationId() {
        return medicationId;
    }

    /**
     * @param medicationId - set the medication id
     */
    public void setMedicationId(int medicationId) {
        this.medicationId = medicationId;
    }

    /**
     * @return the name of the medication
     */
    public String getName() {
        return name;
    }

    /**
     * @param name set the name of the medication
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the current quantity of the medication in stock
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @param quantity set the amount of the medication in stock
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getDaysUntilEmpty() {
        return daysUntilEmpty;
    }

    public void setDaysUntilEmpty(int daysUntilEmpty) {
        this.daysUntilEmpty = daysUntilEmpty;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public double getDosage() {
        return dosage;
    }

    public void setDosage(double dosage) {
        this.dosage = dosage;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public boolean isAutoTake() {
        return autoTake;
    }

    public void setAutoTake(boolean autoTake) {
        this.autoTake = autoTake;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }


    public String getCalendarRefill() {
        return calendarRefill;
    }

    public void setCalendarRefill(String calendarRefill) {
        this.calendarRefill = calendarRefill;
    }

    public String getCalendarEmpty() {
        return calendarEmpty;
    }

    public void setCalendarEmpty(String calendarEmpty) {
        this.calendarEmpty = calendarEmpty;
    }

    public boolean isRefillRequested() {
        return refillRequested;
    }

    public void setRefillRequested(boolean refillRequested) {
        this.refillRequested = refillRequested;
    }

}
