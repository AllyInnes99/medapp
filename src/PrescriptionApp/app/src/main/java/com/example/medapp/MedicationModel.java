package com.example.medapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MedicationModel implements Serializable {

    private int medicationId;
    private String name;
    private int quantity;
    private int refillAt;
    private String type;
    private String dayFrequency;
    private double dosage;
    private String measurement;
    private String profile;
    private boolean autoTake;
    private static int id = 0;

    public MedicationModel(String name, int quantity, int refillAt, String type, String dayFrequency,
                           double dosage, String measurement, String profile, boolean autoTake) {
        this.medicationId = ++id;
        this.name = name;
        this.quantity = quantity;
        this.refillAt = refillAt;
        this.type = type;
        this.dayFrequency = dayFrequency;
        this.dosage = dosage;
        this.measurement = measurement;
        this.autoTake = autoTake;
        this.profile = profile;
    }

    public MedicationModel(String name, int quantity, int refillAt, String type, String dayFrequency,
                           double dosage, String measurement, String profile) {
        this.medicationId = ++id;
        this.name = name;
        this.quantity = quantity;
        this.refillAt = refillAt;
        this.type = type;
        this.dayFrequency = dayFrequency;
        this.dosage = dosage;
        this.measurement = measurement;
        this.autoTake = false;
        this.profile = profile;
    }

    public MedicationModel(int medicationId, String name, int quantity,
                           int refillAt, String type, String dayFrequency,
                           double dosage, String measurement, String profile) {
        this.medicationId = medicationId;
        this.name = name;
        this.quantity = quantity;
        this.refillAt = refillAt;
        this.type = type;
        this.dayFrequency = dayFrequency;
        this.dosage = dosage;
        this.measurement = measurement;
        this.autoTake = false;
        this.profile = profile;
    }

    public MedicationModel(int medicationId, String name, int quantity,
                           int refillAt, String type, String dayFrequency,
                           double dosage, String measurement, String profile, boolean autoTake) {
        this.medicationId = medicationId;
        this.name = name;
        this.quantity = quantity;
        this.refillAt = refillAt;
        this.type = type;
        this.dayFrequency = dayFrequency;
        this.dosage = dosage;
        this.measurement = measurement;
        this.autoTake = autoTake;
        this.profile = profile;
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

    public int getRefillAt() {
        return refillAt;
    }

    public void setRefillAt(int refillAt) {
        this.refillAt = refillAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDayFrequency() {
        return dayFrequency;
    }

    public void setDayFrequency(String dayFrequency) {
        this.dayFrequency = dayFrequency;
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
}
