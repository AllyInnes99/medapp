package com.example.medapp;

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
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
        PreferenceCategory googlePreferences = findPreference("google");
        Preference googleLogin = findPreference("login");
        Preference googleSignout = findPreference("logout");
        Preference selectCarers = findPreference("carer");
        Preference doseEvents = findPreference("dose_events");
        Preference reminderEvents = findPreference("refill_reminders");
        ListPreference lp = findPreference("reminderDay");
        SwitchPreferenceCompat calendar = findPreference("calendar");

        final GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(requireContext());

        if(acct != null) {
            googlePreferences.removePreference(googleLogin);
            googleSignout.setSummary(String.format("Currently signed in as %s", acct.getDisplayName()));
            calendar.setChecked(true);
        }
        else {
            googlePreferences.removePreference(googleSignout);
            googlePreferences.removePreference(calendar);
            googlePreferences.removePreference(selectCarers);
            googlePreferences.removePreference(doseEvents);
            googlePreferences.removePreference(reminderEvents);
        }

        if(lp != null) {
            lp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if(acct != null) {
                        int prev = Integer.parseInt(sp.getString("reminderDay", ""));
                        GoogleCalendarHelper gch = new GoogleCalendarHelper(requireContext());
                        int v = Integer.parseInt(newValue.toString());
                        if(v != prev) {
                            gch.updateRefillReminderEvents(v);
                        }
                    }
                    return true;
                }
            });
        }

        if(googleLogin != null) {
            googleLogin.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(requireContext(), SignInActivity.class);
                    startActivity(i);
                    return true;
                }
            });
        }



        if(googleSignout != null) {
            googleSignout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    signOutOfGoogle();
                    return true;
                }
            });
        }

    }

    private void signOutOfGoogle() {
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