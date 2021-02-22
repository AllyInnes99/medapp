package com.example.medapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Fragment that represents the list of medications that the user has added to the app
 */
public class MedFragment extends Fragment {

    DatabaseHelper databaseHelper;
    RecyclerView recyclerView;
    FloatingActionButton btnAdd;
    MedicationAdapter medicationAdapter;
    SearchView searchView;

    public MedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle("MedApp - Medication");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_med, container, false);
        btnAdd = view.findViewById(R.id.floating_add_button_med);
        recyclerView = view.findViewById(R.id.recycler_view_med);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CreateMedicationActivity.class));
            }
        });
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        databaseHelper = new DatabaseHelper(getActivity());
        displayRecycler();
    }

    @Override
    public void onResume() {
        super.onResume();
        displayRecycler();
    }

    private void displayRecycler() {
        List<MedicationModel> models = databaseHelper.selectAllMedication();
        medicationAdapter = new MedicationAdapter(getActivity(), models);
        recyclerView.setAdapter(medicationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
