package com.cityone.entertainment.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cityone.R;
import com.cityone.activities.PaymentMethodActivity;
import com.cityone.databinding.ActivityEntCartBinding;
import com.cityone.entertainment.adapters.AdapterEntMyCart;
import com.cityone.entertainment.models.ModelEntMyCart;
import com.cityone.entertainment.models.UpdateCartEntInterf;
import com.cityone.models.ModelLogin;
import com.cityone.stores.activities.SetDeliveryLocationActivity;
import com.cityone.stores.adapters.AdapterMyCart;
import com.cityone.stores.models.ModelMyStoreCart;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.AppConstant;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EntCartActivity extends AppCompatActivity implements UpdateCartEntInterf {

    Context mContext = EntCartActivity.this;
    ActivityEntCartBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    StringBuilder builder = new StringBuilder();
    String storeId = null;
    double itemTotal = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_ent_cart);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        init();

        getCartApiCall();

    }

    private void getCartApiCall() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));

        HashMap<String,String> param = new HashMap<>();
        param.put("user_id",modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getCartEntApiCall(param);
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

                        ModelEntMyCart modelEntMyCart = new Gson().fromJson(responseString,ModelEntMyCart.class);

                        AdapterEntMyCart adapterMyCart = new AdapterEntMyCart(mContext,modelEntMyCart.getResult());
                        binding.rvCartItem.setAdapter(adapterMyCart);
                        binding.itemPlusDevCharges.setText(AppConstant.DOLLAR + (modelEntMyCart.getTotal_amount()));
                        itemTotal = Double.parseDouble(modelEntMyCart.getTotal_amount()+"");
//                        for(int i=0;i<modelMyStoreCart.getResult().size();i++)
//                            builder.append(modelMyStoreCart.getResult().get(i).getItem_id()+",");
//                        // Log.e("builderbuilder","builder = " + builder.deleteCharAt(builder.length()-1));
//
//                        builder = builder.deleteCharAt(builder.length()-1);

                    } else {
                        builder = new StringBuilder();
                        binding.itemPlusDevCharges.setText(AppConstant.DOLLAR + 0.0);
                        AdapterEntMyCart adapterStores = new AdapterEntMyCart(mContext,null);
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

    private void init() {

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCartApiCall();
            }
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btCheckout.setOnClickListener(v -> {
            if(itemTotal != 0.0) {
                HashMap<String,String> params = new HashMap<>();

                String currentDate = ProjectUtil.getCurrentDate();
                String currentTime = ProjectUtil.getCurrentTime();

                params.put("user_id",modelLogin.getResult().getId());
                params.put("cart_id",builder.toString());
                params.put("restaurant_id",storeId);
                params.put("date",currentDate);
                params.put("time",currentTime);
                params.put("total_amount",String.valueOf(itemTotal));

                Toast.makeText(mContext, "We are working on further proceess", Toast.LENGTH_LONG).show();
//                startActivity(new Intent(mContext, SetDeliveryLocationActivity.class)
//                        .putExtra(AppConstant.STORE_BOOKING_PARAMS,params)
//                );
            } else {
                Toast.makeText(mContext, getString(R.string.please_add_item), Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    public void onSuccess(ArrayList<ModelEntMyCart.Result> itemsList) {
        if(itemsList != null && itemsList.size() != 0) {
            for(int i=0;i<itemsList.size();i++) {
                double amount = Double.parseDouble(itemsList.get(i).getPrice());
                double quantity = Double.parseDouble(itemsList.get(i).getQuantity());
                itemTotal = itemTotal + (amount * quantity);
            }
            binding.itemPlusDevCharges.setText(AppConstant.DOLLAR + (itemTotal));
        } else {
            binding.itemPlusDevCharges.setText(AppConstant.DOLLAR + 0.0);
        }
    }

    @Override
    public void onSuccess() {
        getCartApiCall();
    }

}