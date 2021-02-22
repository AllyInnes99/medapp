package com.example.medapp;

import android.content.Context;
import android.os.Build;
import android.provider.ContactsContract;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
     *
     * @param databaseHelper retrieves data from db when required
     * @return the no. of days until the med is empty
     */
    public int calcDaysUntilEmpty(DatabaseHelper databaseHelper) {
        List<String> dayStrings = new ArrayList<>(App.days);
        dayStrings.remove(0);
        int count = 0;
        Calendar c = Calendar.getInstance();
        int dayVal = c.get(Calendar.DAY_OF_WEEK) - 1;
        Map<Integer, Integer> noToBeTaken = new HashMap<>(dayStrings.size());
        int current = this.getQuantity();
        while (current > 0) {
            int target = dayVal % dayStrings.size();
            if (noToBeTaken.get(target) == null) {
                int total = 0;
                List<DoseModel> doses = databaseHelper.selectDoseFromDay(dayStrings.get(target));
                for (DoseModel dose : doses) {
                    total += dose.getAmount();
                }
                noToBeTaken.put(target, total);
            }
            current -= noToBeTaken.get(target);
            dayVal += 1;
            count += 1;
        }
        return count;
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
