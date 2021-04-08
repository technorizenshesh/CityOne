package com.cityone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.cityone.R;
import com.cityone.databinding.ActivityVerifyOtpBinding;
import com.cityone.databinding.ProviderReqDialogBinding;
import com.cityone.models.ModelLogin;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.AppConstant;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtpActivity extends AppCompatActivity {

    Context mContext = VerifyOtpActivity.this;
    ActivityVerifyOtpBinding binding;
    String mobile="";
    String id;
    HashMap<String,String> paramHash = new HashMap<>();
    HashMap<String, File> fileHashMap = new HashMap<>();
    private FirebaseAuth mAuth;
    Api api;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_verify_otp);
        sharedPref = SharedPref.getInstance(mContext);
        api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        mAuth = FirebaseAuth.getInstance();

        paramHash = (HashMap<String, String>) getIntent().getSerializableExtra("resgisterHashmap");
        mobile = getIntent().getStringExtra("mobile");
//        type = getIntent().getStringExtra("request");
//        fileHashMap = (HashMap<String, File>) getIntent().getSerializableExtra("fileHashmap");

        init();

        sendVerificationCode();

    }

    private void init() {

        binding.next.setOnClickListener(v -> {
            if(TextUtils.isEmpty(binding.etOtp.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_otp), Toast.LENGTH_SHORT).show();
            } else {
                ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, binding.etOtp.getText().toString().trim());
                signInWithPhoneAuthCredential(credential);
            }
        });

        binding.tvResend.setOnClickListener(v -> {
            sendVerificationCode();
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }

    private void sendVerificationCode() {

        binding.tvVerifyText.setText("We have send you an SMS on " + mobile + " with 6 digit verification code.");

        new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.tvResend.setText("" + millisUntilFinished/1000);
                binding.tvResend.setEnabled(false);
            }

            @Override
            public void onFinish() {
                binding.tvResend.setText(mContext.getString(R.string.resend));
                binding.tvResend.setEnabled(true);
            }
        }.start();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile.replace(" ",""), // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        VerifyOtpActivity.this.id = id;
                        Toast.makeText(mContext, getString(R.string.enter_6_digit_code), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        ProjectUtil.pauseProgressDialog();
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        ProjectUtil.pauseProgressDialog();
                        Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });        // OnVerificationStateChangedCallbacks

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            ProjectUtil.pauseProgressDialog();
                            // Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show();
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = task.getResult().getUser();
                            signUpApiCall();
//                            if(type.equals("request")) {
//                                providerRequestApi();
//                            } else {
//                            }

                        } else {
                            ProjectUtil.pauseProgressDialog();
                            Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {}

                        }
                    }
                });

    }

    private void providerRequestApi() {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build();

        AndroidNetworking.initialize(getApplicationContext(),okHttpClient);

        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        AndroidNetworking.upload(AppConstant.BASE_URL + "signup")
                .addMultipartParameter(paramHash)
                .addMultipartFile(fileHashMap)
                .setPriority(Priority.HIGH)
                .setTag("signup")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        ProjectUtil.pauseProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.getString("status").equals("1")) {

                                openProviderReqDialog();

                                Log.e("zdgfxsdgfxdg","response = " + response);

                            } else {
                                // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("Exception","Exception = " + e.getMessage());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        ProjectUtil.pauseProgressDialog();
                    }

                });
    }

    private void openProviderReqDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        ProviderReqDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
        ,R.layout.provider_req_dialog,null,false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.btOk.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        dialog.show();

    }

    private void signUpApiCall() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Call<ResponseBody> call = api.signUpApiCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        modelLogin = new Gson().fromJson(responseString, ModelLogin.class);

                        sharedPref.setBooleanValue(AppConstant.IS_REGISTER,true);
                        sharedPref.setUserDetails(AppConstant.USER_DETAILS,modelLogin);

                        startActivity(new Intent(mContext,DashboardActivity.class));
                        finish();
                    } else {
                       // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
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