package com.cityone.entertainment.movies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cityone.R;
import com.cityone.databinding.AdapterMovieDatesBinding;
import com.cityone.entertainment.movies.models.ModelTheaterDetailsNew;

import java.util.ArrayList;

public class AdapterAvailDatesNew  extends RecyclerView.Adapter<AdapterAvailDatesNew.StoreCatHolder> {

    Context mContext;
    ArrayList<ModelTheaterDetailsNew.Result.slots.seats> entList;
    int parentPosition = 0;
    AdapterAvailDates.UpdateDataInterf updateDataInterf;

    public AdapterAvailDatesNew(Context mContext, ArrayList<ModelTheaterDetailsNew.Result.slots.seats> entList) {
        this.mContext = mContext;
        this.entList = entList;
    }

    @NonNull
    @Override
    public StoreCatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterMovieDatesBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.adapter_movie_dates,parent,false);
        return new StoreCatHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreCatHolder holder, int position) {

        ModelTheaterDetailsNew.Result.slots.seats data = entList.get(position);

       holder.binding.btDate.setText(data.getSeat_no());

        holder.binding.btDate.setOnClickListener(v -> {});

        if(data.getStatus().equals("BOOKED")) {
            holder.binding.btDate.setBackgroundResource(R.drawable.light_orange_back_5);
        } else {
            holder.binding.btDate.setBackgroundResource(R.drawable.orange_outline_back_5);
        }

    }

    public interface UpdateDataInterf {
        void onSuccess(String date);
    }

    @Override
    public int getItemCount() {
        return entList == null?0:entList.size();
    }

    public class StoreCatHolder extends RecyclerView.ViewHolder{

        AdapterMovieDatesBinding binding;
        public StoreCatHolder(AdapterMovieDatesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}
