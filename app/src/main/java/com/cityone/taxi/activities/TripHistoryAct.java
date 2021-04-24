package com.cityone.taxi.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cityone.R;
import com.cityone.databinding.ActivityTripHistoryBinding;
import com.cityone.models.ModelLogin;
import com.cityone.taxi.adapters.AdapterTripHistory;
import com.cityone.taxi.models.ModelTripHistory;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.AppConstant;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
// import com.mercadopago.android.px.core.MercadoPagoCheckout;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripHistoryAct extends AppCompatActivity {

    Context mContext = TripHistoryAct.this;
    ActivityTripHistoryBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    int position;
    // MercadoPagoCheckout checkout;
    private int requestCodeMercadoPago = 101;
    AdapterTripHistory adapterTripHistory;
    private ModelTripHistory modelTripHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_trip_history);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

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

    public void payNowClicked(ModelTripHistory.Result data,String payMethod,int position) {
        doPayment(data,data.getId(),payMethod,data.getEstimate_charge_amount(),position);
//        checkout = new MercadoPagoCheckout.Builder("public_key", "checkout_preference_id")
//                .build();
//        checkout.startPayment(mContext, requestCodeMercadoPago);
    }

    private void doPayment(ModelTripHistory.Result data,String requestId,String payMethod,String estimate_charge_amount,int position) {

        ProjectUtil.showProgressDialog(mContext,false, getString(R.string.please_wait));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("sfdasdfasd","requestCode = " + requestCode);
        Log.e("sfdasdfasd","resultCode = " + resultCode);
        Log.e("sfdasdfasd","data = " + data.getExtras());
    }

    private void getPendingRequest() {
        ProjectUtil.showProgressDialog(mContext,false, getString(R.string.please_wait));
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