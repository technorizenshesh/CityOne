package com.cityone.entertainment.movies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cityone.R;
import com.cityone.databinding.AdapterSeatsBinding;
import com.cityone.entertainment.movies.activities.AvailSeatsActivity;

import java.util.ArrayList;

public class AdapterClassicSeats extends RecyclerView.Adapter<AdapterClassicSeats.SeatsViewHolder> {

    Context mContext;
    AdapterSeatsBinding binding;
    ArrayList<AvailSeatsActivity.Model> classiclist;

    public AdapterClassicSeats(Context mContext,ArrayList<AvailSeatsActivity.Model> classiclist) {
        this.mContext = mContext;
        this.classiclist = classiclist;
    }

    @NonNull
    @Override
    public AdapterClassicSeats.SeatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.adapter_seats,parent,false);
        return new AdapterClassicSeats.SeatsViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterClassicSeats.SeatsViewHolder holder, int position) {

        binding.tvSeats.setText(String.valueOf(position+1));

    }

    @Override
    public int getItemCount() {
        return classiclist.size();
    }

    public class SeatsViewHolder extends RecyclerView.ViewHolder {

        public SeatsViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }


}
