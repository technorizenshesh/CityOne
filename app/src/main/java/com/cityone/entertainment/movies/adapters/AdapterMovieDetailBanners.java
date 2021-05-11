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

public class AdapterMovieDetailBanners extends SliderViewAdapter<AdapterMovieDetailBanners
        .AdapterRecentAdsViewHolder> {

    Context mContext;
    ArrayList<String> adsList;

    public AdapterMovieDetailBanners(Context mContext,ArrayList<String> adsList) {
        this.mContext = mContext;
        this.adsList = adsList;
    }

    public void renewItems(ArrayList<String> adsList) {
        this.adsList = adsList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.adsList.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(String sliderItem) {
        this.adsList.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public AdapterMovieDetailBanners.AdapterRecentAdsViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.adapter_upcoming_movies,parent,false);
        return new AdapterMovieDetailBanners.AdapterRecentAdsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterMovieDetailBanners.AdapterRecentAdsViewHolder holder, int position) {

        try {
            Picasso.get().load(adsList.get(position)).into(holder.itemsImage);
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
