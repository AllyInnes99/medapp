package com.example.medapp;

import android.content.Intent;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddDoseUITest {

    @Rule
    public ActivityTestRule<AddDosesActivity> activityRule =
            new ActivityTestRule<>(AddDosesActivity.class, false, false);

    public AddDoseUITest() {

    }

    @Before
    public void setup() {
        Intents.init();
        MedicationModel newMed = new MedicationModel("new", 25, "pill", 0.5, "g", false);
        Intent i = new Intent();
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("MedModel", newMed);
        activityRule.launchActivity(i);
    }

    @After
    public void tearDown() {
        Intents.release();
    }


    @Test
    public void testAddDoses() throws Exception {
        // Check recycler empty
        onView(withId(R.id.recyclerView)).check(new RecyclerViewItemCountAssertion(0));

        // Launch add activity
        onView(withId(R.id.addDoseButton)).perform(click());
        intended(hasComponent(CreateDoseActivity.class.getName()));

        // Add detail for the dose, w/ invalid quantity
        onView(withId(R.id.et_time)).perform(ViewActions.replaceText("10:00"));
        Thread.sleep(100);

        onView(withId(R.id.et_amount)).perform(ViewActions.replaceText("0"));
        Thread.sleep(100);

        onView(withId(R.id.select_all)).perform(ViewActions.click());
        Thread.sleep(100);

        // Attempt to add dose
        onView(withId(R.id.btnAdd)).perform(ViewActions.scrollTo(), ViewActions.click());
        Thread.sleep(250);

        // Update quantity field and try again
        onView(withId(R.id.et_amount)).perform(ViewActions.replaceText("1"));
        onView(withId(R.id.btnAdd)).perform(ViewActions.scrollTo(), ViewActions.click());
        Thread.sleep(250);

        // Test that the dose has been added by checking the size of recycler
        onView(withId(R.id.recyclerView)).check(new RecyclerViewItemCountAssertion(1));
        Thread.sleep(250);

        // Add the med and return to MainActivity
        onView(withId(R.id.nextButton)).perform(ViewActions.click());
        Thread.sleep(250);
        intended(hasComponent(MainActivity.class.getName()));


    }

    @Test
    public void noDosesTest() {

        // Firstly, assert that there are no doses in the recycler
        onView(withId(R.id.recyclerView)).check(new RecyclerViewItemCountAssertion(0));

        // Attempt to move to Main activity
        onView(withId(R.id.nextButton)).perform(ViewActions.click());

    }

}
