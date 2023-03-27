package com.cityone.entertainment.movies.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.cityone.R;
import com.cityone.databinding.ActivityMovieDetailsBinding;
import com.cityone.entertainment.movies.adapters.AdapterMovieDetailBanners;
import com.cityone.entertainment.movies.models.ModelMovieDetails;
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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    Context mContext = MovieDetailsActivity.this;
    ActivityMovieDetailsBinding binding;
    String movieId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_movie_details);
        movieId = getIntent().getStringExtra("id");
        App.checkToken(mContext);
        init();
    }

    private void init() {

        getMovieDetails();

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btBookTicket.setOnClickListener(v -> {
            startActivity(new Intent(mContext,AvailableTheatersActivity.class)
                .putExtra("id",movieId)
            );
        });

    }

    private void getMovieDetails() {

        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> params = new HashMap<>();
        params.put("movie_id",movieId);

        Call<ResponseBody> call = api.getMovieDetailsCall(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        ModelMovieDetails modelMovieDetails = new Gson().fromJson(responseString,ModelMovieDetails.class);

                        binding.setData(modelMovieDetails.getResult());

                        ArrayList<String> adsList = new ArrayList<>();
                        adsList.add(modelMovieDetails.getResult().getFirst_image());
                        adsList.add(modelMovieDetails.getResult().getSecond_image());

                        binding.categorAdsSlider.setSliderAdapter(new AdapterMovieDetailBanners(mContext,adsList));
                        binding.categorAdsSlider.startAutoCycle();
                        binding.categorAdsSlider.setIndicatorAnimation(IndicatorAnimationType.SLIDE);
                        binding.categorAdsSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                        binding.categorAdsSlider.setScrollTimeInSec(5);
                        binding.categorAdsSlider.setAutoCycle(true);
                        binding.categorAdsSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);

                    } else {}

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


}