package com.example.medapp;

import androidx.core.widget.TextViewCompat;

import org.junit.Test;

import static org.junit.Assert.*;


public class MedicationTest {

    private MedicationModel med;
    private int id;
    private String name;
    private int quantity;
    private int daysUntilEmpty;
    private String type;
    private double dosage;
    private String measurement;
    private String profile;
    private boolean autoTake;
    private boolean refillRequested;
    private String calendarRefill;
    private String calendarEmpty;



    public MedicationTest() {
        id = 0;
        name = "test";
        quantity = 25;
        daysUntilEmpty = 25;
        type = "pill(s)";
        dosage = 0.1;
        measurement = "g";
        profile = "me";
        autoTake = false;
        refillRequested = false;
        calendarEmpty = "id2";
        calendarRefill = "id1";
        med = new MedicationModel(id, name, quantity, daysUntilEmpty, type, dosage, measurement,
                profile, autoTake, refillRequested, calendarRefill, calendarEmpty);
    }

    @Test
    public void testNameIsValid() {
        assertTrue(MedicationModel.validateMedicationName("name"));
        assertFalse(MedicationModel.validateMedicationName(""));
    }

    @Test
    public void testNameAddedCorrectly() {
        assertEquals(name, med.getName());
    }

    @Test
    public void testIdAddedCorrectly() {
        assertEquals(id, med.getMedicationId());
    }

    @Test
    public void testQuantityAddedCorrectly() {
        assertEquals(quantity, med.getQuantity());
    }

    @Test
    public void testDaysUntilEmptyAddedCorrectly() {
        assertEquals(daysUntilEmpty, med.getDaysUntilEmpty());
    }

    @Test
    public void testDosageAddedCorrectly() {
        assertEquals(dosage, med.getDosage(),0.1);
    }

    @Test
    public void testMeasurementAddedCorrectly() {
        assertEquals(measurement, med.getMeasurement());
    }

    @Test
    public void testTypeAddedCorrectly() {
        assertEquals(type, med.getType());
    }

    @Test
    public void testAutoTakeSetCorrectly() {
        assertEquals(autoTake, med.isAutoTake());
    }

    @Test
    public void testRequestRefillSetCorrectly() {
        assertEquals(refillRequested, med.isRefillRequested());
    }

    @Test
    public void testEmptyIdAddedCorrectly() {
        assertEquals(calendarEmpty, med.getCalendarEmpty());
    }

    @Test
    public void testRefillIdAddedCorrectly() {
        assertEquals(calendarRefill, med.getCalendarRefill());
    }

    @Test
    public void testSetId() {
        int newId = id + 1;
        med.setMedicationId(newId);
        assertNotEquals(med.getMedicationId(), id);
    }

    @Test
    public void testSetName() {
        String newName = "test2";
        med.setName(newName);
        assertNotEquals(med.getName(), name);
    }

    @Test
    public void testSetQuantity() {
        int newQuantity = quantity - 1;
        med.setQuantity(newQuantity);
        assertNotEquals(med.getQuantity(), quantity);
    }

    @Test
    public void testSetDaysUntilEmpty() {
        int newDays = med.getDaysUntilEmpty() + 1;
        med.setDaysUntilEmpty(newDays);
        assertNotEquals(med.getDaysUntilEmpty(), daysUntilEmpty);
    }

    @Test
    public void testSetDosage() {
        double newDosage = med.getDosage() + 1;
        med.setDosage(newDosage);
        assertNotEquals(med.getDosage(), dosage);
    }

    @Test
    public void testSetMeasurement() {
        String newMeasurement = "mg";
        med.setMeasurement(newMeasurement);
        assertNotEquals(med.getMeasurement(), measurement);
    }

    @Test
    public void testSetProfile() {
        String newProfile = "not em";
        med.setProfile(newProfile);
        assertNotEquals(med.getProfile(), profile);
    }

    @Test
    public void testSetAutoTake() {
        boolean newAutoTake = !med.isAutoTake();
        med.setAutoTake(newAutoTake);
        assertNotEquals(med.isAutoTake(), autoTake);
    }

    @Test
    public void testSetRefillRequest() {
        boolean newRequest = !med.isRefillRequested();
        med.setRefillRequested(newRequest);
        assertNotEquals(med.isRefillRequested(), refillRequested);
    }

    @Test
    public void testSetEmptyId() {
        String newEmptyId = "id5";
        med.setCalendarEmpty(newEmptyId);
        assertNotEquals(med.getCalendarEmpty(), calendarEmpty);
    }

    @Test
    public void testSetRefillId() {
        String newRefillId = "id5";
        med.setCalendarRefill(newRefillId);
        assertNotEquals(med.getCalendarRefill(), calendarRefill);
    }

}
