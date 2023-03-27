package com.cityone.entertainment.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cityone.R;
import com.cityone.databinding.AdapterRemMoviesBinding;
import com.cityone.databinding.ItemMoviesBinding;
import com.cityone.entertainment.movies.activities.MovieDetailsActivity;
import com.cityone.entertainment.movies.adapters.AdapterRemMovies;
import com.cityone.entertainment.movies.models.ModelUpcMovies;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {


    Context mContext;
    ArrayList<ModelUpcMovies.Result> arrayList;

    public MovieAdapter(Context mContext, ArrayList<ModelUpcMovies.Result> arrayList) {
        this.mContext = mContext;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMoviesBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.item_movies,parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            Picasso.get().load(arrayList.get(position).getFirst_image()).into(holder.binding.moviImg);
            holder.binding.tvName.setText(arrayList.get(position).getName());

            holder.binding.moviImg.setOnClickListener(v -> {
                mContext.startActivity(new Intent(mContext, MovieDetailsActivity.class)
                        .putExtra("id",arrayList.get(position).getId())
                );
            });


        } catch (Exception e){}
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemMoviesBinding binding;
        public MyViewHolder(@NonNull ItemMoviesBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
