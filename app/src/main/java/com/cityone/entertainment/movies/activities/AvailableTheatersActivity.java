package com.cityone.entertainment.movies.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cityone.R;
import com.cityone.databinding.ActivityAvailableTheatersBinding;
import com.cityone.entertainment.movies.adapters.AdapterAvailTheater;
import com.cityone.entertainment.movies.adapters.AdapterClassicSeats;
import com.cityone.entertainment.movies.adapters.AdapterExclutiveSeats;
import com.cityone.entertainment.movies.adapters.AdapterNormalSeats;
import com.cityone.entertainment.movies.adapters.AdapterRemMovies;
import com.cityone.entertainment.movies.models.ModelAvilTheater;
import com.cityone.entertainment.movies.models.ModelUpcMovies;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.ProjectUtil;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AvailableTheatersActivity extends AppCompatActivity {

    Context mContext = AvailableTheatersActivity.this;
    ActivityAvailableTheatersBinding binding;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_available_theaters);
        id = getIntent().getStringExtra("id");
        Log.e("ididid","id = " + id);
        init();
    }

    private void init() {

        getAvilTheater();

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAvilTheater();
            }
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }

    private void getAvilTheater() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> param = new HashMap<>();
        param.put("movie_id",id);

        Call<ResponseBody> call = api.getTheaterApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        ModelAvilTheater modelAvilTheater = new Gson().fromJson(responseString, ModelAvilTheater.class);

                        AdapterAvailTheater adapterRemMovies = new AdapterAvailTheater(mContext,modelAvilTheater.getResult());
                        binding.rvAvailTheater.setAdapter(adapterRemMovies);

                    } else {
                        Toast.makeText(AvailableTheatersActivity.this, "No Theater Avilable", Toast.LENGTH_SHORT).show();
                        AdapterAvailTheater adapterRemMovies = new AdapterAvailTheater(mContext,null);
                        binding.rvAvailTheater.setAdapter(adapterRemMovies);
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