package com.cityone.entertainment.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cityone.R;
import com.cityone.databinding.AdapterStoreCategoryBinding;
import com.cityone.databinding.ItemEntertainmentBinding;
import com.cityone.entertainment.models.ModelEntCat;
import com.cityone.entertainment.movies.activities.MovieHomeActivity;

import java.util.ArrayList;

public class AdapterEntCat extends RecyclerView.Adapter<AdapterEntCat.StoreCatHolder> {

    Context mContext;
    ArrayList<ModelEntCat.Result> entList;
    int index = 0;
    boolean isMeals;
    UpdateDataInterf updateDataInterf;

    public AdapterEntCat(Context mContext, ArrayList<ModelEntCat.Result> entList,UpdateDataInterf updateDataInterf) {
        this.mContext = mContext;
        this.entList = entList;
        this.isMeals = isMeals;
        this.updateDataInterf = updateDataInterf;
    }

    @NonNull
    @Override
    public AdapterEntCat.StoreCatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEntertainmentBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.item_entertainment,parent,false);
        return new StoreCatHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterEntCat.StoreCatHolder holder, int position) {

        ModelEntCat.Result data = entList.get(position);

        holder.binding.tvName.setText(data.getName());

        Glide.with(mContext).load(data.getImage()).into(holder.binding.ivImage);

        holder.binding.layoutEn.setOnClickListener(v -> {
          //  index = position;
         //   updateDataInterf.onSuccess(data.getId());
          //  notifyDataSetChanged();
        if(data.getName().equals("Movies"))    mContext.startActivity(new Intent(mContext, MovieHomeActivity.class));

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

        ItemEntertainmentBinding binding;
        public StoreCatHolder(ItemEntertainmentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}

