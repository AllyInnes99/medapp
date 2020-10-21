package com.example.prescriptionapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

public class UpdateActivity extends AppCompatActivity {

    EditText edit_name, edit_quantity;
    Switch edit_taken;
    Button update_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        edit_name = findViewById(R.id.edit_name);
        edit_quantity = findViewById(R.id.edit_quantity);
        edit_taken = findViewById(R.id.edit_taken);
        update_button = findViewById(R.id.update_button);

        update_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
    }

    void getIntentData() {
    }
}