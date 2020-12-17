package com.example.medapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AddDailyAdapter extends RecyclerView.Adapter<AddDailyAdapter.MyViewHolder> {

    private Context context;
    private List<DoseModel> doseModels;
    DatabaseHelper databaseHelper;

    AddDailyAdapter(Context context, List<DoseModel> doseModels) {
        this.context = context;
        this.doseModels = doseModels;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.add_application_row, parent, false);
        return new AddDailyAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DoseModel model = doseModels.get(position);
        holder.appl_amount_txt.setText("Amount to take: " + model.getAmount());
        holder.appl_time_txt.setText("Time: " + model.getTime());
    }

    @Override
    public int getItemCount() {
        return doseModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView appl_amount_txt, appl_time_txt;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            appl_amount_txt = itemView.findViewById(R.id.appl_amount_txt);
            appl_time_txt = itemView.findViewById(R.id.appl_time_txt);
        }
    }

}