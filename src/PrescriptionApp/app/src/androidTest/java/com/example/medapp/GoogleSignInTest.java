package com.example.medapp;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class GoogleSignInTest {

    private Context appContext;
    private DatabaseHelper databaseHelper;

    public GoogleSignInTest() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        databaseHelper = new DatabaseHelper(appContext);
    }

    @Test
    public void signInTest() {
        
    }


}
