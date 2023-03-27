package com.cityone.stores.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cityone.R;
import com.cityone.activities.DashboardActivity;
import com.cityone.activities.FoodHomeActivity;
import com.cityone.databinding.AdapterStoreCategory1Binding;
import com.cityone.databinding.AdapterStoreCategoryBinding;
import com.cityone.meals.MealsHomeActivity;
import com.cityone.stores.activities.StoresActivity;
import com.cityone.stores.models.ModelStoreCat;
import com.cityone.stores.models.ModelStoreCat1;

import java.util.ArrayList;

public class AdapterStoreCat1 extends RecyclerView.Adapter<AdapterStoreCat1.StoreCatHolder> {

    Context mContext;
    ArrayList<ModelStoreCat1.Result> storeList;
    int index = 0;
    boolean isMeals,isDashboard,isFood;

    public AdapterStoreCat1(Context mContext, ArrayList<ModelStoreCat1.Result> storeList, boolean isMeals, boolean isDashboard,boolean isFood) {
        this.mContext = mContext;
        this.storeList = storeList;
        this.isMeals = isMeals;
        this.isDashboard = isDashboard;
        this.isFood = isFood;

    }

    @NonNull
    @Override
    public StoreCatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterStoreCategory1Binding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.adapter_store_category1,parent,false);
        return new StoreCatHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreCatHolder holder, int position) {

        ModelStoreCat1.Result data = storeList.get(position);

        holder.binding.btCatName.setText(data.getName());

        holder.binding.btCatName.setOnClickListener(v -> {
            index = position;
            if(isMeals) {
                ((MealsHomeActivity)mContext).getStoresById(data.getId(),data.getName());
            } else if(isDashboard) {
                ((DashboardActivity)mContext).getStoresById(data.getId(),data.getName());
            }
            else if(isFood) {
                ((FoodHomeActivity)mContext).getStoresById(data.getId(),data.getName());
            }
            else {
                ((StoresActivity)mContext).getStoresById(data.getId(),data.getName());
            }
            notifyDataSetChanged();
        });


        if(index == position) {
            holder.binding.layCat.setBackgroundResource(R.drawable.orange_back_20);
        } else {
            holder.binding.layCat.setBackgroundResource(R.drawable.gray_back_20);
        }
if(!storeList.get(position).getImage().equals("")) {
    holder.binding.ivCate.setVisibility(View.VISIBLE);
    Glide.with(mContext)
            .load(storeList.get(position).getImage())
            .override(50, 50)
            .into(holder.binding.ivCate);
}
else holder.binding.ivCate.setVisibility(View.GONE);


    }

    @Override
    public int getItemCount() {
        return storeList == null?0:storeList.size();
    }

    public class StoreCatHolder extends RecyclerView.ViewHolder{

        AdapterStoreCategory1Binding binding;
        public StoreCatHolder(AdapterStoreCategory1Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}
