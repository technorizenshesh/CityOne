package com.cityone.taxi.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.cityone.R;
import com.cityone.databinding.ActivityTripHistoryBinding;
import com.cityone.models.ModelLogin;
import com.cityone.taxi.adapters.AdapterTripHistory;
import com.cityone.taxi.models.ModelTripHistory;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.App;
import com.cityone.utils.AppConstant;
import com.cityone.utils.PaypalClientId;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
//import com.paypal.android.sdk.payments.PayPalConfiguration;
//import com.paypal.android.sdk.payments.PayPalPayment;
//import com.paypal.android.sdk.payments.PayPalService;
//import com.paypal.android.sdk.payments.PaymentActivity;
//import com.paypal.android.sdk.payments.PaymentConfirmation;
// import com.mercadopago.android.px.core.MercadoPagoCheckout;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;

import lib.android.paypal.com.magnessdk.Environment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripHistoryAct extends AppCompatActivity {

    Context mContext = TripHistoryAct.this;
    ActivityTripHistoryBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    String clientToken = "";
    int position;
    // MercadoPagoCheckout checkout;
    AdapterTripHistory adapterTripHistory;
    private ModelTripHistory modelTripHistory;
    private int PAYPAL_REQUEST_CODE = 101;

    ModelTripHistory.Result dataResult;
    String requestId = "";
    String tripAmount = "";
    int taxiPosition = 0;
    private PayPalConfiguration payPalConfiguration = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PaypalClientId.PAYPAL_CLIENT_ID_SENDBOX);
    private BraintreeFragment braintree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_trip_history);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        App.checkToken(mContext);

        try {
            braintree = BraintreeFragment.newInstance(TripHistoryAct.this,getString(R.string.tokenizationKey));
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(mContext, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,payPalConfiguration);
        startService(intent);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        getPendingRequest();

        TabLayout.Tab pendingTab = binding.tabLayout.newTab();
        pendingTab.setText("Pending");
        binding.tabLayout.addTab(pendingTab);

        TabLayout.Tab activeTab = binding.tabLayout.newTab();
        activeTab.setText("Active");
        binding.tabLayout.addTab(activeTab);

        TabLayout.Tab finishTab = binding.tabLayout.newTab();
        finishTab.setText("Finished");
        binding.tabLayout.addTab(finishTab);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0) {
                    position = 0;
                    getPendingRequest();
                } else if(tab.getPosition() == 1) {
                    position = 1;
                    getActiveBookings();
                } else {
                    position = 2;
                    getFinishedBookings();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(position == 0) {
                    getPendingRequest();
                } else if(position == 1) {
                    getActiveBookings();
                } else {
                    getFinishedBookings();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(mContext,PayPalService.class));
        super.onDestroy();
    }

    public void payNowClicked(ModelTripHistory.Result data,String payMethod,int position) {
        dataResult = data;
        tripAmount = data.getId();
        requestId = data.getEstimate_charge_amount();
        taxiPosition = position;
        makepaypalPayment(data,data.getId(),payMethod,data.getEstimate_charge_amount(),position);
//        doPayment(data,data.getId(),payMethod,data.getEstimate_charge_amount(),position);
//        checkout = new MercadoPagoCheckout.Builder("public_key", "checkout_preference_id")
//                .build();
//        checkout.startPayment(mContext, requestCodeMercadoPago);
    }

    private void makepaypalPayment(ModelTripHistory.Result data,String requestId
            ,String payMethod,String estimate_charge_amount,int position) {
        //getClientToken(data,requestId,payMethod,estimate_charge_amount,position);
        double amount = Double.parseDouble(data.getEstimate_charge_amount());
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal("10"),
                "USD","Trip",PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(mContext, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,payPalConfiguration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
        startActivityForResult(intent,PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       if(requestCode == PAYPAL_REQUEST_CODE) {
               if (resultCode == Activity.RESULT_OK) {

                   PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                   Log.e("kjghjgkjfkjdsf",confirm.toJSONObject().toString());
                   try {
                       Log.e("kjghjgkjfkjdsf",confirm.toJSONObject().toString(4));
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }

                   if (confirm != null) {
                       try {
                          // Getting the payment details
                          // String paymentDetails = confirm.toJSONObject().toString(4);
                          // Log.e("paymentExample", paymentDetails);

                          // Starting a new activity for the payment details and also putting the payment details with intent
//                           startActivity(new Intent(this, ConfirmationActivity.class)
//                                  .putExtra("PaymentDetails", paymentDetails)
//                                  .putExtra("PaymentAmount", paymentAmount));

                       } catch (Exception e) {
                           Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                       }
                   }
               } else if (resultCode == Activity.RESULT_CANCELED) {
                   Log.e("paymentExample", "The user canceled.");
               } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                   Log.e("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
               }
        }

    }

    private void getClientToken(ModelTripHistory.Result data,String requestId,
                                String payMethod,String estimate_charge_amount,int position) {

        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        Log.e("sfddsfdsfdsf","user_id = " + modelLogin.getResult().getId());

        Call<ResponseBody> call = api.getClientTokenApi();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);

                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    Log.e("kjagsdkjgaskjd","stringResponse = " + response);
                    Log.e("kjagsdkjgaskjd","stringResponse = " + stringResponse);

                    clientToken = jsonObject.getString("result");
                    Log.e("kjagsdkjgaskjd","clientToken = " + clientToken);
                    DropInRequest dropInRequest = new DropInRequest()
                            .tokenizationKey(/*"sandbox_f252zhq7_hh4cpc39zq4rgjcg"*/ getString(R.string.tokenizationKey));
                            //.clientToken("client_id$sandbox$zrcjnv838t9m39kf");
                    Log.e("kjagsdkjgaskjd","dropInRequest.isPayPalEnabled() = " + dropInRequest.isPayPalEnabled());
                    dropInRequest.amount(data.getEstimate_charge_amount());
                    startActivityForResult(dropInRequest.getIntent(mContext),PAYPAL_REQUEST_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                Log.e("kjagsdkjgaskjd","stringResponse = " + t.getMessage());
            }

        });

    }

    private void doPayment(ModelTripHistory.Result data,String requestId,String payMethod,String estimate_charge_amount,int position) {

        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        Log.e("sfddsfdsfdsf","user_id = " + modelLogin.getResult().getId());

        HashMap<String,String> params = new HashMap<>();
        params.put("request_id",requestId);
        params.put("user_id",modelLogin.getResult().getId());
        params.put("amount",estimate_charge_amount);
        params.put("payment_type",payMethod);

        Log.e("hjadkjshakjdhkjas","params = " + params);

        Call<ResponseBody> call = api.doTaxiPayment(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);

                Log.e("kghkljsdhkljf","response = " + response);

                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    Log.e("kjagsdkjgaskjd","stringResponse = " + response);
                    Log.e("kjagsdkjgaskjd","stringResponse = " + stringResponse);

                    if (jsonObject.getString("status").equals("1")) {
                        modelTripHistory.getResult().get(position).setStatus("Paid");
                        adapterTripHistory = new AdapterTripHistory(mContext,modelTripHistory.getResult(),"Finish",TripHistoryAct.this::payNowClicked);
                        binding.rvHistory.setAdapter(adapterTripHistory);
                        Toast.makeText(TripHistoryAct.this, "Payment Success", Toast.LENGTH_SHORT).show();
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                Log.e("kjagsdkjgaskjd","stringResponse = " + t.getMessage());
            }

        });

    }

    private void getPendingRequest() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        Log.e("sfddsfdsfdsf","user_id = " + modelLogin.getResult().getId());

        HashMap<String,String> params = new HashMap<>();
        params.put("user_id",modelLogin.getResult().getId());

        Log.e("hjadkjshakjdhkjas","params = " + params);

        Call<ResponseBody> call = api.getPendingBooking(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);

                Log.e("kghkljsdhkljf","response = " + response);

                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    Log.e("kjagsdkjgaskjd","stringResponse = " + response);
                    Log.e("kjagsdkjgaskjd","stringResponse = " + stringResponse);

                    if (jsonObject.getString("status").equals("1")) {

                        modelTripHistory = new Gson().fromJson(stringResponse,ModelTripHistory.class);

                        adapterTripHistory = new AdapterTripHistory(mContext,modelTripHistory.getResult(),"Pending",TripHistoryAct.this::payNowClicked);
                        binding.rvHistory.setAdapter(adapterTripHistory);
                    } else {
                        adapterTripHistory = new AdapterTripHistory(mContext,null,"Pending",TripHistoryAct.this::payNowClicked);
                        binding.rvHistory.setAdapter(adapterTripHistory);
                        Toast.makeText(mContext, getString(R.string.no_trip_avail), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                Log.e("kjagsdkjgaskjd","stringResponse = " + t.getMessage());
            }

        });
    }

    private void getActiveBookings() {
        ProjectUtil.showProgressDialog(mContext,false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        Log.e("sfddsfdsfdsf","user_id = " + modelLogin.getResult().getId());

        HashMap<String,String> params = new HashMap<>();
        params.put("user_id",modelLogin.getResult().getId());
        params.put("type","USER");

        Log.e("hjadkjshakjdhkjas","params = " + params);

        Call<ResponseBody> call = api.getActiveBooking(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);

                Log.e("kghkljsdhkljf","response = " + response);

                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    Log.e("kjagsdkjgaskjd","stringResponse = " + response);
                    Log.e("kjagsdkjgaskjd","stringResponse = " + stringResponse);

                    if (jsonObject.getString("status").equals("1")) {

                        modelTripHistory = new Gson().fromJson(stringResponse,ModelTripHistory.class);

                        adapterTripHistory = new AdapterTripHistory(mContext,modelTripHistory.getResult(),"Active",TripHistoryAct.this::payNowClicked);
                        binding.rvHistory.setAdapter(adapterTripHistory);
                    } else {
                        adapterTripHistory = new AdapterTripHistory(mContext,null,"Pending",TripHistoryAct.this::payNowClicked);
                        binding.rvHistory.setAdapter(adapterTripHistory);
                        Toast.makeText(mContext, getString(R.string.no_trip_avail), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                Log.e("kjagsdkjgaskjd","stringResponse = " + t.getMessage());
            }

        });
    }

    private void getFinishedBookings() {
        ProjectUtil.showProgressDialog(mContext,false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        Log.e("sfddsfdsfdsf","user_id = " + modelLogin.getResult().getId());

        HashMap<String,String> params = new HashMap<>();
        params.put("user_id",modelLogin.getResult().getId());
        params.put("type","USER");

        Log.e("hjadkjshakjdhkjas","params = " + params);

        Call<ResponseBody> call = api.getFinishedBooking(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);

                Log.e("kghkljsdhkljf","response = " + response);

                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    Log.e("kjagsdkjgaskjd","stringResponse = " + response);
                    Log.e("kjagsdkjgaskjd","stringResponse = " + stringResponse);

                    if (jsonObject.getString("status").equals("1")) {

                        modelTripHistory = new Gson().fromJson(stringResponse,ModelTripHistory.class);

                        adapterTripHistory = new AdapterTripHistory(mContext,modelTripHistory.getResult(),"Finish",TripHistoryAct.this::payNowClicked);
                        binding.rvHistory.setAdapter(adapterTripHistory);
                    } else {
                        adapterTripHistory = new AdapterTripHistory(mContext,null,"Finish",TripHistoryAct.this::payNowClicked);
                        binding.rvHistory.setAdapter(adapterTripHistory);
                        Toast.makeText(mContext, getString(R.string.no_trip_avail), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                Log.e("kjagsdkjgaskjd","stringResponse = " + t.getMessage());
            }

        });
    }

}