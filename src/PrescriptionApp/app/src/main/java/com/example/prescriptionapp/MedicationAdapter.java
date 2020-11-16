package com.example.prescriptionapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MyViewHolder> {

    private Context context;
    private List<MedicationModel> medicationModels;
    private MedicationModel medModel;

    MedicationAdapter(Context context, List<MedicationModel> medicationModels) {
        this.context = context;
        this.medicationModels = medicationModels;
    }

    @NonNull
    @Override
    public MedicationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.medication_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationAdapter.MyViewHolder holder, int position) {
        final MedicationModel model = medicationModels.get(position);
        holder.med_name_txt.setText("Medication: " + model.getName());
        holder.med_qty_txt.setText("Quantity: " + model.getQuantity());
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateMedActivity.class);
                intent.putExtra("MedModel", model);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return medicationModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView med_id_txt, med_name_txt, med_qty_txt, med_taken_txt;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            med_name_txt = itemView.findViewById(R.id.med_name_txt);
            med_qty_txt = itemView.findViewById(R.id.med_qty_txt);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }

    /**
     * Method to be used when filtering
     * @param query
     */
    public void filter(String query){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        List<MedicationModel> copy = dbHelper.selectAllMedication();
        medicationModels.clear();
        if(query.isEmpty()){
            medicationModels.addAll(copy);
        }
        else {
            query = query.toLowerCase();
            for(MedicationModel model: copy) {
                if(model.getName().toLowerCase().contains(query)){
                    medicationModels.add(model);
                }
            }
            Toast.makeText(context, Integer.toString(medicationModels.size()), Toast.LENGTH_SHORT).show();
        }
        notifyDataSetChanged();
    }

}
