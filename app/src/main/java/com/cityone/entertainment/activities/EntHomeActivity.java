package com.cityone.entertainment.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cityone.R;
import com.cityone.databinding.ActivityEntertainmentHomeBinding;
import com.cityone.entertainment.movies.activities.MovieHomeActivity;

public class EntHomeActivity extends AppCompatActivity {

    Context mContext = EntHomeActivity.this;
    ActivityEntertainmentHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_entertainment_home);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.ivPubImage.setOnClickListener(v -> {
            startActivity(new Intent(mContext, EntDetailsActivity.class));
        });

        binding.btMovie.setOnClickListener(v -> {
            startActivity(new Intent(mContext, MovieHomeActivity.class));
        });

        binding.btPub.setOnClickListener(v -> {
            binding.btClub.setBackgroundResource(R.drawable.gray_back_10);
            binding.btEvent.setBackgroundResource(R.drawable.gray_back_10);
            binding.btPub.setBackgroundResource(R.drawable.orange_back_10);
        });

        binding.btClub.setOnClickListener(v -> {
            binding.btEvent.setBackgroundResource(R.drawable.gray_back_10);
            binding.btPub.setBackgroundResource(R.drawable.gray_back_10);
            binding.btClub.setBackgroundResource(R.drawable.orange_back_10);
        });

        binding.btEvent.setOnClickListener(v -> {
            binding.btPub.setBackgroundResource(R.drawable.gray_back_10);
            binding.btEvent.setBackgroundResource(R.drawable.orange_back_10);
            binding.btClub.setBackgroundResource(R.drawable.gray_back_10);
        });

    }


}