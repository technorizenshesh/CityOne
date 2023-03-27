package com.cityone.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import com.cityone.R;
import com.cityone.databinding.ActivityUpdateProfileBinding;
import com.cityone.models.ModelLogin;
import com.cityone.utils.AppConstant;
import com.cityone.utils.SharedPref;

public class UpdateProfileActivity extends AppCompatActivity {

    Context mContext = UpdateProfileActivity.this;
    ActivityUpdateProfileBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_update_profile);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    private void itit() {

        binding.etUsername.setText(modelLogin.getResult().getUser_name());
        binding.etEmail.setText(modelLogin.getResult().getEmail());
        binding.etPhone.setText(modelLogin.getResult().getMobile());
        binding.etAddress.setText(modelLogin.getResult().getAddress());
        binding.etLandmark.setText(modelLogin.getResult().getLand_mark());

    }


}