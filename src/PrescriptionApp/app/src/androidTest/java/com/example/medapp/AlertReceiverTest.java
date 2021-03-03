package com.example.medapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;
import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class AlertReceiverTest {

    private Context context;
    private DatabaseHelper db;
    private MedicationModel med;
    private DoseModel dose;
    private AlertReceiver receiver;
    private NotificationManager notificationManager;

    public AlertReceiverTest() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        receiver = new AlertReceiver();
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

    @Test
    public void testNotificationLaunched() {
        Intent i = new Intent("android.intent.action.NOTIFY");
        i.putExtra("doseID", dose.getDoseId());
        i.putExtra("medID", med.getMedicationId());
        receiver.onReceive(context, i);
        assertEquals(dose.getDoseId(), receiver.doseId);
        assertEquals(med.getMedicationId(), receiver.medId);

        StatusBarNotification[] notifications = notificationManager.getActiveNotifications();
        boolean notifcationFound = false;
        for (StatusBarNotification notification : notifications) {
            if (notification.getId() == dose.getDoseId()) {
                notifcationFound = true;
            }
        }
        assertTrue(notifcationFound);

    }

}
