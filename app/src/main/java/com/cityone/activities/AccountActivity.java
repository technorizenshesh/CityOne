package com.cityone.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.cityone.R;
import com.cityone.databinding.ActivityAccountBinding;
import com.cityone.models.ModelLogin;
import com.cityone.utils.AppConstant;
import com.cityone.utils.SharedPref;
import com.squareup.picasso.Picasso;

public class AccountActivity extends AppCompatActivity {

    Context mContext = AccountActivity.this;
    ActivityAccountBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_account);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        init();

    }

    private void init() {

        binding.tvName.setText(modelLogin.getResult().getUser_name());
        binding.tvEmail.setText(modelLogin.getResult().getEmail());

        Picasso.get().load(modelLogin.getResult().getImage())
                .placeholder(R.drawable.default_profile_icon)
                .error(R.drawable.default_profile_icon)
                .into(binding.ivProfile);

        binding.btLogout.setOnClickListener(v -> {
            logoutAppDialog();
        });

        binding.ivWallet.setOnClickListener(v -> {
            startActivity(new Intent(mContext, WalletActivity.class));
        });

        binding.ivMyOrders.setOnClickListener(v -> {
            // startActivity(new Intent(mContext, MyOrdersActivity.class));
        });

        binding.ivDevAdd.setOnClickListener(v -> {
            // startActivity(new Intent(mContext, DeliveryAddressActivity.class));
        });

        binding.ivSettings.setOnClickListener(v -> {
            // startActivity(new Intent(mContext, SettingsActivity.class));
        });

        binding.ivMyReviews.setOnClickListener(v -> {
            // startActivity(new Intent(mContext, MyReviewsActivity.class));
        });

    }

    private void logoutAppDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.logout_text))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPref.clearAllPreferences();
                        Intent loginscreen = new Intent(mContext, GetStartedActivity.class);
                        loginscreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        NotificationManager nManager = ((NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE));
                        nManager.cancelAll();
                        startActivity(loginscreen);
                        finishAffinity();
                    }
                }).setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

}