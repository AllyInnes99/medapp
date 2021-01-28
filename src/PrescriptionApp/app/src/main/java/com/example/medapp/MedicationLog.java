package com.example.medapp;

import java.util.Calendar;

/**
 * Class that represents an entry of the medication taking history log
 */
public class MedicationLog {

    private int logId;
    private int medicationId;
    private String msg;
    private int amount;

    private long time;
    private boolean onTime;

    /**
     * Constructor used when reading log from database
     * @param logId the unique int primary id of the log entry
     * @param medicationId the int foreign key that links the log to a medication
     * @param msg the message that the log is displayed
     * @param time the time of which the medication was taken
     * @param onTime if the med was taken on time or not
     */
    public MedicationLog(int logId, int medicationId, String msg, long time, boolean onTime) {
        this.logId = logId;
        this.medicationId = medicationId;
        this.msg = msg;
        this.time = time;
        this.onTime = onTime;
    }

    public Calendar timeToCalendar() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(this.time);
        return c;
    }

    public void CalendarToTime(Calendar c) {
        setTime(c.getTimeInMillis());
    }

    /*
        GETTERS AND SETTERS
    */

    public int getLogId() {
        return logId;
    }

    public void setLogId(int medLogId) {
        this.logId = medLogId;
    }

    public int getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(int medicationId) {
        this.medicationId = medicationId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isOnTime() {
        return onTime;
    }

    public void setOnTime(boolean onTime) {
        this.onTime = onTime;
    }


}
