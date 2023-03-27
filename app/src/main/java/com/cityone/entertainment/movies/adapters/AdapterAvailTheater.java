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
import com.cityone.databinding.AdapterRemMoviesBinding;
import com.cityone.entertainment.movies.activities.MovieDetailsActivity;
import com.cityone.entertainment.movies.activities.TheaterDetailActivity;
import com.cityone.entertainment.movies.models.ModelAvilTheater;
import com.cityone.entertainment.movies.models.ModelAvilTheaterOne;
import com.cityone.entertainment.movies.models.ModelUpcMovies;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterAvailTheater extends RecyclerView.Adapter<AdapterAvailTheater.StoreCatHolder> {

    Context mContext;
    ArrayList<ModelAvilTheaterOne.Result> entList;

    public AdapterAvailTheater(Context mContext, ArrayList<ModelAvilTheaterOne.Result> entList) {
        this.mContext = mContext;
        this.entList = entList;
    }

    @NonNull
    @Override
    public AdapterAvailTheater.StoreCatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterAvailTheaterBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.adapter_avail_theater,parent,false);
        return new AdapterAvailTheater.StoreCatHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAvailTheater.StoreCatHolder holder, int position) {

        ModelAvilTheaterOne.Result data = entList.get(position);

        holder.binding.setData(data);

        holder.binding.ivTheatre.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext,TheaterDetailActivity.class)
                    .putExtra("id",data.getId()));
        });

    }

    public interface UpdateDataInterf {
        void onSuccess(String id);
    }

    @Override
    public int getItemCount() {
        return entList == null?0:entList.size();
    }

    public class StoreCatHolder extends RecyclerView.ViewHolder{

        AdapterAvailTheaterBinding binding;
        public StoreCatHolder(AdapterAvailTheaterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}

