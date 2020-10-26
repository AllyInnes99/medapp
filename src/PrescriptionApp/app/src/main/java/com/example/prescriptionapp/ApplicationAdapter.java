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

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.MyViewHolder> {

    private Context context;
    private List<ApplicationModel> applicationModels;

    ApplicationAdapter(Context context, List<ApplicationModel> applicationModels) {
        this.context = context;
        this.applicationModels = applicationModels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.application_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ApplicationModel model = applicationModels.get(position);
        holder.appl_dosage_txt.setText(String.valueOf(model.getDosage()));
        holder.appl_time_txt.setText(String.valueOf(model.getTime()));
        holder.appl_amount_txt.setText(String.valueOf(model.getAmount()));

    }

    @Override
    public int getItemCount() {
        return applicationModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView appl_amount_txt, appl_dosage_txt, appl_time_txt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            appl_amount_txt = itemView.findViewById(R.id.appl_amount_txt);
            appl_dosage_txt = itemView.findViewById(R.id.appl_dosage_txt);
            appl_time_txt = itemView.findViewById(R.id.appl_time_txt);

        }
    }


}
