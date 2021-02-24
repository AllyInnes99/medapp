package com.example.medapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.PeopleServiceScopes;
import com.google.api.services.people.v1.model.ContactGroup;
import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.ListContactGroupsResponse;
import com.google.api.services.people.v1.model.ListDirectoryPeopleResponse;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;
import com.google.api.services.people.v1.model.Relation;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Class that is used as a helper to make People API requests
 */
public class PeopleAPIHelper {

    private static final String TAG = "MedApp";
    public static final String PATIENT = "patient";
    private com.google.api.services.people.v1.PeopleService service;
    private DatabaseHelper databaseHelper;
    private static final NetHttpTransport NET_HTTP_TRANSPORT =
            new com.google.api.client.http.javanet.NetHttpTransport();
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public PeopleAPIHelper(Context context) {
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                context, Collections.singleton(PeopleServiceScopes.CONTACTS_READONLY));
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(context);
        if (acct != null) {
            credential.setSelectedAccount(acct.getAccount());
            this.service = new PeopleService.Builder(
                    NET_HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(context.getString(R.string.app_name))
                    .build();
            this.databaseHelper = new DatabaseHelper(context);
        }
    }

    public PeopleService getService() {
        return service;
    }

    /**
     * Method that calls the People API and receives the authenticated user's contacts, and then adds them
     * to the app's database if they are listed as a patient
     */
    public void getContacts() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ListConnectionsResponse response = service.people().connections()
                            .list("people/me")
                            .setPersonFields("names,emailAddresses,relations")
                            .execute();

                    if (response != null && response.size() > 0) {
                        checkConnections(response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Method that goes through the response of the API call and checks the user's connections in
     * an attempt to find patients
     *
     * @param response data returned by API call
     */
    private void checkConnections(ListConnectionsResponse response) {
        List<Person> connections = response.getConnections();
        for (Person person : connections) {
            List<EmailAddress> emails = person.getEmailAddresses();
            List<Name> names = person.getNames();
            List<Relation> relations = person.getRelations();
            if (names != null && emails != null && relations != null && names.size() > 0) {
                Relation relation = relations.get(0);
                if (isPatient(relation)) {
                    addContact(person, names.get(0).getDisplayName(), emails.get(0).getValue());
                }
            }
        }
    }

    /**
     * From a person in the user's contacts, add the contact to the database
     *
     * @param person the person to add
     * @param name   the person's name
     * @param email  the person's email address
     */
    private void addContact(Person person, String name, String email) {
        ContactDetails cd = new ContactDetails(person.getResourceName(), name, email);
        databaseHelper.addContact(cd);
    }

    /**
     * Method that checks if the relation of the contact is a patient
     *
     * @param relation the relation to the user
     * @return true if relation is a patient, false otherwise
     */
    private boolean isPatient(Relation relation) {
        return relation.getPerson().toLowerCase(Locale.ROOT).equals(PATIENT);
    }
}
