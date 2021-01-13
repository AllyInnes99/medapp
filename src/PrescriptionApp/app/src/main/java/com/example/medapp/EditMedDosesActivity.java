package com.example.medapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditMedDosesActivity extends AppCompatActivity {

    MedicationModel medModel;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton, nextButton;
    AddDoseAdapter applicationAdapter;
    List<String> days = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday",
                                        "Friday", "Saturday", "Sunday");
    Context context = EditMedDosesActivity.this;
    DatabaseHelper databaseHelper = new DatabaseHelper(context);
    List<AddDoseModel> tempModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_med_doses);

        int id = getIntent().getIntExtra("medID", 0);
        MedicationModel medModel = databaseHelper.selectMedicationFromID(id);
        List<DoseModel> doseModels = databaseHelper.selectDoseFromMedication(medModel);
        tempModels = new ArrayList<>();
        for(DoseModel dm: doseModels) {

            boolean flag = true;

            AddDoseModel addDoseModel = new AddDoseModel(dm.getTime(), dm.getAmount());
            if(dm.getDay().equals("Daily")){
                if(flag)
                addDoseModel.setDays(days);
            }
            else{
                flag = false;

            }

        }

    }
}