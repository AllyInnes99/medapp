package com.example.medapp;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.View;

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

@RunWith(AndroidJUnit4.class)
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
        databaseHelper.close();
        context.deleteDatabase(databaseHelper.getDatabaseName());
        databaseHelper = new DatabaseHelper(context);
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

        // Go back to the update activity
        Thread.sleep(500);
        Intent i = new Intent();
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("medID", med.getMedicationId());
        activityRule.launchActivity(i);

        // Go back to the refill activity
        onView(withId(R.id.btn_refill)).perform(ViewActions.scrollTo(), ViewActions.click());
        Thread.sleep(500);

        // Check that there is an visible log entry for the med refill
        onView(withId(R.id.recycler_view)).check(new RecyclerViewItemCountAssertion(1));

        // Now, lets try the removing a quantity from the med quantity
        int amountToRemove = 4;
        originalAmount = med.getQuantity();
        onView(withId(R.id.et_new)).perform(ViewActions.replaceText(Integer.toString(amountToRemove)));
        Thread.sleep(200);
        onView(withId(R.id.btn_remove)).perform(ViewActions.click());
        Thread.sleep(200);
        onView(withId(R.id.btn_update)).perform(ViewActions.click());

        // Check that the med quantity has been updated as intended
        med = databaseHelper.selectAllMedication().get(0);
        assertEquals(originalAmount - amountToRemove, med.getQuantity());


    }

    @Test
    public void testEditDoses() throws Exception {

        DoseModel dose1 = new DoseModel(med.getMedicationId(), "23:59", "Tuesday", 1);
        dose1.setMedicationId(med.getMedicationId());
        databaseHelper.addDose(dose1);

        // Click the edit doses button
        onView(withId(R.id.btn_dose)).perform(ViewActions.scrollTo(), ViewActions.click());
        intended(hasComponent(EditMedDosesActivity.class.getName()));
        Thread.sleep(500);

        // Check that there is one dose in the recycler
        onView(withId(R.id.recyclerView)).check(new RecyclerViewItemCountAssertion(1));

        // Add a new dose
        Thread.sleep(500);
        onView(withId(R.id.addDoseButton)).perform(ViewActions.click());
        intended(hasComponent(CreateDoseActivity.class.getName()));

        onView(withId(R.id.et_time)).perform(ViewActions.replaceText("22:59"));
        onView(withId(R.id.et_amount)).perform(ViewActions.replaceText("1"));
        onView(withId(R.id.select_all)).perform(ViewActions.click());
        onView(withId(R.id.btnAdd)).perform(ViewActions.scrollTo(), ViewActions.click());

        // Check that new dose has been added
        onView(withId(R.id.recyclerView)).check(new RecyclerViewItemCountAssertion(2));
        Thread.sleep(500);

        // Press the add button to update the doses, and verify that they have been added
        onView(withId(R.id.nextButton)).perform(ViewActions.click());
        Thread.sleep(500);
        int noOfDoses = databaseHelper.countDosesFromMed(med);
        assertEquals(noOfDoses, 2);

    }

    @Test
    public void deleteMedTest() throws InterruptedException {
        // Click the delete button
        onView(withId(R.id.btn_del)).perform(ViewActions.scrollTo(), ViewActions.click());

        // Check that med has been deleted
        Thread.sleep(100);
        assertEquals(databaseHelper.countMedication(), 0);
    }



    @Test
    public void testUpdateAutoTake() throws InterruptedException {

        DoseModel dose1 = new DoseModel(med.getMedicationId(), "23:59", "Daily", 1);
        dose1.setMedicationId(med.getMedicationId());
        databaseHelper.addDose(dose1);

        // click the autotake switch
        onView(withId(R.id.autotake)).perform(ViewActions.scrollTo(), ViewActions.click());
        Thread.sleep(100);
        onView(withId(R.id.btn_update)).perform(ViewActions.scrollTo(), ViewActions.click());


        // start activity again
        Intent i = new Intent();
        Thread.sleep(100);
        med = databaseHelper.selectAllMedication().get(0);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("medID", med.getMedicationId());
        activityRule.launchActivity(i);
        Thread.sleep(100);

        // click the autotake switch again
        onView(withId(R.id.autotake)).perform(ViewActions.scrollTo(), ViewActions.click());
        Thread.sleep(100);
        onView(withId(R.id.btn_update)).perform(ViewActions.scrollTo(), ViewActions.click());



    }

}
