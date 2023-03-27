package com.cityone.entertainment.activities;

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

import com.cityone.R;
import com.cityone.databinding.ActivityEntertainmentDetailsBinding;
import com.cityone.databinding.EntDetailDialogBinding;
import com.cityone.entertainment.adapters.AdapterEntCat;
import com.cityone.entertainment.adapters.AdapterTickets;
import com.cityone.entertainment.models.ModelEntCat;
import com.cityone.entertainment.models.ModelEntDetails;
import com.cityone.entertainment.models.ModelEntTicket;
import com.cityone.models.ModelLogin;
import com.cityone.stores.adapters.AdapterStoreCat;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.App;
import com.cityone.utils.AppConstant;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EntDetailsActivity extends AppCompatActivity {

    Context mContext = EntDetailsActivity.this;
    ActivityEntertainmentDetailsBinding binding;
    String id = "";
    SharedPref sharedPref;
    ModelLogin modelLogin;
    ModelEntDetails modelEntDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_entertainment_details);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        App.checkToken(mContext);

        id = getIntent().getStringExtra("id");
        getEntDetails();

        init();

    }

    private void init() {

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(modelEntDetails != null)
                    getAllTickets(modelEntDetails.getResult().getEntertentment_category_id(),modelEntDetails.getResult().getImage());
                else
                    binding.swipLayout.setRefreshing(false);
            }
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.tvGoToCart.setOnClickListener(v -> {
            startActivity(new Intent(mContext,EntCartActivity.class));
        });

    }

    private void getEntDetails() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        HashMap<String,String> params = new HashMap<>();
        params.put("entertentment_id",id);

        Log.e("fasfdasdfsads","entertentment_id = " + id);

        Call<ResponseBody> call = api.getEntDetailsCall(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        modelEntDetails = new Gson().fromJson(responseString, ModelEntDetails.class);

                        try {
                            Picasso.get().load(modelEntDetails.getResult().getImage()).into(binding.ivPartyImage);
                        } catch (Exception e) {}
                        binding.tvResName.setText(modelEntDetails.getResult().getName());
                        binding.tvAddress.setText(modelEntDetails.getResult().getAddress());
                        binding.tvTime.setText(modelEntDetails.getResult().getOpen_time() + " - " +
                                modelEntDetails.getResult().getClose_time());

                        getAllTickets(modelEntDetails.getResult().getEntertentment_category_id(),modelEntDetails.getResult().getImage());

                    } else {}

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    private void getAllTickets(String entertentment_category_id,String image) {
        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        HashMap<String,String> params = new HashMap<>();
        params.put("entertentment_category_id",entertentment_category_id);

        Call<ResponseBody> call = api.getEntTicketCall(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        ModelEntTicket modelEntTicket = new Gson().fromJson(responseString, ModelEntTicket.class);

                        AdapterTickets adapterTickets = new AdapterTickets(mContext,modelEntTicket.getResult(),image,EntDetailsActivity.this::updateCounter,id);
                        binding.rvTickets.setAdapter(adapterTickets);

                    } else {}

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCartCount();
    }

    private void getCartCount() {
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> param = new HashMap<>();
        param.put("user_id",modelLogin.getResult().getId());

        Call<ResponseBody> call = api.getCartCountEntApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("fsdfdsfds","response = " + jsonObject.getInt("count"));

                    if(jsonObject.getString("status").equals("1")) {
                        binding.tvCartCount.setText(jsonObject.getInt("count") + " Ticket");
                    } else {
                        binding.tvCartCount.setText("0 Ticket");
                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
            }

        });
    }


    public void updateCounter(int count) {
        if(1234 == count) getCartCount();
        else binding.tvCartCount.setText(count + " Ticket");
    }

    private void ticketDetailDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        EntDetailDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                ,R.layout.ent_detail_dialog,null,false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.tvAddTicket.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }


}

