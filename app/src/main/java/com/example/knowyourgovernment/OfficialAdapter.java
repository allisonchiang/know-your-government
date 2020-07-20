package com.example.knowyourgovernment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OfficialAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private static final String TAG = "OfficialAdapter";
    private List<Official> officialList;
    private MainActivity mainAct;

    OfficialAdapter(List<Official> officialList, MainActivity mainAct) {
        this.officialList = officialList;
        this.mainAct = mainAct;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: Making new MyViewHolder");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.official_list_row, parent, false);

        itemView.setOnClickListener(mainAct);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Filling viewHolder employee " + position);
        Official official = officialList.get(position);

        holder.office.setText(official.getOffice());
        holder.name.setText(official.getName());
        holder.party.setText(" (" + official.getParty() + ")");
    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }
}
