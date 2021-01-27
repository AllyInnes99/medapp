package com.example.medapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
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




        final ListPreference lp = findPreference("reminderDay");
        final String i = lp.getValue();

        lp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                int prev = Integer.parseInt(sp.getString("reminderDay", ""));
                //Toast.makeText(requireContext(), newValue.toString(), Toast.LENGTH_SHORT).show();

                GoogleCalendarHelper gch = new GoogleCalendarHelper(requireContext());
                int v = Integer.parseInt(newValue.toString());
                if(v != prev) {
                    gch.updateRefillReminderEvents(v);
                }
                else{
                    Toast.makeText(requireContext(), "a", Toast.LENGTH_SHORT).show();
                }


                return true;
            }
        });


        final Preference googleLogin = findPreference("login");
        googleLogin.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(requireContext(), "here", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        final Preference googleSignout = findPreference("logout");
        googleSignout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Scope scope = new Scope("https://www.googleapis.com/auth/calendar.events");
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(scope)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

                FirebaseAuth.getInstance().signOut();
                mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity(),
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getActivity(), "Signed out.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), SignInActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        });

                return true;

            }
        });

    }

}