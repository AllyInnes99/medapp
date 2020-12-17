package com.example.medapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AddWeeklyAdapter extends RecyclerView.Adapter<AddWeeklyAdapter.MyViewHolder> {

    private Context context;
    private List<DoseModel> doseModels;
    DatabaseHelper databaseHelper;

    AddWeeklyAdapter(Context context, List<DoseModel> doseModels) {
        this.context = context;
        this.doseModels = doseModels;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.weekly_application_row, parent, false);
        return new AddWeeklyAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DoseModel model = doseModels.get(position);
        holder.appl_amount_txt.setText("Amount to take: " + model.getAmount());
        holder.appl_time_txt.setText("Time: " + model.getTime());
        holder.appl_day_txt.setText(model.getDay());
    }

    @Override
    public int getItemCount() {
        return doseModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView appl_amount_txt, appl_time_txt, appl_day_txt;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            appl_amount_txt = itemView.findViewById(R.id.appl_amount_txt);
            appl_time_txt = itemView.findViewById(R.id.appl_time_txt);
            appl_day_txt = itemView.findViewById(R.id.appl_day_txt);
        }
    }

}