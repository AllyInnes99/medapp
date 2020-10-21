package com.example.prescriptionapp;

import java.util.ArrayList;
import java.util.List;

public class MedicationModel {

    private int medicationId;
    private String name;
    private int quantity;
    private boolean isTaken;
    private int refillAt;
    private String type;
    private String dayFrequency;
    private String measurement;
    private List<ApplicationModel> applications;
    private String profile;

    /**
     * Initialise a MedicationModel object
     * @param id - unique identifier of medication
     * @param name - the name of the medication
     * @param quantity - how much medication has been taken
     * @param isTaken - if the medication has been taken or not
     */
    public MedicationModel(int id, String name, int quantity, boolean isTaken) {
        this.medicationId = id;
        this.name = name;
        this.quantity = quantity;
        this.isTaken = isTaken;
    }



    /**
     * Initialise a MedicationModel object
     * @param medicationId - unique identifier of medication
     * @param name - the name of the medication
     * @param quantity - how much medication has been taken
     * @param isTaken - if the medication has been taken or no
     * @param refillAt - value for when quantity hits for a refill reminder
     * @param type - if the medication is a tablet, pill, etc.
     * @param dayFrequency - if the medication is to be daily, weekly or custom
     * @param measurement - what the medication is measured in
     * @param profile - profile for who the medication is for
     */
    public MedicationModel(int medicationId, String name, int quantity, boolean isTaken,
                           int refillAt, String type, String dayFrequency, String measurement,
                           String profile) {
        this.medicationId = medicationId;
        this.name = name;
        this.quantity = quantity;
        this.isTaken = isTaken;
        this.refillAt = refillAt;
        this.type = type;
        this.dayFrequency = dayFrequency;
        this.measurement = measurement;
        this.applications = new ArrayList<>();
        this.profile = profile;
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

    /**
     * @return if the medication has been taken or not
     */
    public boolean isTaken() {
        return isTaken;
    }

    /**
     * @param taken set whether or not the medication has been taken
     */
    public void setTaken(boolean taken) {
        isTaken = taken;
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

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public List<ApplicationModel> getApplications() {
        return applications;
    }

    public void setApplications(List<ApplicationModel> applications) {
        this.applications = applications;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
