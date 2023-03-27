package com.cityone.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.cityone.R;
import com.cityone.adapters.AdapterHome;
import com.cityone.adapters.AdapterPoints;
import com.cityone.adapters.AllStoresAdapter;
import com.cityone.adapters.SliderAdapter;
import com.cityone.adapters.StoreCatAdapter;
import com.cityone.databinding.ActivityDashboardBinding;
import com.cityone.databinding.ProviderReqDialogBinding;
import com.cityone.databinding.SubmitRequestDialogBinding;
import com.cityone.entertainment.activities.EntHomeActivity;
import com.cityone.listener.HomeListener;
import com.cityone.meals.MealsHomeActivity;
import com.cityone.models.BannerModel;
import com.cityone.models.ModelHome;
import com.cityone.models.ModelLogin;
import com.cityone.models.ModelReferrals;
import com.cityone.shipping.ShippingActivity;
import com.cityone.shipping.ShippingHomeAct;
import com.cityone.stores.activities.StoresActivity;
import com.cityone.stores.adapters.AdapterStoreCat;
import com.cityone.stores.adapters.AdapterStores;
import com.cityone.stores.models.ModelStoreCat;
import com.cityone.stores.models.ModelStoreCat1;
import com.cityone.stores.models.ModelStoreItems;
import com.cityone.stores.models.ModelStores;
import com.cityone.taxi.activities.TaxiHomeActivity;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.App;
import com.cityone.utils.AppConstant;
import com.cityone.utils.Compress;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class DashboardActivity extends AppCompatActivity implements HomeListener {

    private static final int AUTOCOMPLETE_REQUEST_DIALOG_CODE = 1022;
    Context mContext = DashboardActivity.this;
    ActivityDashboardBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 3000;  /* 5 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    final int REQUEST_LOCATION_PERMISSION_CODE = 101;
    Location currentLocation;
    SubmitRequestDialogBinding dialogBinding;
    private Dialog parentDialog;
    private File doc1Dialog,doc2Dialog;
    private LatLng latLng;
    ArrayList<ModelHome.Result>arrayList;
    ArrayList<ModelStoreCat1.Result>storeCatList;
    ArrayList<BannerModel.Result>bannerArrayList;
    SliderAdapter sliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        ProjectUtil.changeStatusBarColor(DashboardActivity.this);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        arrayList = new ArrayList<>();
        if (!Places.isInitialized()) {
            Places.initialize(mContext, getString(R.string.places_api_key));
        }

        Log.e("fsdfdsfds", "modelLogin = " + new Gson().toJson(modelLogin));

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);

        App.checkToken(mContext);

        init();

        getHomeCate();
        GetBannerAPi();
    }

    private void getHomeCate() {
        ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getAllHomeCate();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.e("get All CateHome Response", "response = " + response);
                    if (jsonObject.getString("status").equals("1")) {
                        ModelHome modelHome = new Gson().fromJson(responseString, ModelHome.class);
                        arrayList.clear();
                        arrayList.addAll(modelHome.getResult());
                        AdapterHome adapterHome = new AdapterHome(mContext,arrayList,DashboardActivity.this);
                        binding.rvHomeCat.setAdapter(adapterHome);
                        getStoreCat(modelHome.getResult().get(0).getId());

                    } else {
                        AdapterStores adapterStores = new AdapterStores(mContext, null);
                        binding.rvHomeCat.setAdapter(adapterStores);
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




    private void getStoreCat(String id) {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Map<String,String> map = new HashMap<>();
        map.put("main_category_id",id);
        Call<ResponseBody> call = api.getAllStoreCats(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.getString("status").equals("1")) {

                        ModelStoreCat1 modelStoreCat = new Gson().fromJson(responseString, ModelStoreCat1.class);
                        storeCatList.clear();
                        storeCatList.addAll(modelStoreCat.getResult());
                        StoreCatAdapter adapterStoreCat = new StoreCatAdapter(mContext, modelStoreCat.getResult(),DashboardActivity.this);
                        binding.rvStoresCat.setAdapter(adapterStoreCat);

                        Log.e("responseString", "response = " + response);
                        Log.e("responseString", "responseString = " + responseString);
                        Log.e("responseString", "id = " + modelStoreCat.getResult().get(0).getId());

                        getAllStores(modelStoreCat.getResult().get(0).getId(), modelStoreCat.getResult().get(0).getMain_category_id());

                    } else {
                        AdapterStoreCat adapterStoreCat = new AdapterStoreCat(mContext, null, false, true,false);
                        binding.rvStoresCat.setAdapter(adapterStoreCat);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       if (requestCode == AUTOCOMPLETE_REQUEST_DIALOG_CODE) {
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

    private void getAllStores(String id, String name) {
        ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));

        HashMap<String, String> param = new HashMap<>();
        param.put("sub_category_id", id);
        param.put("main_category_id", name);
        Log.e("check ===",param.toString());
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getStoreByCatApiCallNew(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.getString("status").equals("1")) {

                        Log.e("responseString", "response = " + response);
                        Log.e("responseString", "responseString = " + responseString);

                        ModelStores modelStores = new Gson().fromJson(responseString, ModelStores.class);

                        AllStoresAdapter adapterStores = new AllStoresAdapter(mContext, modelStores.getResult());
                        binding.rvStores.setAdapter(adapterStores);

                    } else {
                        AdapterStores adapterStores = new AdapterStores(mContext, null);
                        binding.rvStores.setAdapter(adapterStores);
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

    public void getStoresById(String id, String name) {
        Log.e("ididididid", "id = " + id);
        Log.e("ididididid", "name = " + name);
        getAllStores(id, name);
    }

    private void init() {

        storeCatList = new ArrayList<>();

        bannerArrayList = new ArrayList<>();

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                setCurrentLocation();
            }
        } else {
            requestPermissions();
        }

        binding.cvCashBack.setOnClickListener(v -> {
            startActivity(new Intent(mContext, CashBackAct.class));
        });

        binding.tvEarnings.setOnClickListener(v -> {
            startActivity(new Intent(mContext, CashBackAct.class));
        });

        binding.cvTaxi.setOnClickListener(v -> {
            startActivity(new Intent(mContext, TaxiHomeActivity.class));
        });

        binding.tvName.setText(modelLogin.getResult().getUser_name());
        binding.navItems.tvName.setText(modelLogin.getResult().getUser_name());

        binding.navItems.tvMybooking.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, MyBookingActivity.class));
        });

        binding.navItems.tvChangeLan.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            changeLangDialog();
        });

        binding.navItems.tvWantSupplier.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            openRequestDialog();
        });

        binding.navItems.tvMoveiBooks.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            // startActivity(new Intent(mContext,MyBookingActivity.class));
        });

        binding.navItems.tvAccount.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, AccountActivity.class));
        });

        binding.navItems.tvAccount.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, AccountActivity.class));
        });

        binding.navItems.tvWallet.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, WalletActivity.class));
        });

        binding.navItems.tvLogout.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            logoutAppDialog();
        });

        binding.ivMenu.setOnClickListener(v -> {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        });

        binding.cvStores.setOnClickListener(v -> {
            startActivity(new Intent(mContext, StoresActivity.class));
        });

        binding.btViewAll.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this,StoreListAct.class).putExtra("id",arrayList.get(0).getId()).putExtra("title","Stores"));

        });

        binding.cvMeals.setOnClickListener(v -> {
            startActivity(new Intent(mContext, MealsHomeActivity.class));
        });

        binding.cvEnt.setOnClickListener(v -> {
            startActivity(new Intent(mContext, EntHomeActivity.class));
        });

        binding.cvShipping.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ShippingActivity.class));
        });
    }

    private void getAllPoints() {
        // ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("my_referral_code", modelLogin.getResult().getMy_referral_code());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getReferralPOints(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(stringResponse);

                        if (jsonObject.getString("status").equals("1")) {

                            Log.e("asfddasfasdf", "response = " + stringResponse);
                            ModelReferrals modelReferrals = new Gson().fromJson(stringResponse, ModelReferrals.class);

                            binding.tvEarnings.setText("You Currently have " + modelReferrals.getReferral_code_point() + " points");
                            binding.tvOldPoints.setText("Previous Points : " + modelReferrals.getReferral_point_fifteen_day());
                            binding.tvPurchasedAmount.setText("You can earn : " + AppConstant.CURRENCY + " " + modelReferrals.getPurches_amount());

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
                startActivity(new Intent(mContext, DashboardActivity.class));
                dialog.dismiss();
            } else if (radioSpanish.isChecked()) {
                ProjectUtil.updateResources(mContext, "es");
                sharedPref.setlanguage("lan", "es");
                finish();
                startActivity(new Intent(mContext, DashboardActivity.class));
                dialog.dismiss();
            }
        });

        dialog.show();

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

                }).show(DashboardActivity.this);
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

                }).show(DashboardActivity.this);
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
            } else if (dialogBinding.pass.getText().toString().trim().length() < 4) {
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
    public void onResume() {
        super.onResume();
        getAllPoints();
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                setCurrentLocation();
            } else {
                Toast.makeText(mContext, getString(R.string.turn_on_loc), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                DashboardActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,},
                REQUEST_LOCATION_PERMISSION_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isLocationEnabled()) {
                    setCurrentLocation();
                } else {
                    Toast.makeText(DashboardActivity.this, getString(R.string.turn_on_loc), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            }
        }
    }

    private void setCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Log.e("ivCurrentLocation", "location = " + location);

                    try {
                        String address = ProjectUtil.getCompleteAddressString(mContext, currentLocation.getLatitude(), currentLocation.getLongitude());

                        if (TextUtils.isEmpty(binding.tvAddress.getText().toString().trim())) {
                            binding.tvAddress.setText(address);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    startLocationUpdates();
                    Log.e("ivCurrentLocation", "location = " + location);
                }
            }
        });
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(DashboardActivity.this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        getFusedLocationProviderClient(DashboardActivity.this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (currentLocation != null) {
                    try {
                        String address = ProjectUtil.getCompleteAddressString(mContext, currentLocation.getLatitude(), currentLocation.getLongitude());

                        if (TextUtils.isEmpty(binding.tvAddress.getText().toString().trim())) {
                            binding.tvAddress.setText(address);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (locationResult != null) {
                        Log.e("hdasfkjhksdf", "Location = " + locationResult.getLastLocation());
                        currentLocation = locationResult.getLastLocation();
                        try {
                            String address = ProjectUtil.getCompleteAddressString(mContext, currentLocation.getLatitude(), currentLocation.getLongitude());

                            if (TextUtils.isEmpty(binding.tvAddress.getText().toString().trim())) {
                                binding.tvAddress.setText(address);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        onResume();
                    }
                }

            }

        }, Looper.myLooper());

    }

    private void exitAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.close_app_text))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        exitAppDialog();
    }

    private void logoutAppDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.logout_text))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPref.clearAllPreferences();
                        Intent loginscreen = new Intent(mContext, GetStartedActivity.class);
                        loginscreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        NotificationManager nManager = ((NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE));
                        nManager.cancelAll();
                        startActivity(loginscreen);
                        finishAffinity();
                    }
                }).setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


    @Override
    public void OnHome(int position, String name) {
          if(name.equals("TIENDAS")) {startActivity(new Intent(DashboardActivity.this,StoreListAct.class).putExtra("id",arrayList.get(position).getId()).putExtra("title","TIENDAS"));}
          else if(name.equals("COMIDAS")) {startActivity(new Intent(DashboardActivity.this,StoreListAct.class).putExtra("id",arrayList.get(position).getId()).putExtra("title","COMIDAS"));}
          else if(name.equals("MASCOTAS")) {startActivity(new Intent(DashboardActivity.this,StoreListAct.class).putExtra("id",arrayList.get(position).getId()).putExtra("title","MASCOTAS"));}
          else if(name.equals("ENTRETENIMIENTO")) {startActivity(new Intent(DashboardActivity.this,EntHomeActivity.class));}
        //  else if(name.equals("Shipping")) {startActivity(new Intent(DashboardActivity.this,ShippingActivity.class));}
          else if(name.equals("ENVÍOS")) {startActivity(new Intent(DashboardActivity.this, ShippingHomeAct.class));}
          else if(name.equals("TRANSPORTE")) {}
          else if(name.equals("MIS GANANCIAS")) {}
          else if(name.equals("FARMACIAS Y SALUD")) {startActivity(new Intent(DashboardActivity.this,StoreListAct.class).putExtra("id",arrayList.get(position).getId()
                  ).putExtra("title","FARMACIAS Y SALUD"));}


          else if(name.equals(storeCatList.get(position).getName())){
              getAllStores(storeCatList.get(position).getId(),storeCatList.get(position).getMain_category_id());

          }

          }



    private void GetBannerAPi() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Map<String, String> map = new HashMap<>();
      /*  map.put("user_id", PreferenceConnector.readString(getContext(), PreferenceConnector.User_id, ""));
        map.put("country_id", addresses.get(0).getCountryName()+"");
        Log.e("MapMap", "EXERSICE LIST" + map);*/
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        Call<ResponseBody> loginCall = api.getAllBanners();

        loginCall.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                 try {
                String responseString = response.body().string();
                JSONObject jsonObject = new JSONObject(responseString);

                if(jsonObject.getString("status").equals("1")) {
                    BannerModel bannerModel = new Gson().fromJson(responseString, BannerModel.class);
                    bannerArrayList.clear();
                    bannerArrayList.addAll(bannerModel.getResult());
                    sliderAdapter = new SliderAdapter(DashboardActivity.this, bannerArrayList);
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
                        Toast.makeText(DashboardActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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



}
