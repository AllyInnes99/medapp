package com.example.medapp;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.google.api.client.googleapis.testing.auth.oauth2.MockGoogleCredential;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;


@RunWith(AndroidJUnit4.class)
public class GoogleTest {

    private GoogleCalendarHelper gch;
    private Context context;
    private DatabaseHelper db;
    private MedicationModel med;

    public GoogleTest(){
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        db = new DatabaseHelper(context);
        med = new MedicationModel("name", 25, "pill", 0.5, "g", false);
        gch = new GoogleCalendarHelper(context, new MockGoogleCredential.Builder().build());
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

    @Test
    public void testIfServiceCreated() {
        com.google.api.services.calendar.Calendar service = gch.getService();
        assertNotNull(service);
    }

}
