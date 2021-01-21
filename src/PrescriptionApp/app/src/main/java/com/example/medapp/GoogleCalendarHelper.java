package com.example.medapp;

import android.content.Context;
import android.telephony.mbms.MbmsErrors;
import android.util.Log;
import android.widget.Toast;

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
    private DatabaseHelper databaseHelper;
    private com.google.api.services.calendar.Calendar service;
    private static final NetHttpTransport NET_HTTP_TRANSPORT =
            new com.google.api.client.http.javanet.NetHttpTransport();
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CALENDAR_ID = "primary";


    public GoogleCalendarHelper(Context context) {
        this.context = context;
        this.databaseHelper = new DatabaseHelper(context);
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
     * @param medModel the med to remove all events from user's Google Calendar
     */
    public void deleteMedEvents(final MedicationModel medModel){
        if(medModel.getCalendarEmpty() != null || medModel.getCalendarRefill() != null) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        service.events().delete(CALENDAR_ID, medModel.getCalendarRefill()).execute();
                        service.events().delete(CALENDAR_ID, medModel.getCalendarEmpty()).execute();
                        DatabaseHelper databaseHelper = new DatabaseHelper(context);
                        for(DoseModel doseModel: databaseHelper.selectDoseFromMedication(medModel)) {
                            service.events().delete(CALENDAR_ID, doseModel.getCalendarID()).execute();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
    }

    public void deleteRefillEvent(final MedicationModel medModel) {
        if(medModel.getCalendarRefill() != null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        service.events().delete(CALENDAR_ID, medModel.getCalendarRefill()).execute();
                        databaseHelper.updateRefillID(medModel, "a");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public void updateMedEvents(MedicationModel medModel) {

        // update the refill reminder event
        if(medModel.getRefillAt() >= 14) {
            updateRefillEvent(medModel);
        }
        // if there can be no refill reminder event, remove existing
        else {
            deleteRefillEvent(medModel);
        }

        // update the empty event
        updateEmptyEvent(medModel);

        // update all of the dose events
        List<DoseModel> doses = databaseHelper.selectDoseFromMedication(medModel);
        for(DoseModel dose: doses) {
            updateDoseEvent(medModel, dose);
        }
    }


    /**
     * Update the reminders on the users Google Calendar when the quantity of medication is updated
     * @param medModel the medication that is to be taken
     * @param doseModel the details of when to take the medication etc.
     */
    public void updateDoseEvent(MedicationModel medModel, final DoseModel doseModel) {
        final String eventID = doseModel.getCalendarID();
        if(eventID != null) {
            final Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, medModel.getRefillAt());

            String year = Integer.toString(c.get(Calendar.YEAR));
            String day = padDate(c.get(Calendar.DATE));
            String month = padDate(c.get(Calendar.MONTH) + 1);
            final String recurrenceEndDate = year + month + day;

            final String recurrence;
            if(doseModel.getDay().equals("Daily")) {
                recurrence = "RRULE:FREQ=DAILY;UNTIL=";
            }
            else {
                recurrence = "RRULE:FREQ=WEEKLY;UNTIL=";
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Event event = service.events().get(CALENDAR_ID, eventID).execute();
                        event.setRecurrence(Collections.singletonList(recurrence + recurrenceEndDate));
                        Event updated = service.events().update(CALENDAR_ID, eventID, event).execute();
                        databaseHelper.updateDoseCalendarID(doseModel, updated.getId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public void updateRefillEvent(final MedicationModel medModel) {
        final String eventID = medModel.getCalendarRefill();
        if(eventID != null) {
            final Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, medModel.getRefillAt() - 14);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Event event = service.events().get(CALENDAR_ID, eventID).execute();
                        Log.d("UpdateRefill", event.getSummary());
                        setTime(c, event);
                        Event updated = service.events().update(CALENDAR_ID, eventID, event).execute();
                        //databaseHelper.updateRefillID(medModel, updated.getId());
                    } catch (IOException e) {
                        Log.d("MedApp", e.toString());
                    }

                }
            }).start();
        }
    }

    public void updateEmptyEvent(final MedicationModel medModel) {
        final String eventID = medModel.getCalendarEmpty();
        if(eventID != null) {
            final Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, medModel.getRefillAt());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Event event = service.events().get(CALENDAR_ID, eventID).execute();
                        Log.d("UpdateEmpty", event.getSummary());
                        setTime(c, event);
                        Event updated = service.events().update(CALENDAR_ID, eventID, event).execute();
                        //databaseHelper.updateRefillID(medModel, updated.getId());
                    } catch (IOException e) {
                        Log.d("MedApp", "error");
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    /**
     * Method that deletes every MedApp event in the user's Google Calendar
     */
    public void deleteAllEvents() {
        List<MedicationModel> medModels = databaseHelper.selectAllMedication();
        for (MedicationModel medModel : medModels) {
            deleteMedEvents(medModel);
        }
    }

    /**
     * Adds a reminder to take medication to Google Calendar
     * @param medicationModel
     */
    public void addMedReminder(MedicationModel medicationModel) {
        List<DoseModel> doseModels = databaseHelper.selectDoseFromMedication(medicationModel);
        Calendar c = Calendar.getInstance();
        for(DoseModel dose: doseModels){

            Event event = new Event()
                    .setSummary("MedApp: " + medicationModel.getName() + " Reminder")
                    .setDescription("Reminder to take " + dose.getAmount()
                            + " of " + medicationModel.getName());

            String recurrence;
            if(dose.getDay().equals("Daily")) {
                setMedReminderTime(c, event, dose);
                recurrence = "RRULE:FREQ=DAILY;UNTIL=";
            }
            else {
                Calendar b = nextDayOfWeek(App.days.indexOf(dose.getDay()));
                setMedReminderTime(b, event, dose);
                recurrence = "RRULE:FREQ=WEEKLY;UNTIL=";
            }

            int daysUntilEmpty = medicationModel.getRefillAt();
            c.add(Calendar.DATE, daysUntilEmpty);
            String year = Integer.toString(c.get(Calendar.YEAR));
            String day = padDate(c.get(Calendar.DATE));
            String month = padDate(c.get(Calendar.MONTH) + 1);
            String recurrenceEndDate = year + month + day;

            event.setRecurrence(Collections.singletonList(recurrence + recurrenceEndDate));
            addDoseEvent(dose, event);
            c = Calendar.getInstance();
        }
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
        addEmptyEvent(medModel, emptyEvent);

        // Set a refill reminder event to remind users to refill their medication before it becomes empty
        if(medModel.getRefillAt() >= 14){
            c.add(Calendar.DATE, -14);
            Event refillEvent = new Event()
                    .setDescription(medModel.getName() + " will run out of supply in 14 days. Please order a new prescription.")
                    .setSummary("MedApp: " + medModel.getName() + " Refill Reminder");
            setTime(c, refillEvent);
            addRefillReminderEvent(medModel, refillEvent);
        }
    }

    private void addRefillReminderEvent(final MedicationModel medicationModel, Event event) {
        final Event[] events = {event};
        // Can't run IO operations on UI thread, so start new thread for operation
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    events[0] = service.events().insert(CALENDAR_ID, events[0]).execute();
                    String eventID = events[0].getId();
                    databaseHelper.updateRefillID(medicationModel, eventID);
                } catch (final Exception e) {
                    Log.e("MedApp", "exception", e);
                }
            }
        });
        t.start();
    }

    private void addEmptyEvent(final MedicationModel medicationModel, Event event) {
        final Event[] events = {event};
        // Can't run IO operations on UI thread, so start new thread for operation
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    events[0] = service.events().insert(CALENDAR_ID, events[0]).execute();
                    String eventID = events[0].getId();
                    databaseHelper.updateEmptyID(medicationModel, eventID);
                } catch (IOException e) {
                    Log.e("MedApp", "exception", e);
                }
            }
        });
        t.start();
    }

    private void addDoseEvent(final DoseModel doseModel, Event event) {
        final Event[] events = {event};
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    events[0] = service.events().insert(CALENDAR_ID, events[0]).execute();
                    String eventID = events[0].getId();
                    databaseHelper.updateDoseCalendarID(doseModel, eventID);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

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

    private String createDateString(Calendar c){
        String year = Integer.toString(c.get(Calendar.YEAR));
        String day = padDate(c.get(Calendar.DATE));
        String month = padDate(c.get(Calendar.MONTH) + 1);
        return String.format("%s-%s-%s", year, month, day);
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

    private Calendar nextDayOfWeek(int dow) {
        Calendar date = Calendar.getInstance();
        int diff = dow - date.get(Calendar.DAY_OF_WEEK);
        if (diff < 0) {
            diff += 7;
        }
        date.add(Calendar.DAY_OF_MONTH, diff);
        return date;
    }

}
