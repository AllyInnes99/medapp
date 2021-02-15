package com.example.medapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    public void onResume() {
        super.onResume();
        displayRecycler();
    }

    public void displayRecycler() {
        contacts = databaseHelper.selectAllContacts();
        carerAdapter = new CarerAdapter(context, contacts, this);
        recyclerView.setAdapter(carerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    public void addToCalendar(ContactDetails contact) {
        GoogleCalendarHelper gac = new GoogleCalendarHelper(context);
        gac.setContact(contact);
        gac.addDoseReminder(medModel);
        gac.addRefillEvents(medModel);
        medModel.setProfile(contact.getName());
        databaseHelper.updateMedication(medModel);
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Toast.makeText(context, "Successfully create medication " + medModel.getName(), Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
}