package com.cityone.shipping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cityone.R;
import com.cityone.databinding.ActivityShippStatusBinding;
import com.cityone.databinding.AdapterMySendingsBinding;
import com.cityone.models.ModelLogin;
import com.cityone.shipping.adapters.AdapterShipRequest;
import com.cityone.shipping.adapters.AdapterShipStatus;
import com.cityone.shipping.models.ModelMySendings;
import com.cityone.shipping.models.ModelShipRequest;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.AppConstant;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShippStatusActivity extends AppCompatActivity {

    Context mContext = ShippStatusActivity.this;
    ActivityShippStatusBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    ModelMySendings modelMySendings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_shipp_status);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        init();

    }

    private void init() {

        getMySendings();

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.swipLayout.setRefreshing(true);
        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMySendings();
            }
        });

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(getIntent() != null){
                getMySendingNew();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver,new IntentFilter("sending"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private void getMySendingNew(){
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        Log.e("dfhkshdfklhksd","user_id = " + modelLogin.getResult().getId());

        HashMap<String,String> params = new HashMap<>();
        params.put("user_id",modelLogin.getResult().getId());

        Call<ResponseBody> call = api.getMySendingsApiCall(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        modelMySendings = new Gson().fromJson(responseString,ModelMySendings.class);

                        AdapterShipStatus adapterShipStatus = new AdapterShipStatus(mContext,modelMySendings.getResult());
                        binding.rvrequest.setAdapter(adapterShipStatus);

                        Log.e("responseStringdfsdfsd","responseString = " + responseString);

                    } else {
                        Toast.makeText(mContext, getString(R.string.no_request_found), Toast.LENGTH_SHORT).show();
                        AdapterShipStatus adapterShipStatus = new AdapterShipStatus(mContext,null);
                        binding.rvrequest.setAdapter(adapterShipStatus);
                        // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
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

    private void getMySendings() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        Log.e("dfhkshdfklhksd","user_id = " + modelLogin.getResult().getId());

        HashMap<String,String> params = new HashMap<>();
        params.put("user_id",modelLogin.getResult().getId());

        Call<ResponseBody> call = api.getMySendingsApiCall(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        modelMySendings = new Gson().fromJson(responseString,ModelMySendings.class);

                        AdapterShipStatus adapterShipStatus = new AdapterShipStatus(mContext,modelMySendings.getResult());
                        binding.rvrequest.setAdapter(adapterShipStatus);

                        Log.e("responseStringdfsdfsd","responseString = " + responseString);

                    } else {
                        Toast.makeText(mContext, getString(R.string.no_request_found), Toast.LENGTH_SHORT).show();
                        AdapterShipStatus adapterShipStatus = new AdapterShipStatus(mContext,null);
                        binding.rvrequest.setAdapter(adapterShipStatus);
                        // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
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

}