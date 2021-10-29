package com.cityone.stores.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.cityone.R;
import com.cityone.databinding.ActivityStoreDetailsBinding;
import com.cityone.models.ModelLogin;
import com.cityone.stores.adapters.AdapterStoreItemCat;
import com.cityone.stores.adapters.AdapterStoreItemCatFilter;
import com.cityone.stores.adapters.AdapterStoreItems;
import com.cityone.stores.models.ModelStoreDetails;
import com.cityone.stores.models.ModelStoreItems;
import com.cityone.stores.models.ModelStoreItemFilterCat;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.AppConstant;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.facebook.login.Login;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import javax.security.auth.login.LoginException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreDetailsActivity extends AppCompatActivity {

    Context mContext = StoreDetailsActivity.this;
    ActivityStoreDetailsBinding binding;
    String storeId = "";
    SharedPref sharedPref;
    ModelLogin modelLogin;
    ModelStoreDetails modelStoreDetails;
    ModelStoreItemFilterCat modelStoreItemFilterCat;
    HashMap<String,String> bookingParam = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_store_details);
        storeId = getIntent().getStringExtra("storeid");
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        bookingParam.put("store_id",storeId);

        init();

        fetchCategory();
        getStoreDetails();

    }

    private void init() {

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getStoreDetails();
            }
        });

        binding.spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(modelStoreItemFilterCat != null) {
                    if(position != 0) {
                        if(position == 1) {
                            fetchAllStoreItems();
                        } else {
                            searchCategory(modelStoreItemFilterCat.getResult().get(position-2).getId());
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.tvGoToCart.setOnClickListener(v -> {
            startActivity(new Intent(mContext,MyCartActivity.class)
               .putExtra("param",bookingParam)
            );
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getCartCount();
    }

    private void findStorecat(String storeId,String id) {
        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> param = new HashMap<>();
        param.put("restaurant_id",storeId);

        Call<ResponseBody> call = api.getStoreDetailApiCall(param);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {
                        binding.swipLayout.setRefreshing(false);

                        Log.e("responseString","response = " + response);
                        Log.e("responseString","responseString = " + responseString);

//                        ModelStoreDetails.Result.Restaurant_sub_category modelStoreDetails = new Gson().fromJson(responseString, ModelStoreDetails.Result.Restaurant_sub_categoryclass);
//
//                        AdapterStoreItemCat adapterStoreItemCat = new AdapterStoreItemCat(mContext, modelStoreDetails.getResult().getRestaurant_sub_category());
//                        binding.rvStoresItemCat.setAdapter(adapterStoreItemCat);
//
//                        getStoreItems(modelStoreDetails.getResult().getRestaurant_sub_category().get(0).getId());

                    } else {
                        AdapterStoreItemCat adapterStores = new AdapterStoreItemCat(mContext,null);
                        binding.rvStoresItemCat.setAdapter(adapterStores);
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
                        binding.tvCartCount.setText(jsonObject.getInt("count") + " Item");
                    } else {
                        binding.tvCartCount.setText("0 Item");
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

    private void searchCategory(String catId) {
        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> param = new HashMap<>();
        param.put("restaurant_id",storeId);
        param.put("special_cate_id",catId);

        Call<ResponseBody> call = api.searchStoreApiCall(param);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {
                        binding.swipLayout.setRefreshing(false);

                        Log.e("responseString","response = " + response);
                        Log.e("responseString","responseString = " + responseString);

                        JSONArray jsonArray = jsonObject.getJSONArray("restaurant_sub_category");

                        Log.e("responseString","jsonArray = " + jsonArray.toString());

                        ArrayList<ModelStoreDetails.Result.Restaurant_sub_category> storeList = new Gson().fromJson(jsonArray.toString(),new TypeToken<ArrayList<ModelStoreDetails.Result.Restaurant_sub_category>>(){}.getType());

                        AdapterStoreItemCat adapterStoreItemCat = new AdapterStoreItemCat(mContext, storeList);
                        binding.rvStoresItemCat.setAdapter(adapterStoreItemCat);

                        getStoreItems(storeList.get(0).getId());

                    } else {
                        AdapterStoreItemCat adapterStores = new AdapterStoreItemCat(mContext,null);
                        binding.rvStoresItemCat.setAdapter(adapterStores);

                        AdapterStoreItems adapterStoreItems = new AdapterStoreItems(mContext,null);
                        binding.rvStoresItems.setAdapter(adapterStoreItems);
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

    private void fetchAllStoreItems() {
        AdapterStoreItemCat adapterStoreItemCat = new AdapterStoreItemCat(mContext, modelStoreDetails.getResult().getRestaurant_sub_category());
        binding.rvStoresItemCat.setAdapter(adapterStoreItemCat);

        try {
            getStoreItems(modelStoreDetails.getResult().getRestaurant_sub_category().get(0).getId());
        } catch (Exception e) {}

    }

    private void getStoreDetails() {
        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> param = new HashMap<>();
        param.put("restaurant_id",storeId);

        Call<ResponseBody> call = api.getStoreDetailApiCall(param);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {
                        binding.swipLayout.setRefreshing(false);

                        Log.e("responseString","response = " + response);
                        Log.e("responseString","responseString = " + responseString);

                        modelStoreDetails = new Gson().fromJson(responseString, ModelStoreDetails.class);

                        Picasso.get().load(modelStoreDetails.getResult().getImage()).into(binding.ivResImage);
                        binding.tvResName.setText(modelStoreDetails.getResult().getName());
                        binding.tvAddress.setText(modelStoreDetails.getResult().getAddress());
                        binding.tvTime.setText(modelStoreDetails.getResult().getOpen_time() + " - " +
                                modelStoreDetails.getResult().getClose_time());

                        AdapterStoreItemCat adapterStoreItemCat = new AdapterStoreItemCat(mContext, modelStoreDetails.getResult().getRestaurant_sub_category());
                        binding.rvStoresItemCat.setAdapter(adapterStoreItemCat);

                        Log.e("adasdasda",modelStoreDetails.getResult().getRestaurant_sub_category().get(0).getId());

                        getStoreItems(modelStoreDetails.getResult().getRestaurant_sub_category().get(0).getId());

                    } else {
                        AdapterStoreItemCat adapterStores = new AdapterStoreItemCat(mContext,null);
                        binding.rvStoresItemCat.setAdapter(adapterStores);
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

    private void fetchCategory() {

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        Call<ResponseBody> call = api.getSpecialCatApiCall();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {
                        binding.swipLayout.setRefreshing(false);

                        Log.e("fetchCategory","response = " + response);
                        Log.e("fetchCategory","responseString = " + responseString);

                        modelStoreItemFilterCat = new Gson().fromJson(responseString, ModelStoreItemFilterCat.class);

                        ModelStoreItemFilterCat.Result result = new ModelStoreItemFilterCat().new Result();
                        result.setId("0");
                        result.setName("Filter Category");

                        ModelStoreItemFilterCat.Result result1 = new ModelStoreItemFilterCat().new Result();
                        result1.setId("0");
                        result1.setName("All Category");

                        modelStoreItemFilterCat.getResult().add(0,result);
                        modelStoreItemFilterCat.getResult().add(1,result1);

                        AdapterStoreItemCatFilter adapterStoreItemCatFilter = new AdapterStoreItemCatFilter(mContext, modelStoreItemFilterCat.getResult());
                        binding.spCategory.setAdapter(adapterStoreItemCatFilter);

                    } else {
                        AdapterStoreItemCatFilter adapterStores = new AdapterStoreItemCatFilter(mContext,null);
                        binding.spCategory.setAdapter(adapterStores);
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

    private void getStoreItems(String id) {
        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> param = new HashMap<>();
        param.put("restaurant_sub_category_id",id);

        Call<ResponseBody> call = api.getStoreItemsApiCall(param);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {
                        binding.swipLayout.setRefreshing(false);

                        Log.e("responseString","response = " + response);
                        Log.e("responseString","responseString = " + responseString);

                        ModelStoreItems modelStoreItems = new Gson().fromJson(responseString, ModelStoreItems.class);

                        AdapterStoreItems adapterStoreItems = new AdapterStoreItems(mContext, modelStoreItems.getResult());
                        binding.rvStoresItems.setAdapter(adapterStoreItems);

                    } else {
                        Log.e("responseString","responseString = " + responseString);
                        AdapterStoreItems adapterStoreItems = new AdapterStoreItems(mContext,null);
                        binding.rvStoresItems.setAdapter(adapterStoreItems);
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

    public void getItemsById(String id) {
        getStoreItems(id);
    }

    public void updateCartCount(int count) {
        binding.tvCartCount.setText(count + " Items");
    }

}