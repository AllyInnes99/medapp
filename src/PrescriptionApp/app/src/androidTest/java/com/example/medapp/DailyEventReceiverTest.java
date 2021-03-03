package com.example.medapp;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ServiceTestRule;
import androidx.test.runner.AndroidJUnit4;


import org.joda.time.Instant;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
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
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        receiver = new DailyEventReceiver();
        db = new DatabaseHelper(context);
        med = new MedicationModel("name", 25, "pill", 0.5, "g", false);
        doses = new ArrayList<>();
        doses.add(new DoseModel(med.getMedicationId(), "12:30", "Daily", 1));
        //doses.add(new DoseModel(med.getMedicationId(), "15:30", "Daily", 1));
        db.addMedication(med);
    }

    @Before
    public void setup() {
        db.addMedication(med);
        med = db.selectAllMedication().get(0);
        for(DoseModel dose: doses) {
            dose.setMedicationId(med.getMedicationId());
            db.addDose(dose);
        }
        db.updateDaysUntilEmpty(med);
        med = db.selectAllMedication().get(0);
    }

    @After
    public void tearDown() {
        db.close();
        context.deleteDatabase(db.getDatabaseName());
        db = new DatabaseHelper(context);
    }

    @Test
    public void testDaysUntilEmptyDecremented() {
        int original = med.getDaysUntilEmpty();
        receiver.onReceive(context, new Intent("android.intent.action.NOTIFY"));
        med = db.selectAllMedication().get(0);
        int updated = med.getDaysUntilEmpty();
        assertEquals(original - 1, updated);
    }

    @Test
    public void testLogsAdded() {
        int originalSize = db.selectAllLogs().size();
        receiver.onReceive(context, new Intent("android.intent.action.NOTIFY"));
        int newSize = db.selectAllLogs().size();
        assertNotEquals(originalSize, newSize);
    }



}



