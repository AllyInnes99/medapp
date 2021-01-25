package com.example.medapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatFragment extends Fragment {

    Button btnMed, btnRefill, btnSignOut, btnTest;
    NotificationManagerCompat notificationManager;

    public StatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_stat, container, false);
        btnTest = view.findViewById(R.id.btn_test);
        btnMed = view.findViewById(R.id.btnMed);
        btnRefill = view.findViewById(R.id.btnRefill);
        btnSignOut = view.findViewById(R.id.sign_out_btn);
        notificationManager = NotificationManagerCompat.from(requireActivity());

        btnTest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(requireContext(), "Yes", Toast.LENGTH_SHORT).show();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(requireContext(), "No", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });



        btnMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Deleting from Google Calendar", Toast.LENGTH_SHORT).show();

                DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());
                GoogleCalendarHelper gch = new GoogleCalendarHelper(requireContext());
                for(MedicationModel m: databaseHelper.selectAllMedication()){
                    //gch.deleteMedEvents(m);
                }


            }
        });

        btnRefill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Adding to Google Calendar", Toast.LENGTH_SHORT).show();
                DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
                GoogleCalendarHelper gch = new GoogleCalendarHelper(getActivity());
                List<MedicationModel> models = databaseHelper.selectAllMedication();

                for(MedicationModel model: models){
                    gch.addRefillEvents(model);
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
