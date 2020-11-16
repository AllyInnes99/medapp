package com.example.prescriptionapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    DatabaseHelper databaseHelper;
    RecyclerView recyclerView;
    FloatingActionButton btnAdd;
    MedicationAdapter medicationAdapter;
    SearchView searchView;

    public MedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MedFragment newInstance(String param1, String param2) {
        MedFragment fragment = new MedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_med, container, false);
        btnAdd = view.findViewById(R.id.floating_add_button_med);
        recyclerView = view.findViewById(R.id.recycler_view_med);
        searchView = view.findViewById(R.id.searchView);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(getActivity(), AddMedicationActivity.class));
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                medicationAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                medicationAdapter.filter(newText);
                return true;
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
