package com.cityone.entertainment.movies.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.cityone.R;
import com.cityone.databinding.AdapterAvailTheaterBinding;
import com.cityone.databinding.AdapterMovieDatesBinding;
import com.cityone.entertainment.movies.activities.TheaterDetailActivity;
import com.cityone.entertainment.movies.models.ModelAvilTheater;
import java.util.ArrayList;

public class AdapterAvailDates extends RecyclerView.Adapter<AdapterAvailDates.StoreCatHolder> {

    Context mContext;
    ArrayList<String> entList;
    int parentPosition = 0;
    UpdateDataInterf updateDataInterf;

    public AdapterAvailDates(Context mContext, ArrayList<String> entList,UpdateDataInterf updateDataInterf) {
        this.mContext = mContext;
        this.entList = entList;
        this.updateDataInterf = updateDataInterf;
    }

    @NonNull
    @Override
    public AdapterAvailDates.StoreCatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterMovieDatesBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.adapter_movie_dates,parent,false);
        return new AdapterAvailDates.StoreCatHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAvailDates.StoreCatHolder holder, int position) {

        String data = entList.get(position);

        holder.binding.btDate.setText(data);

        holder.binding.btDate.setOnClickListener(v -> {
            parentPosition = position;
            notifyDataSetChanged();
        });

        if(parentPosition == position) {
            updateDataInterf.onSuccess(data);
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

