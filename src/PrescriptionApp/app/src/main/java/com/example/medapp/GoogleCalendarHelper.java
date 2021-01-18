package com.example.medapp;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Helper class that represents the interaction of using the Google Calendar API
 * within Android studio
 */
public class GoogleCalendarHelper {

    private Context context;
    private com.google.api.services.calendar.Calendar service;
    private static final NetHttpTransport NET_HTTP_TRANSPORT =
            new com.google.api.client.http.javanet.NetHttpTransport();
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CALENDAR_ID = "primary";


    public GoogleCalendarHelper(Context context) {
        this.context = context;
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2
                (context, Collections.singleton(CalendarScopes.CALENDAR_EVENTS));
        credential.setSelectedAccount(GoogleSignIn.getLastSignedInAccount(context).getAccount());

        // build the service that will be used to make API calls
        this.service = new com.google.api.services.calendar.Calendar.Builder(
                        NET_HTTP_TRANSPORT, JSON_FACTORY, credential)
                        .setApplicationName(context.getString(R.string.app_name))
                        .build();
    }

    /**
     * Method that deletes all events in a user's Google Calendar that are related to a given med
     * @param medicationModel the med to remove all events from user's Google Calendar
     */
    public void deleteMedEvents(final MedicationModel medicationModel){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Events events = service.events().list("primary").execute();
                    for(Event event: events.getItems()){
                        if(event.getSummary().contains("MedApp: " + medicationModel.getName())){
                            Log.d("MedApp", event.getSummary());
                            service.events().delete(CALENDAR_ID, event.getId()).execute();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void updateRefillEvents(final MedicationModel medModel) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Events events = service.events().list(CALENDAR_ID).execute();
                    for(Event event: events.getItems()){

                        String summary = event.getSummary();
                        if (summary.equals("MedApp: " + medModel.getName() + " Empty") ||
                                summary.equals("MedApp: " + medModel.getName() + " Refill Reminder")) {
                            Calendar c = Calendar.getInstance();
                            c.add(Calendar.DATE, medModel.getRefillAt());
                            String updatedDate = createDateString(c);
                            EventDateTime eventDateTime = new EventDateTime()
                                    .setDateTime(new DateTime(updatedDate))
                                    .setTimeZone("Europe/London");
                            event.setStart(eventDateTime);
                            event.setEnd(eventDateTime);
                            Event updated = service.events().update(CALENDAR_ID, event.getId(), event).execute();
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    /**
     * Method that deletes every MedApp event in the user's Google Calendar
     */
    public void deleteAllEvents() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Events events = service.events().list("primary").execute();
                    for(Event event: events.getItems()){
                        if(event.getSummary().contains("MedApp:")){
                            Log.d("MedApp", event.getSummary());
                            service.events().delete(CALENDAR_ID, event.getId()).execute();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    /**
     * Adds a reminder to take medication to Google Calendar
     * @param medicationModel
     */
    public void addMedReminder(MedicationModel medicationModel) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        List<DoseModel> doseModels = databaseHelper.selectDoseFromMedication(medicationModel);


        Calendar c = Calendar.getInstance();
        for(DoseModel dose: doseModels){

            Event e = new Event()
                    .setSummary("MedApp: " + medicationModel.getName() + " Reminder")
                    .setDescription("Reminder to take " + dose.getAmount()
                            + " of " + medicationModel.getName());

            String recurrence;
            if(dose.getDay().equals("Daily")) {
                setMedReminderTime(c, e, dose);
                recurrence = "RRULE:FREQ=DAILY;UNTIL=";
            }
            else {
                Calendar b = nextDayOfWeek(App.days.indexOf(dose.getDay()));
                setMedReminderTime(b, e, dose);
                recurrence = "RRULE:FREQ=WEEKLY;UNTIL=";
            }

            int daysUntilEmpty = medicationModel.getRefillAt();
            c.add(Calendar.DATE, daysUntilEmpty);
            String year = Integer.toString(c.get(Calendar.YEAR));
            String day = padDate(c.get(Calendar.DATE));
            String month = padDate(c.get(Calendar.MONTH) + 1);
            String recurrenceEndDate = year + month + day;

            e.setRecurrence(Arrays.asList(recurrence + recurrenceEndDate));
            addEventToCalendar(e);
            c = Calendar.getInstance();

        }
    }

    public void testThread(){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatabaseHelper databaseHelper = new DatabaseHelper(context);
                    MedicationModel model = new MedicationModel("new",5,5, "tablet",  0.1, "g", "me", false);
                    databaseHelper.addMedication(model);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();

    }


    private Calendar nextDayOfWeek(int dow) {
        Calendar date = Calendar.getInstance();
        int diff = dow - date.get(Calendar.DAY_OF_WEEK);
        if (diff < 0) {
            diff += 7;
        }
        date.add(Calendar.DAY_OF_MONTH, diff);
        return date;
    }

    private String createRecurrenceEndDate(MedicationModel medicationModel, Calendar c) {
        int daysUntilEmpty = medicationModel.getRefillAt();
        c.add(Calendar.DATE, daysUntilEmpty);
        String year = Integer.toString(c.get(Calendar.YEAR));
        String day = padDate(c.get(Calendar.DATE));
        String month = padDate(c.get(Calendar.MONTH) + 1);
        return year + month + day;
    }

    private Event createMedReminderEvent(MedicationModel medModel, DoseModel doseModel) {
        Event e = new Event()
                .setSummary("MedApp: " + medModel.getName() + " Reminder")
                .setDescription("Reminder to take " + doseModel.getAmount() + " of " + medModel.getName());
        return e;
    }

    /**
     * Helper function that is used to set the time of when the event is to take place
     * @param calendar - Calendar obj
     * @param event - Event obj to set the reminder time of
     * @param doseModel - the Dose obj that we are creating the Event for
     */
    private void setMedReminderTime(Calendar calendar, Event event, DoseModel doseModel) {
        String date = createDateString(calendar);
        String time = doseModel.getTime();
        String dateTime = date + "T" + time + ":00-00:00";
        EventDateTime eventDateTime = new EventDateTime()
                .setDateTime(new DateTime(dateTime))
                .setTimeZone("Europe/London");
        event.setStart(eventDateTime);
        event.setEnd(eventDateTime);
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
                    events[0] = service.events().insert(CALENDAR_ID, events[0]).execute();
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
    public void addRefillEvents(MedicationModel medModel) {

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

    /**
     * EventDateTime objects require the DateTime to be in YYYY-MM-DD format, so we need to pad
     * the month and day fields with a 0 if they are a single digit
     * @param val the int to turn into String and pad if necessary
     * @return String that is valid for DateTime obj
     */
    private String padDate(int val){
        String valStr = Integer.toString(val);
        if(val < 10){
            valStr = "0" + valStr;
        }
        return valStr;
    }

}
