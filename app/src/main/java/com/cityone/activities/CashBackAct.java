package com.cityone.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cityone.R;
import com.cityone.adapters.AdapterPoints;
import com.cityone.databinding.ActivityCashBackBinding;
import com.cityone.models.ModelLogin;
import com.cityone.models.ModelReferrals;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.AppConstant;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CashBackAct extends AppCompatActivity {

    Context mContext = CashBackAct.this;
    ActivityCashBackBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cash_back);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    private void itit() {

        getAllPoints();

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllPoints();
            }
        });

    }

    private void getAllPoints() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("my_referral_code", modelLogin.getResult().getMy_referral_code());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getReferralPOints(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String stringResponse = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(stringResponse);

                        if (jsonObject.getString("status").equals("1")) {

                            Log.e("asfddasfasdf", "response = " + stringResponse);
                            ModelReferrals modelReferrals = new Gson().fromJson(stringResponse, ModelReferrals.class);

                            AdapterPoints adapterPoints = new AdapterPoints(mContext, modelReferrals.getResult());
                            binding.rvPoints.setAdapter(adapterPoints);

                            binding.tvPoints.setText("You have " + modelReferrals.getReferral_code_point() + " points");

                        }

                    } catch (Exception e) {
                        binding.tvPoints.setText("You have " + 0 + "points");
                        e.printStackTrace();
                    }

                    // Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
            }

        });

    }

    public void shareText() {
        String mimeType = "text/plain";
        String mimeType1 = "https://drive.google.com/file/d/1sX_Um5juVH8_kQu8PqddiNYOtYXD8D8-/view?usp=sharing";
        String text = "This Cityone User App Link\n\n" +
                "https://drive.google.com/file/d/11KehCHplBp_Ugx4pfnCymJYbBPSuX395/view?usp=sharing" +
                "\n\nThis is Cityone Driver App link\n\n";

        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType(mimeType)
                .setText(text)
                .getIntent();
        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(shareIntent);
        }
    }


}