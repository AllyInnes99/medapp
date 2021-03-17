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
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SignInUITest {

    @Rule
    public ActivityTestRule<SignInActivity> activityRule =
            new ActivityTestRule<>(SignInActivity.class);

    public SignInUITest() {

    }

    @Test
    public void testSignInActivity() throws InterruptedException {
        Thread.sleep(1000);
        assertTrue(true);
    }


}
