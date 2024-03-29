package com.cityone.entertainment.movies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cityone.R;
import com.cityone.entertainment.movies.models.ModelUpcMovies;
import com.makeramen.roundedimageview.RoundedImageView;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterUpcomeMovies extends SliderViewAdapter<AdapterUpcomeMovies.AdapterRecentAdsViewHolder> {

    Context mContext;
    ArrayList<ModelUpcMovies.Result> adsList;

    public AdapterUpcomeMovies(Context mContext, ArrayList<ModelUpcMovies.Result> adsList) {
        this.mContext = mContext;
        this.adsList = adsList;
    }

    public void renewItems(ArrayList<ModelUpcMovies.Result> adsList) {
        this.adsList = adsList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.adsList.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(ModelUpcMovies.Result sliderItem) {
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

        try {
            Picasso.get().load(adsList.get(position).getFirst_image()).into(holder.itemsImage);
        } catch (Exception e){}


    }

    @Override
    public int getCount() {
        return adsList==null?0:adsList.size();
    }

    public class AdapterRecentAdsViewHolder extends SliderViewAdapter.ViewHolder {

        RoundedImageView itemsImage;

        public AdapterRecentAdsViewHolder(View itemView) {
            super(itemView);
            itemsImage = itemView.findViewById(R.id.itemsImage);
        }

    }


}
