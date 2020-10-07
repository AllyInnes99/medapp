package com.example.prescriptionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // references to controls on the layout
    Button btn_add, btn_view;
    EditText et_name, et_quantity;
    Switch sw_activeTaken;
    ListView listView_medicationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_add = findViewById(R.id.btn_add);
        btn_view = findViewById(R.id.btn_view);
        et_name =  (EditText) findViewById(R.id.et_name);
        et_quantity = findViewById(R.id.et_quantity);
        sw_activeTaken = findViewById(R.id.sw_activeTaken);
        listView_medicationList = findViewById(R.id.listview_medicationList);

        btn_add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                try {
                    MedicationModel medicationModel = new MedicationModel(0, et_name.getText().toString(), Integer.parseInt(et_quantity.getText().toString()), sw_activeTaken.isChecked());
                    Toast.makeText(MainActivity.this, medicationModel.toString(), Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    Toast.makeText(MainActivity.this, "Error creating customer", Toast.LENGTH_SHORT);
                }

            }
        });

        btn_view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(MainActivity.this, "View Button", Toast.LENGTH_SHORT).show();

            }
        });
    }

}