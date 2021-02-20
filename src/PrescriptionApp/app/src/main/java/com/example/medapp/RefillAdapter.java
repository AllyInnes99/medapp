package com.example.medapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Calendar;
import java.util.List;

/**
 * RecyclerView adapter for the refill logs for a medicine
 */
public class RefillAdapter extends RecyclerView.Adapter<RefillAdapter.MyViewHolder> {

    private Context context;
    private List<RefillData> refillData;

    RefillAdapter(Context context, List<RefillData> refillData) {
        this.context = context;
        this.refillData = refillData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.refill_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final RefillData data = refillData.get(position);
        String date = data.createDateString();
        holder.refill_date.setText(date);

        String txt;
        int diff = data.getRefillAmount() - data.getOriginalQty();
        if (diff > 0) {
            txt = "+" + diff;
            holder.refill_amount.setTextColor(Color.GREEN);
        } else {
            txt = Integer.toString(diff);
            holder.refill_amount.setTextColor(Color.RED);
        }
        holder.refill_amount.setText(txt);

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = "Original quantity: " + data.getOriginalQty()
                        + "\nNew quantity: " + data.getRefillAmount();
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Refill Entry")
                        .setMessage(msg)
                        .setPositiveButton("ok", null)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return refillData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView refill_date, refill_amount;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            refill_date = itemView.findViewById(R.id.refill_date);
            refill_amount = itemView.findViewById(R.id.refill_amount);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }



}
