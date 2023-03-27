package com.cityone.entertainment.movies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cityone.R;
import com.cityone.databinding.AdapterSeatsBinding;
import com.cityone.databinding.ItemSlotsBinding;
import com.cityone.entertainment.movies.activities.AvailSeatsActivity;
import com.cityone.entertainment.movies.models.ModelTheaterDetailsNew;

import java.util.ArrayList;

public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<ModelTheaterDetailsNew.Result.slots> arrayList;

    public SlotAdapter(Context mContext,ArrayList<ModelTheaterDetailsNew.Result.slots> arrayList) {
        this.mContext = mContext;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSlotsBinding binding;  binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_slots,parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.binding.tvslots.setText(arrayList.get(position).getSlots_start() + " to " + arrayList.get(position).getSlots_end());

        AdapterAvailDatesNew adapterAvailDates = new AdapterAvailDatesNew(mContext,arrayList.get(position).getSeats());
        holder.binding.rvSeats.setAdapter(adapterAvailDates);


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemSlotsBinding binding;
        public MyViewHolder(@NonNull ItemSlotsBinding itemView) {
            super(itemView.getRoot());
            binding =itemView;
        }

    }


}
