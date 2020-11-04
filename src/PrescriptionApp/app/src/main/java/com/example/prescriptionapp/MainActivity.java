package com.example.prescriptionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton btn_add;
    RecyclerView recyclerView;
    MedicationAdapter medicationAdapter;
    DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_add = findViewById(R.id.floating_add_button);
        recyclerView = findViewById(R.id.recycler_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.action_home:
                        Toast.makeText(MainActivity.this, "Recents", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_med:
                        Toast.makeText(MainActivity.this, "Favorites", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_nearby:
                        Toast.makeText(MainActivity.this, "Nearby", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

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

    
    /**
     * Helper method used to populate the recycler view with the data from the database
     */
    private void displayRecycler() {

        List<MedicationModel> models = databaseHelper.selectAllMedication();
        medicationAdapter = new MedicationAdapter(MainActivity.this, models);
        recyclerView.setAdapter(medicationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

}
