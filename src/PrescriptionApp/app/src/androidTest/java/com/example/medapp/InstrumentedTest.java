package com.example.medapp;

import android.content.Context;
import android.provider.ContactsContract;

import androidx.test.InstrumentationRegistry;

import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedTest {

    private Context appContext;

    public InstrumentedTest(){
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }


    @Test
    public void useAppContext() {
        // Context of the app under test.
        assertEquals("com.example.medapp", appContext.getPackageName());
    }

    @Test
    public void testAddingToDb() {
        DatabaseHelper databaseHelper = new DatabaseHelper(appContext);
        
    }

}