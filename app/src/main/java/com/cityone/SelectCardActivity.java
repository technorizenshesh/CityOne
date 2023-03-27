package com.cityone;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.cityone.activities.DashboardActivity;
import com.cityone.activities.WalletActivity;
import com.cityone.databinding.ActivitySelectCardBinding;
import com.cityone.databinding.AddCardDialogBinding;
import com.cityone.models.ModelLogin;
import com.cityone.parentmodels.ModelPayCardsPro;
import com.cityone.stores.activities.StorePaymentActivity;
import com.cityone.stores.adapters.AdapterCards;
import com.cityone.stores.adapters.AdapterStoreCat;
import com.cityone.stores.models.ModelStoreCat;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.AppConstant;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.gson.Gson;
import com.redeban.payment.model.Card;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectCardActivity extends AppCompatActivity {

    Context mContext = SelectCardActivity.this;
    ActivitySelectCardBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    String amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_card);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        amount = getIntent().getStringExtra("amount");
        itit();
    }

    private void itit() {

        binding.swipReferesh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAvailableCardApi();
            }
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        getAvailableCardApi();

        binding.ivAddCard.setOnClickListener(v -> {
            addCardDialog();
        });

    }

    private void getAvailableCardApi() {
        ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));

        Log.e("getAvailableCardApi", "stringRes = " + AppConstant.PAY_GET_ALL_CARD
                + "user_id=" + modelLogin.getResult().getId());

        AndroidNetworking.post(AppConstant.PAY_GET_ALL_CARD
                + "user_id=" + modelLogin.getResult().getId())
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        ProjectUtil.pauseProgressDialog();
                        binding.swipReferesh.setRefreshing(false);
                        try {
                            String stringRes = response;
                            Log.e("stringResstringRes", "stringRes = " + stringRes);
                            JSONObject jsonObject = new JSONObject(stringRes);
                            if (jsonObject.getInt("result_size") != 0) {
                                ModelPayCardsPro modelPayCardsPro = new Gson().fromJson(stringRes, ModelPayCardsPro.class);

                                AdapterCards adapterCards = new AdapterCards(mContext, modelPayCardsPro.getCards(), "wallet");
                                binding.rvCards.setAdapter(adapterCards);

                            } else {
                                Toast.makeText(mContext, getString(R.string.please_add_card), Toast.LENGTH_SHORT).show();
                                AdapterCards adapterCards = new AdapterCards(mContext, null, "wallet");
                                binding.rvCards.setAdapter(adapterCards);
                            }
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        ProjectUtil.pauseProgressDialog();
                        binding.swipReferesh.setRefreshing(false);
                    }
                });

    }

    public void paymentApiCall(String token) {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> param = new HashMap<>();
        param.put("amount", amount);
        param.put("token", token);
        param.put("user_id", modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.addWalletAmountApi(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();

                try {
                    String responseString = response.body().string();

                    Log.e("responseString", "responseString = " + responseString);

                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        startActivity(new Intent(mContext, WalletActivity.class));
                        finish();
                    } else {

                    }

                } catch (Exception e) {
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });


    }

    private void addCardDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);

        AddCardDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.add_card_dialog,
                        null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.btMakePayment.setOnClickListener(v -> {

            Card cardToSave = dialogBinding.cardForm.getCard();

            if (cardToSave == null) {
                Toast.makeText(mContext, getString(R.string.Invalid_card_info), Toast.LENGTH_SHORT).show();
                return;
            } else {

                HashMap<String, String> param = new HashMap<>();
                param.put("user_id", modelLogin.getResult().getId());
                param.put("number", cardToSave.getNumber());
                param.put("holder_name", cardToSave.getHolderName());
                param.put("expiry_month", String.valueOf(cardToSave.getExpiryMonth()));
                param.put("expiry_year", String.valueOf(cardToSave.getExpiryYear()));
                param.put("cvc", cardToSave.getCVC());
                param.put("email", modelLogin.getResult().getEmail());

                addCardApi(dialog,
                        cardToSave.getNumber(),
                        cardToSave.getHolderName(),
                        String.valueOf(cardToSave.getExpiryMonth()),
                        String.valueOf(cardToSave.getExpiryYear()),
                        cardToSave.getCVC());
            }

        });

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    private void addCardApi(Dialog dialog, String number,
                            String holderName,
                            String exp_mon,
                            String exp_year,
                            String cvc) {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        Log.e("sdfsdfdsf", "URL = " + AppConstant.PAY_ADD_CARD +
                "number=" + number + "&" +
                "holder_name=" + holderName + "&" +
                "expiry_month=" + exp_mon + "&" +
                "expiry_year=" + exp_year + "&" +
                "cvc=" + cvc + "&" +
                "user_id=" + modelLogin.getResult().getId() + "&" +
                "email=" + modelLogin.getResult().getEmail());

        AndroidNetworking.post(AppConstant.PAY_ADD_CARD +
                "number=" + number + "&" +
                "holder_name=" + holderName + "&" +
                "expiry_month=" + exp_mon + "&" +
                "expiry_year=" + exp_year + "&" +
                "cvc=" + cvc + "&" +
                "user_id=" + modelLogin.getResult().getId() + "&" +
                "email=" + modelLogin.getResult().getEmail())
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        ProjectUtil.pauseProgressDialog();
                        try {
                            String stringRes = response;
                            JSONObject jsonObject = new JSONObject(stringRes);

                            Log.e("dfsdfdsf", "stringRes = " + stringRes);
                            // Log.e("dfsdfdsf","params = " + params.toString());

                            if (jsonObject.getString("status").equals("1")) {
                                dialog.dismiss();
                                getAvailableCardApi();
                            } else {
                            }
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        ProjectUtil.pauseProgressDialog();
                        Log.e("dfsdfdsf", "anError = " + anError.getErrorBody());
                        Log.e("dfsdfdsf", "anError = " + anError.getErrorDetail());
                    }
                });

    }


}