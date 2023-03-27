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
import com.cityone.activities.DashboardActivity;
import com.cityone.adapters.SliderAdapter;
import com.cityone.adapters.SliderEnAdapter;
import com.cityone.databinding.ActivityEntertainmentHomeBinding;
import com.cityone.entertainment.adapters.AdapterEntCat;
import com.cityone.entertainment.adapters.AdapterEntertain;
import com.cityone.entertainment.adapters.EventAdapter;
import com.cityone.entertainment.adapters.MovieAdapter;
import com.cityone.entertainment.models.EventModel;
import com.cityone.entertainment.models.ModelEntCat;
import com.cityone.entertainment.models.ModelEnts;
import com.cityone.entertainment.movies.activities.MovieHomeActivity;
import com.cityone.entertainment.movies.adapters.AdapterUpcomeMovies;
import com.cityone.entertainment.movies.models.ModelUpcMovies;
import com.cityone.models.BannerModel;
import com.cityone.stores.adapters.AdapterStoreCat;
import com.cityone.stores.models.ModelStoreCat;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.App;
import com.cityone.utils.ProjectUtil;
import com.google.gson.Gson;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EntHomeActivity extends AppCompatActivity {

    Context mContext = EntHomeActivity.this;
    ActivityEntertainmentHomeBinding binding;
    ArrayList<BannerModel.Result> bannerArrayList;
    ArrayList<ModelUpcMovies.Result> movieArrayList;
    ArrayList<EventModel.Result> eventArrayList;

    SliderEnAdapter sliderAdapter;
    MovieAdapter movieAdapter;
    EventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_entertainment_home);

        App.checkToken(mContext);

        init();

    }

    private void init() {
        bannerArrayList = new ArrayList<>();
        movieArrayList = new ArrayList<>();
        eventArrayList = new ArrayList<>();


        getEntCat();
        GetBannerAPi();
        getEvent();
        movieAdapter = new MovieAdapter(EntHomeActivity.this,movieArrayList);
        binding.rvMovie.setAdapter(movieAdapter);

        getRecommandedMovies();


        eventAdapter = new EventAdapter(mContext,eventArrayList);
        binding.rvEvent.setAdapter(eventAdapter);

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

        HashMap<String,String> params = new HashMap<>();
        params.put("main_category_id","4");
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getEntCatApiCallNew(params);
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


    private void GetBannerAPi() {
       // ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
      /*  map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        map.put("country_id", addresses.get(0).getCountryName()+"");
        Log.e("MapMap", "EXERSICE LIST" + map);*/
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        Call<ResponseBody> loginCall = api.getAllBanners();

        loginCall.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
               // ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {
                        BannerModel bannerModel = new Gson().fromJson(responseString, BannerModel.class);
                        bannerArrayList.clear();
                        bannerArrayList.addAll(bannerModel.getResult());
                        sliderAdapter = new SliderEnAdapter(EntHomeActivity.this, bannerArrayList);
                        binding.imageSlider.setSliderAdapter(sliderAdapter);
                        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                        binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                        //     binding.imageSlider.setIndicatorSelectedColor(R.color.colorPrimary);
                        //      binding.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
                        binding.imageSlider.setScrollTimeInSec(3);
                        binding.imageSlider.setAutoCycle(true);
                        binding.imageSlider.startAutoCycle();

                    } else if (jsonObject.getString("status").equals("0")) {
                        Toast.makeText(EntHomeActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();
                ProjectUtil.pauseProgressDialog();
            }
        });
    }

    private void getRecommandedMovies() {
       // ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getRecommandedMoviesApiCall();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {
                        ModelUpcMovies modelUpcMovies = new Gson().fromJson(responseString, ModelUpcMovies.class);
                        movieArrayList.clear();
                        movieArrayList.addAll(modelUpcMovies.getResult());
                        movieAdapter.notifyDataSetChanged();
                    } else {
                        movieArrayList.clear();
                        movieAdapter.notifyDataSetChanged();
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



    private void getEvent() {
       // ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        HashMap<String,String> params = new HashMap<>();
        params.put("main_category_id","4");
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getEventsApiCall(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    if(jsonObject.getString("status").equals("1")) {
                        EventModel eventModel = new Gson().fromJson(responseString, EventModel.class);
                        eventArrayList.clear();
                        eventArrayList.addAll(eventModel.getResult());
                        eventAdapter.notifyDataSetChanged();



                    } else {
                        eventArrayList.clear();
                        eventAdapter.notifyDataSetChanged();

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