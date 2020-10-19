package com.example.prescriptionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // references to controls on the layout
    FloatingActionButton btn_add;
    RecyclerView recyclerView;
    CustomAdapter customAdapter;
    DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_add = findViewById(R.id.floating_add_button);
        recyclerView = findViewById(R.id.recycler_view);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, AddMedicationActivity.class));
            }
        });

        displayRecycler();
    }


    @Override
    protected void onResume() {
        super.onResume();
        displayRecycler();

    }

    private void displayRecycler() {

        List<MedicationModel> models = databaseHelper.selectAll();
        customAdapter = new CustomAdapter(MainActivity.this, models);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

}