package com.example.prescriptionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // references to controls on the layout
    ListView listView;
    FloatingActionButton btn_add;
    CustomAdapter customAdapter;
    DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_add = findViewById(R.id.floating_add_button);
        listView = findViewById(R.id.listView);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, AddMedicationActivity.class));
            }
        });

        display();
    }


    @Override
    protected void onResume() {
        super.onResume();
        display();

    }

    private void display() {
        List<MedicationModel> models = databaseHelper.selectAll();
        ArrayAdapter medicationArrayAdapter = new ArrayAdapter<MedicationModel>(MainActivity.this, android.R.layout.simple_list_item_1, models);
        listView.setAdapter(medicationArrayAdapter);
    }
}