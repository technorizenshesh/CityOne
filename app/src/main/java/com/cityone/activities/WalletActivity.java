package com.cityone.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.cityone.R;
import com.cityone.SelectCardActivity;
import com.cityone.databinding.ActivityWalletBinding;
import com.cityone.databinding.AddMoneyDialogBinding;
import com.cityone.databinding.SendMoneyDialogBinding;
import com.cityone.models.ModelLogin;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.App;
import com.cityone.utils.AppConstant;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletActivity extends AppCompatActivity {

    Context mContext = WalletActivity.this;
    ActivityWalletBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        init();
    }

    private void init() {

        App.checkToken(mContext);

        getProfile();

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

    private void getProfile() {

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getProfileApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringRes = response.body().string();

                    Log.e("stringResstringRes", "stringRes = " + stringRes);

                    JSONObject jsonObject = new JSONObject(stringRes);
                    if (jsonObject.getString("status").equals("1")) {
                        ModelLogin modelLogin = new Gson().fromJson(stringRes, ModelLogin.class);
                        sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);

                        binding.tvWalletAmount.setText("COP " + modelLogin.getResult().getWallet_amount());

                    } else {

                    }
                } catch (Exception e) {}

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    private void tranferMoneyApi(String money, String email) {
        ProjectUtil.showProgressDialog(mContext, true, "Please wait...");

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        param.put("email", email);
        param.put("amount", money);

        Log.e("stringResstringRes", "stringRes = " + param);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.sendMoneyApi(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringRes = response.body().string();

                    Log.e("stringResstringRes", "stringRes = " + stringRes);

                    JSONObject jsonObject = new JSONObject(stringRes);
                    if (jsonObject.getString("status").equals("1")) {
                        getProfile();
                    } else {

                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    private void tranferMOneyDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        SendMoneyDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.send_money_dialog, null, false);

        dialogBinding.btDone.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dialogBinding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, "Please enter recipient's email address", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(dialogBinding.etAmount.getText().toString().trim())) {
                Toast.makeText(mContext, "Please enter amount", Toast.LENGTH_SHORT).show();
            } else {
                if (ProjectUtil.isValidEmail(dialogBinding.etEmail.getText().toString().trim())) {
                    if (dialogBinding.etAmount.getText().toString().trim().equals("0")) {
                        Toast.makeText(mContext, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.dismiss();
                        tranferMoneyApi(dialogBinding.etAmount.getText().toString().trim(),
                                dialogBinding.etEmail.getText().toString().trim());
                    }
                } else {
                    Toast.makeText(mContext, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                }
            }
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
                .inflate(LayoutInflater.from(mContext), R.layout.add_money_dialog, null, false);

        dialogBinding.btAdd.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dialogBinding.etMoney.getText().toString().trim())) {
                Toast.makeText(mContext, "Please enter amount", Toast.LENGTH_SHORT).show();
            } else {
                if (dialogBinding.etMoney.getText().toString().trim().equals("0")) {
                    Toast.makeText(mContext, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    startActivity(new Intent(mContext, SelectCardActivity.class)
                            .putExtra("amount", dialogBinding.etMoney.getText().toString().trim())
                    );
                }

            }
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