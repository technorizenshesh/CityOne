package com.cityone.entertainment.movies.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cityone.R;
import com.cityone.databinding.ActivityTheaterDetailBinding;

public class TheaterDetailActivity extends AppCompatActivity {

    Context mContext = TheaterDetailActivity.this;
    ActivityTheaterDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_theater_detail);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btSeat.setOnClickListener(v -> {
            startActivity(new Intent(mContext,AvailSeatsActivity.class));
        });

        binding.btDate1.setOnClickListener(v -> {
            binding.btDate1.setBackgroundResource(R.drawable.light_orange_back_5);
            binding.btDate2.setBackgroundResource(R.drawable.orange_outline_back_5);
        });

        binding.btDate2.setOnClickListener(v -> {
            binding.btDate1.setBackgroundResource(R.drawable.orange_outline_back_5);
            binding.btDate2.setBackgroundResource(R.drawable.light_orange_back_5);
        });

        binding.bttime1.setOnClickListener(v -> {
            binding.bttime1.setBackgroundResource(R.drawable.light_orange_back_5);
            binding.bttime2.setBackgroundResource(R.drawable.orange_outline_back_5);
            binding.bttime3.setBackgroundResource(R.drawable.orange_outline_back_5);
            binding.bttime4.setBackgroundResource(R.drawable.orange_outline_back_5);
        });

        binding.bttime2.setOnClickListener(v -> {
            binding.bttime2.setBackgroundResource(R.drawable.light_orange_back_5);
            binding.bttime1.setBackgroundResource(R.drawable.orange_outline_back_5);
            binding.bttime3.setBackgroundResource(R.drawable.orange_outline_back_5);
            binding.bttime4.setBackgroundResource(R.drawable.orange_outline_back_5);
        });

        binding.bttime3.setOnClickListener(v -> {
            binding.bttime3.setBackgroundResource(R.drawable.light_orange_back_5);
            binding.bttime2.setBackgroundResource(R.drawable.orange_outline_back_5);
            binding.bttime1.setBackgroundResource(R.drawable.orange_outline_back_5);
            binding.bttime4.setBackgroundResource(R.drawable.orange_outline_back_5);
        });

        binding.bttime4.setOnClickListener(v -> {
            binding.bttime4.setBackgroundResource(R.drawable.light_orange_back_5);
            binding.bttime2.setBackgroundResource(R.drawable.orange_outline_back_5);
            binding.bttime3.setBackgroundResource(R.drawable.orange_outline_back_5);
            binding.bttime1.setBackgroundResource(R.drawable.orange_outline_back_5);
        });

    }


}