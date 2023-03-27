package com.cityone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.cityone.R;
import com.cityone.databinding.ActivityLoginBinding;
import com.cityone.models.ModelLogin;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.App;
import com.cityone.utils.AppConstant;
import com.cityone.utils.InternetConnection;
import com.cityone.utils.NetworkReceiver;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    Context mContext = LoginActivity.this;
    ActivityLoginBinding binding;
    String registerId;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        sharedPref = SharedPref.getInstance(mContext);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {

                        if (!task.isSuccessful()) {
                            return;
                        }

                        String token = task.getResult().getToken();
                        registerId = token;
                        Log.e("registerIdregisterId","registerId = " + registerId);

                    }

                });

        init();

    }

    private void init() {

        binding.changeLang.setOnClickListener(v -> {
            changeLangDialog();
        });

        binding.tvForogtPassword.setOnClickListener(v -> {
            startActivity(new Intent(mContext,ForgotPassActivity.class));
        });

        binding.tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(mContext,SignUpActivity.class));
            finish();
        });

        binding.btLogin.setOnClickListener(v -> {
           if(TextUtils.isEmpty(binding.etEmail.getText().toString().trim())){
               Toast.makeText(mContext, getString(R.string.please_enter_email_add), Toast.LENGTH_SHORT).show();
           } else if(TextUtils.isEmpty(binding.etPass.getText().toString().trim())){
               Toast.makeText(mContext, getString(R.string.please_enter_pass), Toast.LENGTH_SHORT).show();
           } else {
               if(InternetConnection.checkConnection(mContext)) {
                   loginApiCall();
               } else {
                   App.showConnectionDialog(mContext);
               }
           }
        });

    }

    private void changeLangDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.change_language_dialog);
        dialog.setCancelable(true);

        Button btContinue = dialog.findViewById(R.id.btContinue);
        RadioButton radioEng = dialog.findViewById(R.id.radioEng);
        RadioButton radioSpanish = dialog.findViewById(R.id.radioSpanish);

        if ("es".equals(sharedPref.getLanguage("lan"))) {
            radioSpanish.setChecked(true);
        } else {
            radioEng.setChecked(true);
        }

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        btContinue.setOnClickListener(v -> {
            if (radioEng.isChecked()) {
                ProjectUtil.updateResources(mContext, "en");
                sharedPref.setlanguage("lan", "en");
                finish();
                startActivity(new Intent(mContext, LoginActivity.class));
                dialog.dismiss();
            } else if (radioSpanish.isChecked()) {
                ProjectUtil.updateResources(mContext, "es");
                sharedPref.setlanguage("lan", "es");
                finish();
                startActivity(new Intent(mContext, LoginActivity.class));
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void loginApiCall() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));

        HashMap<String,String> paramHash = new HashMap<>();
        paramHash.put("email",binding.etEmail.getText().toString().trim());
        paramHash.put("password",binding.etPass.getText().toString().trim());
        paramHash.put("lat","");
        paramHash.put("lon","");
        paramHash.put("type","USER");
        paramHash.put("register_id",registerId);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.loginApiCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        Log.e("ahsjgdasd","responseString = " + responseString);
                        modelLogin = new Gson().fromJson(responseString, ModelLogin.class);

                        sharedPref.setBooleanValue(AppConstant.IS_REGISTER,true);
                        sharedPref.setUserDetails(AppConstant.USER_DETAILS,modelLogin);

                        if(modelLogin.getResult().getType().equals(AppConstant.USER)) {
                            startActivity(new Intent(mContext,DashboardActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
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