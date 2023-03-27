package com.cityone.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cityone.R;
import com.cityone.databinding.AdapterPointEarnedBinding;
import com.cityone.databinding.ItemHomeBinding;
import com.cityone.listener.HomeListener;
import com.cityone.models.ModelHome;
import com.cityone.utils.Compress;

import java.util.ArrayList;

public class AdapterHome extends RecyclerView.Adapter<AdapterHome.MyViewHolder> {
    Context mContext;
    ArrayList<ModelHome.Result>arrayList;
    HomeListener listener;

    public AdapterHome(Context mContext, ArrayList<ModelHome.Result> arrayList, HomeListener listener) {
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         ItemHomeBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_home, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.cvStores.setCardBackgroundColor(Color.parseColor(arrayList.get(position).getColor()));
        Glide.with(mContext).load(arrayList.get(position).getImage()).into(holder.binding.ivCat);
        holder.binding.tvCat.setText(arrayList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemHomeBinding binding;
        public MyViewHolder(@NonNull ItemHomeBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            binding.cvStores.setOnClickListener(v -> listener.OnHome(getAdapterPosition(),arrayList.get(getAdapterPosition()).getName()));
        }
    }
}
