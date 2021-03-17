package com.example.medapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import androidx.test.InstrumentationRegistry;
import androidx.test.annotation.UiThreadTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;
import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class TakeReceiverTest {

    private Context context;
    private DatabaseHelper db;
    private MedicationModel med;
    private DoseModel dose;
    private TakeReceiver receiver;
    private NotificationManager notificationManager;

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class);

    public TakeReceiverTest() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        receiver = new TakeReceiver();
        db = new DatabaseHelper(context);
        med = new MedicationModel("name", 25, "pill", 0.5, "g", false);
        dose = new DoseModel(med.getMedicationId(), "12:30", "Daily", 1);
    }

    @Before
    public void setup() {
        db.addMedication(med);
        med = db.selectAllMedication().get(0);
        dose.setMedicationId(med.getMedicationId());
        db.addDose(dose);
        dose = db.selectAllDoses().get(0);
    }

    @After
    public void tearDown() {
        db.close();
        context.deleteDatabase(db.getDatabaseName());
        db = new DatabaseHelper(context);
    }

    @Test
    @UiThreadTest
    public void testTakeReceiver() {
        Intent i = new Intent();
        i.putExtra("doseID", dose.getDoseId());
        i.putExtra("medID", med.getMedicationId());
        receiver.onReceive(activityRule.getActivity(), i);

        // update dose, and check that it has been taken
        dose = db.selectDoseFromID(dose.getDoseId());
        assertTrue(dose.isTaken());

    }
}
