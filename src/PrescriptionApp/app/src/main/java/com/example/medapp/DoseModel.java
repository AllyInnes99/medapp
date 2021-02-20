package com.example.medapp;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Class that represents a given dose of a medication that a user needs to take
 */
public class DoseModel implements Comparable<DoseModel>, Serializable {

    private int doseId;
    private int medicationId;
    private String time;
    private String day;
    private int amount;
    private boolean isTaken;
    private String calendarID;

    private static List<String> dayList = Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday");

    /**
     * Initialise a DoseModel instance, used when initially creating dose
     *
     * @param medicationId the int ID of the medication model that this dose references
     * @param time         - String that represents the time for dose is to be taken
     * @param day          - String for the day of when this dose is to be taken
     * @param amount       - int for the amount of medicine to be taken in this dose
     */
    public DoseModel(int medicationId, String time, String day, int amount) {
        this.doseId = (int) System.currentTimeMillis();
        this.medicationId = medicationId;
        this.time = time;
        this.day = day;
        this.amount = amount;
        this.isTaken = false;
        this.calendarID = null;
    }

    /**
     * Initialise DoseModel instance, used when retrieving dose from database
     *
     * @param doseId       unique int ID that identifies the dose in the DB
     * @param medicationId int ID of the medicaiton that this dose references
     * @param timeMinutes  int that represents the minutes of when the dose is to be taken
     * @param timeHour     int that represents the hours of when the dose is to be taken
     * @param day          String that represents the day of when the dose is to be taken
     * @param amount       integer that represents the no. of the medication that should be taken w/ dose
     * @param isTaken      bool that represents whether the medication has been taken
     * @param calendarID   String for ID of the Google Calendar event for the dose
     */
    public DoseModel(int doseId, int medicationId, int timeMinutes, int timeHour, String day,
                     int amount, boolean isTaken, String calendarID) {
        this.doseId = doseId;
        this.medicationId = medicationId;
        this.time = intTimeToString(timeHour) + ":" + intTimeToString(timeMinutes);
        this.day = day;
        this.amount = amount;
        this.isTaken = isTaken;
        this.calendarID = calendarID;
    }

    public String[] timeToHourAndMin() {
        return this.getTime().split(":");
    }

    private String intTimeToString(int time) {
        String returnString = Integer.toString(time);
        if (returnString.length() == 1) {
            returnString = "0" + returnString;
        }
        return returnString;
    }

    public boolean isDoseDaily() {
        return this.day.equals("Daily");
    }

    public int dayToInt() {
        return dayList.indexOf(this.day);
    }

    public int getDoseId() {
        return doseId;
    }

    public void setDoseId(int doseId) {
        this.doseId = doseId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getCalendarID() {
        return calendarID;
    }

    public void setCalendarID(String calendarID) {
        this.calendarID = calendarID;
    }

    @Override
    public int compareTo(DoseModel o) {
        String thisTime = this.getTime();
        String otherTime = o.getTime();
        if (thisTime.equals(otherTime)) return 0;
        return thisTime.compareTo(otherTime);
    }
}
