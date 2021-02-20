package com.example.medapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents an object of a dose to be added to the database for a medication.
 * This exists so that the days selected for a given dose can be visible during creation
 */
public class AddDoseModel implements Serializable {

    private String time;
    private int quantity;
    private List<String> days;

    /**
     * Constructor for creating a dose model
     *
     * @param time     String that reps. the time of when the dose is to be taken
     * @param quantity int that reps. the amount of the medication is to be taken at the given dose
     */
    public AddDoseModel(String time, int quantity) {
        this.time = time;
        this.quantity = quantity;
        this.days = new ArrayList<>();
    }

    /**
     * If the days of the dose is size 7 (i.e. it has every day in it), then the dose is to be taken
     * every day. This method returns whether the dose is a daily dose or not.
     *
     * @return true if the dose is to be taken daily, false otherwise
     */
    public boolean isDoseDaily() {
        return getDays().size() == 7;
    }

    private void addDay(String day) {
        this.days.add(day);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }
}
