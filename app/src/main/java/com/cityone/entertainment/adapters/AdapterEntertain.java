package com.cityone.entertainment.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.cityone.R;
import com.cityone.databinding.AdapterEntertainmentsBinding;
import com.cityone.databinding.AdapterStoresBinding;
import com.cityone.entertainment.activities.EntDetailsActivity;
import com.cityone.entertainment.models.ModelEnts;
import com.cityone.stores.activities.StoreDetailsActivity;
import com.cityone.stores.adapters.AdapterStores;
import com.cityone.stores.models.ModelStores;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class AdapterEntertain extends RecyclerView.Adapter<AdapterEntertain.StoreHolder> {

    Context mContext;
    ArrayList<ModelEnts.Result> entsList;

    public AdapterEntertain(Context mContext, ArrayList<ModelEnts.Result> entsList) {
        this.mContext = mContext;
        this.entsList = entsList;
    }

    @NonNull
    @Override
    public AdapterEntertain.StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterEntertainmentsBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.adapter_entertainments,parent,false);
        return new AdapterEntertain.StoreHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterEntertain.StoreHolder holder, int position) {
        ModelEnts.Result data = entsList.get(position);

        holder.binding.tvEntName.setText(data.getName());
        holder.binding.tvAddress.setText(data.getAddress());

        Picasso.get().load(data.getImage()).into(holder.binding.ivPubImage);

        holder.binding.ivPubImage.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, EntDetailsActivity.class)
                    .putExtra("id",data.getId())
            );
        });

    }

    @Override
    public int getItemCount() {
        return entsList == null?0:entsList.size();
    }

    public class StoreHolder extends RecyclerView.ViewHolder{

        AdapterEntertainmentsBinding binding;

        public StoreHolder(AdapterEntertainmentsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}

