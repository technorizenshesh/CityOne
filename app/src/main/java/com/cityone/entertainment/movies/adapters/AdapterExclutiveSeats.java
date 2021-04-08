package com.cityone.entertainment.movies.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.cityone.R;
import com.cityone.databinding.AdapterSeatsBinding;
import com.cityone.entertainment.movies.activities.AvailSeatsActivity;

import java.util.ArrayList;

public class AdapterExclutiveSeats extends RecyclerView.Adapter<AdapterExclutiveSeats.SeatsViewHolder> {

    private final SeatsListener listener;
    Context mContext;
    ArrayList<AvailSeatsActivity.Model> exclist;

    public AdapterExclutiveSeats(Context mContext,ArrayList<AvailSeatsActivity.Model> exclist,SeatsListener listener) {
        this.mContext = mContext;
        this.exclist = exclist;
        this.listener=listener;
    }

    public void Update(ArrayList<AvailSeatsActivity.Model> exclist){
        this.exclist=exclist;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SeatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterSeatsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.adapter_seats,parent,false);
        return new SeatsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatsViewHolder holder, int position) {
        holder.binding.tvSeats.setText(exclist.get(position).getSets());
        holder.binding.tvSeats.setBackgroundResource(exclist.get(position).isSelected()?R.drawable.selected_back:R.drawable.aval_back);
        holder.binding.tvSeats.setTextColor(exclist.get(position).isSelected()? Color.WHITE :Color.BLACK);
        holder.binding.tvSeats.setOnClickListener(v->listener.success(position));
    }

   public interface SeatsListener {
        void success(int pso);
    }

    @Override
    public int getItemCount() {
        return exclist.size();
    }

    public class SeatsViewHolder extends RecyclerView.ViewHolder {
        AdapterSeatsBinding binding;
        public SeatsViewHolder(@NonNull AdapterSeatsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}
