package com.example.medapp;

import android.content.Context;
import android.content.Intent;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class RefreshReceiverTest {

    private Context context;
    private DatabaseHelper db;
    private MedicationModel med;
    private List<DoseModel> doses;
    private RefreshReceiver receiver;

    public RefreshReceiverTest() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        receiver = new RefreshReceiver();
        db = new DatabaseHelper(context);
        med = new MedicationModel("name", 25, "pill", 0.5, "g", false);
        doses = new ArrayList<>();
        doses.add(new DoseModel(med.getMedicationId(), "12:30", "Daily", 1));
        doses.add(new DoseModel(med.getMedicationId(), "15:30", "Tuesday", 1));
    }

    @Before
    public void setup() {
        db.addMedication(med);
        med = db.selectAllMedication().get(0);
        for(DoseModel dose: doses) {
            dose.setTaken(true);
            dose.setMedicationId(med.getMedicationId());
            db.addDose(dose);
        }
        db.updateDaysUntilEmpty(med);
        med = db.selectAllMedication().get(0);
    }

    @Test
    public void testDosesRefreshed() {
        receiver.onReceive(context, new Intent("android.intent.action.NOTIFY"));
        List<DoseModel> refreshedDoses = db.selectAllDoses();
        for(int i = 0; i < doses.size(); i++) {
            DoseModel previous = doses.get(i);
            DoseModel refreshed = refreshedDoses.get(i);
            assertNotEquals(previous.isTaken(), refreshed.isTaken());
        }

    }


}
