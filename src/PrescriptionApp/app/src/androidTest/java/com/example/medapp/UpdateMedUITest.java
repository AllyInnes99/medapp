package com.example.medapp;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;

import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;


import com.google.android.material.button.MaterialButtonToggleGroup;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static org.junit.Assert.assertEquals;

public class UpdateMedUITest {

    private Context context;
    private MedicationModel med;
    private DatabaseHelper databaseHelper;

    @Rule
    public ActivityTestRule<UpdateMedActivity> activityRule =
            new ActivityTestRule<>(UpdateMedActivity.class, false, false);

    public UpdateMedUITest() {
        databaseHelper = new DatabaseHelper(context);
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        med = new MedicationModel("new", 25, "pill", 0.5, "g", false);

    }

    @Before
    public void setUp() throws Exception {
        Intents.init();
        databaseHelper = new DatabaseHelper(context);
        databaseHelper.addMedication(med);
        med = databaseHelper.selectAllMedication().get(0);
        Intent i = new Intent();
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("medID", med.getMedicationId());
        activityRule.launchActivity(i);
    }

    @After
    public void tearDown() throws Exception {
        Intents.release();
    }

    @Test
    public void testUpdateMed() throws Exception {
        // Update the name
        String newName = "updatedName";
        onView(withId(R.id.et_name)).perform(ViewActions.replaceText(newName));
        Thread.sleep(500);

        // Press the update button
        onView(withId(R.id.btn_update)).perform(ViewActions.scrollTo(), ViewActions.click());

        // Test that the name of the med has been updated
        Thread.sleep(500);
        med = databaseHelper.selectAllMedication().get(0);
        assertEquals(med.getName(), newName);
    }

    @Test
    public void refillTest() throws Exception {
        int amountToAdd = 5;
        int originalAmount = med.getQuantity();

        // Press the refill button
        onView(withId(R.id.btn_refill)).perform(ViewActions.scrollTo(), ViewActions.click());
        Thread.sleep(500);

        // Check that the refill activity has been launched
        intended(hasComponent(MedRefill.class.getName()));

        // Add a refill to the medication
        onView(withId(R.id.et_new)).perform(ViewActions.replaceText(Integer.toString(amountToAdd)));

        Thread.sleep(500);

        // Refill the medication
        onView(withId(R.id.btn_update)).perform(ViewActions.click());

        // Check that the med quantity has been updated as intended
        med = databaseHelper.selectAllMedication().get(0);
        assertEquals(originalAmount + amountToAdd, med.getQuantity());

    }
}
