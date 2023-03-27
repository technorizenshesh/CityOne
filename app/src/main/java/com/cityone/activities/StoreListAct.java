package com.cityone.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cityone.R;
import com.cityone.adapters.AllStoresAdapter;
import com.cityone.adapters.StoreCatAdapter;
import com.cityone.databinding.ActivityStoreListBinding;
import com.cityone.listener.HomeListener;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreListAct extends AppCompatActivity implements HomeListener {
    Context mContext = StoreListAct.this;
    ActivityStoreListBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    String id="";
    ArrayList<ModelStoreCat1.Result>storeCatList;
    ArrayList<ModelStores.Result>storeList;
    StoreCatAdapter adapterStoreCat;
    AllStoresAdapter adapterStores;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = DataBindingUtil.setContentView(this,R.layout.activity_store_list);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        App.checkToken(mContext);
        init();

        if(getIntent()!=null){
            id = getIntent().getStringExtra("id");
            binding.tvTitle.setText(getIntent().getStringExtra("title"));
            getStoreCat();
        }
    }

    private void init() {
        storeCatList = new ArrayList<>();
        storeList = new ArrayList<>();


        adapterStoreCat = new StoreCatAdapter(mContext,storeCatList,StoreListAct.this);
        binding.rvStoresCat.setAdapter(adapterStoreCat);

        adapterStores = new AllStoresAdapter(mContext,storeList);
        binding.rvStores.setAdapter(adapterStores);


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

    private void getAllStores(String id,String name) {
        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));

        HashMap<String,String> param = new HashMap<>();
        param.put("sub_category_id",id);
        param.put("main_category_id",name);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getStoreByCatApiCallNew(param);
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
                       storeList.clear();
                       storeList.addAll(modelStores.getResult());
                       adapterStores.notifyDataSetChanged();


                    } else {
                       storeList.clear();
                       adapterStores.notifyDataSetChanged();
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
        Call<ResponseBody> call = api.getAllStoreCats(map);
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
                        storeCatList.clear();
                        storeCatList.addAll(modelStoreCat.getResult());
                        adapterStoreCat.notifyDataSetChanged();
                        Log.e("responseString","response = " + response);
                        Log.e("responseString","responseString = " + responseString);
                        Log.e("responseString","id = " + modelStoreCat.getResult().get(0).getId());
                        getAllStores(modelStoreCat.getResult().get(0).getId(),modelStoreCat.getResult().get(0).getMain_category_id());

                    } else {
                        storeCatList.clear();
                        adapterStoreCat.notifyDataSetChanged();

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



    @Override
    public void OnHome(int position, String name) {
        getAllStores(storeCatList.get(position).getId(),storeCatList.get(position).getMain_category_id());
    }
}
