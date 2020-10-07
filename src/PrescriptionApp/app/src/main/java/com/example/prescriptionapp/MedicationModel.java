package com.example.prescriptionapp;

public class MedicationModel {

    private int id;
    private String name;
    private int quantity;
    private boolean isTaken;

    // Constructors

    /**
     * Initialise a MedicationModel object
     * @param id - unique identifier of medication
     * @param name - the name of the medication
     * @param quantity - how much mediacation has been taken
     * @param isTaken - if the medication has been taken or not
     */
    public MedicationModel(int id, String name, int quantity, boolean isTaken) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.isTaken = isTaken;
    }

    /**
     * @return a String that prints the contents of a medication object
     */
    @Override
    public String toString() {
        return "MedicationModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", isTaken=" + isTaken +
                '}';
    }

    // Getters and setters

    /**
     * @return the id of the medication
     */
    public int getId() {
        return id;
    }

    /**
     * @param id - set the medication id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name of the medication
     */
    public String getName() {
        return name;
    }

    /**
     * @param name set the name of the medication
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return the current quantity of the medication in stock
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @param quantity set the amount of the medication in stock
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * @return if the medication has been taken or not
     */
    public boolean isTaken() {
        return isTaken;
    }

    /**
     * @param taken set whether or not the medication has been taken
     */
    public void setTaken(boolean taken) {
        isTaken = taken;
    }
}
