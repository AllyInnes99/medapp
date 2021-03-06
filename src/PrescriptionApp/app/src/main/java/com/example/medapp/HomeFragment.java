package com.example.medapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Home page fragment, where the user can see the doses that they need to take in a given day
 */
public class HomeFragment extends Fragment {

    FloatingActionButton btnAdd;
    RecyclerView recyclerView;
    DoseAdapter applicationAdapter;
    DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
    List<DoseModel> models;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle("MedApp - Home");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        databaseHelper = new DatabaseHelper(getActivity());
        displayRecycler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        btnAdd = view.findViewById(R.id.floating_add_button_home);
        recyclerView = view.findViewById(R.id.recycler_view_home);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(getActivity(), CreateMedicationActivity.class));
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        displayRecycler();
    }

    /**
     * Method that is used to display the rec
     */
    public void displayRecycler() {
        models = databaseHelper.selectTodaysDoseAndNotTaken();
        List<DoseModel> filtered = new ArrayList<>();
        for(DoseModel dm: models) {
            MedicationModel med = databaseHelper.selectMedicationFromID(dm.getMedicationId());
            if(!med.isAutoTake()){
                filtered.add(dm);
            }
        }

        Collections.sort(filtered);
        applicationAdapter = new DoseAdapter(getActivity(), filtered, this);
        recyclerView.setAdapter(applicationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
