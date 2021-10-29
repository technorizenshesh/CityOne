package com.cityone.stores.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cityone.R;
import com.cityone.activities.DeliveryAddressActivity;
import com.cityone.databinding.ActivityMyCartBinding;
import com.cityone.models.ModelLogin;
import com.cityone.stores.adapters.AdapterMyCart;
import com.cityone.stores.adapters.AdapterStores;
import com.cityone.stores.models.ModelMyStoreCart;
import com.cityone.stores.models.ModelStores;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.AppConstant;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCartActivity extends AppCompatActivity {

    Context mContext = MyCartActivity.this;
    ActivityMyCartBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    StringBuilder builder = new StringBuilder();
    String storeId = null;
    double itemTotal = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_my_cart);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        init();
        getCartApiCall();
    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCartApiCall();
            }
        });

        binding.btCheckout.setOnClickListener(v -> {
            if(itemTotal != 0.0) {
                if(builder == null || builder.length() == 0) {
                    Toast.makeText(mContext, getString(R.string.please_add_items_in_cart), Toast.LENGTH_SHORT).show();
                } else {
                    HashMap<String,String> params = new HashMap<>();

                    String currentDate = ProjectUtil.getCurrentDate();
                    String currentTime = ProjectUtil.getCurrentTime();

                    params.put("user_id",modelLogin.getResult().getId());
                    params.put("cart_id",builder.toString());
                    params.put("restaurant_id",storeId);
                    params.put("date",currentDate);
                    params.put("time",currentTime);
                    params.put("total_amount", String.valueOf(itemTotal+10));

                    startActivity(new Intent(mContext,SetDeliveryLocationActivity.class)
                            .putExtra(AppConstant.STORE_BOOKING_PARAMS,params)
                    );

                }
            } else {
                Toast.makeText(mContext, getString(R.string.please_add_item), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void updateCartCount() {
        getCartApiCall();
    }

    private void getCartApiCall() {

        Log.e("dsfdsffs","userId = " + modelLogin.getResult().getId());

        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));

        HashMap<String,String> param = new HashMap<>();
        param.put("user_id",modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getStoreCartApiCall(param);
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

                        ModelMyStoreCart modelMyStoreCart = new Gson().fromJson(responseString, ModelMyStoreCart.class);

                        storeId = modelMyStoreCart.getResult().get(0).getRestaurant_id();
                        itemTotal = modelMyStoreCart.getTotal_amount();

                        AdapterMyCart adapterMyCart = new AdapterMyCart(mContext,modelMyStoreCart.getResult());
                        binding.rvCartItem.setAdapter(adapterMyCart);
                        binding.itemsTotal.setText(AppConstant.DOLLAR + modelMyStoreCart.getTotal_amount());
                        binding.itemPlusDevCharges.setText(AppConstant.DOLLAR + (modelMyStoreCart.getTotal_amount() + 10));

                        for(int i=0;i<modelMyStoreCart.getResult().size();i++)
                            builder.append(modelMyStoreCart.getResult().get(i).getItem_id()+",");
                        // Log.e("builderbuilder","builder = " + builder.deleteCharAt(builder.length()-1));

                        builder = builder.deleteCharAt(builder.length()-1);

                    } else {
                        builder = new StringBuilder();
                        binding.itemsTotal.setText(AppConstant.DOLLAR + 0.0);
                        binding.itemPlusDevCharges.setText(AppConstant.DOLLAR + 0.0);
                        AdapterMyCart adapterStores = new AdapterMyCart(mContext,null);
                        binding.rvCartItem.setAdapter(adapterStores);
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

    public void updateCartId(ArrayList<ModelMyStoreCart.Result> itemsList) {
        builder = new StringBuilder();
        if(itemsList != null && itemsList.size() != 0) {
            for(int i=0;i<itemsList.size();i++) {
                double amount = Double.parseDouble(itemsList.get(i).getAmount());
                double quantity = Double.parseDouble(itemsList.get(i).getQuantity());
                itemTotal = itemTotal + (amount * quantity);
                builder.append(itemsList.get(i).getItem_id() + ",");
            }
            binding.itemsTotal.setText(AppConstant.DOLLAR + itemTotal);
            binding.itemPlusDevCharges.setText(AppConstant.DOLLAR + (itemTotal + 10));
            builder = builder.deleteCharAt(builder.length() - 1);
        } else {
            binding.itemsTotal.setText(AppConstant.DOLLAR + 0.0);
            binding.itemPlusDevCharges.setText(AppConstant.DOLLAR + 0.0);
        }
    }


}