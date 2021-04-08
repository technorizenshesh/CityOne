package com.cityone.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import com.cityone.R;
import com.cityone.databinding.ActivityMyReviewsBinding;

public class MyReviewsActivity extends AppCompatActivity {

    Context mContext = MyReviewsActivity.this;
    ActivityMyReviewsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_my_reviews);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }


}