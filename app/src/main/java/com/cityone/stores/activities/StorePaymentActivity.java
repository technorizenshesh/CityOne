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

import com.braintreepayments.cardform.view.CardForm;
import com.cityone.R;
import com.cityone.activities.DashboardActivity;
import com.cityone.databinding.ActivityStorePaymentBinding;
import com.cityone.databinding.AddCardDialogBinding;
import com.cityone.models.ModelLogin;
import com.cityone.stores.adapters.AdapterCards;
import com.cityone.stores.models.ModelCards;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.AppConstant;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.gson.Gson;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

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
        binding = DataBindingUtil.setContentView(this,R.layout.activity_store_payment);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        param = (HashMap<String, String>) getIntent().getSerializableExtra("param");

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
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));

        HashMap<String,String> param = new HashMap<>();
        param.put("user_id",modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getCardApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipReferesh.setRefreshing(false);
                try {
                    String stringRes = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringRes);
                    if(jsonObject.getString("status").equals("1")) {
                        ModelCards modelCards = new Gson().fromJson(stringRes,ModelCards.class);
                        AdapterCards adapterCards = new AdapterCards(mContext,modelCards.getResult());
                        binding.rvCards.setAdapter(adapterCards);
                    } else {
                        Toast.makeText(mContext,getString(R.string.please_add_card), Toast.LENGTH_SHORT).show();
                        AdapterCards adapterCards = new AdapterCards(mContext,null);
                        binding.rvCards.setAdapter(adapterCards);
                    }
                } catch (Exception e) {}

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipReferesh.setRefreshing(false);
            }

        });

    }

    public void callBookingApiFromAdapter(String token) {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.bookingStoreApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringRes = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringRes);
                    if(jsonObject.getString("status").equals("1")) {

                        JSONObject resultJson = jsonObject.getJSONObject("result");

                        HashMap<String,String> param = new HashMap<>();

                        param.put("transaction_type","transaction_type");
                        param.put("payment_type","Card");
                        param.put("amount",resultJson.getString("total_amount"));
                        param.put("user_id",modelLogin.getResult().getId());
                        param.put("order_id",resultJson.getString("id"));
                        param.put("restaurant_id",resultJson.getString("restaurant_id"));
                        param.put("time_zone",timZone);
                        param.put("token",token);
                        param.put("currency","INR");
                        param.put("tip","0");

                        Log.e("paramparam","param = " + param.toString());

                        paymentApiCall(param);

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

    private void paymentApiCall(HashMap<String,String> param) {

        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.paymentStoreApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringRes = response.body().string();

                    Log.e("stringResstringRes","stringRes = " + stringRes);

                    JSONObject jsonObject = new JSONObject(stringRes);
                    if(jsonObject.getString("status").equals("1")) {
                        Toast.makeText(StorePaymentActivity.this, getString(R.string.order_placed), Toast.LENGTH_SHORT).show();
                        finishAffinity();
                        startActivity(new Intent(mContext, DashboardActivity.class));
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

    private void addCardDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);

        AddCardDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext),R.layout.add_card_dialog,null,false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                .cardholderName(CardForm.FIELD_REQUIRED)
                .setup(StorePaymentActivity.this);

        dialogBinding.cardForm.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        dialogBinding.btMakePayment.setOnClickListener(v -> {
            if (dialogBinding.cardForm.isValid()) {
                alertBuilder = new AlertDialog.Builder(mContext);
                alertBuilder.setTitle("Confirm before purchase");
                alertBuilder.setMessage("Card number: " + dialogBinding.cardForm.getCardNumber() + "\n" +
                        "Card expiry date: " + dialogBinding.cardForm.getExpirationDateEditText().getText().toString() + "\n" +
                        "Card CVV: " + dialogBinding.cardForm.getCvv() + "\n");
                alertBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        Card.Builder card = new Card.Builder(dialogBinding.cardForm.getCardNumber(),
                                Integer.valueOf(dialogBinding.cardForm.getExpirationMonth()),
                                Integer.valueOf(dialogBinding.cardForm.getExpirationYear()),
                                dialogBinding.cardForm.getCvv());

                        if (!card.build().validateCard()) {
                            Toast.makeText(mContext, "Card Not Valid", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Stripe stripe = new Stripe(mContext, "pk_test_51HGVYvEA8txfY5aoKS3kcgQ7PsKWlkRpwf0gB6R9hERR2iQyS7qNf1Gx5IfRkAOAf5SLBYrpdrRg3LadRPyl0fjo005Z3Z8G3Z");
                        Stripe stripe = new Stripe(mContext, getString(R.string.stripe_test_key));

                        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
                        stripe.createCardToken(
                                card.build(), new ApiResultCallback<Token>() {
                                    @Override
                                    public void onSuccess(Token token) {
                                        ProjectUtil.pauseProgressDialog();
                                        dialog.dismiss();
                                        String name = dialogBinding.cardForm.getCardholderName();

                                        Log.e("stripeToken","token = " + token.getId());
                                        addCardApi(name,dialog,dialogBinding.cardForm.getCardNumber(),
                                                dialogBinding.cardForm.getExpirationMonth()+"/"+
                                                        dialogBinding.cardForm.getExpirationYear(),
                                                 dialogBinding.cardForm.getExpirationMonth()
                                                ,dialogBinding.cardForm.getCvv(),token.getId());
                                    }

                                    @Override
                                    public void onError(@NotNull Exception e) {
                                        ProjectUtil.pauseProgressDialog();
                                    }
                                });
                    }
                });
                alertBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();

            } else {
                Toast.makeText(mContext, "Please complete the form", Toast.LENGTH_LONG).show();
            }
        });

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    private void addCardApi(String name,Dialog dialog,String cardNo,String expDate,String expMonth,String cvv,String tokenId) {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> param = new HashMap<>();
        param.put("user_id",modelLogin.getResult().getId());
        param.put("card_number",cardNo);
        param.put("expiry_date",expDate);
        param.put("expiry_month",expMonth);
        param.put("cvc_code",cvv);
        param.put("holder_name",name);

        Log.e("sdfgdsfd","param = " + param.toString());

        Call<ResponseBody> call = api.addCardApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringRes = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringRes);

                    Log.e("dfsdfdsf","stringRes = " + stringRes);

                    if(jsonObject.getString("status").equals("1")) {
                        dialog.dismiss();
                        getAvailableCardApi();
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


}