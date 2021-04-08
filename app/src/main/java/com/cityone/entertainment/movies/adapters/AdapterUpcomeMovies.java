package com.cityone.entertainment.movies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cityone.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class AdapterUpcomeMovies extends SliderViewAdapter<AdapterUpcomeMovies.AdapterRecentAdsViewHolder> {

    Context mContext;
    ArrayList<Integer> adsList;

    public AdapterUpcomeMovies(Context mContext, ArrayList<Integer> adsList) {
        this.mContext = mContext;
        this.adsList = adsList;
    }

    public void renewItems(ArrayList<Integer> adsList) {
        this.adsList = adsList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.adsList.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(Integer sliderItem) {
        this.adsList.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public AdapterUpcomeMovies.AdapterRecentAdsViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_upcoming_movies,parent,false);
        return new AdapterUpcomeMovies.AdapterRecentAdsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterUpcomeMovies.AdapterRecentAdsViewHolder holder, int position) {

        holder.itemsImage.setImageResource(adsList.get(position));

    }

    @Override
    public int getCount() {
        return adsList.size();
    }

    public class AdapterRecentAdsViewHolder extends SliderViewAdapter.ViewHolder {

        RoundedImageView itemsImage;

        public AdapterRecentAdsViewHolder(View itemView) {
            super(itemView);
            itemsImage = itemView.findViewById(R.id.itemsImage);
        }

    }


}
