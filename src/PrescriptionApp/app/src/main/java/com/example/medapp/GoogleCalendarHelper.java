package com.example.medapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.testing.auth.oauth2.MockGoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
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

    private DatabaseHelper databaseHelper;
    private com.google.api.services.calendar.Calendar service;
    private static final NetHttpTransport NET_HTTP_TRANSPORT =
            new com.google.api.client.http.javanet.NetHttpTransport();
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private int refillReminderDays;
    private Context context;
    private EventAttendee contact;
    private static final String CALENDAR_ID = "primary";

    /**
     * Initialise a Google Calendar api connection
     *
     * @param context the app context
     */
    public GoogleCalendarHelper(Context context) {
        this.context = context;
        this.databaseHelper = new DatabaseHelper(context);
        try {
            setRefillReminderDays(context);
        }
        catch (Exception e) {
            this.refillReminderDays = 7;
        }
        
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2
                (context, Collections.singleton(CalendarScopes.CALENDAR_EVENTS));
        credential.setSelectedAccount(GoogleSignIn.getLastSignedInAccount(context).getAccount());

        this.service = new com.google.api.services.calendar.Calendar.Builder(
                NET_HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(context.getString(R.string.app_name))
                .build();
    }

    public void purgeAllMedappEvents() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Events events = service.events().list(CALENDAR_ID).execute();
                    Log.e("Medapp", Integer.toString(events.size()));
                    for(Event event: events.getItems()) {
                        Log.e("Medapp", event.getSummary());
                        if(event.getSummary().contains("MedApp")) {
                            service.events().delete(CALENDAR_ID, event.getId()).execute();

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    /**
     * Method that deletes all events in a user's Google Calendar that are related to a given med
     *
     * @param medModel the med to remove all events from user's Google Calendar
     */
    public void deleteMedEvents(final MedicationModel medModel, final List<DoseModel> doses) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    for (DoseModel doseModel : doses) {
                        Log.d("deletion", "deleting dose event");
                        deleteDoseEvent(doseModel, 0);
                        Thread.sleep(250);
                    }

                    Log.d("deletion", "deleting refill event");
                    deleteRefillEvent(medModel, 0);
                    Thread.sleep(250);

                    Log.d("deletion", "deleting empty event");
                    deleteEmptyEvent(medModel, 0);
                    Thread.sleep(250);

                } catch (Exception e) {
                    Log.d("deletion", "deletion failed");
                }
            }
        }).start();

    }

    /**
     * Method that trawls through every event in a users Google Calendar, identified MedApp events,
     * and deletes them.
     */
    public void purgeMedEvents() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Events events = null;
                try {
                    events = service.events().list(CALENDAR_ID).setMaxResults(9999).execute();
                    Log.i("purge", Integer.toString(events.size()));
                    for(Event event: events.getItems()) {
                        if(event.getSummary().contains("MedApp")) {
                            deleteEventFromId(event.getId());
                            Thread.sleep(250);
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    /**
     * Method that deletes an event by ID
     * @param eventID - the id of the event to be deleted
     */
    public void deleteEventFromId(final String eventID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    service.events().delete(CALENDAR_ID, eventID).execute();
                } catch (IOException e) {
                    deleteEventFromId(eventID);
                }
            }
        }).start();
    }

    public void setContact(ContactDetails contact) {
        this.contact = new EventAttendee();
        this.contact.setDisplayName(contact.getName());
        this.contact.setEmail(contact.getEmail());
    }

    /**
     * Method that deletes the corresponding event for a dose from a user's Google Calendar
     *
     * @param doseModel the medication dose that is to be removed from the user's Google Calendar
     */
    public void deleteDoseEvent(final DoseModel doseModel, final int depth) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    service.events().delete(CALENDAR_ID, doseModel.getCalendarID()).execute();
                } catch (IOException e) {

                    // If fail, use exponential backoff
                    try {
                        if(depth < 7) {
                            Thread.sleep((long) Math.pow(2, depth) * 10);
                            deleteDoseEvent(doseModel, depth + 1);
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }


    /**
     * Method that deletes the refill event for a medication
     *
     * @param medModel the medication that wants the refill event to be removed
     */
    public void deleteRefillEvent(final MedicationModel medModel, final int depth) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    service.events().delete(CALENDAR_ID, medModel.getCalendarRefill()).execute();
                } catch (IOException e) {

                    // If fail, use exponential backoff
                    try {
                        if(depth < 7) {
                            Thread.sleep((long) Math.pow(2, depth) * 10);
                            deleteRefillEvent(medModel, depth + 1);
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * Method that deletes the med empty event from the Google calendar
     * @param medModel the med to delete the event of
     */
    public void deleteEmptyEvent(final MedicationModel medModel, final int depth) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    service.events().delete(CALENDAR_ID, medModel.getCalendarEmpty()).execute();
                } catch (IOException e) {
                    try {
                        if(depth < 7) {
                            Thread.sleep((long) Math.pow(2, depth) * 10);
                            deleteEmptyEvent(medModel, depth + 1);
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * Method that updates all of the events in Google Calendar for a given medication
     *
     * @param medModel the medication to update the Google Calendar events for
     */
    public void updateMedEvents(MedicationModel medModel) {

        // update the refill reminder event
        if (medModel.getDaysUntilEmpty() >= refillReminderDays) {
            updateRefillEvent(medModel);
        }
        // if there can be no refill reminder event, remove existing
        else {
            deleteRefillEvent(medModel, 0);
        }

        // update the empty event
        updateEmptyEvent(medModel);

        // update all of the dose events
        List<DoseModel> doses = databaseHelper.selectDoseFromMedication(medModel);
        for (DoseModel dose : doses) {
            updateDoseEvent(medModel, dose);
        }
    }

    /**
     * Method that updates the refill event for every medication
     */
    public void updateRefillReminderEvents(int val) {

        List<MedicationModel> medModels = databaseHelper.selectAllMedication();
        for (MedicationModel medModel : medModels) {
            updateOrDeleteRefillEvent(medModel, val);
        }

    }

    /**
     * Method that updates the date of refill events, or deletes them if there can't be one
     *
     * @param medModel medication to update/delete event for
     */
    public void updateOrDeleteRefillEvent(MedicationModel medModel, int val) {
        // update the refill reminder event
        if (medModel.getDaysUntilEmpty() >= val) {
            updateRefillEvent(medModel, val);
        }
        // if there can be no refill reminder event, remove existing
        else {
            deleteRefillEvent(medModel, 0);
        }
    }


    /**
     * Update the reminders on the users Google Calendar when the quantity of medication is updated
     *
     * @param medModel  the medication that is to be taken
     * @param doseModel the details of when to take the medication etc.
     */
    public void updateDoseEvent(final MedicationModel medModel, final DoseModel doseModel) {
        final String eventID = doseModel.getCalendarID();
        if (eventID != null) {
            final Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, medModel.getDaysUntilEmpty());

            String year = Integer.toString(c.get(Calendar.YEAR));
            String day = padDate(c.get(Calendar.DATE));
            String month = padDate(c.get(Calendar.MONTH) + 1);
            final String recurrenceEndDate = year + month + day;

            final String recurrence;
            if (doseModel.isDoseDaily()) {
                recurrence = "RRULE:FREQ=DAILY;UNTIL=";
            } else {
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
                        updateDoseEvent(medModel, doseModel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    /**
     * Method that updates the medication refill reminder event in Google Calendar
     *
     * @param medModel the medication that the event is to be updated for
     */
    public void updateRefillEvent(final MedicationModel medModel) {
        final String eventID = medModel.getCalendarRefill();
        if (eventID != null) {
            final Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, medModel.getDaysUntilEmpty() - refillReminderDays);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Event event = service.events().get(CALENDAR_ID, eventID).execute();
                        Log.d("UpdateRefill", event.getSummary());
                        setTime(c, event);
                        event.setDescription(getRefillMsg(medModel));
                        Event updated = service.events().update(CALENDAR_ID, eventID, event).execute();
                    } catch (IOException e) {
                        updateRefillEvent(medModel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }

    /**
     * Method that updates the medication refill reminder event in Google Calendar
     *
     * @param medModel the medication that the event is to be updated for
     */
    public void updateRefillEvent(final MedicationModel medModel, final int val) {
        final String eventID = medModel.getCalendarRefill();
        if (eventID != null) {
            final Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, medModel.getDaysUntilEmpty() - val);
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
                        updateRefillEvent(medModel, val);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }

    /**
     * Method that updates the medication empty event in Google Calendar
     *
     * @param medModel the medication that the event is to be updated for
     */
    public void updateEmptyEvent(final MedicationModel medModel) {
        final String eventID = medModel.getCalendarEmpty();
        if (eventID != null) {
            final Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, medModel.getDaysUntilEmpty());
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    /**
     * Adds a reminder to take medication to Google Calendar
     *
     * @param medModel
     */
    public void addDoseReminder(MedicationModel medModel) {

        boolean t = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean("dose_events", false);
        if(!medModel.isAutoTake() && t) {
            List<DoseModel> doseModels = databaseHelper.selectDoseFromMedication(medModel);
            Calendar c = Calendar.getInstance();
            for (DoseModel dose : doseModels) {

                Event event = new Event()
                        .setSummary("MedApp: " + medModel.getName() + " Reminder")
                        .setDescription("Reminder to take " + dose.getAmount()
                                + " of " + medModel.getName());

                String recurrence;
                if (dose.isDoseDaily()) {
                    setMedReminderTime(c, event, dose);
                    recurrence = "RRULE:FREQ=DAILY;UNTIL=";
                } else {
                    Calendar b = nextDayOfWeek(App.days.indexOf(dose.getDay()));
                    setMedReminderTime(b, event, dose);
                    recurrence = "RRULE:FREQ=WEEKLY;UNTIL=";
                }

                int daysUntilEmpty = medModel.getDaysUntilEmpty();
                c.add(Calendar.DATE, daysUntilEmpty);
                String year = Integer.toString(c.get(Calendar.YEAR));
                String day = padDate(c.get(Calendar.DATE));
                String month = padDate(c.get(Calendar.MONTH) + 1);
                String recurrenceEndDate = year + month + day;

                event.setRecurrence(Collections.singletonList(recurrence + recurrenceEndDate));
                if (contact != null) {
                    event.setAttendees(Collections.singletonList(contact));
                }
                addDoseEvent(dose, event);
                c = Calendar.getInstance();
            }
        }
    }

    /**
     * Method that adds the events that are in relations to the refill reminders
     *
     * @param medModel the medication to create an event for
     */
    public void addRefillEvents(MedicationModel medModel) {


        Boolean t =  PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("refill_reminders", false);
        if(t) {
            // Obtain the date of when the med will be empty
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, medModel.getDaysUntilEmpty());

            Event emptyEvent = new Event()
                    .setDescription(medModel.getName() + " will run out of supply today.")
                    .setSummary("MedApp: " + medModel.getName() + " Empty");
            if (contact != null) {
                emptyEvent.setAttendees(Collections.singletonList(contact));
            }

            setTime(c, emptyEvent);
            addEmptyEvent(medModel, emptyEvent);

            // Set a refill reminder event to remind users to refill their medication before it becomes empty
            if (medModel.getDaysUntilEmpty() >= refillReminderDays) {
                c.add(Calendar.DATE, -refillReminderDays);
                Event refillEvent = new Event()
                        .setDescription(getRefillMsg(medModel))
                        .setSummary("MedApp: " + medModel.getName() + " Refill Reminder");
                setTime(c, refillEvent);
                if (contact != null) {
                    refillEvent.setAttendees(Collections.singletonList(contact));
                }
                addRefillReminderEvent(medModel, refillEvent);
            }
        }


    }

    /**
     * Helper method that returns the message for the Calendar refill reminder
     * @param medModel the med the refill reminder is for
     * @return String for refill reminder message
     */
    private String getRefillMsg(MedicationModel medModel) {
        return String.format(medModel.getName() + " will run out of supply in %d days. Please order a new prescription.", refillReminderDays);

    }

    /**
     * Method that adds the event to Google Calendar that acts as a warning to the user that their
     * medication supply is running low and needs to resupplied
     *
     * @param medModel the medication that the event is for
     * @param event    the event to be added to the calendar
     */
    private void addRefillReminderEvent(final MedicationModel medModel, final Event event) {
        final Event[] events = {event};
        // Can't run IO operations on UI thread, so start new thread for operation
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    events[0] = service.events().insert(CALENDAR_ID, events[0]).execute();
                    String eventID = events[0].getId();
                    databaseHelper.updateCalRefillId(medModel, eventID);
                } catch (IOException e) {
                    addRefillReminderEvent(medModel, event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    /**
     * Method that adds the event to Google Calendar that indicates when the medication will
     * run out of supply
     *
     * @param medModel the medication that the event is for
     * @param event    the event to be added to the calendar
     */
    private void addEmptyEvent(final MedicationModel medModel, final Event event) {
        final Event[] events = {event};
        // Can't run IO operations on UI thread, so start new thread for operation
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    events[0] = service.events().insert(CALENDAR_ID, events[0]).execute();
                    String eventID = events[0].getId();
                    databaseHelper.updateEmptyID(medModel, eventID);
                } catch (IOException e) {
                    addEmptyEvent(medModel, event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    /**
     * Method that adds the recurring event that reminds users to take their medication
     *
     * @param doseModel the dose of medication that is to be taken
     * @param event     the event that is to be added to the calendar
     */
    private void addDoseEvent(final DoseModel doseModel, final Event event) {
        final Event[] events = {event};
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    events[0] = service.events().insert(CALENDAR_ID, events[0]).execute();
                    String eventID = events[0].getId();
                    databaseHelper.updateDoseCalendarID(doseModel, eventID);
                } catch (IOException e) {
                    addDoseEvent(doseModel, event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * Helper function that is used to set the time of when the event is to take place
     *
     * @param calendar  - Calendar obj
     * @param event     - Event obj to set the reminder time of
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
     * Method that sets the date/time of when a given Google Calendar event is to take place
     *
     * @param cal   calendar obj that represents the date/time of when the event is to occur
     * @param event the event that is to be added to the calendar
     */
    private void setTime(Calendar cal, Event event) {
        String date = createDateString(cal);
        String time = "12:30";
        String dateTime = date + "T" + time + ":00-00:00";
        EventDateTime eventDateTime = new EventDateTime()
                .setDateTime(new DateTime(dateTime))
                .setTimeZone("Europe/London");
        event.setStart(eventDateTime);
        event.setEnd(eventDateTime);
    }

    private String createDateString(Calendar c) {
        String year = Integer.toString(c.get(Calendar.YEAR));
        String day = padDate(c.get(Calendar.DATE));
        String month = padDate(c.get(Calendar.MONTH) + 1);
        return String.format("%s-%s-%s", year, month, day);
    }

    /**
     * EventDateTime objects require the DateTime to be in YYYY-MM-DD format, so we need to pad
     * the month and day fields with a 0 if they are a single digit
     *
     * @param val the int to turn into String and pad if necessary
     * @return String that is valid for DateTime obj
     */
    private String padDate(int val) {
        String valStr = Integer.toString(val);
        if (val < 10) {
            valStr = "0" + valStr;
        }
        return valStr;
    }

    /**
     * Given a day of the week, obtain the next occurrence of that day
     *
     * @param dow Calender.{%DAY}
     * @return Calendar obj set to the next occurrence of provided day
     */
    private Calendar nextDayOfWeek(int dow) {
        Calendar date = Calendar.getInstance();
        int diff = dow - date.get(Calendar.DAY_OF_WEEK);
        if (diff < 0) {
            diff += 7;
        }
        date.add(Calendar.DAY_OF_MONTH, diff);
        return date;
    }

    /**
     * Method that obtains and sets the no. of days before empty the refill reminder will occur
     *
     * @param context the application context to retrieve selected preferences
     */
    private void setRefillReminderDays(Context context) {
        String i = PreferenceManager.getDefaultSharedPreferences(context).
                getString("reminderDay", "7");
        if (i == null) {
            this.refillReminderDays = 7;
        } else {
            this.refillReminderDays = Integer.parseInt(i);
        }
    }

}
