package com.example.medapp;

import android.Manifest;
import android.accounts.Account;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Credentials;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthCredential;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Button btnMed, btnRefill, btnSignOut;
    NotificationManagerCompat notificationManager;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    
    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_setting, container, false);
        btnMed = view.findViewById(R.id.btnMed);
        btnRefill = view.findViewById(R.id.btnRefill);
        btnSignOut = view.findViewById(R.id.sign_out_btn);
        notificationManager = NotificationManagerCompat.from(getActivity());


        btnMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calTest();
            }
        });

        btnRefill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    addToCalendar();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getActivity(), "Signed out.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        return view;
    }

    private void addEvent(NetHttpTransport httpTransport) {

        final String scope = "audience:server:client_id:" + "760826647786-auv3uj2k1je1dmjiohges1d846evjqm7.apps.googleusercontent.com";
        GoogleAccountCredential gac =
                GoogleAccountCredential.usingOAuth2(getActivity(), Collections.singleton(CalendarScopes.CALENDAR_EVENTS));
        gac.setSelectedAccount(GoogleSignIn.getLastSignedInAccount(getActivity()).getAccount());

        // Java has no aliasing of import names, need to use full path
        final com.google.api.services.calendar.Calendar service =
                new com.google.api.services.calendar.Calendar.Builder(
                        httpTransport, JacksonFactory.getDefaultInstance(), gac)
                        .setApplicationName(getString(R.string.app_name))
                        .build();

        final Event[] event = {new Event()
                .setSummary("THis is a test summary")
                .setLocation("Test location")
                .setDescription("Test description for this test event!")};

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

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    event[0] = service.events().insert("primary", event[0]).execute();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Pass", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                } catch (final Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e("MedApp", "exception", e);
                        }
                    });
                }
            }
        });
        t.start();


    }

    private void getCalendarID(){
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String[] projection = new String[] {
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.CALENDAR_COLOR
        };

        Cursor cursor = getActivity().getContentResolver().query(uri, projection,
                null, null, null);

        if(cursor.moveToFirst()){

            do {
                Log.d("MedApp", Integer.toString(cursor.getInt(0)));
                Log.d("MedApp", cursor.getString(1));

            } while(cursor.moveToNext());

            int a  = Integer.parseInt(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars._ID)));
            Toast.makeText(getActivity(), Integer.toString(a), Toast.LENGTH_SHORT).show();

        }
        cursor.close();

    }

    private void calTest() {
        // Construct event details
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2020, 12, 20, 7, 30);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(2020, 12, 20, 8, 45);
        endMillis = endTime.getTimeInMillis();

        // Insert Event
        ContentResolver cr = getActivity().getApplicationContext().getContentResolver();
        ContentValues values = new ContentValues();
        TimeZone timeZone = TimeZone.getDefault();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
        values.put(CalendarContract.Events.TITLE, "Walk The Dog");
        values.put(CalendarContract.Events.DESCRIPTION, "My dog is bored, so we're going on a really long walk!");
        values.put(CalendarContract.Events.CALENDAR_ID, 3);
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        // Retrieve ID for new event
        String eventID = uri.getLastPathSegment();
        Toast.makeText(getActivity(), "Added to cal", Toast.LENGTH_SHORT).show();
    }




    private void addToCalendar() throws GeneralSecurityException, IOException {

        NetHttpTransport HTTP_TRANSPORT;
        HTTP_TRANSPORT = new com.google.api.client.http.javanet.NetHttpTransport();
        addEvent(HTTP_TRANSPORT);

    }



    private void checkCalendarPermission(String... permissionsId){
        boolean permissions = true;
        for (String p : permissionsId) {
            permissions = permissions && ContextCompat.checkSelfPermission(getActivity(), p) == PERMISSION_GRANTED;
        }
        if (!permissions){
            ActivityCompat.requestPermissions(getActivity(), permissionsId, App.CALLBACK_ID);
        }
    }

    private void delDatabase(){
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
        List<DoseModel> ds = databaseHelper.selectAllDoses();
        List<MedicationModel> ms = databaseHelper.selectAllMedication();
        for(DoseModel d: ds){
            databaseHelper.deleteDose(d);
        }
        for(MedicationModel m: ms){
            databaseHelper.deleteMedication(m);
        }
        Toast.makeText(getActivity(), "Deleted all med records.", Toast.LENGTH_SHORT).show();
    }


    public void sendOnChannel1(View v){
        String title = "Take medication!";
        String msg = "Time to take medication!";

        Notification notification = new NotificationCompat.Builder(getActivity(), App.MED_TAKING_CHANNEL)
                .setSmallIcon(R.drawable.ic_healing)
                .setContentTitle(title)
                .setContentText(msg)
                .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(msg))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER).build();
        notificationManager.notify(1, notification);
    }

    public void sendOnChannel2(View v){
        String title = "Medication refill";
        String msg = "You need to restock medication!";

        Notification notification = new NotificationCompat.Builder(getActivity(), App.REFILL_CHANNEL)
                .setSmallIcon(R.drawable.ic_healing)
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER).build();
        notificationManager.notify(2, notification);
    }

    private void getCredentials() {

    }



    public void credentialTest() {

        /**
        FirebaseUser usr = FirebaseAuth.getInstance().getCurrentUser();
        String msg;
        if(usr.isAnonymous()){
            msg = "User is anonymous!";
        }
        else{
            msg = usr.getDisplayName();
        }
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
         **/

        GoogleSignInAccount usr = GoogleSignIn.getLastSignedInAccount(getActivity());
        
        String msg;
        if(usr == null){
            msg = "User is anonymous!";
        }
        else{
            msg = usr.getDisplayName();
        }

        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

    }

    private void notifTest() {
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);


        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, 10);

        Intent intent = new Intent(getActivity(), AlertReceiver.class);
        intent.setAction("android.intent.action.NOTIFY");

        intent.putExtra("quantity", 1);
        intent.putExtra("name", "test");

        // Register receiver
        getActivity().registerReceiver(new AlertReceiver(), new IntentFilter());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

}