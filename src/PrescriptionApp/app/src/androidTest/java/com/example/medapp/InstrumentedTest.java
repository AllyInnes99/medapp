package com.example.medapp;

import android.content.Context;
import android.provider.ContactsContract;

import androidx.test.InstrumentationRegistry;

import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedTest {

    private Context appContext;
    private DatabaseHelper databaseHelper;
    private MedicationModel medModel1;
    private MedicationModel medModel2;

    public InstrumentedTest(){
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        databaseHelper = new DatabaseHelper(appContext);
        medModel1 = new MedicationModel("test", 100, 0, "pill",
                                        0.1, "g", "me", false);

        databaseHelper.addMedication(medModel1);
    }


    @Test
    public void useAppContext() {
        // Context of the app under test.
        assertEquals("com.example.medapp", appContext.getPackageName());
    }

    @Test
    public void addMedModelToDB() {

        // get the expected values we want to check are added correctly
        String expectedName = medModel1.getName();
        int expectedQuantity = medModel1.getQuantity();
        String expectedType = medModel1.getType();
        double expectedDosage = medModel1.getDosage();
        String expectedMeasurement = medModel1.getMeasurement();

        // assert that the medication has been added as expected
        assertTrue(databaseHelper.addMedication(medModel1));

        // obtain the added model to the database
        MedicationModel actual = databaseHelper.selectAllMedication().get(0);
        assertEquals(actual.getName(), expectedName);
        assertEquals(actual.getQuantity(), expectedQuantity);
        assertEquals(actual.getType(), expectedType);
        assertEquals(actual.getDosage(), expectedDosage, 0.1);
        assertEquals(actual.getMeasurement(), expectedMeasurement);

        // delete medication for other tests
        databaseHelper.deleteMedication(actual);
    }

}