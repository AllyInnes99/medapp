package com.example.medapp;

import java.util.Calendar;

public class MedicationLog {

    private int logId;
    private int medicationId;
    private String msg;
    private long time;
    private boolean onTime;

    public MedicationLog(int logId, int medicationId, String msg, long time, boolean onTime) {
        this.logId = logId;
        this.medicationId = medicationId;
        this.msg = msg;
        this.time = time;
        this.onTime = onTime;
    }

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

    public Calendar timeToCalendar() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(this.time);
        return c;
    }

}
