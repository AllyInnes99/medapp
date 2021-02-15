package com.example.medapp;

import android.content.Context;

import androidx.test.InstrumentationRegistry;

import androidx.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Ref;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Set of unit tests for testing the SQLite database of the application
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    private Context context;
    private DatabaseHelper db;
    private MedicationModel med;

    public DatabaseTest(){
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        db = new DatabaseHelper(context);
        med = new MedicationModel("name", 25, "pill", 0.5, "g", false);
    }

    @Before
    public void setUp() {
        db.addMedication(med);
    }

    /**
     * After the execution of each test, tear down the db and create a new one
     */
    @After
    public void tearDown() {
        db.close();
        context.deleteDatabase(db.getDatabaseName());
        db = new DatabaseHelper(context);
    }

    /**
     * Test that a medication can be added to the database successfully
     */
    @Test
    public void addMedModelToDB() {
        MedicationModel newMed = new MedicationModel("new", 25, "pill", 0.5, "g", false);
        assertTrue(db.addMedication(newMed));
        MedicationModel actual = db.selectAllMedication().get(0);
        assertEquals(newMed.getName(), actual.getName());
    }

    /**
     * Test that a dose can be added to the database successfully
     */
    @Test
    public void addDoseModelToDb() {
        med = db.selectAllMedication().get(0);
        DoseModel dose1 = new DoseModel(med.getMedicationId(), "12:30", "Daily", 1);
        DoseModel dose2 = new DoseModel(med.getMedicationId(), "15:30", "Daily", 1);
        assertTrue(db.addDose(dose1));
        assertTrue(db.addDose(dose2));
        List<DoseModel> addedDoses = db.selectAllDoses();
        DoseModel actual1 = addedDoses.get(0);
        DoseModel actual2 = addedDoses.get(1);
        assertNotEquals(actual1.getDoseId(), actual2.getDoseId());
        assertEquals(actual1.getTime(), dose1.getTime());
        assertEquals(actual2.getTime(), dose2.getTime());
    }

    /**
     * Test that a contact can be added to the database successfully
     */
    @Test
    public void addContactToDb() {
        ContactDetails contact = new ContactDetails("id", "John Smith", "johnsmith@email.com");
        assertTrue(db.addContact(contact));
        ContactDetails actual = db.selectAllContacts().get(0);
        assertEquals(contact.getId(), actual.getId());
    }

    /**
     * Test that a log can be added to the database successfully
     */
    @Test
    public void addLogToDb() {
        med = db.selectAllMedication().get(0);
        MedicationLog log = new MedicationLog(med.getMedicationId(), "msg", 1, Calendar.getInstance().getTimeInMillis(), true, true);
        assertTrue(db.addLog(log));
        MedicationLog actual = db.selectAllLogs().get(0);
        assertEquals(actual.getMsg(), log.getMsg());
    }

    /**
     * Test that a refill log can be added to the database successfully
     */
    @Test
    public void addRefillLogToDb() {
        med = db.selectAllMedication().get(0);
        RefillData data = new RefillData(med.getMedicationId(), 1, 1, 2020, 20, 10);
        assertTrue(db.addRefill(data));
        RefillData actual = db.selectAllRefill().get(0);
        assertEquals(actual.getRefillAmount(), data.getRefillAmount());
    }

    /**
     * Test that the calculation of the days until empty field is accurate
     */
    @Test
    public void testDaysUntilEmpty() {
        med = db.selectAllMedication().get(0);
        addDoseModelToDb();
        int expected = 13;
        int actual = med.daysUntilEmpty(db);
        assertEquals(expected, actual);
    }

    /**
     * Test that the editing a medication successfully changes the value in the db
     */
    @Test
    public void testEditingMedication() {
        med = db.selectAllMedication().get(0);
        String originalName = med.getName();
        int id = med.getMedicationId();
        med.setName("new");
        db.updateMedication(med);
        MedicationModel updatedMed = db.selectMedicationFromID(id);
        assertNotEquals(originalName, updatedMed.getName());
    }

    /**
     * Test that today's and yesterday's doses are accurate
     */
    @Test
    public void testTodayDoses() {
        med = db.selectAllMedication().get(0);
        Calendar c = Calendar.getInstance();
        String day1 = App.days.get(c.get(Calendar.DAY_OF_WEEK));
        c.add(Calendar.DATE, -1);
        String day2 = App.days.get(c.get(Calendar.DAY_OF_WEEK));

        DoseModel today = new DoseModel(med.getMedicationId(), "12:30", day1, 1);
        DoseModel yesterday = new DoseModel(med.getMedicationId(), "11:30", day2, 1);
        DoseModel daily =  new DoseModel(med.getMedicationId(), "09:30", "Daily", 1);

        db.addDose(today);
        db.addDose(yesterday);
        db.addDose(daily);

        List<DoseModel> todayDoses = db.selectTodaysDoseAndNotTaken();
        assertEquals(todayDoses.size(), 2);
        assertEquals(day1, todayDoses.get(0).getDay());

        List<DoseModel> yesterdayDoses = db.selectYesterdaysDoseAndNotTaken();
        assertEquals(yesterdayDoses.size(), 2);
        assertEquals(day1, yesterdayDoses.get(0).getDay());
    }

    /**
     * Test the daily refresh set the isTaken field of every dose that is listed as daily in the
     * database is set to false after execution and ensure that non-daily doses aren't updated
     */
    @Test
    public void testDailyRefresh() {
        med = db.selectAllMedication().get(0);
        DoseModel dose1 = new DoseModel(med.getMedicationId(), "09:20", "Tuesday", 1);
        DoseModel dose2 = new DoseModel(med.getMedicationId(), "09:40", "Daily", 1);
        db.addDose(dose1);
        db.addDose(dose2);

        assertFalse(dose1.isDoseDaily());
        assertTrue(dose2.isDoseDaily());

        dose1 = db.selectAllDoses().get(0);
        dose2 = db.selectAllDoses().get(1);
        db.takeMedication(dose1, med);
        db.takeMedication(dose2, med);

        dose1 = db.selectDoseFromID(dose1.getDoseId());
        dose2 = db.selectDoseFromID(dose2.getDoseId());
        assertTrue(dose1.isTaken());
        assertTrue(dose2.isTaken());

        db.refreshDailyDoses();
        dose1 = db.selectDoseFromID(dose1.getDoseId());
        dose2 = db.selectDoseFromID(dose2.getDoseId());
        assertTrue(dose1.isTaken());
        assertFalse(dose2.isTaken());
    }

    /**
     * Test that the weekly refresh of doses successfully sets the field for if the dose has been taken
     * to false for every dose in the database
     */
    @Test
    public void testWeeklyRefresh() {
        med = db.selectAllMedication().get(0);
        DoseModel dose1 = new DoseModel(med.getMedicationId(), "09:20", "Tuesday", 1);
        DoseModel dose2 = new DoseModel(med.getMedicationId(), "09:40", "Daily", 1);
        db.addDose(dose1);
        db.addDose(dose2);

        dose1 = db.selectAllDoses().get(0);
        dose2 = db.selectAllDoses().get(1);
        db.takeMedication(dose1, med);
        db.takeMedication(dose2, med);

        dose1 = db.selectDoseFromID(dose1.getDoseId());
        dose2 = db.selectDoseFromID(dose2.getDoseId());
        assertTrue(dose1.isTaken());
        assertTrue(dose2.isTaken());

        db.refreshDoses();
        dose1 = db.selectDoseFromID(dose1.getDoseId());
        dose2 = db.selectDoseFromID(dose2.getDoseId());
        assertFalse(dose1.isTaken());
        assertFalse(dose2.isTaken());
    }

    @Test
    public void testLogEntryAddedAfterMedicationTaken() {
        med = db.selectAllMedication().get(0);
        DoseModel dose1 = new DoseModel(med.getMedicationId(), "09:20", "Tuesday", 1);
        db.addDose(dose1);
        dose1 = db.selectAllDoses().get(0);
        db.takeMedication(dose1, med, true);
        List<MedicationLog> logs = db.selectAllLogs();
        assertFalse(logs.isEmpty());
    }
}