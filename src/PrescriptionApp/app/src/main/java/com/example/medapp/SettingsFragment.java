package com.example.medapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        Toast.makeText(requireContext(), "here", Toast.LENGTH_SHORT).show();
    }
}