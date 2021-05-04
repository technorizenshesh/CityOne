package com.cityone.entertainment.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.cityone.R;
import com.cityone.databinding.AdapterStoreCategoryBinding;
import com.cityone.entertainment.models.ModelEntCat;

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
        AdapterStoreCategoryBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.adapter_store_category,parent,false);
        return new StoreCatHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterEntCat.StoreCatHolder holder, int position) {

        ModelEntCat.Result data = entList.get(position);

        holder.binding.btCatName.setText(data.getName());

        holder.binding.btCatName.setOnClickListener(v -> {
            index = position;
            updateDataInterf.onSuccess(data.getId());
            notifyDataSetChanged();
        });

        if(index == position) {
            holder.binding.btCatName.setBackgroundResource(R.drawable.orange_back_20);
        } else {
            holder.binding.btCatName.setBackgroundResource(R.drawable.gray_back_20);
        }

    }

    public interface UpdateDataInterf {
        void onSuccess(String id);
    }

    @Override
    public int getItemCount() {
        return entList == null?0:entList.size();
    }

    public class StoreCatHolder extends RecyclerView.ViewHolder{

        AdapterStoreCategoryBinding binding;
        public StoreCatHolder(AdapterStoreCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}

