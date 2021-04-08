package com.cityone.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import com.cityone.R;
import com.cityone.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    Context mContext = SettingsActivity.this;
    ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_settings);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btUpdate.setOnClickListener(v -> {
            finish();
        });

    }

}