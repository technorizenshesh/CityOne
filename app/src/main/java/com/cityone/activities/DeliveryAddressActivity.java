package com.cityone.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.cityone.R;
import com.cityone.databinding.ActivityDeliveryAddressBinding;
import com.cityone.utils.AppConstant;

import java.util.HashMap;

public class DeliveryAddressActivity extends AppCompatActivity {

    Context mContext = DeliveryAddressActivity.this;
    ActivityDeliveryAddressBinding binding;
    HashMap<String,String> bookingParams = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_delivery_address);
        init();
    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.tvAddress.setOnClickListener(v -> {
            startActivity(new Intent(mContext,PaymentMethodActivity.class));
        });

        binding.ivAddress.setOnClickListener(v -> {
            addAddressDialog();
        });

        binding.tvEditAdd.setOnClickListener(v -> {
            addAddressDialog();
        });

    }

    private void addAddressDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.add_address_dialog);

        ImageView ivBack = dialog.findViewById(R.id.ivBack);

        ivBack.setOnClickListener(v -> {
            finish();
        });

        dialog.show();
    }

}


