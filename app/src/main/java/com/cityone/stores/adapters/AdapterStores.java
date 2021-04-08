package com.cityone.stores.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.cityone.R;
import com.cityone.databinding.AdapterStoresBinding;
import com.cityone.stores.activities.StoreDetailsActivity;
import com.cityone.stores.models.ModelStores;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterStores extends RecyclerView.Adapter<AdapterStores.StoreHolder> {

    Context mContext;
    ArrayList<ModelStores.Result> storeList;

    public AdapterStores(Context mContext, ArrayList<ModelStores.Result> storeList) {
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

        Picasso.get().load(data.getImage()).into(holder.binding.ivStoreImg);

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
