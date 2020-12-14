package com.example.medapp;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;


/**
 * Helper class that represents the interaction of using the Google Calendar API
 * within Android studio
 */
public class GoogleCalendarHelper {

    private Context context;
    private final Handler handler;
    private static final NetHttpTransport NET_HTTP_TRANSPORT =
            new com.google.api.client.http.javanet.NetHttpTransport();
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public GoogleCalendarHelper(Context context) {
        this.context = context;
        this.handler = new Handler(context.getMainLooper());
    }

    public void addEvent() {

        GoogleAccountCredential gac =
                GoogleAccountCredential.usingOAuth2(context, Collections.singleton(CalendarScopes.CALENDAR_EVENTS));
        gac.setSelectedAccount(GoogleSignIn.getLastSignedInAccount(context).getAccount());

        // Java has no aliasing of import names, need to use full path
        final com.google.api.services.calendar.Calendar service =
                new com.google.api.services.calendar.Calendar.Builder(
                        NET_HTTP_TRANSPORT, JSON_FACTORY, gac)
                        .setApplicationName(context.getString(R.string.app_name))
                        .build();

        // need to use array so that the IO thread can access
        final Event[] event = {new Event()
                .setSummary("THis is a test summary")
                .setLocation("Test location")
                .setDescription("Test description for this test event!")
        };

        DateTime startDateTime = new DateTime("2020-12-28T09:00:00-07:00");
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Europe/London");
        event[0].setStart(start);

        DateTime endDateTime = new DateTime("2020-12-28T17:00:00-07:00");
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Europe/London");
        event[0].setEnd(end);

        // Can't run IO operations on UI thread, so start new thread for operation
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    event[0] = service.events().insert("primary", event[0]).execute();
                } catch (final Exception e) {
                    Log.e("MedApp", "exception", e);
                }
            }
        });
        t.start();
    }

}
