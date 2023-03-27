package com.cityone.stores.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cityone.R;
import com.cityone.databinding.AdapterStoreCategoryBinding;
import com.cityone.stores.activities.StoreDetailsActivity;
import com.cityone.stores.models.ItemSubModel;
import com.cityone.stores.models.ModelStoreDetails;

import java.util.ArrayList;

public class AdapterStoreItemCat1 extends RecyclerView.Adapter<AdapterStoreItemCat1.StoreCatHolder> {

    Context mContext;
    ArrayList<ItemSubModel.Result> storeList;
    int index = 0;

    public AdapterStoreItemCat1(Context mContext, ArrayList<ItemSubModel.Result> storeList) {
        this.mContext = mContext;
        this.storeList = storeList;
    }

    @NonNull
    @Override
    public StoreCatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterStoreCategoryBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.adapter_store_category,parent,false);
        return new StoreCatHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreCatHolder holder, int position) {

        // ModelStoreDetails.Result.Restaurant_sub_category data = storeList.get(position);

        holder.binding.btCatName.setText(storeList.get(position).getName());

        holder.binding.btCatName.setOnClickListener(v -> {
            index = position;
            ((StoreDetailsActivity)mContext).getItemsById(storeList.get(position).getId());
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


