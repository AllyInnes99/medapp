package com.example.prescriptionapp;

public class ApplicationModel {

    private int applicationId;
    private String time;
    private double dosage;
    private String day;
    private int amount;
    private boolean isTaken;

    /**
     * Initialise an ApplicationModel instance
     * @param applicationId - unique identifier of instance
     * @param time - time for application to be taken
     * @param dosage - the dosage of the application to be taken
     * @param day - the day of when this application is to be takne
     * @param amount - the amount of medicine to be taken in this applicaition
     * @param isTaken - if this application has been taken or not
     */
    public ApplicationModel(int applicationId, String time, double dosage, String day, int amount, boolean isTaken) {
        this.applicationId = applicationId;
        this.time = time;
        this.dosage = dosage;
        this.day = day;
        this.amount = amount;
        this.isTaken = isTaken;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getDosage() {
        return dosage;
    }

    public void setDosage(double dosage) {
        this.dosage = dosage;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isTaken() {
        return isTaken;
    }

    public void setTaken(boolean taken) {
        isTaken = taken;
    }
}
