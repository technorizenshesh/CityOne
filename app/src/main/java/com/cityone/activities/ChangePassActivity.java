package com.cityone.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.cityone.R;
import com.cityone.databinding.ActivityChangePassBinding;
import com.cityone.models.ModelLogin;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.App;
import com.cityone.utils.AppConstant;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import org.json.JSONObject;
import java.util.HashMap;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassActivity extends AppCompatActivity {

    Context mContext = ChangePassActivity.this;
    ActivityChangePassBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_pass);

        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        App.checkToken(mContext);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btUpdate.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etOldPass.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_old_pass), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etNewPass.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_new_pass), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etConfirmPass.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_conf_pass), Toast.LENGTH_SHORT).show();
            } else if (!modelLogin.getResult().getPassword().equals(binding.etOldPass.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.old_pass_wrong), Toast.LENGTH_SHORT).show();
            } else if (!binding.etConfirmPass.getText().toString().trim()
                    .equals(binding.etNewPass.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.password_not_match), Toast.LENGTH_SHORT).show();
            } else {
                changePassApiCall();
            }
        });

    }

    private void changePassApiCall() {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String, String> paramsHash = new HashMap<>();
        paramsHash.put("user_id", modelLogin.getResult().getId());
        paramsHash.put("password", binding.etNewPass.getText().toString().trim());

        Call<ResponseBody> call = api.changePassApiCall(paramsHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        Toast.makeText(mContext, getString(R.string.success), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }


}