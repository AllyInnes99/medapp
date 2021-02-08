package com.example.medapp;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
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
    private static final NetHttpTransport NET_HTTP_TRANSPORT =
            new com.google.api.client.http.javanet.NetHttpTransport();
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public PeopleAPIHelper(Context context) {
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                context, Collections.singleton(PeopleServiceScopes.CONTACTS_READONLY));
        credential.setSelectedAccount(GoogleSignIn.getLastSignedInAccount(context).getAccount());
        this.service = new PeopleService.Builder(
                NET_HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(context.getString(R.string.app_name))
                .build();
    }

    public PeopleService getService() {
        return service;
    }

    public void setService(PeopleService service) {
        this.service = service;
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
                    if(connections != null && connections.size() > 0) {
                        for(Person person: connections) {

                            String t = person.toPrettyString();
                            t  = person.getEtag();


                            Log.w(TAG, t);
                            List<EmailAddress> emails = person.getEmailAddresses();
                            List<Name> names = person.getNames();

                            if(names != null && names.size() > 0) {
                                String name = names.get(0).getDisplayName();
                                //String email = emails.get(0).getDisplayName();
                                Log.w(TAG, "name: " + name);

                                for(EmailAddress email: emails) {
                                    String e = email.getValue();
                                    Log.w(TAG, "email: " + e);
                                }
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
