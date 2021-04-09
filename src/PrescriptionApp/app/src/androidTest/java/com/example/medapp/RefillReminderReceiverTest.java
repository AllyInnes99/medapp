package com.example.medapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.service.notification.StatusBarNotification;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Context.NOTIFICATION_SERVICE;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RefillReminderReceiverTest {

    private Context context;
    private DatabaseHelper db;
    private MedicationModel med;
    private DoseModel dose;
    private RefillReminderReceiver receiver;
    private NotificationManager notificationManager;

    public RefillReminderReceiverTest() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        receiver = new RefillReminderReceiver();
        db = new DatabaseHelper(context);
        med = new MedicationModel("name", 2, "pill", 0.5, "g", false);
    }

    @Before
    public void setup() {
        db.addMedication(med);
        med = db.selectAllMedication().get(0);
    }

    @Test
    public void testRefillNotification() {
        Intent i = new Intent("android.intent.action.NOTIFY");
        i.putExtra("medId", med.getMedicationId());
        receiver.onReceive(context, i);
        StatusBarNotification[] notifications = notificationManager.getActiveNotifications();
        boolean notificationFound = false;
        for (StatusBarNotification notification : notifications) {
            if (notification.getId() == -med.getMedicationId()) {
                notificationFound = true;
            }
        }
        assertTrue(notificationFound);
    }
}
