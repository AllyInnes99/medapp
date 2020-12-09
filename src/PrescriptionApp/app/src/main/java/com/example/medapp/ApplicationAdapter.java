package com.example.medapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.MyViewHolder> {

    private Context context;
    private List<DoseModel> doseModels;
    DatabaseHelper databaseHelper;

    ApplicationAdapter(Context context, List<DoseModel> doseModels) {
        this.context = context;
        this.doseModels = doseModels;
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
        final DoseModel model = doseModels.get(position);
        final MedicationModel medModel = databaseHelper.selectMedicationFromDose(model);
        holder.appl_amount_txt.setText("Amount to take: " + model.getAmount());
        holder.appl_time_txt.setText("Time: " + model.getTime());
        holder.appl_med_txt.setText("Name: " + medModel.getName());

        holder.button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String msg = "";
                if(model.getAmount() > medModel.getQuantity()){
                    msg = "You don't have enough stock to take this medication.";
                }
                else{
                    databaseHelper.takeMedication(model, medModel);
                    msg = "You have taken " + model.getAmount() + " of " + medModel.getName();
                }
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });


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
