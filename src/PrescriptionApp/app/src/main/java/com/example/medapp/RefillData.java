package com.example.medapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * Class that represents the data held for the refill log
 */
public class RefillData {

    private int id;
    private int medicationId;
    private int day;
    private int month;
    private int year;
    private int refillAmount;
    private int originalQty;

    public RefillData(int medicationId, int day, int month, int year, int refillAmount, int originalQty) {
        this.medicationId = medicationId;
        this.day = day;
        this.month = month;
        this.year = year;
        this.refillAmount = refillAmount;
        this.originalQty = originalQty;
    }

    public RefillData(int id, int medicationId, int day, int month, int year, int refillAmount, int originalQty) {
        this.id = id;
        this.medicationId = medicationId;
        this.day = day;
        this.month = month;
        this.year = year;
        this.refillAmount = refillAmount;
        this.originalQty = originalQty;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(int medicationId) {
        this.medicationId = medicationId;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getRefillAmount() {
        return refillAmount;
    }

    public void setRefillAmount(int refillAmount) {
        this.refillAmount = refillAmount;
    }

    public int getOriginalQty() {
        return originalQty;
    }

    public void setOriginalQty(int originalQty) {
        this.originalQty = originalQty;
    }

    private String padDate(int val){
        String valStr = Integer.toString(val);
        if(val < 10){
            valStr = "0" + valStr;
        }
        return valStr;
    }

}
