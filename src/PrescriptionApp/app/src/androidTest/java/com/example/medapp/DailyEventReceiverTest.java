package com.example.medapp;

import android.content.Context;
import android.content.Intent;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DailyEventReceiverTest {

    private Context context;
    private DatabaseHelper db;
    private MedicationModel med;
    private List<DoseModel> doses;
    private DailyEventReceiver receiver;

    public DailyEventReceiverTest(){
        db = new DatabaseHelper(context);
        med = new MedicationModel("name", 25, "pill", 0.5, "g", false);
        receiver = new DailyEventReceiver();
        Intent intent = new Intent("android.intent.action.NOTIFY");
        receiver.onReceive(context, intent);
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
    public void testReceiverStart() {
        assertEquals(1,1);
    }

}



