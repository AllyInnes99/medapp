package com.example.medapp;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Adapter that represents the behaviour of a medication log row
 */
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
        holder.log_med.setText(String.format("Med: %s", medModel.getName()));
        holder.log_amount.setText(String.format("Amount to be taken: %s", log.getAmount()));
        holder.log_on_time.setText(String.format("On time: %s", getOnTimeString(log)));
        holder.log_taken.setText(String.format("Taken: %s", getTakenString(log)));
        DateFormat format = SimpleDateFormat.getDateInstance();
        holder.log_date.setText(String.format("%s", format.format(log.getTime())));

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
                        .setIcon(android.R.drawable.ic_input_add)
                        .show();
                }
                else {
                    new MaterialAlertDialogBuilder(context)
                            .setTitle("Log Entry")
                            .setMessage(log.getMsg())
                            .setPositiveButton("ok", null)
                            .setIcon(android.R.drawable.ic_dialog_info)
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

        TextView log_med, log_amount, log_taken, log_on_time, log_date;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            log_med = itemView.findViewById(R.id.log_med);
            log_amount = itemView.findViewById(R.id.log_amount);
            log_taken = itemView.findViewById(R.id.log_taken);
            log_on_time = itemView.findViewById(R.id.log_on_time);
            log_date = itemView.findViewById(R.id.log_date);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }

    private void updateMissedDose(MedicationLog log, MedicationModel medModel) {
        medModel.setQuantity(medModel.getQuantity() - log.getAmount());
        databaseHelper.updateMedication(medModel);

        log.setTaken(true);
        log.setMsg("Medication has been updated to indicate that it has been taken on time");
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
