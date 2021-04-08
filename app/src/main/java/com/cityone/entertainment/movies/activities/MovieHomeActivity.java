package com.cityone.entertainment.movies.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cityone.R;
import com.cityone.databinding.ActivityMovieHomeBinding;
import com.cityone.entertainment.movies.adapters.AdapterUpcomeMovies;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class MovieHomeActivity extends AppCompatActivity {

    Context mContext = MovieHomeActivity.this;
    ActivityMovieHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_movie_home);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        ArrayList<Integer> adsList = new ArrayList<>();
        adsList.add(R.drawable.movie1);
        adsList.add(R.drawable.movie2);
        adsList.add(R.drawable.movie3);

        binding.categorAdsSlider.setSliderAdapter(new AdapterUpcomeMovies(mContext,adsList));
        binding.categorAdsSlider.startAutoCycle();
        binding.categorAdsSlider.setIndicatorAnimation(IndicatorAnimationType.SLIDE);
        binding.categorAdsSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        binding.categorAdsSlider.setScrollTimeInSec(5);
        binding.categorAdsSlider.setAutoCycle(true);
        binding.categorAdsSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);

        binding.btEnglish.setOnClickListener(v -> {
            binding.btItalian.setBackgroundResource(R.drawable.gray_back_10);
            binding.btSpanish.setBackgroundResource(R.drawable.gray_back_10);
            binding.btEnglish.setBackgroundResource(R.drawable.orange_back_10);
        });

        binding.btSpanish.setOnClickListener(v -> {
            binding.btItalian.setBackgroundResource(R.drawable.gray_back_10);
            binding.btEnglish.setBackgroundResource(R.drawable.gray_back_10);
            binding.btSpanish.setBackgroundResource(R.drawable.orange_back_10);
        });

        binding.btItalian.setOnClickListener(v -> {
            binding.btEnglish.setBackgroundResource(R.drawable.gray_back_10);
            binding.btSpanish.setBackgroundResource(R.drawable.gray_back_10);
            binding.btItalian.setBackgroundResource(R.drawable.orange_back_10);
        });

        binding.cvMovie1.setOnClickListener(v -> {
            startActivity(new Intent(mContext,MovieDetailsActivity.class));
        });

    }

}