package com.cityone.meals;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cityone.R;
import com.cityone.adapters.AdapterMealStores;
import com.cityone.databinding.ActivityMealsHomeBinding;
import com.cityone.models.ModelLogin;
import com.cityone.stores.activities.MyCartActivity;
import com.cityone.stores.activities.StoreDetailsActivity;
import com.cityone.stores.adapters.AdapterStoreCat;
import com.cityone.stores.adapters.AdapterStores;
import com.cityone.stores.models.ModelStoreCat;
import com.cityone.stores.models.ModelStores;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
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

public class MealsHomeActivity extends AppCompatActivity {

    Context mContext = MealsHomeActivity.this;
    ActivityMealsHomeBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    String id ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_meals_home);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

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
        Call<ResponseBody> call = api.getStoreMealsCatApiCall(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        ModelStoreCat modelStoreCat = new Gson().fromJson(responseString, ModelStoreCat.class);

                        AdapterStoreCat adapterStoreCat = new AdapterStoreCat(mContext,modelStoreCat.getResult(),true,false,false);
                        binding.rvStoresCat.setAdapter(adapterStoreCat);

                        Log.e("responseString","response = " + response);
                        Log.e("responseString","responseString = " + responseString);
                        Log.e("responseString","id = " + modelStoreCat.getResult().get(0).getId());

                        getAllSTores(modelStoreCat.getResult().get(0).getId()
                                ,modelStoreCat.getResult().get(0).getName());

                    } else {
                        AdapterStoreCat adapterStoreCat = new AdapterStoreCat(mContext,null,true,false,false);
                        binding.rvStoresCat.setAdapter(adapterStoreCat);
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
        getAllSTores(id,name);
    }

    private void getAllSTores(String id,String name) {
        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));

        HashMap<String,String> param = new HashMap<>();
        param.put("restaurant_sub_category_id",id);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getStoreList(param);
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

                        AdapterMealStores adapterStores = new AdapterMealStores(mContext,modelStores.getResult());
                        binding.rvStores.setAdapter(adapterStores);

                    } else {
                        AdapterMealStores adapterStores = new AdapterMealStores(mContext,null);
                        binding.rvStores.setAdapter(adapterStores);
                    }

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

}