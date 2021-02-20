package com.example.medapp;

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

    /**
     * Method that creates a readable date for the refill log
     *
     * @return formatted String that represents the date
     */
    public String createDateString() {
        return String.format("%s-%s-%s", padDate(this.getDay()), padDate(this.getMonth()), this.getYear());
    }

    /**
     * Helper method that pads the date value to have a leading 0 if it is single digit
     *
     * @param val int representing the date value
     * @return padded string with preceding 0 if required
     */
    private String padDate(int val) {
        String valStr = Integer.toString(val);
        if (val < 10) {
            valStr = "0" + valStr;
        }
        return valStr;
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


}
