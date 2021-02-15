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
 * Instrumented test, which will execute on an Android device.
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

    @After
    public void tearDown() {
        db.close();
        context.deleteDatabase(db.getDatabaseName());
        db = new DatabaseHelper(context);
    }


    @Test
    public void useAppContext() {
        // Context of the app under test.
        assertEquals("com.example.medapp", context.getPackageName());
    }

    @Test
    public void addMedModelToDB() {
        MedicationModel actual = db.selectAllMedication().get(0);
        assertEquals(med.getName(), actual.getName());
    }

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

    @Test
    public void addContactToDb() {
        ContactDetails contact = new ContactDetails("id", "John Smith", "johnsmith@email.com");
        assertTrue(db.addContact(contact));
        ContactDetails actual = db.selectAllContacts().get(0);
        assertEquals(contact.getId(), actual.getId());
    }

    @Test
    public void addLogToDb() {
        med = db.selectAllMedication().get(0);
        MedicationLog log = new MedicationLog(med.getMedicationId(), "msg", 1, Calendar.getInstance().getTimeInMillis(), true, true);
        assertTrue(db.addLog(log));
        MedicationLog actual = db.selectAllLogs().get(0);
        assertEquals(actual.getMsg(), log.getMsg());
    }

    @Test
    public void addRefillLogToDb() {
        med = db.selectAllMedication().get(0);
        RefillData data = new RefillData(med.getMedicationId(), 1, 1, 2020, 20, 10);
        assertTrue(db.addRefill(data));
        RefillData actual = db.selectAllRefill().get(0);
        assertEquals(actual.getRefillAmount(), data.getRefillAmount());
    }

    @Test
    public void calcDaysUntilEmpty() {
        med = db.selectAllMedication().get(0);
        addDoseModelToDb();
        int expected = 13;
        int actual = med.daysUntilEmpty(db);
        assertEquals(expected, actual);
    }


}