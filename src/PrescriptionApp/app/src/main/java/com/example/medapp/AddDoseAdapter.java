package com.example.medapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * RecyclerView adapter for the doses to be added for a medication
 */
public class AddDoseAdapter extends RecyclerView.Adapter<AddDoseAdapter.MyViewHolder> {

    private Context context;
    private List<AddDoseModel> doseModels;
    DatabaseHelper databaseHelper;
    List<TextView> dayIcons;

    AddDoseAdapter(Context context, List<AddDoseModel> doseModels) {
        this.context = context;
        this.doseModels = doseModels;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.add_dose_row, parent, false);
        return new AddDoseAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
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
            if (days.contains(tag) || days.contains("Daily")) {
                textView.setTextColor(Color.BLACK);
            } else {
                textView.setTextColor(Color.LTGRAY);
            }
        }

        holder.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doseModels.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, doseModels.size());
                holder.itemView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doseModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView appl_amount_txt, appl_time_txt;
        TextView monday, tuesday, wednesday, thursday, friday, saturday, sunday;
        ImageView clear;

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
            clear = itemView.findViewById(R.id.clear);
            dayIcons = new ArrayList<>();
        }
    }

}
