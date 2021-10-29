package com.cityone.entertainment.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.cityone.R;
import com.cityone.databinding.ActivityEntertainmentHomeBinding;
import com.cityone.entertainment.adapters.AdapterEntCat;
import com.cityone.entertainment.adapters.AdapterEntertain;
import com.cityone.entertainment.models.ModelEntCat;
import com.cityone.entertainment.models.ModelEnts;
import com.cityone.entertainment.movies.activities.MovieHomeActivity;
import com.cityone.stores.adapters.AdapterStoreCat;
import com.cityone.stores.models.ModelStoreCat;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.ProjectUtil;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.HashMap;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EntHomeActivity extends AppCompatActivity {

    Context mContext = EntHomeActivity.this;
    ActivityEntertainmentHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_entertainment_home);

        init();

    }

    private void init() {

        getEntCat();

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getEntCat();
            }
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btMovie.setOnClickListener(v -> {
            startActivity(new Intent(mContext, MovieHomeActivity.class));
        });

    }

    private void getEntCat() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getEntCatApiCall();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        ModelEntCat modelStoreCat = new Gson().fromJson(responseString, ModelEntCat.class);

                        AdapterEntCat adapterStoreCat = new AdapterEntCat(mContext,modelStoreCat.getResult(),EntHomeActivity.this::updateEnt);
                        binding.rvEntCat.setAdapter(adapterStoreCat);

                        Log.e("responseString","response = " + response);
                        Log.e("responseString","responseString = " + responseString);
                        Log.e("responseString","id = " + modelStoreCat.getResult().get(0).getId());

                        getAllEnt(modelStoreCat.getResult().get(0).getId(),modelStoreCat.getResult().get(0).getName());

                    } else {
                        AdapterEntCat adapterStoreCat = new AdapterEntCat(mContext,null,EntHomeActivity.this::updateEnt);
                        binding.rvEntCat.setAdapter(adapterStoreCat);
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

    private void getAllEnt(String id, String name) {
        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> params = new HashMap<>();
        params.put("entertentment_category_id",id);

        Call<ResponseBody> call = api.getEntCall(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        ModelEnts modelEnts = new Gson().fromJson(responseString, ModelEnts.class);

                        AdapterEntertain adapterEntertain = new AdapterEntertain(mContext,modelEnts.getResult());
                        binding.rvEntertainment.setAdapter(adapterEntertain);

                        Log.e("responseString","response = " + response);
                        Log.e("responseString","responseString = " + responseString);
                        Log.e("responseString","id = " + modelEnts.getResult().get(0).getId());

                    } else {
                        AdapterEntertain adapterStoreCat = new AdapterEntertain(mContext,null);
                        binding.rvEntertainment.setAdapter(adapterStoreCat);
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

    public void updateEnt(String id) {
        getAllEnt(id,"");
    }

}