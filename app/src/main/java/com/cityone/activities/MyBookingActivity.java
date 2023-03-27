package com.cityone.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.cityone.R;
import com.cityone.adapters.AdapterStoreBooking;
import com.cityone.databinding.ActivityMyOrdersBinding;
import com.cityone.models.ModelLogin;
import com.cityone.models.ModelStoreBooking;
import com.cityone.stores.adapters.AdapterStoreItemCat;
import com.cityone.stores.models.ModelStoreDetails;
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

public class MyBookingActivity extends AppCompatActivity {

    Context mContext = MyBookingActivity.this;
    ActivityMyOrdersBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_my_orders);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        App.checkToken(mContext);

        init();
    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMyOrder();
            }
        });

        getMyOrder();

    }

    private void getMyOrder() {
        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        Log.e("responseString","response = " + modelLogin.getResult().getId());

        HashMap<String,String> param = new HashMap<>();
        param.put("user_id",modelLogin.getResult().getId());

        Call<ResponseBody> call = api.getMyBookingApiCall(param);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        Log.e("responseString","response = " + response);
                        Log.e("responseString","responseString = " + responseString);

                        ModelStoreBooking modelStoreDetails = new Gson().fromJson(responseString, ModelStoreBooking.class);

                        AdapterStoreBooking adapterStoreItemCat = new AdapterStoreBooking(mContext, modelStoreDetails.getResult());
                        binding.rvMyOrder.setAdapter(adapterStoreItemCat);

                    } else {
                        AdapterStoreBooking adapterStores = new AdapterStoreBooking(mContext,null);
                        binding.rvMyOrder.setAdapter(adapterStores);
                    }

                } catch (Exception e) {
                    //Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                binding.swipLayout.setRefreshing(false);
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    private void orderDetailDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.food_order_detail_dialog);

        ImageView ivBack = dialog.findViewById(R.id.ivBack);

        ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }


}