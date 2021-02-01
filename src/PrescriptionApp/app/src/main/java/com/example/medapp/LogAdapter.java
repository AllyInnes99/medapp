package com.example.medapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.MyViewHolder> {

    private Context context;
    private List<MedicationLog> logs;
    private DatabaseHelper databaseHelper;

    LogAdapter(Context context, List<MedicationLog> logs) {
        this.context = context;
        this.logs = logs;
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
        MedicationLog log = logs.get(position);
        MedicationModel medModel = databaseHelper.selectMedicationFromID(log.getMedicationId());
        holder.log_med.setText(String.format("Med: %s", medModel.getName()));
        holder.log_amount.setText(String.format("Amount to be taken: %s", log.getAmount()));
        holder.log_on_time.setText(String.format("On time: %s", getOnTimeString(log)));
        holder.log_taken.setText(String.format("Taken: %s", getTakenString(log)));
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView log_med, log_amount, log_taken, log_on_time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            log_med = itemView.findViewById(R.id.log_med);
            log_amount = itemView.findViewById(R.id.log_amount);
            log_taken = itemView.findViewById(R.id.log_taken);
            log_on_time = itemView.findViewById(R.id.log_on_time);
            Toast.makeText(context, "hi", Toast.LENGTH_SHORT).show();
        }
    }

    private String getTakenString(MedicationLog log) {
        return log.isTaken() ? "Yes" : "No";
    }

    private String getOnTimeString(MedicationLog log) {
        return log.isOnTime() ? "Yes" : "No";
    }

}
