package com.cityone.stores.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cityone.R;
import com.cityone.databinding.AdapterItemDetailsBinding;
import com.cityone.databinding.AdapterStoresBinding;
import com.cityone.models.ModelStoreBooking;
import com.cityone.stores.activities.StoreDetailsActivity;
import com.cityone.stores.models.ModelStoreDetails;
import com.cityone.stores.models.ModelStores;
import com.cityone.utils.AppConstant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterOrderItems extends RecyclerView.Adapter<AdapterOrderItems.StoreHolder> {

    Context mContext;
    ArrayList<ModelStoreBooking.Result.Item_data> storeList;

    public AdapterOrderItems(Context mContext, ArrayList<ModelStoreBooking.Result.Item_data> storeList) {
        this.mContext = mContext;
        this.storeList = storeList;
    }

    @NonNull
    @Override
    public AdapterOrderItems.StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterItemDetailsBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.adapter_item_details,parent,false);
        return new StoreHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOrderItems.StoreHolder holder, int position) {
        ModelStoreBooking.Result.Item_data data = storeList.get(position);

        holder.binding.tvName.setText(data.getItem_name());
        holder.binding.tvPrice.setText(AppConstant.DOLLAR + data.getPrice() +" x "+ data.getQuantity());

        Picasso.get().load(data.getItem_image()).into(holder.binding.ivImage);
    }

    @Override
    public int getItemCount() {
        return storeList == null?0:storeList.size();
    }

    public class StoreHolder extends RecyclerView.ViewHolder{

        AdapterItemDetailsBinding binding;

        public StoreHolder(AdapterItemDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}
