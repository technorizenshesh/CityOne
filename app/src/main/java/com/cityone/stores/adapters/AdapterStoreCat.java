package com.cityone.stores.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cityone.R;
import com.cityone.activities.DashboardActivity;
import com.cityone.databinding.AdapterStoreCategoryBinding;
import com.cityone.meals.MealsHomeActivity;
import com.cityone.stores.activities.StoresActivity;
import com.cityone.stores.models.ModelStoreCat;
import com.cityone.stores.models.ModelStores;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;

import java.util.ArrayList;

public class AdapterStoreCat extends RecyclerView.Adapter<AdapterStoreCat.StoreCatHolder> {

    Context mContext;
    ArrayList<ModelStoreCat.Result> storeList;
    int index = 0;
    boolean isMeals,isDashboard;

    public AdapterStoreCat(Context mContext, ArrayList<ModelStoreCat.Result> storeList,boolean isMeals,boolean isDashboard) {
        this.mContext = mContext;
        this.storeList = storeList;
        this.isMeals = isMeals;
        this.isDashboard = isDashboard;
    }

    @NonNull
    @Override
    public AdapterStoreCat.StoreCatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterStoreCategoryBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.adapter_store_category,parent,false);
        return new StoreCatHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterStoreCat.StoreCatHolder holder, int position) {

        ModelStoreCat.Result data = storeList.get(position);

        holder.binding.btCatName.setText(data.getName());

        holder.binding.btCatName.setOnClickListener(v -> {
            index = position;
            if(isMeals) {
                ((MealsHomeActivity)mContext).getStoresById(data.getId(),data.getName());
            } else if(isDashboard) {
                ((DashboardActivity)mContext).getStoresById(data.getId(),data.getName());
            } else {
                ((StoresActivity)mContext).getStoresById(data.getId(),data.getName());
            }
            notifyDataSetChanged();
        });

        if(index == position) {
            holder.binding.btCatName.setBackgroundResource(R.drawable.orange_back_20);
        } else {
            holder.binding.btCatName.setBackgroundResource(R.drawable.gray_back_20);
        }

    }

    @Override
    public int getItemCount() {
        return storeList == null?0:storeList.size();
    }

    public class StoreCatHolder extends RecyclerView.ViewHolder{

        AdapterStoreCategoryBinding binding;
        public StoreCatHolder(AdapterStoreCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}

