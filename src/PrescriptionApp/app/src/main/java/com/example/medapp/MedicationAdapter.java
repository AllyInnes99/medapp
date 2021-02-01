package com.example.medapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MyViewHolder> {

    private Context context;
    private List<MedicationModel> medicationModels;
    private SharedPreferences sp;

    MedicationAdapter(Context context, List<MedicationModel> medicationModels) {
        this.context = context;
        this.medicationModels = medicationModels;
        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
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

        String days = sp.getString("reminderDay", "");

        if(model.isRefillRequested()){
            holder.imageView.setImageResource(R.drawable.ic_baseline_hourglass_full_24);
        }
        else if(model.getDaysUntilEmpty() > Integer.parseInt(days)) {
            holder.imageView.setImageDrawable(null);
        }

        holder.med_name_txt.setText("Medication: " + model.getName());
        holder.med_qty_txt.setText("Quantity: " + model.getQuantity());
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateMedActivity.class);
                intent.putExtra("medID", model.getMedicationId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return medicationModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView med_name_txt, med_qty_txt;
        LinearLayout mainLayout;
        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            med_name_txt = itemView.findViewById(R.id.med_name_txt);
            med_qty_txt = itemView.findViewById(R.id.med_qty_txt);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            imageView = itemView.findViewById(R.id.imageView);
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
        }
        notifyDataSetChanged();
    }

}
