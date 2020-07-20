package com.example.knowyourgovernment;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

class MyViewHolder extends RecyclerView.ViewHolder {

    TextView office;
    TextView name;
    TextView party;

    MyViewHolder(View view) {
        super(view);
        office = view.findViewById(R.id.office);
        name = view.findViewById(R.id.name);
        party = view.findViewById(R.id.party);
    }
}