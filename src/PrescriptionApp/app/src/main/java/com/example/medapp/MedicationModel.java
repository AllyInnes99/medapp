package com.example.medapp;

import java.io.Serializable;

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


    public MedicationModel(String name, int quantity, int daysUntilEmpty, String type, double dosage,
                           String measurement, String profile, boolean autoTake) {
        this.medicationId = (int) System.currentTimeMillis();
        this.name = name;
        this.quantity = quantity;
        this.daysUntilEmpty = daysUntilEmpty;
        this.type = type;
        this.dosage = dosage;
        this.measurement = measurement;
        this.autoTake = autoTake;
        this.refillRequested = false;
        this.profile = profile;
        this.calendarRefill = null;
        this.calendarEmpty = null;
    }

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
     * @param medicationName - String of the med name
     * @return true if valid, false otherwise
     */
    public static boolean validateMedicationName(String medicationName) {
        return !medicationName.isEmpty();
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
     *
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
