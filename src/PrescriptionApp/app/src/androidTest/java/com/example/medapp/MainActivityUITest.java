package com.example.medapp;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.FragmentTransaction;
import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.google.common.net.InternetDomainName;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Collection;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityUITest {

    Intent i;
    Context context;
    DatabaseHelper db;
    MedicationModel med;

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class, false, false);

    public MainActivityUITest() {
        db = new DatabaseHelper(context);
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Before
    public void setup() {
        Intents.init();
        db = new DatabaseHelper(context);
        med = new MedicationModel("new", 25, "pill", 0.5, "g", false);
        db.addMedication(med);
        med = db.selectAllMedication().get(0);
        MedicationLog log = new MedicationLog(med.getMedicationId(), "msg", 1, Calendar.getInstance().getTimeInMillis(), true, true);
        db.addLog(log);
        DoseModel dose1 = new DoseModel(med.getMedicationId(), "12:30", "Daily", 1);
        db.addDose(dose1);
        i = new Intent();
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

    }

    @After
    public void tearDown() {
        Intents.release();
        db.close();
        context.deleteDatabase(db.getDatabaseName());
        db = new DatabaseHelper(context);
    }

    @Test
    public void testSettingsFragment() throws InterruptedException {
        i.putExtra("r", R.id.settingFragment);
        activityRule.launchActivity(i);
        Thread.sleep(1000);

    }

    @Test
    public void testMedFragment() throws InterruptedException {
        i.putExtra("r", R.id.medFragment);
        activityRule.launchActivity(i);
        Thread.sleep(1000);

    }

    @Test
    public void testLogFragment() throws InterruptedException {
        i.putExtra("r", R.id.statFragment);
        activityRule.launchActivity(i);
        Thread.sleep(1000);

    }




}
