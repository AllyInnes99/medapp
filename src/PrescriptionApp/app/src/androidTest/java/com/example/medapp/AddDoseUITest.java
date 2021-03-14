package com.example.medapp;

import android.app.Activity;
import android.content.Intent;
import android.widget.TimePicker;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.google.common.net.InternetDomainName;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class AddDoseUITest {

    @Rule
    public ActivityTestRule<AddDosesActivity> activityRule =
            new ActivityTestRule<>(AddDosesActivity.class, false, false);

    public AddDoseUITest() {

    }

    @Before
    public void setup() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }


    @Test
    public void testActivityStart() throws Exception {
        MedicationModel newMed = new MedicationModel("new", 25, "pill", 0.5, "g", false);
        Intent i = new Intent();
        i.putExtra("MedModel", newMed);
        activityRule.launchActivity(i);

        // Check recycler empty
        onView(withId(R.id.recyclerView)).check(new RecyclerViewItemCountAssertion(0));

        // Launch add activity
        onView(withId(R.id.addDoseButton)).perform(click());
        intended(hasComponent(CreateDoseActivity.class.getName()));

        // Add detail for the dose
        onView(withId(R.id.et_time)).perform(ViewActions.replaceText("10:00"));
        Thread.sleep(100);

        onView(withId(R.id.et_amount)).perform(ViewActions.replaceText("1"));
        Thread.sleep(100);

        onView(withId(R.id.select_all)).perform(ViewActions.click());
        Thread.sleep(100);

        // Add the dose
        onView(withId(R.id.btnAdd)).perform(ViewActions.scrollTo(), ViewActions.click());
        Thread.sleep(250);

        // Test that the dose has been added by checking the size of recycler
        onView(withId(R.id.recyclerView)).check(new RecyclerViewItemCountAssertion(1));
        Thread.sleep(250);

        // Add the med and return to MainActivity
        onView(withId(R.id.nextButton)).perform(ViewActions.click());
        Thread.sleep(250);
    }


}
