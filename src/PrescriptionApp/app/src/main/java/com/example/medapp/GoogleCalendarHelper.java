package com.example.medapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * Helper class that represents the interaction of using the Google Calendar API
 * within Android studio
 */
public class GoogleCalendarHelper {

    private Context context;
    private static final NetHttpTransport NET_HTTP_TRANSPORT =
            new com.google.api.client.http.javanet.NetHttpTransport();
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private GoogleAccountCredential credential;
    private com.google.api.services.calendar.Calendar service;

    public GoogleCalendarHelper(Context context) {
        this.context = context;
        credential = GoogleAccountCredential.usingOAuth2
                (context, Collections.singleton(CalendarScopes.CALENDAR_EVENTS));
        credential.setSelectedAccount(GoogleSignIn.getLastSignedInAccount(context).getAccount());

        service = new com.google.api.services.calendar.Calendar.Builder(
                        NET_HTTP_TRANSPORT, JSON_FACTORY, credential)
                        .setApplicationName(context.getString(R.string.app_name))
                        .build();
    }

    public void deleteEvents() throws InterruptedException, ExecutionException {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Events events = service.events().list("primary").execute();
                    for(Event event: events.getItems()){
                        if(event.getSummary().contains("MedApp:")){
                            Log.d("MedApp", event.getSummary());
                            service.events().delete("primary", event.getId()).execute();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void addMedReminder(MedicationModel medicationModel) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        List<DoseModel> doseModels = databaseHelper.selectDoseFromMedication(medicationModel);

        if(medicationModel.getDayFrequency().equals("Daily")){

            Calendar c = Calendar.getInstance();
            String prevTime = "";
            for(DoseModel dose: doseModels){
                if(!dose.getTime().equals(prevTime)){
                    prevTime = dose.getTime();
                    Event e = new Event()
                            .setSummary("MedApp: " + medicationModel.getName() + " Reminder")
                            .setDescription("Reminder to take " + dose.getAmount()
                                    + " of " + medicationModel.getName());

                    setMedReminderStart(c, e, dose);
                    setMedReminderEnd(c, e, dose);

                    int daysUntilEmpty = medicationModel.getRefillAt();
                    c.add(Calendar.DATE, daysUntilEmpty);
                    String year = Integer.toString(c.get(Calendar.YEAR));
                    String day = padDate(c.get(Calendar.DATE));
                    String month = padDate(c.get(Calendar.MONTH) + 1);
                    String recurrenceEndDate = year + month + day;
                    Toast.makeText(context, recurrenceEndDate, Toast.LENGTH_SHORT).show();

                    e.setRecurrence(Arrays.asList("RRULE:FREQ=DAILY;UNTIL=" + recurrenceEndDate));
                    addEventToCalendar(e);
                }


            }
        }
    }

    private void setMedReminderStart(Calendar c, Event e, DoseModel doseModel) {
        String date = createDateString(c);
        String time = doseModel.getTime();
        String dateTime = date + "T" + time + ":00-00:00";
        EventDateTime eventDateTime = new EventDateTime()
                .setDateTime(new DateTime(dateTime))
                .setTimeZone("Europe/London");
        e.setStart(eventDateTime);
    }

    private void setMedReminderEnd(Calendar c, Event e, DoseModel doseModel) {
        String date = createDateString(c);
        String time = doseModel.getTime();
        String dateTime = date + "T" + time + ":00-00:00";
        EventDateTime eventDateTime = new EventDateTime()
                .setDateTime(new DateTime(dateTime))
                .setTimeZone("Europe/London");
        e.setEnd(eventDateTime);
    }



    /**
     * Function that takes a Google Calendar Event and adds it to the user's primary calendar
     * @param event - the event to be added to the user's calendar
     */
    public void addEventToCalendar(Event event) {

        final Event[] events = {event};
        //Log.e("MedApp", events[0].getId());
        // Can't run IO operations on UI thread, so start new thread for operation
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    events[0] = service.events().insert("primary", events[0]).execute();
                    Log.e("MedApp", events[0].getId() + "\n");

                } catch (final Exception e) {
                    Log.e("MedApp", "exception", e);
                }
            }
        });
        t.start();
    }

    /**
     * Creates a Google Calendar Event for when the medication will run out
     * @param medModel the medication to create an event for
     */
    public void addEventForMedication(MedicationModel medModel) {

        // Obtain the date of when the med will be empty
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, medModel.getRefillAt());

        Event emptyEvent = new Event()
                .setDescription(medModel.getName() + " will run out of supply today.")
                .setSummary("MedApp: " + medModel.getName() + " Empty");


        setTime(c, emptyEvent);
        addEventToCalendar(emptyEvent);

        // Set a refill reminder event to remind users to refill their medication before it becomes empty
        if(medModel.getRefillAt() >= 14){
            c.add(Calendar.DATE, -14);
            Event refillEvent = new Event()
                    .setDescription(medModel.getName() + " will run out of supply in 14 days. Please order a new prescription.")
                    .setSummary("MedApp: " + medModel.getName() + " Refill Reminder");
            setTime(c, refillEvent);
            addEventToCalendar(refillEvent);
        }


    }

    private String createDateString(Calendar c){
        String year = Integer.toString(c.get(Calendar.YEAR));
        String day = padDate(c.get(Calendar.DATE));
        String month = padDate(c.get(Calendar.MONTH) + 1);
        String date = String.format("%s-%s-%s", year, month, day);
        return date;
    }


    private void setTime(Calendar c, Event event){
        String date = createDateString(c);
        DateTime dateTime = new DateTime(date);
        EventDateTime eventDateTime = new EventDateTime()
                .setDate(dateTime)
                .setTimeZone("Europe/London");
        event.setStart(eventDateTime);
        event.setEnd(eventDateTime);
    }

    public void deleteEvents(final MedicationModel model) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    service.events().delete("primary", model.getName().toLowerCase() + 0).execute();
                    service.events().delete("primary", model.getName().toLowerCase() + 1).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        t.start();
    }


    /**
     * EventDateTime objects require the DateTime to be in YYYY-MM-DD format, so we need to pad
     * the month and day fields with a 0 if they are < 10
     * @param val the int to turn into String and pad if necessary
     * @return String that is valid for
     */
    private String padDate(int val){
        String valStr = Integer.toString(val);
        if(valStr.length() == 1){
            valStr = "0" + valStr;
        }
        return valStr;
    }

}
