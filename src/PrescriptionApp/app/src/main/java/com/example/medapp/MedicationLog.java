package com.example.medapp;

import java.util.Calendar;

/**
 * Class that represents an entry of the medication taking history log
 */
public class MedicationLog implements Comparable<MedicationLog> {

    private int logId;
    private int medicationId;
    private String msg;
    private int amount;
    private long time;
    private boolean taken;
    private boolean onTime;

    public MedicationLog(int logId, int medicationId, String msg, int amount, long time, boolean taken, boolean onTime) {
        this.logId = logId;
        this.medicationId = medicationId;
        this.msg = msg;
        this.amount = amount;
        this.time = time;
        this.taken = taken;
        this.onTime = onTime;
    }

    public MedicationLog(int medicationId, String msg, int amount, long time, boolean taken, boolean onTime) {
        this.logId = 0;
        this.medicationId = medicationId;
        this.msg = msg;
        this.amount = amount;
        this.time = time;
        this.taken = taken;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    @Override
    public int compareTo(MedicationLog o) {
        Long thisTime = this.getTime();
        Long otherTime = o.getTime();
        return otherTime.compareTo(thisTime);
    }
}
