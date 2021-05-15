package com.cityone.entertainment.movies.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.cityone.R;
import com.cityone.databinding.ActivityMovieHomeBinding;
import com.cityone.entertainment.movies.adapters.AdapterRemMovies;
import com.cityone.entertainment.movies.adapters.AdapterUpcomeMovies;
import com.cityone.entertainment.movies.models.ModelUpcMovies;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.ProjectUtil;
import com.google.gson.Gson;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import org.json.JSONObject;
import java.util.ArrayList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieHomeActivity extends AppCompatActivity {

    Context mContext = MovieHomeActivity.this;
    ActivityMovieHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_movie_home);

        init();

    }

    private void init() {

        getUpMovies();

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRemMovies();
            }
        });

        ArrayList<Integer> adsList = new ArrayList<>();
        adsList.add(R.drawable.movie1);
        adsList.add(R.drawable.movie2);
        adsList.add(R.drawable.movie3);

        //        binding.btEnglish.setOnClickListener(v -> {
//            binding.btItalian.setBackgroundResource(R.drawable.gray_back_10);
//            binding.btSpanish.setBackgroundResource(R.drawable.gray_back_10);
//            binding.btEnglish.setBackgroundResource(R.drawable.orange_back_10);
//        });
//
//        binding.btSpanish.setOnClickListener(v -> {
//            binding.btItalian.setBackgroundResource(R.drawable.gray_back_10);
//            binding.btEnglish.setBackgroundResource(R.drawable.gray_back_10);
//            binding.btSpanish.setBackgroundResource(R.drawable.orange_back_10);
//        });
//
//        binding.btItalian.setOnClickListener(v -> {
//            binding.btEnglish.setBackgroundResource(R.drawable.gray_back_10);
//            binding.btSpanish.setBackgroundResource(R.drawable.gray_back_10);
//            binding.btItalian.setBackgroundResource(R.drawable.orange_back_10);
//        });

//        binding.cvMovie1.setOnClickListener(v -> {
//            startActivity(new Intent(mContext,MovieDetailsActivity.class));
//        });

    }

    private void getUpMovies() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getUpMoviesApiCall();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        ModelUpcMovies modelUpcMovies = new Gson().fromJson(responseString, ModelUpcMovies.class);

                        binding.categorAdsSlider.setSliderAdapter(new AdapterUpcomeMovies(mContext,modelUpcMovies.getResult()));
                        binding.categorAdsSlider.startAutoCycle();
                        binding.categorAdsSlider.setIndicatorAnimation(IndicatorAnimationType.SLIDE);
                        binding.categorAdsSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                        binding.categorAdsSlider.setScrollTimeInSec(5);
                        binding.categorAdsSlider.setAutoCycle(true);
                        binding.categorAdsSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                        getRemMovies();
                    } else {
                        binding.categorAdsSlider.setSliderAdapter(new AdapterUpcomeMovies(mContext,null));
                        binding.categorAdsSlider.startAutoCycle();
                        binding.categorAdsSlider.setIndicatorAnimation(IndicatorAnimationType.SLIDE);
                        binding.categorAdsSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                        binding.categorAdsSlider.setScrollTimeInSec(5);
                        binding.categorAdsSlider.setAutoCycle(true);
                        binding.categorAdsSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                        getRemMovies();
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

    private void getRemMovies() {
        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getRemMoviesApiCall();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        ModelUpcMovies modelUpcMovies = new Gson().fromJson(responseString, ModelUpcMovies.class);

                        AdapterRemMovies adapterRemMovies = new AdapterRemMovies(mContext,modelUpcMovies.getResult());
                        binding.rvRemMovie.setAdapter(adapterRemMovies);

                    } else {
                        AdapterRemMovies adapterRemMovies = new AdapterRemMovies(mContext,null);
                        binding.rvRemMovie.setAdapter(adapterRemMovies);
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