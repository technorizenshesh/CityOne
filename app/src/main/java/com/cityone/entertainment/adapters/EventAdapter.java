package com.cityone.entertainment.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cityone.R;
import com.cityone.databinding.ItemEventsBinding;
import com.cityone.databinding.ItemMoviesBinding;
import com.cityone.entertainment.models.EventModel;
import com.cityone.entertainment.movies.models.ModelUpcMovies;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EventAdapter  extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {


    Context mContext;
    ArrayList<EventModel.Result> arrayList;

    public EventAdapter(Context mContext, ArrayList<EventModel.Result> arrayList) {
        this.mContext = mContext;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEventsBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.item_events,parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            Picasso.get().load(arrayList.get(position).getImage()).into(holder.binding.moviImg);
            holder.binding.tvEvent.setText(arrayList.get(position).getId() + " EVENTS");
            holder.binding.tvTitle.setText(arrayList.get(position).getTitle());

        } catch (Exception e){}
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemEventsBinding binding;
        public MyViewHolder(@NonNull ItemEventsBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}