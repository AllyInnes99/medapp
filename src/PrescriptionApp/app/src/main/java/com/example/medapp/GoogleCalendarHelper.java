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
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


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

    public void getEvents() throws IOException {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Events events = null;
                try {
                    events = service.events().list("primary").execute();
                    List<Event> items = events.getItems();
                    for(final Event event: items) {
                        Log.d("MedApp", event.getSummary());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    public void addMedReminder(MedicationModel medicationModel) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        List<DoseModel> doseModels = databaseHelper.selectDoseFromMedication(medicationModel);

        if(medicationModel.getDayFrequency().equals("Daily")){

            Calendar c = Calendar.getInstance();
            for(DoseModel dose: doseModels){
                Event e = new Event()
                        .setSummary(medicationModel.getName() + " Reminder")
                        .setDescription("Reminder to take " + dose.getAmount()
                                + " of " + medicationModel.getName());
            }
        }
    }

    private void setDateTime(Calendar c, Event event, DoseModel doseModel){
        String year = Integer.toString(c.get(Calendar.YEAR));
        String day = padDate(c.get(Calendar.DATE));
        String month = padDate(c.get(Calendar.MONTH) + 1);
        String[] time = doseModel.getTime().split(":");
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        c.set(Calendar.MINUTE, Integer.parseInt(time[1]));

    }

    private void setMedReminderStart(Calendar c, Event e, DoseModel doseModel) {


    }

    private void setMedReminderEnd(Calendar c, Event e, DoseModel doseModel) {

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
        //Toast.makeText(context, events[0].getId(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Creates a Google Calendar Event for when the mediication will run out
     * @param medModel the medication to create an event for
     */
    public void addEventForMedication(MedicationModel medModel) {

        // Obtain the date of when the med will be empty
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, medModel.getRefillAt());

        Event emptyEvent = new Event()
                .setDescription(medModel.getName() + " will run out of supply today.")
                .setSummary(medModel.getName() + " Empty");


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

    private void setTime(Calendar c, Event event){
        String year = Integer.toString(c.get(Calendar.YEAR));
        String day = padDate(c.get(Calendar.DATE));
        String month = padDate(c.get(Calendar.MONTH) + 1);
        String date = String.format("%s-%s-%s", year, month, day);

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
