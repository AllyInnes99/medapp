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
import com.google.api.client.util.Data;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Calendar;
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

    public void addEventToCalendar(Event event) {
        GoogleAccountCredential gac =
                GoogleAccountCredential.usingOAuth2(context, Collections.singleton(CalendarScopes.CALENDAR_EVENTS));
        gac.setSelectedAccount(GoogleSignIn.getLastSignedInAccount(context).getAccount());

        // Java has no aliasing of import names, need to use full path
        final com.google.api.services.calendar.Calendar service =
                new com.google.api.services.calendar.Calendar.Builder(
                        NET_HTTP_TRANSPORT, JSON_FACTORY, gac)
                        .setApplicationName(context.getString(R.string.app_name))
                        .build();

        final Event[] events = {event};

        // Can't run IO operations on UI thread, so start new thread for operation
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    events[0] = service.events().insert("primary", events[0]).execute();
                } catch (final Exception e) {
                    Log.e("MedApp", "exception", e);
                }
            }
        });
        t.start();

    }

    public void addEventForMedication(MedicationModel medModel) {

        GoogleAccountCredential gac =
                GoogleAccountCredential.usingOAuth2(context, Collections.singleton(CalendarScopes.CALENDAR_EVENTS));
        gac.setSelectedAccount(GoogleSignIn.getLastSignedInAccount(context).getAccount());

        // Java has no aliasing of import names, need to use full path
        final com.google.api.services.calendar.Calendar service =
                new com.google.api.services.calendar.Calendar.Builder(
                        NET_HTTP_TRANSPORT, JSON_FACTORY, gac)
                        .setApplicationName(context.getString(R.string.app_name))
                        .build();




        String summary = "Resupply of " + medModel.getName();
        Toast.makeText(context, summary, Toast.LENGTH_SHORT).show();
        // Set the description of the event, i.e. what the user will see
        final Event event = new Event()
                .setDescription(summary)
                .setSummary(summary);

        // Obtain the date of when the med will be empty
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, medModel.getRefillAt());
        String year = Integer.toString(c.get(Calendar.YEAR));
        String day = padDate(c.get(Calendar.DATE));
        String month = padDate(c.get(Calendar.MONTH) + 1);
        String date = String.format("%s-%s-%s", year, month, day);
        Toast.makeText(context, date, Toast.LENGTH_SHORT).show();

        // Create dateTime objs and set the start/end time to them for reminder
        DateTime dateTime = new DateTime(date);
        EventDateTime eventDateTime = new EventDateTime()
                .setDate(dateTime)
                .setTimeZone("Europe/London");
        event.setStart(eventDateTime);
        event.setEnd(eventDateTime);
        addEventToCalendar(event);
    }

    private String padDate(int val){
        String valStr = Integer.toString(val);
        if(valStr.length() == 1){
            valStr = "0" + valStr;
        }
        return valStr;
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

        DateTime startDateTime = new DateTime("2020-12-28");
        EventDateTime start = new EventDateTime()
                .setDate(startDateTime)
                .setTimeZone("Europe/London");
        event[0].setStart(start);

        DateTime endDateTime = new DateTime("2020-12-28");
        EventDateTime end = new EventDateTime()
                .setDate(endDateTime)
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
