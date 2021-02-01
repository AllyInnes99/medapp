package com.example.medapp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.joda.time.Period;
import java.util.Calendar;
import java.util.List;

public class DoseAdapter extends RecyclerView.Adapter<DoseAdapter.MyViewHolder> {

    private Context context;
    private List<DoseModel> doseModels;
    DatabaseHelper databaseHelper;
    HomeFragment fragment;

    DoseAdapter(Context context, List<DoseModel> doseModels, HomeFragment fragment) {
        this.context = context;
        this.doseModels = doseModels;
        this.fragment = fragment;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.application_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final DoseModel doseModel = doseModels.get(position);
        final MedicationModel medModel = databaseHelper.selectMedicationFromDose(doseModel);
        holder.appl_amount_txt.setText("Amount to take: " + doseModel.getAmount());
        holder.appl_time_txt.setText("Time: " + doseModel.getTime());
        holder.appl_med_txt.setText("Name: " + medModel.getName());

        holder.button.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if(doseModel.getAmount() > medModel.getQuantity()){
                    String msg = "You don't have enough stock to take this medication.";
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
                else{

                    Calendar actual = Calendar.getInstance();
                    Calendar expected = Calendar.getInstance();
                    String[] time = doseModel.getTime().split(":");
                    expected.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
                    expected.set(Calendar.MINUTE, Integer.parseInt(time[1]));

                    Period period = new Period(actual.getTimeInMillis(), expected.getTimeInMillis());
                    int diff = period.getMinutes();

                    if(diff > 60) {
                        medTakenTooLate(medModel, doseModel);
                    }
                    else if(diff < -60) {
                        medTakenTooEarly(medModel, doseModel);
                    }
                    else {
                        registerMedAsTaken(doseModel, medModel, true);
                    }

                }
            }
        });
    }

    /**
     * Method that defines the behaviour of when a medication is taken too late
     * @param medModel the medication that is to be taken
     * @param doseModel the dose of the medication that is to be taken
     */
    private void medTakenTooLate(final MedicationModel medModel, final DoseModel doseModel) {
        new MaterialAlertDialogBuilder(context)
                .setTitle("Just checking...")
                .setMessage("This dose of " + medModel.getName() + " was set to be taken at "
                        + doseModel.getTime() + ". Did you take it on time?")

                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "Yes", Toast.LENGTH_SHORT).show();
                        registerMedAsTaken(doseModel, medModel, true);
                    }
                })

                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "No", Toast.LENGTH_SHORT).show();
                        registerMedAsTaken(doseModel, medModel, false);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Method that defines the behaviour of when a medication is taken too early
     * @param medModel the medication that is to be taken
     * @param doseModel the dose of the medication that is to be taken
     */
    private void medTakenTooEarly(final MedicationModel medModel, final DoseModel doseModel) {
        new MaterialAlertDialogBuilder(context)
                .setTitle("Just checking...")
                .setMessage("This dose of " + medModel.getName() + " is set to be taken at "
                        + doseModel.getTime() + ". Are you sure you want to take it now?")

                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "Yes", Toast.LENGTH_SHORT).show();
                        registerMedAsTaken(doseModel, medModel, false);
                    }
                })

                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "You did not take the medication.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Method that makes changes to the database so that the provided medication dose is listed as taken
     * @param doseModel the dose of the medication to be taken
     * @param medModel the medication that is to be taken
     */
    private void registerMedAsTaken(DoseModel doseModel, MedicationModel medModel, boolean onTime) {
        databaseHelper.takeMedication(doseModel, medModel, onTime);
        String msg = "You have taken " + doseModel.getAmount() + " of " + medModel.getName();
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        fragment.displayApplRecycler();
    }


    @Override
    public int getItemCount() {
        return doseModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView appl_med_txt, appl_amount_txt, appl_time_txt;
        Button button;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            appl_med_txt = itemView.findViewById(R.id.appl_med_txt);
            appl_amount_txt = itemView.findViewById(R.id.appl_amount_txt);
            appl_time_txt = itemView.findViewById(R.id.appl_time_txt);
            button = itemView.findViewById(R.id.buttonTake);
        }
    }
}
