package com.cityone.fragments;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.cityone.R;
import com.cityone.activities.GetStartedActivity;
import com.cityone.activities.WalletActivity;
import com.cityone.databinding.FragmentAccountBinding;
import com.cityone.models.ModelLogin;
import com.cityone.utils.AppConstant;
import com.cityone.utils.SharedPref;
import com.squareup.picasso.Picasso;

public class AccountFragment extends Fragment {

    Context mContext;
    FragmentAccountBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_account, container, false);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        init();

        // Inflate the layout for this fragment
        return binding.getRoot();

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
                        getActivity().finishAffinity();
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