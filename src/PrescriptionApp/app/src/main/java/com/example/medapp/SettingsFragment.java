package com.example.medapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that displays the shared preferences options to the user
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        requireActivity().setTitle("MedApp - Settings");
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
        PreferenceCategory googlePreferences = findPreference("google");
        Preference googleLogin = findPreference("login");
        Preference googleSignout = findPreference("logout");
        final SwitchPreferenceCompat doseEvents = findPreference("dose_events");
        final SwitchPreferenceCompat reminderEvents = findPreference("refill_reminders");
        ListPreference lp = findPreference("reminderDay");
        final DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());
        final GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(requireContext());

        if (acct != null) {
            googlePreferences.removePreference(googleLogin);
            googleSignout.setSummary(String.format("Currently signed in as %s", acct.getDisplayName()));
        } else {
            googlePreferences.removePreference(googleSignout);
            googlePreferences.removePreference(doseEvents);
            googlePreferences.removePreference(reminderEvents);
        }

        if (lp != null) {
            lp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (acct != null) {
                        int prev = Integer.parseInt(sp.getString("reminderDay", ""));
                        GoogleCalendarHelper gch = new GoogleCalendarHelper(requireContext());
                        int v = Integer.parseInt(newValue.toString());
                        if (v != prev) {
                            gch.updateRefillReminderEvents(v);
                        }
                    }
                    return true;
                }
            });
        }

        if (googleLogin != null) {
            googleLogin.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(requireContext(), SignInActivity.class);
                    startActivity(i);
                    return true;
                }
            });
            googleSignout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    signOutOfGoogle();
                    return true;
                }
            });
        }

        doseEvents.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                GoogleCalendarHelper gch = new GoogleCalendarHelper(requireContext());
                List<MedicationModel> meds = databaseHelper.selectAllMedication();
                if(doseEvents.isChecked()) {
                    for(MedicationModel med: meds) {
                        gch.addDoseReminder(med);
                    }
                }
                else {
                    for(MedicationModel med: meds) {
                        List<DoseModel> doses = databaseHelper.selectDoseFromMedication(med);
                        for(DoseModel dose: doses) {
                            gch.deleteDoseEvent(dose);
                        }
                    }
                }

                return true;
            }
        });

        reminderEvents.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                GoogleCalendarHelper gch = new GoogleCalendarHelper(requireContext());
                List<MedicationModel> meds = databaseHelper.selectAllMedication();
                if(reminderEvents.isChecked()) {
                    for(MedicationModel med: meds) {
                        gch.addRefillEvents(med);
                    }
                }
                else {
                    for(MedicationModel med: meds) {
                        gch.deleteRefillEvent(med);
                        gch.deleteEmptyEvent(med);
                    }
                }

                return true;
            }
        });

    }

    /**
     * Method that signs the user out of their Google Account
     */
    private void signOutOfGoogle() {

        // Before signing out, delete med events from Google Calendar
        GoogleCalendarHelper gch = new GoogleCalendarHelper(requireContext());
        DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());
        List<MedicationModel> meds = databaseHelper.selectAllMedication();
        for(MedicationModel med: meds) {
            List<DoseModel> medDoses = databaseHelper.selectDoseFromMedication(med);
            gch.deleteMedEvents(med, medDoses);
        }


        Scope scope = new Scope("https://www.googleapis.com/auth/calendar.events");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(scope)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity(),
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "Signed out.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
    }


}