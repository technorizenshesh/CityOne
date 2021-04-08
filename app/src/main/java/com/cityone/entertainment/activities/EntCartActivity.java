package com.cityone.entertainment.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.cityone.R;
import com.cityone.activities.PaymentMethodActivity;
import com.cityone.databinding.ActivityEntCartBinding;

public class EntCartActivity extends AppCompatActivity {

    Context mContext = EntCartActivity.this;
    ActivityEntCartBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_ent_cart);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btCheckout.setOnClickListener(v -> {
            startActivity(new Intent(mContext, PaymentMethodActivity.class));
        });

    }


}