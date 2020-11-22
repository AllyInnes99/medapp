package com.example.prescriptionapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApplicationModel implements Comparable<ApplicationModel>, Serializable {

    private int applicationId;
    private int medicationId;
    private String time;
    private double dosage;
    private String day;
    private int amount;
    private boolean isTaken;
    private static String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday",
                                    "Thursday", "Friday", "Saturday"};
    private static List<String> dayList = Arrays.asList(days);


    /**
     * Initialise an ApplicationModel instance
     * @param applicationId - unique identifier of instance
     * @param time - time for application to be taken
     * @param dosage - the dosage of the application to be taken
     * @param day - the day of when this application is to be taknen
     * @param amount - the amount of medicine to be taken in this applicaition
     * @param isTaken - if this application has been taken or not
     */
    public ApplicationModel(int applicationId, int medicationId, String time, double dosage, String day, int amount, boolean isTaken) {
        this.applicationId = applicationId;
        this.medicationId = medicationId;
        this.time = time;
        this.dosage = dosage;
        this.day = day;
        this.amount = amount;
        this.isTaken = isTaken;
    }

    public ApplicationModel(int applicationId, int medicationId, int timeMinutes, int timeHour, double dosage, String day, int amount, boolean isTaken) {
        this.applicationId = applicationId;
        this.medicationId = medicationId;
        this.time = intTimeToString(timeHour) +":"+ intTimeToString(timeMinutes);
        this.dosage = dosage;
        this.day = day;
        this.amount = amount;
        this.isTaken = isTaken;
    }

    public String[] timeToHourAndMin(){
        return this.getTime().split(":");
    }

    private String intTimeToString(int time) {
        String returnString = Integer.toString(time);
        if(returnString.length() == 1){
            returnString = "0" + returnString;
        }
        return returnString;
    }

    public int dayToInt() {
        return dayList.indexOf(this.day);
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

    public int getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(int medicationId) {
        this.medicationId = medicationId;
    }

    @Override
    public int compareTo(ApplicationModel o) {
        String thisTime = this.getTime();
        String otherTime = o.getTime();
        if(thisTime.equals(null) || otherTime.equals(null)) return 0;
        return thisTime.compareTo(otherTime);
    }
}
