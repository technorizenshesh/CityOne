package com.cityone.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cityone.R;
import com.cityone.activities.DashboardActivity;
import com.cityone.activities.FoodHomeActivity;
import com.cityone.databinding.AdapterStoreCategoryBinding;
import com.cityone.databinding.AdapterStoresBinding;
import com.cityone.meals.MealsHomeActivity;
import com.cityone.stores.activities.StoreDetailsActivity;
import com.cityone.stores.activities.StoresActivity;
import com.cityone.stores.adapters.AdapterStores;
import com.cityone.stores.models.ModelStoreCat;
import com.cityone.stores.models.ModelStores;

import java.util.ArrayList;

public class AdapterMealStores extends RecyclerView.Adapter<AdapterMealStores.StoreHolder> {

    Context mContext;
    ArrayList<ModelStores.Result> storeList;

    public AdapterMealStores(Context mContext, ArrayList<ModelStores.Result> storeList) {
        this.mContext = mContext;
        this.storeList = storeList;
    }

    @NonNull
    @Override
    public StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterStoresBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext),R.layout.adapter_stores,parent,false);
        return new StoreHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreHolder holder, int position) {
        ModelStores.Result data = storeList.get(position);

        holder.binding.tvName.setText(data.getRestaurant_name());
        holder.binding.tvAddress.setText(data.getAddress());

        Log.e("imageurls","image = " + data.getImage());

        Glide.with(mContext).load(data.getRestaurant_image()).into(holder.binding.ivStoreImg);
        // Picasso.get().load(data.getImage()).into(holder.binding.ivStoreImg);

        holder.binding.ivStoreImg.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, StoreDetailsActivity.class)
                    .putExtra("storeid",data.getId())
            );
        });

    }

    @Override
    public int getItemCount() {
        return storeList == null?0:storeList.size();
    }

    public class StoreHolder extends RecyclerView.ViewHolder{

        AdapterStoresBinding binding;

        public StoreHolder(AdapterStoresBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}