package com.cityone.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.cityone.R;
import com.cityone.databinding.ActivityAllowLocationBinding;

public class AllowLocationActivity extends AppCompatActivity {

    Context mContext = AllowLocationActivity.this;
    ActivityAllowLocationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_allow_location);
        init();
    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btAllow.setOnClickListener(v -> {
            startActivity(new Intent(mContext, GetStartedActivity.class));
        });

    }


}