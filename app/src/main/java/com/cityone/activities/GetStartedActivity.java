package com.cityone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;
import com.cityone.R;
import com.cityone.databinding.ActivityGetStartedBinding;
import com.cityone.databinding.ActivityWelcomeBinding;
import com.cityone.models.ModelLogin;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.AppConstant;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetStartedActivity extends AppCompatActivity {

    Context mContext = GetStartedActivity.this;
    ActivityGetStartedBinding binding;
    String registerId;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    static final int GOOGLE_SIGN_IN_REQUEST_CODE = 1234;

    SharedPref sharedPref;
    ModelLogin modelLogin;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_get_started);
        sharedPref = SharedPref.getInstance(mContext);
        FirebaseApp.initializeApp(mContext);
        callbackManager = CallbackManager.Factory.create();

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

        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id));
        FacebookSdk.sdkInitialize(mContext);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        init();

    }

    private void init() {

        binding.btfacebook.setOnClickListener(v -> {

            FacebookSdk.setApplicationId(getString(R.string.facebook_app_id));
            FacebookSdk.sdkInitialize(mContext);
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Log.e("kjsgdfkjdgsf","onCancel");
                }

                @Override
                public void onError(FacebookException error) {
                    Log.e("kjsgdfkjdgsf","error = " + error.getMessage());
                }

            });
            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(50);
            anim.setStartOffset(20);
            anim.setRepeatMode(Animation.REVERSE);
            binding.btfacebook.startAnimation(anim);

        });

        binding.btGoogle.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);
            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(50);
            anim.setStartOffset(20);
            anim.setRepeatMode(Animation.REVERSE);
            binding.btGoogle.startAnimation(anim);
        });

        binding.cvLogin.setOnClickListener(v -> {
            startActivity(new Intent(mContext,LoginActivity.class));
            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(50);
            anim.setStartOffset(20);
            anim.setRepeatMode(Animation.REVERSE);
            binding.cvLogin.startAnimation(anim);
        });

        binding.tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(mContext,SignUpActivity.class));
        });

    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();

                            String profilePhoto = "https://graph.facebook.com/" + token.getUserId() + "/picture?height=500";

                            Log.e("kjsgdfkjdgsf","profilePhoto = " + profilePhoto);
                            Log.e("kjsgdfkjdgsf","name = " + user.getDisplayName());
                            Log.e("kjsgdfkjdgsf","email = " + user.getEmail());
                            Log.e("kjsgdfkjdgsf","Userid = " + user.getUid());

                            socialLoginCall(user.getDisplayName(),
                                    user.getEmail(), profilePhoto,
                                    user.getUid());

                        } else {
                            Toast.makeText(GetStartedActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void socialLoginCall(String username,String email,String image,String socialId) {

        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> paramHash = new HashMap<>();
        paramHash.put("user_name",username);
        paramHash.put("email",email);
        paramHash.put("mobile","");
        paramHash.put("address","");
        paramHash.put("land_mark","");
        paramHash.put("image",image);
        paramHash.put("register_id",registerId);
        paramHash.put("social_id",socialId);

        Call<ResponseBody> call = api.socialLogin(paramHash);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringRes = response.body().string();

                    JSONObject jsonObject = new JSONObject(stringRes);

                    if(jsonObject.getString("status").equals("1")) {

                        modelLogin = new Gson().fromJson(stringRes, ModelLogin.class);

                        Log.e("jafhkjdf","stringRes = " + stringRes);

                        sharedPref.setBooleanValue(AppConstant.IS_REGISTER,true);
                        sharedPref.setUserDetails(AppConstant.USER_DETAILS,modelLogin);

                        startActivity(new Intent(mContext, DashboardActivity.class));
                        finish();

                        // Toast.makeText(mContext, getString(R.string.successful), Toast.LENGTH_SHORT).show();

                    } else {
                       // Toast.makeText(mContext, getString(R.string.unsuccessful), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        signOut();
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        task.getResult();
                    }
                });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();

                            if(user !=  null) {

                                Log.e("kjsgdfkjdgsf","profilePhoto = " + user.getPhotoUrl());
                                Log.e("kjsgdfkjdgsf","name = " + user.getDisplayName());
                                Log.e("kjsgdfkjdgsf","email = " + user.getEmail());
                                Log.e("kjsgdfkjdgsf","Userid = " + user.getUid());

                                socialLoginCall(user.getDisplayName(),
                                        user.getEmail(), String.valueOf(user.getPhotoUrl()),
                                        user.getUid());

                            }

                        } else {

                        }

                    }
                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            /* Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);*/
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
            }

        }

    }

}