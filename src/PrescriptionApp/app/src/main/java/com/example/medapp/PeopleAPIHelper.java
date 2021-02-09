package com.example.medapp;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.PeopleServiceScopes;
import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.ListDirectoryPeopleResponse;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;

import java.io.IOException;
import java.util.Collections;
import java.util.List;


public class PeopleAPIHelper {

    private static final String TAG = "MedApp";

    private com.google.api.services.people.v1.PeopleService service;
    private DatabaseHelper databaseHelper;
    private static final NetHttpTransport NET_HTTP_TRANSPORT =
            new com.google.api.client.http.javanet.NetHttpTransport();
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public PeopleAPIHelper(Context context) {
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                context, Collections.singleton(PeopleServiceScopes.CONTACTS_READONLY));
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(context);
        if(acct != null) {
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

    public void getContacts() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ListConnectionsResponse response = service.people().connections()
                        .list("people/me")
                        .setPersonFields("names,emailAddresses")
                        .execute();
                    List<Person> connections = response.getConnections();
                    Log.w(TAG, Integer.toString(connections.size()));
                    if(connections != null && connections.size() > 0) {
                        for(Person person: connections) {
                            List<EmailAddress> emails = person.getEmailAddresses();
                            List<Name> names = person.getNames();
                            if(names != null && emails != null && names.size() > 0) {
                                String name = names.get(0).getDisplayName();
                                String email = emails.get(0).getValue();
                                String id = person.getResourceName();
                                Log.w(TAG, name);
                                ContactDetails cd = new ContactDetails(id, name, email);
                                boolean t = databaseHelper.addContact(cd);
                                Log.w(TAG, Boolean.toString(t));

                            }
                            else {
                                Log.w(TAG, "No names available");
                            }
                        }
                    }
                    else {
                        Log.w(TAG, "No connections found");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
