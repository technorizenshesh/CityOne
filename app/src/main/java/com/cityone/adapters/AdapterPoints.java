package com.cityone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cityone.R;
import com.cityone.databinding.AdapterPointEarnedBinding;
import com.cityone.models.ModelReferrals;

import java.util.ArrayList;

public class AdapterPoints extends RecyclerView.Adapter<AdapterPoints.PointViewHolder> {

    Context mContext;
    ArrayList<ModelReferrals.Result> pointList;

    public AdapterPoints(Context mContext, ArrayList<ModelReferrals.Result> pointList) {
        this.mContext = mContext;
        this.pointList = pointList;
    }

    @NonNull
    @Override
    public PointViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterPointEarnedBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.adapter_point_earned, parent, false);
        return new PointViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PointViewHolder holder, int position) {
        ModelReferrals.Result data = pointList.get(position);

        holder.binding.tvDate.setText(data.getDate_time().split(" ")[0]);
        holder.binding.tvPoints.setText("You earned " + data.getReferral_code_point() + " points");
    }

    @Override
    public int getItemCount() {
        return pointList == null ? 0 : pointList.size();
    }

    public class PointViewHolder extends RecyclerView.ViewHolder {

        AdapterPointEarnedBinding binding;

        public PointViewHolder(AdapterPointEarnedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}
