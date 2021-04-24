package com.cityone.taxi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.cityone.R;
import com.cityone.databinding.AdapterCarTaxiTypeBinding;
import com.cityone.taxi.models.ModelTaxiType;
import java.util.ArrayList;

public class AdapterCarTypes extends RecyclerView.Adapter<AdapterCarTypes.StoreHolder> {

    Context mContext;
    ArrayList<ModelTaxiType.Result> taxiList;
    int parentPosition = 0;
    CraTypeCallBack craTypeCallBack;

    public AdapterCarTypes(Context mContext, ArrayList<ModelTaxiType.Result> taxiList,CraTypeCallBack craTypeCallBack) {
        this.mContext = mContext;
        this.taxiList = taxiList;
        this.craTypeCallBack = craTypeCallBack;
    }

    @NonNull
    @Override
    public AdapterCarTypes.StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterCarTaxiTypeBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.adapter_car_taxi_type,parent,false);
        return new AdapterCarTypes.StoreHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCarTypes.StoreHolder holder, int position) {

        ModelTaxiType.Result data = taxiList.get(position);

        holder.binding.tvCarName.setText(data.getCar_name());
        holder.binding.tvCharge.setText(mContext.getString(R.string.doller) + " " + data.getCharge());
        holder.binding.tvSeats.setText(data.getNo_of_seats() + " " + mContext.getString(R.string.seater));

        if(parentPosition == position) {
            craTypeCallBack.onSuccess(data.getId(),data.getAmount());
            holder.binding.llCarBack.setBackgroundResource(R.drawable.orange_light_back);
        } else {
            holder.binding.llCarBack.setBackgroundResource(R.drawable.orange_outline_back);
        }

        holder.binding.llCarBack.setOnClickListener(v -> {
            craTypeCallBack.onSuccess(data.getId(),data.getAmount());
            parentPosition = position;
            notifyDataSetChanged();
        });

    }

    public interface CraTypeCallBack {
        void onSuccess(String carId,String amount);
    }

    @Override
    public int getItemCount() {
        return taxiList == null?0:taxiList.size();
    }

    public class StoreHolder extends RecyclerView.ViewHolder{

        AdapterCarTaxiTypeBinding binding;

        public StoreHolder(AdapterCarTaxiTypeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}


