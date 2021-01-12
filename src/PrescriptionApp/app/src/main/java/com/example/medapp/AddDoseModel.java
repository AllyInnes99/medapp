package com.example.medapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents an object of a dose to be added to the database for a medication.
 */
public class AddDoseModel implements Serializable {

    private String time;
    private int quantity;
    private List<String> days;

    public AddDoseModel(String time, int quantity){
        this.time = time;
        this.quantity = quantity;
        this.days = new ArrayList<>();
    }

    public boolean isDoseDaily(){
        return getDays().size() == 7;
    }

    private void addDay(String day){
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
