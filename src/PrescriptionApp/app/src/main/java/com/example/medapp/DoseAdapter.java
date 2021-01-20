package com.example.medapp;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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

                    long hourInMillis = 3600000;
                    if(actual.getTimeInMillis() - expected.getTimeInMillis() > hourInMillis) {
                        new MaterialAlertDialogBuilder(context)
                            .setTitle("Just checking...")
                            .setMessage("This dose of " + medModel.getName() + " was set to be taken at "
                                        + doseModel.getTime() + ". Did you take it on time?")

                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(context, "Yes", Toast.LENGTH_SHORT).show();
                                    takeMed(doseModel, medModel);
                                }
                            })

                            .setNegativeButton("no", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(context, "No", Toast.LENGTH_SHORT).show();
                                    takeMed(doseModel, medModel);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    }

                    else {
                        takeMed(doseModel, medModel);
                    }
                }
            }
        });
    }

    private void takeMed(DoseModel doseModel, MedicationModel medModel) {
        databaseHelper.takeMedication(doseModel, medModel);
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
