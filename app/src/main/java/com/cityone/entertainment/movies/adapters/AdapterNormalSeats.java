package com.cityone.entertainment.movies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.cityone.R;
import com.cityone.databinding.AdapterSeatsBinding;
import com.cityone.entertainment.movies.activities.AvailSeatsActivity;

import java.util.ArrayList;

public class AdapterNormalSeats extends RecyclerView.Adapter<AdapterNormalSeats.SeatsViewHolder> {

    Context mContext;
    AdapterSeatsBinding binding;
    ArrayList<AvailSeatsActivity.Model> normallist;

    public AdapterNormalSeats(Context mContext,ArrayList<AvailSeatsActivity.Model> normallist) {
        this.mContext = mContext;
        this.normallist = normallist;
    }

    @NonNull
    @Override
    public AdapterNormalSeats.SeatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),R.layout.adapter_seats,parent,false);
        return new AdapterNormalSeats.SeatsViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterNormalSeats.SeatsViewHolder holder, int position) {

        binding.tvSeats.setText(String.valueOf(position+1));

    }

    @Override
    public int getItemCount() {
        return normallist.size();
    }

    public class SeatsViewHolder extends RecyclerView.ViewHolder {

        public SeatsViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }


}
