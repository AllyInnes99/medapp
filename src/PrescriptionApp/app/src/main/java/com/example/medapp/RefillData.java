package com.example.medapp;

public class RefillData {

    private int id;
    private int medicationId;
    private long date;
    private int refillAmount;
    private int originalQty;

    public RefillData(int medicationId, long date, int refillAmount, int originalQty) {
        this.medicationId = medicationId;
        this.date = date;
        this.refillAmount = refillAmount;
        this.originalQty = originalQty;
    }

    public RefillData(int id, int medicationId, long date, int refillAmount, int originalQty) {
        this.id = id;
        new RefillData(medicationId, date, refillAmount, originalQty);
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
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
