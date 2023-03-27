package com.cityone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cityone.R;
import com.cityone.databinding.ItemSliderBinding;
import com.cityone.models.BannerModel;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.MyViewHolder> {
    Context context;
    ArrayList<BannerModel.Result>arrayList;

    public SliderAdapter(Context context, ArrayList<BannerModel.Result> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider, null);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        Glide.with(viewHolder.itemView)
                .load(arrayList.get(position).getImage())
                .fitCenter()
                .into(viewHolder.imageViewBackground);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }


    public class MyViewHolder extends SliderViewAdapter.ViewHolder {
        ImageView imageViewBackground;
        View itemView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            this.itemView = itemView;

        }
    }
}
