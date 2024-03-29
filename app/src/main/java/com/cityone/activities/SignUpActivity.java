package com.cityone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.cityone.R;
import com.cityone.databinding.ActivitySignUpBinding;
import com.cityone.databinding.ProviderReqDialogBinding;
import com.cityone.databinding.SubmitRequestDialogBinding;
import com.cityone.models.ModelLogin;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.AppConstant;
import com.cityone.utils.Compress;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private static final int PERMISSION_ID = 101;
    Context mContext = SignUpActivity.this;
    ActivitySignUpBinding binding;
    Api api;
    String registerId = "";
    LatLng latLng;
    File fileImage, doc1Img, doc2Img, doc1Dialog, doc2Dialog;
    Dialog parentDialog;
    SubmitRequestDialogBinding dialogBinding;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static int AUTOCOMPLETE_REQUEST_DIALOG_CODE = 2;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        sharedPref = SharedPref.getInstance(mContext);

        FirebaseInstanceId
                .getInstance()
                .getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {

                        if (!task.isSuccessful()) {
                            return;
                        }

                        String token = task.getResult().getToken();
                        registerId = token;

                    }

                });

        if (!Places.isInitialized()) {
            Places.initialize(mContext, getString(R.string.places_api_key));
        }

        init();

    }

    private void init() {

        binding.tvSendRequest.setOnClickListener(v -> {
            openRequestDialog();
        });

        binding.changeLang.setOnClickListener(v -> {
            changeLangDialog();
        });

        binding.spUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (binding.spUserType.getSelectedItemPosition() != 1) {
                    binding.llDocuments.setVisibility(View.VISIBLE);
                } else {
                    binding.llDocuments.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        binding.ivDoc1.setOnClickListener(v -> {
            if (checkPermissions()) {
                final PickImageDialog dialog = PickImageDialog.build(new PickSetup());
                dialog.setOnPickCancel(new IPickCancel() {
                    @Override
                    public void onCancelClick() {
                        dialog.dismiss();
                    }
                }).setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        if (r.getError() == null) {

                            String path = r.getPath();
                            doc1Img = new File(path);

                            Compress.get(mContext).setQuality(70)
                                    .execute(new Compress.onSuccessListener() {
                                        @Override
                                        public void response(boolean status, String message, File file) {
                                            Log.e("kjsgdfjklgdkjasf", "file = " + file.length() / 1024 + "kb      " + message);
                                            doc1Img = file;
                                            binding.ivDoc1.setImageURI(r.getUri());
                                        }
                                    }).CompressedImage(path);

                        } else {
                            // Handle possible errors
                            // TODO: do what you have to do with r.getError();
                            Toast.makeText(mContext, r.getError().getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }

                }).show(SignUpActivity.this);
            } else {
                requestPermissions();
            }
        });

        binding.ivDoc2.setOnClickListener(v -> {
            if (checkPermissions()) {
                final PickImageDialog dialog = PickImageDialog.build(new PickSetup());
                dialog.setOnPickCancel(new IPickCancel() {
                    @Override
                    public void onCancelClick() {
                        dialog.dismiss();
                    }
                }).setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        if (r.getError() == null) {

                            String path = r.getPath();
                            doc2Img = new File(path);

                            Compress.get(mContext).setQuality(70)
                                    .execute(new Compress.onSuccessListener() {
                                        @Override
                                        public void response(boolean status, String message, File file) {
                                            Log.e("kjsgdfjklgdkjasf", "file = " + file.length() / 1024 + "kb      " + message);
                                            doc2Img = file;
                                            binding.ivDoc2.setImageURI(r.getUri());
                                        }
                                    }).CompressedImage(path);

                        } else {
                            // Handle possible errors
                            // TODO: do what you have to do with r.getError();
                            Toast.makeText(mContext, r.getError().getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }

                }).show(SignUpActivity.this);
            } else {
                requestPermissions();
            }
        });

        binding.address.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });

        binding.tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(mContext, LoginActivity.class));
        });

        binding.btSignUp.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etUsername.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_username), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_email_add), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etPhone.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_phone_add), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.address.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_select_add), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.landAddress.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enterlandmark_add), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.pass.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_pass), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.confirmPass.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_conf_pass), Toast.LENGTH_SHORT).show();
            } else if (!(binding.pass.getText().toString().trim().length() > 4)) {
                Toast.makeText(mContext, getString(R.string.password_validation_text), Toast.LENGTH_SHORT).show();
            } else if (!(binding.pass.getText().toString().trim().equals(binding.confirmPass.getText().toString().trim()))) {
                Toast.makeText(mContext, getString(R.string.password_not_match), Toast.LENGTH_SHORT).show();
            } else if (!ProjectUtil.isValidEmail(binding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            } else if (!isValidMobile(binding.etPhone.getText().toString().replace(" ", ""))) {
                Toast.makeText(mContext, getString(R.string.invalid_number), Toast.LENGTH_SHORT).show();
            } /*else if (!validateUsing_libphonenumber(binding.etPhone.getText().toString().replace(" ", "")
                    , binding.ccp.getSelectedCountryCode())) {
                Toast.makeText(mContext, getString(R.string.invalid_number), Toast.LENGTH_SHORT).show();
            } */else {

                if (TextUtils.isEmpty(binding.etReferralCode.getText().toString().trim())) {
                    HashMap<String, String> params = new HashMap<>();

                    params.put("user_name", binding.etUsername.getText().toString().trim());
                    params.put("email", binding.etEmail.getText().toString().trim());
                    params.put("mobile", binding.etPhone.getText().toString().trim());
                    params.put("address", binding.address.getText().toString().trim());
                    params.put("register_id", registerId);
                    params.put("lat", String.valueOf(latLng.latitude));
                    params.put("lon", String.valueOf(latLng.longitude));
                    params.put("land_mark", binding.landAddress.getText().toString().trim());
                    params.put("password", binding.pass.getText().toString().trim());
                    params.put("referral_code", binding.etReferralCode.getText().toString().trim());
                    params.put("type", "USER");

                    HashMap<String, File> fileHashMap = new HashMap<>();

                    fileHashMap.put("document1", doc1Img);
                    fileHashMap.put("document2", doc2Img);
                    fileHashMap.put("image", null);

                    String mobileWithCounCode = (binding.ccp.getSelectedCountryCodeWithPlus()
                            + binding.etPhone.getText().toString().trim()).replace(" ", "");

                    startActivity(new Intent(mContext, VerifyOtpActivity.class)
                            .putExtra("resgisterHashmap", params)
                            .putExtra("mobile", mobileWithCounCode)
                            .putExtra("request", "user")
                    );
                } else {
                    checkReferCode(binding.etReferralCode.getText().toString().trim());
                }
            }
        });

    }

    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
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
                startActivity(new Intent(mContext, SignUpActivity.class));
                dialog.dismiss();
            } else if (radioSpanish.isChecked()) {
                ProjectUtil.updateResources(mContext, "es");
                sharedPref.setlanguage("lan", "es");
                finish();
                startActivity(new Intent(mContext, SignUpActivity.class));
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void checkReferCode(String code) {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("referral_code", code);
        paramHash.put("type", "USER");

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.checkReferApiCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);

                        Log.e("afafasfasf", "stringResponse = " + stringResponse);

                        if (jsonObject.getString("status").equals("1")) {

                            HashMap<String, String> params = new HashMap<>();

                            params.put("user_name", binding.etUsername.getText().toString().trim());
                            params.put("email", binding.etEmail.getText().toString().trim());
                            params.put("mobile", binding.etPhone.getText().toString().trim());
                            params.put("address", binding.address.getText().toString().trim());
                            params.put("register_id", registerId);
                            params.put("lat", String.valueOf(latLng.latitude));
                            params.put("lon", String.valueOf(latLng.longitude));
                            params.put("land_mark", binding.landAddress.getText().toString().trim());
                            params.put("password", binding.pass.getText().toString().trim());
                            params.put("referral_code", binding.etReferralCode.getText().toString().trim());
                            params.put("type", "USER");

                            HashMap<String, File> fileHashMap = new HashMap<>();

                            fileHashMap.put("document1", doc1Img);
                            fileHashMap.put("document2", doc2Img);
                            fileHashMap.put("image", null);

                            String mobileWithCounCode = (binding.ccp.getSelectedCountryCodeWithPlus()
                                    + binding.etPhone.getText().toString().trim()).replace(" ", "");

                            startActivity(new Intent(mContext, VerifyOtpActivity.class)
                                    .putExtra("resgisterHashmap", params)
                                    .putExtra("mobile", mobileWithCounCode)
                                    .putExtra("request", "user")
                            );

                        } else if (jsonObject.getString("status").equals("2")) {
                            Toast.makeText(mContext, "Not allowed to use drivers refferal code", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, "Your referral code is wrong", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
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
            }

        });
    }

    private void openRequestDialog() {
        parentDialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.submit_request_dialog, null, false);
        parentDialog.setContentView(dialogBinding.getRoot());

        dialogBinding.address.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_DIALOG_CODE);
        });

        dialogBinding.ivDoc1.setOnClickListener(v -> {
            if (checkPermissions()) {
                final PickImageDialog dialog = PickImageDialog.build(new PickSetup());
                dialog.setOnPickCancel(new IPickCancel() {
                    @Override
                    public void onCancelClick() {
                        dialog.dismiss();
                    }
                }).setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        if (r.getError() == null) {

                            String path = r.getPath();
                            doc1Dialog = new File(path);

                            Compress.get(mContext).setQuality(90)
                                    .execute(new Compress.onSuccessListener() {
                                        @Override
                                        public void response(boolean status, String message, File file) {
                                            Log.e("kjsgdfjklgdkjasf", "file = " + file.length() / 1024 + "kb      " + message);
                                            doc1Dialog = file;
                                            dialogBinding.ivDoc1.setImageURI(r.getUri());
                                        }
                                    }).CompressedImage(path);

                        } else {
                            // Handle possible errors
                            // TODO: do what you have to do with r.getError();
                            Toast.makeText(mContext, r.getError().getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }

                }).show(SignUpActivity.this);
            } else {
                requestPermissions();
            }
        });

        dialogBinding.ivDoc2.setOnClickListener(v -> {
            if (checkPermissions()) {
                final PickImageDialog dialog = PickImageDialog.build(new PickSetup());
                dialog.setOnPickCancel(new IPickCancel() {
                    @Override
                    public void onCancelClick() {
                        dialog.dismiss();
                    }
                }).setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        if (r.getError() == null) {

                            String path = r.getPath();
                            doc2Dialog = new File(path);

                            Compress.get(mContext).setQuality(90)
                                    .execute(new Compress.onSuccessListener() {
                                        @Override
                                        public void response(boolean status, String message, File file) {
                                            Log.e("kjsgdfjklgdkjasf", "file = " + file.length() / 1024 + "kb      " + message);
                                            doc2Dialog = file;
                                            dialogBinding.ivDoc2.setImageURI(r.getUri());
                                        }
                                    }).CompressedImage(path);

                        } else {
                            // Handle possible errors
                            // TODO: do what you have to do with r.getError();
                            Toast.makeText(mContext, r.getError().getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }

                }).show(SignUpActivity.this);
            } else {
                requestPermissions();
            }
        });

        dialogBinding.btSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dialogBinding.etUsername.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_username), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(dialogBinding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_email_add), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(dialogBinding.etPhone.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_phone_add), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(dialogBinding.address.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_select_add), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(dialogBinding.pass.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_pass), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(dialogBinding.confirmPass.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_conf_pass), Toast.LENGTH_SHORT).show();
            } else if (!(dialogBinding.pass.getText().toString().trim().length() > 4)) {
                Toast.makeText(mContext, getString(R.string.password_validation_text), Toast.LENGTH_SHORT).show();
            } else if (!(dialogBinding.pass.getText().toString().trim().equals(dialogBinding.confirmPass.getText().toString().trim()))) {
                Toast.makeText(mContext, getString(R.string.password_not_match), Toast.LENGTH_SHORT).show();
            } else if (!ProjectUtil.isValidEmail(dialogBinding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            } else if (!validateUsing_libphonenumber(dialogBinding.etPhone.getText().toString().replace(" ", "")
                    , dialogBinding.ccp.getSelectedCountryCode())) {
                Toast.makeText(mContext, getString(R.string.invalid_number), Toast.LENGTH_SHORT).show();
            } else if (doc1Dialog == null || doc2Dialog == null) {
                Toast.makeText(mContext, getString(R.string.please_upload_both_doc), Toast.LENGTH_SHORT).show();
            } else {

                HashMap<String, String> params = new HashMap<>();

                params.put("name", dialogBinding.etUsername.getText().toString().trim());
                params.put("email", dialogBinding.etEmail.getText().toString().trim());
                params.put("mobile", dialogBinding.etPhone.getText().toString().trim());
                params.put("address", dialogBinding.address.getText().toString().trim());
                params.put("password", dialogBinding.pass.getText().toString().trim());

                HashMap<String, File> fileHashMap = new HashMap<>();
                fileHashMap.put("document1", doc1Dialog);
                fileHashMap.put("document2", doc2Dialog);

                requestSuppliersApi(fileHashMap, params, parentDialog);

            }
        });

        parentDialog.show();

    }

    private void requestSuppliersApi(HashMap<String, File> fileHashMap,
                                     HashMap<String, String> params, Dialog dialog) {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build();

        AndroidNetworking.initialize(getApplicationContext(), okHttpClient);

        Log.e("fsfdsfdsfs", "paramHash = " + params);
        Log.e("fsfdsfdsfs", "paramHash = " + fileHashMap);

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        AndroidNetworking.upload(AppConstant.BASE_URL + "request_subadmin")
                .addMultipartParameter(params)
                .addMultipartFile(fileHashMap)
                .setPriority(Priority.HIGH)
                .setTag("request_subadmin")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        ProjectUtil.pauseProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status").equals("1")) {

                                doc1Dialog = null;
                                doc2Dialog = null;
                                dialog.dismiss();
                                openProviderReqDialog();

                                Log.e("zdgfxsdgfxdg", "response = " + response);

                            } else {
                                Toast.makeText(mContext, jsonObject.getString("result"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("Exception", "Exception = " + e.getMessage());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        ProjectUtil.pauseProgressDialog();
                    }

                });
    }

    private void requestProviderApiCall(HashMap<String, String> params, Dialog dialog) {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Call<ResponseBody> call = api.requestSubadmin(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        dialog.dismiss();
                        openProviderReqDialog();
                    } else {
                        // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    private void openProviderReqDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        ProviderReqDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.provider_req_dialog, null, false);
        dialog.setCancelable(false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.btOk.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        dialog.show();

    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_ID
        );
    }

    private boolean validateUsing_libphonenumber(String phNumber, String code) {

        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(code));
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            //phoneNumber = phoneNumberUtil.parse(phNumber, "IN");  //if you want to pass region code
            phoneNumber = phoneNumberUtil.parse(phNumber, isoCode);
        } catch (Exception e) {
            System.err.println(e);
        }

        boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
        if (isValid) {
            String internationalFormat = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            // Toast.makeText(this, "Phone Number is Valid " + internationalFormat, Toast.LENGTH_LONG).show();
            return true;
        } else {
            // Toast.makeText(this, "Phone Number is Invalid " + phoneNumber, Toast.LENGTH_LONG).show();
            return false;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                latLng = place.getLatLng();
                try {
                    String addresses = ProjectUtil.getCompleteAddressString(mContext, place.getLatLng().latitude, place.getLatLng().longitude);
                    binding.address.setText(addresses);
                } catch (Exception e) {
                }
            }

        } else if (requestCode == AUTOCOMPLETE_REQUEST_DIALOG_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                latLng = place.getLatLng();
                try {
                    String addresses = ProjectUtil.getCompleteAddressString(mContext, place.getLatLng().latitude, place.getLatLng().longitude);
                    dialogBinding.address.setText(addresses);
                } catch (Exception e) {
                }
            }
        }


    }


}