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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddDailyAdapter extends RecyclerView.Adapter<AddDailyAdapter.MyViewHolder> {

    private Context context;
    private List<AddDoseModel> doseModels;
    DatabaseHelper databaseHelper;
    List<TextView> dayIcons;

    AddDailyAdapter(Context context, List<AddDoseModel> doseModels) {
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
        AddDoseModel model = doseModels.get(position);
        holder.appl_amount_txt.setText("Take: " + model.getQuantity());
        holder.appl_time_txt.setText("Time: " + model.getTime());

        Set<String> days = new HashSet<>(model.getDays());

        dayIcons.add(holder.monday);
        dayIcons.add(holder.tuesday);
        dayIcons.add(holder.wednesday);
        dayIcons.add(holder.thursday);
        dayIcons.add(holder.friday);
        dayIcons.add(holder.saturday);
        dayIcons.add(holder.sunday);

        for (TextView textView : dayIcons) {
            String tag = textView.getTag().toString();
            if (days.contains(tag)) {
                textView.setTextColor(Color.BLACK);
            }
            else {
                textView.setTextColor(Color.LTGRAY);
            }
        }
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
            dayIcons = new ArrayList<>();
        }
    }

}
