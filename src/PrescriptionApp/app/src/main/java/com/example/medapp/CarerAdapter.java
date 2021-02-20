package com.example.medapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Recycler view adapter for selecting a patient
 */
public class CarerAdapter extends RecyclerView.Adapter<CarerAdapter.MyViewHolder> {

    private Context context;
    private List<ContactDetails> contacts;
    private CarerActivity activity;

    public CarerAdapter(Context context, List<ContactDetails> contacts, CarerActivity activity) {
        this.context = context;
        this.contacts = contacts;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.contact_row, parent, false);
        return new CarerAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ContactDetails contact = contacts.get(position);
        holder.contact_name.setText(contact.getName());
        holder.contact_email.setText(contact.getEmail());
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.addToCalendar(contact);
            }
        });

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView contact_name, contact_email;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            contact_name = itemView.findViewById(R.id.contact_name);
            contact_email = itemView.findViewById(R.id.contact_email);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }

}
