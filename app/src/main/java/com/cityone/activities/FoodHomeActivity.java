package com.cityone.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cityone.R;
import com.cityone.databinding.ActivityFoodsBinding;
import com.cityone.databinding.ActivityStoresBinding;
import com.cityone.models.ModelLogin;
import com.cityone.stores.activities.MyCartActivity;
import com.cityone.stores.activities.StoresActivity;
import com.cityone.stores.adapters.AdapterStoreCat;
import com.cityone.stores.adapters.AdapterStoreCat1;
import com.cityone.stores.adapters.AdapterStores;
import com.cityone.stores.models.ModelStoreCat1;
import com.cityone.stores.models.ModelStores;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.App;
import com.cityone.utils.AppConstant;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodHomeActivity extends AppCompatActivity {

    Context mContext = FoodHomeActivity.this;
    ActivityFoodsBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    String id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_foods);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        App.checkToken(mContext);
        init();

       if(getIntent()!=null){
           id = getIntent().getStringExtra("id");
           getStoreCat();
       }
    }

    private void init() {

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getStoreCat();
            }
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.rlCart.setOnClickListener(v -> {
            startActivity(new Intent(mContext, MyCartActivity.class));
        });

    }

    private void getAllSTores(String id,String name) {
        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));

        HashMap<String,String> param = new HashMap<>();
        param.put("restaurant_category_id",id);
        param.put("restaurant_category_name",name);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getStoreByCatApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        Log.e("responseString","response = " + response);
                        Log.e("responseString","responseString = " + responseString);

                        ModelStores modelStores = new Gson().fromJson(responseString, ModelStores.class);

                        AdapterStores adapterStores = new AdapterStores(mContext,modelStores.getResult());
                        binding.rvStores.setAdapter(adapterStores);

                    } else {
                        AdapterStores adapterStores = new AdapterStores(mContext,null);
                        binding.rvStores.setAdapter(adapterStores);
                    }

                } catch (Exception e) {
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getCartCount();
    }

    private void getStoreCat() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Map<String,String> map = new HashMap<>();
        map.put("main_category_id",id);
        Call<ResponseBody> call = api.getStoreCatApiCall(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        ModelStoreCat1 modelStoreCat = new Gson().fromJson(responseString, ModelStoreCat1.class);

                        AdapterStoreCat1 adapterStoreCat = new AdapterStoreCat1(mContext,modelStoreCat.getResult(),false,false,true);
                        binding.rvStoresCat.setAdapter(adapterStoreCat);

                        Log.e("responseString","response = " + response);
                        Log.e("responseString","responseString = " + responseString);
                        Log.e("responseString","id = " + modelStoreCat.getResult().get(0).getId());

                        getAllSTores(modelStoreCat.getResult().get(0).getId(),modelStoreCat.getResult().get(0).getName());

                    } else {
                        AdapterStoreCat1 adapterStoreCat = new AdapterStoreCat1(mContext,null,false,false,true);
                        binding.rvStoresCat.setAdapter(adapterStoreCat);
                    }

                } catch (Exception e) {
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call,Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
            }

        });

    }

    private void getCartCount() {
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> param = new HashMap<>();
        param.put("user_id",modelLogin.getResult().getId());

        Call<ResponseBody> call = api.getCartCountApiCall(param);
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
                        binding.tvCartCount.setText(String.valueOf(jsonObject.getInt("count")));
                    } else {
                        binding.tvCartCount.setText("0");
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

    public void getStoresById(String id,String name) {
        Log.e("ididididid","id = " + id);
        Log.e("ididididid","name = " + name);
        getAllSTores(id,name);
    }

}