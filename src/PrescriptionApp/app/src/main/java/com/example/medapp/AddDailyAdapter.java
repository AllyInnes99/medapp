package com.example.medapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

        String[] t = model.getTime().split(":");
        t[0] = padString(t[0]);
        t[1] = padString(t[1]);


        if(t[1].length() == 1){
            t[1] = "0" + t[1];
            Toast.makeText(context, t[1], Toast.LENGTH_SHORT).show();

        }

        holder.appl_amount_txt.setText("Take: " + model.getAmount());
        holder.appl_time_txt.setText("Time: " + t[0] + ":" + t[1]);

        holder.monday.setTextColor(Color.BLACK);
        holder.tuesday.setTextColor(Color.BLACK);
        holder.wednesday.setTextColor(Color.BLACK);
        holder.thursday.setTextColor(Color.BLACK);
        holder.friday.setTextColor(Color.BLACK);
        holder.saturday.setTextColor(Color.BLACK);
        holder.sunday.setTextColor(Color.BLACK);
    }

    @Override
    public int getItemCount() {
        return doseModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView appl_amount_txt, appl_time_txt;
        TextView monday, tuesday, wednesday, thursday, friday, saturday, sunday;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            appl_amount_txt = itemView.findViewById(R.id.appl_amount_txt);
            appl_time_txt = itemView.findViewById(R.id.appl_time_txt);
            monday = itemView.findViewById(R.id.monday);
            tuesday = itemView.findViewById(R.id.tuesday);
            wednesday = itemView.findViewById(R.id.wednesday);
            thursday = itemView.findViewById(R.id.thursday);
            friday = itemView.findViewById(R.id.friday);
            saturday = itemView.findViewById(R.id.saturday);
            sunday = itemView.findViewById(R.id.sunday);
        }
    }

    private String padString(String target){
        if(target.length() == 1){
            target = "0" + target;
        }
        return target;
    }

}