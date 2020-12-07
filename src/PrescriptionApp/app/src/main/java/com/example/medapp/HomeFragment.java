package com.example.medapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FloatingActionButton btnAdd;
    Activity activity;
    RecyclerView recyclerView;
    ApplicationAdapter applicationAdapter;
    DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
    List<DoseModel> models;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        databaseHelper = new DatabaseHelper(getActivity());
        displayApplRecycler();
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
                startActivity(new Intent(getActivity(), AddMedicationActivity.class));
            }
        });
        
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        displayApplRecycler();
        /*
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Toast.makeText(getActivity(), FirebaseAuth.getInstance().getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
        }
        */

    }

    /**
     * Method that is used to display the rec
     */
    private void displayApplRecycler() {
        models = databaseHelper.selectTodaysDoseAndNotTaken();
        Collections.sort(models);
        applicationAdapter = new ApplicationAdapter(getActivity(), models);
        recyclerView.setAdapter(applicationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void getProfile() {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        if(acct != null) {
            String acctEmail = acct.getEmail();
            Toast.makeText(getActivity(), acctEmail, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getActivity(), "no acct", Toast.LENGTH_SHORT).show();

        }
    }


    private void updateRecycler(int pos){
        models.remove(pos);
        applicationAdapter.notifyDataSetChanged();
    }
}
