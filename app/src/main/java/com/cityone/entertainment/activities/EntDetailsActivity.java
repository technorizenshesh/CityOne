package com.cityone.entertainment.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;

import com.cityone.R;
import com.cityone.databinding.ActivityEntertainmentDetailsBinding;
import com.cityone.databinding.EntDetailDialogBinding;

public class EntDetailsActivity extends AppCompatActivity {

    Context mContext = EntDetailsActivity.this;
    ActivityEntertainmentDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_entertainment_details);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.tvDetail.setOnClickListener(v -> {
            ticketDetailDialog();
        });

        binding.tvGoToCart.setOnClickListener(v -> {
            startActivity(new Intent(mContext,EntCartActivity.class));
        });

    }

    private void ticketDetailDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        EntDetailDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                ,R.layout.ent_detail_dialog,null,false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.tvAddTicket.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }


}

