package com.example.medapp;

import android.Manifest;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
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
                notifTest();
            }
        });

        btnRefill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delDatabase();
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

    private int getCalendarID(){
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String[] projection = new String[] {
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.CALENDAR_COLOR
        };

        Cursor cursor = getActivity().getContentResolver().query(uri, projection,
                                                        null, null, null);

        if(cursor.moveToFirst()){

            int a  = Integer.parseInt(cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars._ID)));
            Toast.makeText(getActivity(), Integer.toString(a), Toast.LENGTH_SHORT).show();
            return a;
        }
        else{
            return -1;
        }


    }

    private void addToCalendar(){
        // req permissions
        checkCalendarPermission(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);


        Calendar c1 = Calendar.getInstance();
        c1.set(2020, 12, 25, 9, 0);

        Calendar c2 = Calendar.getInstance();
        c2.set(2020, 12, 25, 10, 0);

        ContentResolver cr = getActivity().getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put(CalendarContract.Events.DTSTART, c1.getTimeInMillis());
        cv.put(CalendarContract.Events.DTEND, c2.getTimeInMillis());
        cv.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        cv.put(CalendarContract.Events.TITLE, "Test Title");
        cv.put(CalendarContract.Events.DESCRIPTION, "Test description");
        cv.put(CalendarContract.Events.CALENDAR_ID, 3);
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, cv);

        String eventId = uri.getLastPathSegment();
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

    public void credentialTest() {
        FirebaseUser usr = FirebaseAuth.getInstance().getCurrentUser();
        String msg;
        if(usr.isAnonymous()){
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