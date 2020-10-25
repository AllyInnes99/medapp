package com.example.prescriptionapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private List<MedicationModel> medicationModels;

    CustomAdapter(Context context, List<MedicationModel> medicationModels) {
        this.context = context;
        this.medicationModels = medicationModels;
    }

    @NonNull
    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.medication_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.MyViewHolder holder, int position) {
        MedicationModel model = medicationModels.get(position);
        holder.med_id_txt.setText(String.valueOf(model.getMedicationId()));
        holder.med_name_txt.setText(String.valueOf(model.getName()));
        holder.med_qty_txt.setText(String.valueOf(model.getQuantity()));
        //holder.med_taken_txt.setText(String.valueOf(model.isTaken()));

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
            med_id_txt = itemView.findViewById(R.id.med_id_txt);
            med_name_txt = itemView.findViewById(R.id.med_name_txt);
            med_qty_txt = itemView.findViewById(R.id.med_qty_txt);
            med_taken_txt = itemView.findViewById(R.id.med_taken_txt);
        }
    }

}
