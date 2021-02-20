package com.example.medapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Activity where the user selects a patient for the medication
 */
public class CarerActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CarerAdapter carerAdapter;
    DatabaseHelper databaseHelper;
    Context context;
    List<ContactDetails> contacts;
    MedicationModel medModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carer);
        context = CarerActivity.this;
        databaseHelper = new DatabaseHelper(context);

        int medId = getIntent().getIntExtra("medId", 0);
        medModel = databaseHelper.selectMedicationFromID(medId);
        recyclerView = findViewById(R.id.recyclerView);
        displayRecycler();
    }

    @Override
    public void onBackPressed() {
        GoogleCalendarHelper gac = new GoogleCalendarHelper(context);
        gac.addDoseReminder(medModel);
        gac.addRefillEvents(medModel);
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Toast.makeText(context, "Successfully created medication " + medModel.getName(), Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        displayRecycler();
    }

    /**
     * Method that displays the available contacts to the recyclerview
     */
    public void displayRecycler() {
        contacts = databaseHelper.selectAllContacts();
        carerAdapter = new CarerAdapter(context, contacts, this);
        recyclerView.setAdapter(carerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    /**
     * Method that adds the medication to the calendar, including the patient
     *
     * @param contact the patient who the mediciation is for
     */
    public void addToCalendar(ContactDetails contact) {
        GoogleCalendarHelper gac = new GoogleCalendarHelper(context);
        gac.setContact(contact);
        gac.addDoseReminder(medModel);
        gac.addRefillEvents(medModel);
        medModel.setProfile(contact.getName());
        databaseHelper.updateMedication(medModel);
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Toast.makeText(context, "Successfully created medication " + medModel.getName(), Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
}
