package com.example.medapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
    int inputVal;

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

        setTitle(String.format("MedApp - Refill %s", medModel.getName()));


        et_current.setText(prevQty);
        toggleGroup.check(R.id.btn_add);

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int btnID = toggleGroup.getCheckedButtonId();
                switch (btnID) {
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
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Request Refill")
                        .setMessage("Would you like to inform MedApp that you have requested a refill? This does not update your medication quantity for you.")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                medModel.setRefillRequested(true);
                                databaseHelper.updateMedication(medModel);
                                closeActivity();
                            }
                        })
                        .setNegativeButton("no", null)
                        .show();

            }
        });

        displayRecycler();
    }

    @Override
    public void onResume() {
        super.onResume();
        displayRecycler();
    }

    /**
     * Method that is called to add to the quantity after refill
     */
    private void addToQuantity() {
        try {
            String v = et_new.getText().toString();
            if (v.equals("")) throw new NullPointerException("Empty input for new quantity.");
            inputVal = Integer.parseInt(v);
            int original = medModel.getQuantity();
            int newQuantity = original + inputVal;

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

        } catch (NullPointerException e) {
            Toast.makeText(context, "Input value is empty.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method that is called to remove from the quantity when requested
     */
    private void removeFromQuantity() {
        try {
            String v = et_new.getText().toString();
            if (v.equals("")) throw new NullPointerException("Empty input for new quantity.");
            inputVal = Integer.parseInt(v);
            int original = medModel.getQuantity();
            int newQuantity = original - inputVal;
            if (newQuantity < 0) {
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
        } catch (NullPointerException e) {
            Toast.makeText(context, "Input value is empty.", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Method that updates the events in the user's Google Calendar upon update of quantity
     */
    private void updateGoogleCal() {
        medModel = databaseHelper.selectMedicationFromID(medModel.getMedicationId());
        GoogleCalendarHelper gch = new GoogleCalendarHelper(context);
        gch.updateMedEvents(medModel);
    }


    /**
     * Helper method that closes the activity and returns to the main activity
     */
    private void closeActivity() {
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    /**
     * Method that adds a refill log event to the database
     * @param refill - the new quantity after refill
     * @param original - the original val before refill
     */
    private void addRefillLog(int refill, int original) {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR);
        RefillData data = new RefillData(medModel.getMedicationId(), day, month, year, refill, original);
        databaseHelper.addRefill(data);
    }

    /**
     * Method that displays the med refill logs
     */
    public void displayRecycler() {
        data = databaseHelper.selectRefillFromMed(medModel);
        refillAdapter = new RefillAdapter(context, data);
        recyclerView.setAdapter(refillAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

}