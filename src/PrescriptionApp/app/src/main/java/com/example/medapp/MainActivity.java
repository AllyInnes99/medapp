package com.example.medapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;


import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    BottomNavigationView bottomNavigationView;
    NavController navController;
    View parentLayout;
    public static final int GOOGLE_SIGN_IN = 100;
    private FirebaseAuth mAuth;

    private static final int DAILY_EVENT_ID = 1;
    private static final int WEEKLY_EVENT_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parentLayout = findViewById(android.R.id.content);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        navController = Navigation.findNavController(this, R.id.navFragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        mAuth = FirebaseAuth.getInstance();
        setDailyEventAlarm();
        setRefreshAlarm();

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
        if(acct != null) {
            PeopleAPIHelper peopleApi = new PeopleAPIHelper(MainActivity.this);
            //Toast.makeText(MainActivity.this, Boolean.toString(peopleApi.getService() == null), Toast.LENGTH_LONG).show();
            peopleApi.getContacts();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                //Log.w(TAG, "Google sign in failed", e);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setMedicationAlarm(){
        // get every application in the db
        DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
        List<DoseModel> applications = databaseHelper.selectAllDoses();
        for(DoseModel doseModel : applications){
            setApplicationAlarm(doseModel);
        }
    }

    /**
     * Method that creates a reminder notification for user to take certain medication
     * @param model - the application to be notified
     */
    private void setApplicationAlarm(DoseModel model){
        Calendar c = Calendar.getInstance();
        String[] time = model.timeToHourAndMin();
        int hour = Integer.parseInt(time[0]);
        int mins = Integer.parseInt(time[1]);

        // Set calendar to represent the day
        c.set(Calendar.DAY_OF_WEEK, model.dayToInt());
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, mins);


        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.setAction("android.intent.action.NOTIFY");

        // Register receiver
        MainActivity.this.registerReceiver(new AlertReceiver(), new IntentFilter());

        intent.putExtra("MyModel", model);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, model.getDoseId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
    }

    /**
     * Helper function that is called on creation to start the daily cycle of "automatically" taking
     * medication that the user wishes to do so.
     */
    private void setDailyEventAlarm() {
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        // set calendar to begin at midnight the next day
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DATE, 1);

        // Setup intent to pass to receiver
        Intent intent = new Intent(MainActivity.this, DailyEventReceiver.class);
        intent.setAction("android.intent.action.NOTIFY");

        // Register the custom broadcast receiver
        //MainActivity.this.registerReceiver(new DailyEventReceiver(), new IntentFilter());

        // Set up pendingIntent for the broadcast to specify action in the future
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, DAILY_EVENT_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set repeating alarm that calls onReceive() of AutoTakeReceiver at supplied time
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }


    /**
     * Helper function that is called on creation to start the refresh cycle for medication
     * I.e., isTaken in each application will be set to false in every dose in db at the start of
     * a new week
     */
    private void setRefreshAlarm() {
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        // Setup calendar obj so that it is set to the coming Monday at 00:00
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        // Setup intent to pass to receiver
        Intent intent = new Intent(MainActivity.this, RefreshReceiver.class);
        intent.setAction("android.intent.action.NOTIFY");

        // Register the custom broadcast receiver
        //MainActivity.this.registerReceiver(new RefreshReceiver(), new IntentFilter());

        // Set up pendingIntent for the broadcast to specify action in the future
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, WEEKLY_EVENT_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set repeating alarm that calls onReceive() of RefreshReceiver at supplied time
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(MainActivity.this, "Authentication success", Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(SignInActivity.this, MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
