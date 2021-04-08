package com.cityone.entertainment.movies.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cityone.R;
import com.cityone.databinding.ActivityMovieDetailsBinding;
import com.cityone.databinding.ActivityMovieHomeBinding;
import com.cityone.entertainment.movies.adapters.AdapterUpcomeMovies;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class MovieDetailsActivity extends AppCompatActivity {

    Context mContext = MovieDetailsActivity.this;
    ActivityMovieDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_movie_details);

        init();

    }

    private void init() {

        ArrayList<Integer> adsList = new ArrayList<>();
        adsList.add(R.drawable.ironman1);
        adsList.add(R.drawable.ironman2);

        binding.categorAdsSlider.setSliderAdapter(new AdapterUpcomeMovies(mContext,adsList));
        binding.categorAdsSlider.startAutoCycle();
        binding.categorAdsSlider.setIndicatorAnimation(IndicatorAnimationType.SLIDE);
        binding.categorAdsSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        binding.categorAdsSlider.setScrollTimeInSec(5);
        binding.categorAdsSlider.setAutoCycle(true);
        binding.categorAdsSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btBookTicket.setOnClickListener(v -> {
            startActivity(new Intent(mContext,AvailableTheatersActivity.class));
        });

    }


}