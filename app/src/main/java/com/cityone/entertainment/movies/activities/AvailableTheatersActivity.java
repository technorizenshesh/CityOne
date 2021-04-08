package com.cityone.entertainment.movies.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cityone.R;
import com.cityone.databinding.ActivityAvailableTheatersBinding;
import com.cityone.entertainment.movies.adapters.AdapterClassicSeats;
import com.cityone.entertainment.movies.adapters.AdapterExclutiveSeats;
import com.cityone.entertainment.movies.adapters.AdapterNormalSeats;

import java.util.ArrayList;

public class AvailableTheatersActivity extends AppCompatActivity {

    Context mContext = AvailableTheatersActivity.this;
    ActivityAvailableTheatersBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_available_theaters);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.ivTheatre.setOnClickListener(v -> {
            startActivity(new Intent(mContext,TheaterDetailActivity.class));
        });


    }


}