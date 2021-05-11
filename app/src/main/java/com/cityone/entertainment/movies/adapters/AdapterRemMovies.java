package com.cityone.entertainment.movies.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.cityone.R;
import com.cityone.databinding.AdapterRemMoviesBinding;
import com.cityone.databinding.AdapterStoreCategoryBinding;
import com.cityone.entertainment.adapters.AdapterEntCat;
import com.cityone.entertainment.models.ModelEntCat;
import com.cityone.entertainment.movies.activities.MovieDetailsActivity;
import com.cityone.entertainment.movies.models.ModelUpcMovies;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterRemMovies extends RecyclerView.Adapter<AdapterRemMovies.StoreCatHolder> {

    Context mContext;
    ArrayList<ModelUpcMovies.Result> entList;

    public AdapterRemMovies(Context mContext, ArrayList<ModelUpcMovies.Result> entList) {
        this.mContext = mContext;
        this.entList = entList;
    }

    @NonNull
    @Override
    public AdapterRemMovies.StoreCatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterRemMoviesBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext),R.layout.adapter_rem_movies,parent,false);
        return new AdapterRemMovies.StoreCatHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRemMovies.StoreCatHolder holder, int position) {

        ModelUpcMovies.Result data = entList.get(position);

        holder.binding.tvName.setText(data.getName());
        try{
            Picasso.get().load(data.getFirst_image()).into(holder.binding.moviImg);
        } catch (Exception E){}
        holder.binding.cvMovie1.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, MovieDetailsActivity.class)
            .putExtra("id",data.getId())
            );
        });holder.binding.moviImg.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, MovieDetailsActivity.class)
            .putExtra("id",data.getId())
            );
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

        AdapterRemMoviesBinding binding;
        public StoreCatHolder(AdapterRemMoviesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}

