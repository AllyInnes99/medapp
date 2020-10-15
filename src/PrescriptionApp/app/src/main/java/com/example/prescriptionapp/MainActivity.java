package com.example.prescriptionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // references to controls on the layout
    ListView listView;
    FloatingActionButton btn_add;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.list_view);
        btn_add = findViewById(R.id.floating_add_button);
        button = findViewById(R.id.button);
        List<MedicationModel> models;

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, AddMedicationActivity.class));
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                displayMedication();
            }
        });
    }

    private void displayMedication() {
        DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
        List<MedicationModel> models = databaseHelper.selectAll();
        Toast.makeText(MainActivity.this, models.toString(), Toast.LENGTH_SHORT ).show();

    }


}