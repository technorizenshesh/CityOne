package com.cityone.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import com.cityone.R;
import com.cityone.databinding.ActivityWalletBinding;
import com.cityone.databinding.AddMoneyDialogBinding;
import com.cityone.databinding.SendMoneyDialogBinding;

public class WalletActivity extends AppCompatActivity {

    Context mContext = WalletActivity.this;
    ActivityWalletBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_wallet);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.cvAddMoney.setOnClickListener(v -> {
            addMoneyDialog();
        });

        binding.cvTransfer.setOnClickListener(v -> {
            tranferMOneyDialog();
        });

    }

    private void tranferMOneyDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        SendMoneyDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext),R.layout.send_money_dialog,null,false);

        dialogBinding.btDone.setOnClickListener(v -> {
            dialog.dismiss();
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.setContentView(dialogBinding.getRoot());

        dialog.show();
    }

    private void addMoneyDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        AddMoneyDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext),R.layout.add_money_dialog,null,false);

        dialogBinding.btAdd.setOnClickListener(v -> {
            dialog.dismiss();
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.setContentView(dialogBinding.getRoot());

        dialog.show();

    }




}