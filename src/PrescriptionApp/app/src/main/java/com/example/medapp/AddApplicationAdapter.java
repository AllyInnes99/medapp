package com.example.medapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AddApplicationAdapter extends RecyclerView.Adapter<AddApplicationAdapter.MyViewHolder> {

    private Context context;
    private List<DoseModel> doseModels;
    DatabaseHelper databaseHelper;

    AddApplicationAdapter(Context context, List<DoseModel> doseModels) {
        this.context = context;
        this.doseModels = doseModels;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.add_application_row, parent, false);
        return new AddApplicationAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DoseModel model = doseModels.get(position);
        //final MedicationModel medModel = databaseHelper.selectMedicationFromDose(model);
        holder.appl_amount_txt.setText("Amount to take: " + model.getAmount());
        holder.appl_time_txt.setText("Time: " + model.getTime());
        //holder.appl_med_txt.setText("Med name: " + medModel.getName());
    }

    @Override
    public int getItemCount() {
        return doseModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView appl_med_txt, appl_amount_txt, appl_time_txt;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            appl_med_txt = itemView.findViewById(R.id.appl_med_txt);
            appl_amount_txt = itemView.findViewById(R.id.appl_amount_txt);
            appl_time_txt = itemView.findViewById(R.id.appl_time_txt);
        }
    }

}