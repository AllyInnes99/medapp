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

import java.util.Calendar;
import java.util.List;

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
        String date = createDateString(data);
        holder.refill_date.setText(date);

        String txt;
        int diff = data.getRefillAmount() - data.getOriginalQty();
        if(diff > 0) {
            txt = "+" + diff;
            holder.refill_amount.setTextColor(Color.GREEN);
        }
        else {
            txt = Integer.toString(diff);
            holder.refill_amount.setTextColor(Color.RED);
        }
        holder.refill_amount.setText(txt);

    }

    @Override
    public int getItemCount() {
        return refillData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView refill_date, refill_amount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            refill_date = itemView.findViewById(R.id.refill_date);
            refill_amount = itemView.findViewById(R.id.refill_amount);
        }
    }

    private String createDateString(RefillData data){
        return String.format("%s-%s-%s", padDate(data.getDay()), padDate(data.getMonth()), data.getYear());
    }

    private String padDate(int val){
        String valStr = Integer.toString(val);
        if(val < 10){
            valStr = "0" + valStr;
        }
        return valStr;
    }

}
