package com.cityone.entertainment.movies.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.cityone.R;
import com.cityone.databinding.ActivityTheaterDetailBinding;
import com.cityone.entertainment.movies.adapters.AdapterAvailDates;
import com.cityone.entertainment.movies.adapters.AdapterRemMovies;
import com.cityone.entertainment.movies.models.ModelMovieDetails;
import com.cityone.entertainment.movies.models.ModelTheaterDetails;
import com.cityone.entertainment.movies.models.ModelUpcMovies;
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

public class TheaterDetailActivity extends AppCompatActivity {

    Context mContext = TheaterDetailActivity.this;
    ActivityTheaterDetailBinding binding;
    private String id,selectedDate,SelectedTimeSlot = null;
    ModelTheaterDetails modelTheaterDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_theater_detail);
        id = getIntent().getStringExtra("id");
        init();
    }

    private void init() {

        getTheaterDetails();

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTheaterDetails();
            }
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btSeat.setOnClickListener(v -> {
            if(modelTheaterDetails != null) {
                startActivity(new Intent(mContext, AvailSeatsActivity.class)
                  .putExtra("data",modelTheaterDetails)
                );
            }
        });

        binding.bttime1.setOnClickListener(v -> {
            SelectedTimeSlot = modelTheaterDetails.getResult().getTreater_time_slote1();
            binding.bttime1.setBackgroundResource(R.drawable.orange_light_back);
            binding.bttime2.setBackgroundResource(R.drawable.orange_outline_back);
            binding.bttime3.setBackgroundResource(R.drawable.orange_outline_back);
        });

        binding.bttime2.setOnClickListener(v -> {
            SelectedTimeSlot = modelTheaterDetails.getResult().getTreater_time_slote2();
            binding.bttime2.setBackgroundResource(R.drawable.orange_light_back);
            binding.bttime1.setBackgroundResource(R.drawable.orange_outline_back);
            binding.bttime3.setBackgroundResource(R.drawable.orange_outline_back);
        });

        binding.bttime3.setOnClickListener(v -> {
            SelectedTimeSlot = modelTheaterDetails.getResult().getTreater_time_slote3();
            binding.bttime3.setBackgroundResource(R.drawable.orange_light_back);
            binding.bttime2.setBackgroundResource(R.drawable.orange_outline_back);
            binding.bttime1.setBackgroundResource(R.drawable.orange_outline_back);
        });

        //        binding.btDate1.setOnClickListener(v -> {
//            binding.btDate1.setBackgroundResource(R.drawable.light_orange_back_5);
//            binding.btDate2.setBackgroundResource(R.drawable.orange_outline_back_5);
//        });
//
//        binding.btDate2.setOnClickListener(v -> {
//            binding.btDate1.setBackgroundResource(R.drawable.orange_outline_back_5);
//            binding.btDate2.setBackgroundResource(R.drawable.light_orange_back_5);
//        });
//
//        binding.bttime1.setOnClickListener(v -> {
//            binding.bttime1.setBackgroundResource(R.drawable.light_orange_back_5);
//            binding.bttime2.setBackgroundResource(R.drawable.orange_outline_back_5);
//            binding.bttime3.setBackgroundResource(R.drawable.orange_outline_back_5);
//            binding.bttime4.setBackgroundResource(R.drawable.orange_outline_back_5);
//        });
//
//        binding.bttime2.setOnClickListener(v -> {
//            binding.bttime2.setBackgroundResource(R.drawable.light_orange_back_5);
//            binding.bttime1.setBackgroundResource(R.drawable.orange_outline_back_5);
//            binding.bttime3.setBackgroundResource(R.drawable.orange_outline_back_5);
//            binding.bttime4.setBackgroundResource(R.drawable.orange_outline_back_5);
//        });
//
//        binding.bttime3.setOnClickListener(v -> {
//            binding.bttime3.setBackgroundResource(R.drawable.light_orange_back_5);
//            binding.bttime2.setBackgroundResource(R.drawable.orange_outline_back_5);
//            binding.bttime1.setBackgroundResource(R.drawable.orange_outline_back_5);
//            binding.bttime4.setBackgroundResource(R.drawable.orange_outline_back_5);
//        });
//
//        binding.bttime4.setOnClickListener(v -> {
//            binding.bttime4.setBackgroundResource(R.drawable.light_orange_back_5);
//            binding.bttime2.setBackgroundResource(R.drawable.orange_outline_back_5);
//            binding.bttime3.setBackgroundResource(R.drawable.orange_outline_back_5);
//            binding.bttime1.setBackgroundResource(R.drawable.orange_outline_back_5);
//        });

    }

    private void getTheaterDetails() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> param = new HashMap<>();
        param.put("treater_id",id);

        Call<ResponseBody> call = api.getTheaterDetailsApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("dsadsdas","response = " + responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        modelTheaterDetails = new Gson().fromJson(responseString,ModelTheaterDetails.class);
                        AdapterAvailDates adapterAvailDates = new AdapterAvailDates(mContext,modelTheaterDetails.getSlote(),TheaterDetailActivity.this::availDate);
                        binding.rvAvailDates.setAdapter(adapterAvailDates);

                        binding.setData(modelTheaterDetails);

                        if(modelTheaterDetails.getResult().getTreater_time_slote1() == null
                                || modelTheaterDetails.getResult().getTreater_time_slote1().equals("")) {
                            binding.bttime1.setVisibility(View.GONE);
                        } else {
                            binding.bttime1.setBackgroundResource(R.drawable.light_orange_back_5);
                            SelectedTimeSlot = modelTheaterDetails.getResult().getTreater_time_slote1();
                        }

                        if(modelTheaterDetails.getResult().getTreater_time_slote2() == null
                                || modelTheaterDetails.getResult().getTreater_time_slote2().equals("")){
                            binding.bttime2.setVisibility(View.GONE);
                         } else {
                            if(SelectedTimeSlot == null) {
                                binding.bttime2.setBackgroundResource(R.drawable.light_orange_back_5);
                                SelectedTimeSlot = modelTheaterDetails.getResult().getTreater_time_slote2();
                            }
                         }

                        if(modelTheaterDetails.getResult().getTreater_time_slote3() == null
                                || modelTheaterDetails.getResult().getTreater_time_slote3().equals("")) {
                            binding.bttime3.setVisibility(View.GONE);
                        } else {
                            if(SelectedTimeSlot == null) {
                                binding.bttime3.setBackgroundResource(R.drawable.light_orange_back_5);
                                SelectedTimeSlot = modelTheaterDetails.getResult().getTreater_time_slote3();
                            }
                        }

                    } else {

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

    public void availDate(String date) {
        selectedDate = date;
        Log.e("selectedDate","selectedDate = " + selectedDate);
    }

}