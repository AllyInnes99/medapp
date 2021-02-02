package com.example.medapp;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.MyViewHolder> {

    private Context context;
    private List<MedicationLog> logs;
    private DatabaseHelper databaseHelper;
    private StatFragment fragment;

    LogAdapter(Context context, List<MedicationLog> logs, StatFragment fragment) {
        this.context = context;
        this.logs = logs;
        this.fragment = fragment;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public LogAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.log_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogAdapter.MyViewHolder holder, int position) {
        final MedicationLog log = logs.get(position);
        final MedicationModel medModel = databaseHelper.selectMedicationFromID(log.getMedicationId());
        Toast.makeText(context, Integer.toString(log.getAmount()), Toast.LENGTH_SHORT).show();

        holder.log_med.setText(String.format("Med: %s", medModel.getName()));
        holder.log_amount.setText(String.format("Amount to be taken: %s", log.getAmount()));
        holder.log_on_time.setText(String.format("On time: %s", getOnTimeString(log)));
        holder.log_taken.setText(String.format("Taken: %s", getTakenString(log)));


        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!log.isTaken()) {
                    new MaterialAlertDialogBuilder(context)
                        .setTitle("Register medication as taken")
                        .setMessage("You missed this dose. Would you like to register it as taken?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateMissedDose(log, medModel);
                            }
                        })
                        .setNegativeButton("no", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "No", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView log_med, log_amount, log_taken, log_on_time;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            log_med = itemView.findViewById(R.id.log_med);
            log_amount = itemView.findViewById(R.id.log_amount);
            log_taken = itemView.findViewById(R.id.log_taken);
            log_on_time = itemView.findViewById(R.id.log_on_time);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }

    private void updateMissedDose(MedicationLog log, MedicationModel medModel) {
        medModel.setQuantity(medModel.getQuantity() - log.getAmount());
        databaseHelper.updateMedication(medModel);

        log.setTaken(true);
        databaseHelper.updateLog(log);

        fragment.displayRecycler();

    }


    private String getTakenString(MedicationLog log) {
        return log.isTaken() ? "Yes" : "No";
    }

    private String getOnTimeString(MedicationLog log) {
        return log.isOnTime() ? "Yes" : "No";
    }

}