package com.cityone.shipping.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.cityone.R;
import com.cityone.activities.AccountActivity;
import com.cityone.databinding.ActivityAccountBinding;
import com.cityone.databinding.FragmentShippingProfileBinding;
import com.cityone.databinding.FragmentStartBinding;
import com.cityone.models.ModelLogin;
import com.cityone.utils.AppConstant;
import com.cityone.utils.SharedPref;
import com.squareup.picasso.Picasso;

public class ShippingProfileFragment extends Fragment {
     FragmentShippingProfileBinding binding;
    Context mContext = getActivity();
    SharedPref sharedPref;
    ModelLogin modelLogin;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_shipping_profile, container, false);

        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        binding.tvName.setText(modelLogin.getResult().getUser_name());
        binding.tvEmail.setText(modelLogin.getResult().getEmail());

        Picasso.get().load(modelLogin.getResult().getImage())
                .placeholder(R.drawable.default_profile_icon)
                .error(R.drawable.default_profile_icon)
                .into(binding.ivProfile);


        return binding.getRoot();
    }
}
