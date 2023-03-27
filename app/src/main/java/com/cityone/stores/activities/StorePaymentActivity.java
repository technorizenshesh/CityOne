package com.cityone.stores.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.braintreepayments.cardform.view.CardForm;
import com.cityone.R;
import com.cityone.activities.DashboardActivity;
import com.cityone.databinding.ActivityStorePaymentBinding;
import com.cityone.databinding.AddCardDialogBinding;
import com.cityone.models.ModelLogin;
import com.cityone.parentmodels.ModelPayCards;
import com.cityone.parentmodels.ModelPayCardsPro;
import com.cityone.stores.adapters.AdapterCards;
import com.cityone.stores.models.ModelCards;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.App;
import com.cityone.utils.AppConstant;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.gson.Gson;
import com.redeban.payment.model.Card;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StorePaymentActivity extends AppCompatActivity {

    Context mContext = StorePaymentActivity.this;
    ActivityStorePaymentBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    AlertDialog.Builder alertBuilder;
    private HashMap<String, String> param = new HashMap<>();
    String timZone = TimeZone.getDefault().getID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_store_payment);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        param = (HashMap<String, String>) getIntent().getSerializableExtra("param");
        App.checkToken(mContext);
        init();
    }

    private void init() {

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
//                                ModelPayCards.Result data;

//                                modelPayCards.setMessage(jsonObject.getString("message"));
//                                modelPayCards.setStatus(jsonObject.getString("status"));
//
//                                data = new Gson().fromJson(jsonObject.getString("result"),ModelPayCards.Result.class);
//                                modelPayCards.setResult(data);

                                AdapterCards adapterCards = new AdapterCards(mContext, modelPayCardsPro.getCards(), "store");
                                binding.rvCards.setAdapter(adapterCards);

                            } else {
                                Toast.makeText(mContext, getString(R.string.please_add_card), Toast.LENGTH_SHORT).show();
                                AdapterCards adapterCards = new AdapterCards(mContext, null, "store");
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

                //        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
//        Call<ResponseBody> call = api.getCardApiCall(param);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                ProjectUtil.pauseProgressDialog();
//                binding.swipReferesh.setRefreshing(false);
//                try {
//                    String stringRes = response.body().string();
//                    JSONObject jsonObject = new JSONObject(stringRes);
//                    if(jsonObject.getString("status").equals("1")) {
//                        ModelCards modelCards = new Gson().fromJson(stringRes,ModelCards.class);
//                        AdapterCards adapterCards = new AdapterCards(mContext,modelCards.getResult());
//                        binding.rvCards.setAdapter(adapterCards);
//                    } else {
//                        Toast.makeText(mContext,getString(R.string.please_add_card), Toast.LENGTH_SHORT).show();
//                        AdapterCards adapterCards = new AdapterCards(mContext,null);
//                        binding.rvCards.setAdapter(adapterCards);
//                    }
//                } catch (Exception e) {}
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                ProjectUtil.pauseProgressDialog();
//                binding.swipReferesh.setRefreshing(false);
//            }
//        });

    }

    public void callBookingApiFromAdapter(String token) {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        Call<ResponseBody> call = api.bookingStoreApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringRes = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringRes);
                    if (jsonObject.getString("status").equals("1")) {

                        JSONObject resultJson = jsonObject.getJSONObject("result");

                        HashMap<String, String> param = new HashMap<>();

                        param.put("transaction_type", "transaction_type");
                        param.put("payment_type", "Card");
                        param.put("amount", resultJson.getString("total_amount"));
                        param.put("user_id", modelLogin.getResult().getId());
                        param.put("order_id", resultJson.getString("id"));
                        param.put("restaurant_id", resultJson.getString("restaurant_id"));
                        param.put("time_zone", timZone);
                        param.put("token", token);
                        param.put("currency", "INR");
                        param.put("tip", "0");

                        Log.e("paramparam", "param = " + param.toString());

                        paymentApiCall(token, resultJson.getString("total_amount"));

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

    private void paymentApiCall(String token, String amount) {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
//        HashMap<String,String> paramas = new HashMap<>();
//        paramas.put("amount",amount);
//        paramas.put("token",token);
//        paramas.put("request_id","");
//        paramas.put("user_id",modelLogin.getResult().getId());
//        paramas.put("email",modelLogin.getResult().getEmail());

        Log.e("adasdasd", "PaymentUrl = " + AppConstant.PAY_PAYMENT_API +
                "amount=" + amount +
                "&token=" + token +
                "&request_id=" +
                "&user_id=" + modelLogin.getResult().getId() +
                "&email=" + modelLogin.getResult().getEmail());

        AndroidNetworking.post(AppConstant.PAY_PAYMENT_API +
                "amount=" + amount +
                "&token=" + token +
                "&request_id=" +
                "&user_id=" + modelLogin.getResult().getId() +
                "&email=" + modelLogin.getResult().getEmail())
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        ProjectUtil.pauseProgressDialog();
                        try {
                            String stringRes = response;

                            Log.e("stringResstringRes", "stringRes = " + stringRes);

                            JSONObject jsonObject = new JSONObject(stringRes);
                            if (jsonObject.getString("status").equals("1")) {
                                Toast.makeText(StorePaymentActivity.this, getString(R.string.order_placed), Toast.LENGTH_SHORT).show();
                               // finishAffinity();
                                startActivity(new Intent(mContext, DashboardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                 finish();
                            } else {
                            }
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        ProjectUtil.pauseProgressDialog();
                    }

                });

            //        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
//        Call<ResponseBody> call = api.paymentStoreApiCall(param);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                ProjectUtil.pauseProgressDialog();
//                try {
//                    String stringRes = response.body().string();
//
//                    Log.e("stringResstringRes","stringRes = " + stringRes);
//
//                    JSONObject jsonObject = new JSONObject(stringRes);
//                    if(jsonObject.getString("status").equals("1")) {
//                        Toast.makeText(StorePaymentActivity.this, getString(R.string.order_placed), Toast.LENGTH_SHORT).show();
//                        finishAffinity();
//                        startActivity(new Intent(mContext, DashboardActivity.class));
//                    } else {
//
//                    }
//                } catch (Exception e) {
//
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                ProjectUtil.pauseProgressDialog();
//            }
//
//        });

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

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        Log.e("sdfgdsfd", "param = " + param.toString());

//        Call<ResponseBody> call = api.addCardApiCall(param);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                ProjectUtil.pauseProgressDialog();
//                try {
//                    String stringRes = response.body().string();
//                    JSONObject jsonObject = new JSONObject(stringRes);
//
//                    Log.e("dfsdfdsf","stringRes = " + stringRes);
//
//                    if(jsonObject.getString("status").equals("1")) {
//                        dialog.dismiss();
//                        getAvailableCardApi();
//                    } else {
//
//                    }
//                } catch (Exception e) {
//
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                ProjectUtil.pauseProgressDialog();
//            }
//        });

    }


}