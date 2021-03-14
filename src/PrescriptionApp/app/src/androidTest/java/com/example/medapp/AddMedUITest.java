package com.example.medapp;

import android.app.Activity;

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

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;


@RunWith(AndroidJUnit4.class)
public class AddMedUITest {

    Activity currentActivity;

    @Rule
    public ActivityTestRule<CreateMedicationActivity> activityRule =
            new ActivityTestRule<>(CreateMedicationActivity.class);

    @Before
    public void setup() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testValidAddMed() throws Exception {

        String name = "test";
        String quantity = "20";
        String measurement = "g";
        String type = "pill(s)";
        String strength = "0.5";

        onView(withId(R.id.et_name)).perform(ViewActions.replaceText(name));
        Thread.sleep(100);

        onView(withId(R.id.et_quantity)).perform(ViewActions.replaceText(quantity));
        Thread.sleep(100);

        onView(withId(R.id.dropdown_measurement)).perform(ViewActions.replaceText(measurement));
        Thread.sleep(100);

        onView(withId(R.id.dropdown_type)).perform(ViewActions.replaceText(type));
        Thread.sleep(100);

        onView(withId(R.id.et_strength)).perform(ViewActions.replaceText(strength));
        Thread.sleep(100);

        onView(withId(R.id.autotake)).perform(ViewActions.click());
        Thread.sleep(1000);

        onView(withId(R.id.submit_btn)).perform(ViewActions.click());
        Thread.sleep(1000);

        intended(hasComponent(AddDosesActivity.class.getName()));
    }


    @Test
    public void testInvalidAddMed() throws Exception {

        String name = "";
        String quantity = "-1";
        String measurement = "g";
        String type = "pill(s)";
        String strength = "0.5";

        onView(withId(R.id.et_name)).perform(ViewActions.replaceText(name));
        Thread.sleep(100);

        onView(withId(R.id.et_quantity)).perform(ViewActions.replaceText(quantity));
        Thread.sleep(100);

        onView(withId(R.id.dropdown_measurement)).perform(ViewActions.replaceText(measurement));
        Thread.sleep(100);

        onView(withId(R.id.dropdown_type)).perform(ViewActions.replaceText(type));
        Thread.sleep(100);

        onView(withId(R.id.et_strength)).perform(ViewActions.replaceText(strength));
        Thread.sleep(100);

        onView(withId(R.id.autotake)).perform(ViewActions.click());
        Thread.sleep(1000);

        onView(withId(R.id.submit_btn)).perform(ViewActions.click());
        Thread.sleep(1000);

    }
}
