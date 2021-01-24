package com.example.medapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireContext());

        /*
        String a = sp.getString("reminderDay", "");
        Toast.makeText(requireContext(), a, Toast.LENGTH_SHORT).show();
         */
    }
}