package com.example.medapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;

/**
 * Activity where the user can update their medication supply and indicate that they have
 * requested a refill
 */
public class MedRefill extends AppCompatActivity {


    Button btn_add, btn_remove, btn_request, btn_update;
    MaterialButtonToggleGroup toggleGroup;
    TextInputEditText et_current, et_new;
    Context context;
    DatabaseHelper databaseHelper;
    MedicationModel medModel;
    RecyclerView recyclerView;
    List<RefillData> data;
    RefillAdapter refillAdapter;
    int prevQty, inputVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_refill);
        context = MedRefill.this;
        databaseHelper = new DatabaseHelper(context);


        btn_add = findViewById(R.id.btn_add);
        btn_remove = findViewById(R.id.btn_remove);
        btn_request = findViewById(R.id.btn_request);
        btn_update = findViewById(R.id.btn_update);
        toggleGroup = findViewById(R.id.toggleGroup);
        et_current = findViewById(R.id.et_current);
        et_new = findViewById(R.id.et_new);
        recyclerView = findViewById(R.id.recycler_view);

        int medID = getIntent().getIntExtra("medID", 0);
        medModel = databaseHelper.selectMedicationFromID(medID);
        String prevQty = Integer.toString(medModel.getQuantity());

        et_current.setText(prevQty);
        toggleGroup.check(R.id.btn_add);

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int btnID = toggleGroup.getCheckedButtonId();
                switch(btnID) {
                    case R.id.btn_add:
                        addToQuantity();
                        break;
                    case R.id.btn_remove:
                        removeFromQuantity();
                        break;
                    default:
                        Toast.makeText(context, "Please select a toggle option", Toast.LENGTH_SHORT).show();
                }
            }
        });



        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medModel.setRefillRequested(true);
                databaseHelper.updateMedication(medModel);
                closeActivity();
            }
        });

        displayRecycler();
    }

    @Override
    public void onResume(){
        super.onResume();
        displayRecycler();
    }


    private void addToQuantity() {
        try {
            String v = et_new.getText().toString();
            if(v.equals("")) throw new NullPointerException("Empty input for new quantity.");
            inputVal = Integer.parseInt(v);
            int original = medModel.getQuantity();
            int newQuantity = original + inputVal;
            Toast.makeText(context, Integer.toString(newQuantity), Toast.LENGTH_LONG).show();


            medModel.setQuantity(newQuantity);
            medModel.setRefillRequested(false);
            databaseHelper.updateMedication(medModel);
            databaseHelper.updateDaysUntilEmpty(medModel);

            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(MedRefill.this);
            if (acct != null && medModel.getCalendarRefill() != null) {
                updateGoogleCal();
            }

            Toast.makeText(context, "Updated quantity of medication", Toast.LENGTH_SHORT).show();
            addRefillLog(newQuantity, original);
            closeActivity();

        }
        catch (NullPointerException e) {
            Toast.makeText(context, "Input value is empty.", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeFromQuantity() {
        try {
            String v = et_new.getText().toString();
            if(v.equals("")) throw new NullPointerException("Empty input for new quantity.");
            inputVal = Integer.parseInt(v);
            int original = medModel.getQuantity();
            int newQuantity = original - inputVal;
            if(newQuantity < 0){
                newQuantity = 0;
            }
            medModel.setQuantity(newQuantity);
            databaseHelper.updateMedication(medModel);
            databaseHelper.updateDaysUntilEmpty(medModel);
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(MedRefill.this);
            if (acct != null && medModel.getCalendarRefill() != null) {
                updateGoogleCal();
            }
            Toast.makeText(context, "Updated quantity of medication", Toast.LENGTH_SHORT).show();
            addRefillLog(newQuantity, original);
            closeActivity();
        }
        catch (NullPointerException e) {
            Toast.makeText(context, "Input value is empty.", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateGoogleCal() {
        medModel = databaseHelper.selectMedicationFromID(medModel.getMedicationId());
        GoogleCalendarHelper gch = new GoogleCalendarHelper(context);
        gch.updateMedEvents(medModel);
    }


    private void closeActivity() {
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    private void addRefillLog(int refill, int original) {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR);
        RefillData data = new RefillData(medModel.getMedicationId(), day, month, year, refill, original);
        boolean t = databaseHelper.addRefill(data);
        Toast.makeText(context, Boolean.toString(t), Toast.LENGTH_SHORT).show();
    }

    public void displayRecycler() {
        data = databaseHelper.selectRefillFromMed(medModel);
        //Collections.sort(data);
        refillAdapter = new RefillAdapter(context, data);
        recyclerView.setAdapter(refillAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

}