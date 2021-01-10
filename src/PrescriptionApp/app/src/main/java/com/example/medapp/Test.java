package com.example.medapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

public class Test extends AppCompatActivity {

    TextInputEditText et_name, et_quantity, et_strength;
    AutoCompleteTextView dropdown_measurement, dropdown_type;
    Button submit_btn;
    RadioButton radio1, radio2;
    SwitchMaterial autoTake;

    final String[] medTypes = new String[] {"tablet", "pill", "injection", "powder",
            "drops", "inhalers", "topical"};
    final String[] measurements = new String[] {"g", "mg", "ml", "l"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        et_name = findViewById(R.id.et_name);
        et_quantity = findViewById(R.id.et_quantity);
        et_strength = findViewById(R.id.et_strength);
        dropdown_measurement = findViewById(R.id.dropdown_measurement);
        dropdown_type = findViewById(R.id.dropdown_type);
        submit_btn = findViewById(R.id.submit_btn);
        radio1 = findViewById(R.id.radio_button_1);
        radio2 = findViewById(R.id.radio_button_2);
        autoTake = findViewById(R.id.autotake);



        ArrayAdapter<String> measurementAdapter =
                new ArrayAdapter<>(Test.this, R.layout.list_item, measurements);
        dropdown_measurement.setAdapter(measurementAdapter);

        ArrayAdapter<String> typeAdapter =
                new ArrayAdapter<String>(Test.this, R.layout.list_item, medTypes);
        dropdown_type.setAdapter(typeAdapter);


        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Test.this, dropdown_measurement.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}