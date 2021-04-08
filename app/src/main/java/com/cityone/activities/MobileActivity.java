package com.cityone.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cityone.R;
import com.cityone.databinding.ActivityMobileBinding;

public class MobileActivity extends AppCompatActivity {

    Context mContext = MobileActivity.this;
    ActivityMobileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_mobile);

        init();

    }

    private void init() {
        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.next.setOnClickListener(v -> {
            startActivity(new Intent(mContext,VerifyOtpActivity.class));
        });
    }


}