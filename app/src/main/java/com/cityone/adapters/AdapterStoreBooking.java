package com.cityone.adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.cityone.R;
import com.cityone.databinding.AdapterStoreBookingBinding;
import com.cityone.databinding.AdapterStoreCategoryBinding;
import com.cityone.databinding.FoodOrderDetailDialogBinding;
import com.cityone.models.ModelStoreBooking;
import com.cityone.stores.activities.StoresActivity;
import com.cityone.stores.adapters.AdapterOrderItems;
import com.cityone.stores.adapters.AdapterStoreCat;
import com.cityone.stores.models.ModelStoreCat;
import com.cityone.utils.AppConstant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterStoreBooking extends RecyclerView.Adapter<AdapterStoreBooking.StoreCatHolder> {

    Context mContext;
    ArrayList<ModelStoreBooking.Result> storeList;

    public AdapterStoreBooking(Context mContext, ArrayList<ModelStoreBooking.Result> storeList) {
        this.mContext = mContext;
        this.storeList = storeList;
    }

    @NonNull
    @Override
    public AdapterStoreBooking.StoreCatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterStoreBookingBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.adapter_store_booking,parent,false);
        return new StoreCatHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterStoreBooking.StoreCatHolder holder, int position) {

        ModelStoreBooking.Result data = storeList.get(position);

        Picasso.get().load(data.getRestaurant_image()).into(holder.binding.ivStoreImage);
        holder.binding.tvStoreName.setText(data.getRestaurant_name());
        holder.binding.tvStoreAddress.setText(data.getRestaurant_address());
        holder.binding.tvOrderId.setText(mContext.getString(R.string.order_id) +" : "+ data.getOrder_id());
        holder.binding.tvDate.setText(data.getDate());
        holder.binding.tvTime.setText(data.getTime());
        holder.binding.tvStatus.setText(data.getStatus());
        holder.binding.tvOrderAmt.setText(AppConstant.DOLLAR + data.getTotal_amount());

        holder.binding.tvDetails.setOnClickListener(v -> {
            openOrderDetailDialog(data);
        });

    }

    private void openOrderDetailDialog(ModelStoreBooking.Result data) {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        FoodOrderDetailDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext),R.layout.food_order_detail_dialog,null,false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.itemsTotal.setText(AppConstant.DOLLAR + data.getTotal_amount());
        dialogBinding.tvDevAddress.setText(data.getAddress());
        dialogBinding.tvOrderId.setText(data.getOrder_id());
        dialogBinding.tvStatus.setText(data.getStatus());

        AdapterOrderItems adapterOrderItems = new AdapterOrderItems(mContext,data.getItem_data());
        dialogBinding.rvItems.setAdapter(adapterOrderItems);

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    @Override
    public int getItemCount() {
        return storeList == null?0:storeList.size();
    }

    public class StoreCatHolder extends RecyclerView.ViewHolder{

        AdapterStoreBookingBinding binding;
        public StoreCatHolder(AdapterStoreBookingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}