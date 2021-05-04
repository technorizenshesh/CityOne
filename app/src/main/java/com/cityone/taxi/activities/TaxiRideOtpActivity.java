package com.cityone.taxi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.cityone.R;
import com.cityone.databinding.ActivityTaxiRideOtpBinding;
import com.cityone.databinding.SerachDriverDialogBinding;
import com.cityone.models.ModelLogin;
import com.cityone.shipping.adapters.AdapterChats;
import com.cityone.shipping.models.ModelChatList;
import com.cityone.taxi.adapters.AdapterCarTypes;
import com.cityone.taxi.models.ModelTaxiType;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.AppConstant;
import com.cityone.utils.DrawPollyLine;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.maps.model.JointType.ROUND;

public class TaxiRideOtpActivity extends
        AppCompatActivity implements OnMapReadyCallback {

    Context mContext = TaxiRideOtpActivity.this;
    ActivityTaxiRideOtpBinding binding;
    GoogleMap mMap;
    ArrayList<LatLng> polyLineLatLngs;
    LatLng pickLatLng,dropLatLng;
    PolylineOptions polylineOptions;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    String carId = "";
    Dialog searchDialog;
    private String estiCost;
    String pickAdd,dropAdd;
    String timeZone = TimeZone.getDefault().getID();
    private String request_id,currentDate,currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_taxi_ride_otp);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        currentDate = ProjectUtil.getCurrentDate();
        currentTime = ProjectUtil.getCurrentTime();

        polyLineLatLngs = sharedPref.getLatLngList(AppConstant.LAT_LON_LIST);
        pickLatLng = getIntent().getExtras().getParcelable("picklatlon");
        dropLatLng = getIntent().getExtras().getParcelable("droplatlon");

        pickAdd = ProjectUtil.getCompleteAddressString(mContext,pickLatLng.latitude,pickLatLng.longitude);
        dropAdd = ProjectUtil.getCompleteAddressString(mContext,dropLatLng.latitude,dropLatLng.longitude);

        init();
    }

    private void init() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(TaxiRideOtpActivity.this);

        getAllCarTypes();

        binding.btRequestNow.setOnClickListener(v -> {
           addBookingRequest();
        });

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("fsdfsfsdfdsfdsf","Broadcast intent = " + intent.getStringExtra("object"));
            if(intent.getStringExtra("object") != null) {
                searchDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(intent.getStringExtra("object"));
                    request_id = String.valueOf(jsonObject.get("request_id"));
                    startActivity(new Intent(mContext,TrackTaxiAct.class)
                       .putExtra("request_id",request_id)
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver,new IntentFilter("driver_accept_request"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private void searchDriverDialog() {
        searchDialog = new Dialog(mContext,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        searchDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        SerachDriverDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.serach_driver_dialog,null,false);
        searchDialog.setContentView(dialogBinding.getRoot());
        searchDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogBinding.riipleIcon.startRippleAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                dialogBinding.riipleIcon.stopRippleAnimation();
//                searchDialog.dismiss();
//                startActivity(new Intent(mContext,TrackTaxiAct.class));
            }
        },5000);

        dialogBinding.btnCancel.setOnClickListener(v -> {
            searchDialog.dismiss();
        });
        searchDialog.show();
    }

    private void getAllCarTypes() {
        ProjectUtil.showProgressDialog(mContext,false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> params = new HashMap<>();
        params.put("user_id",modelLogin.getResult().getId());
        params.put("picuplat",String.valueOf(pickLatLng.latitude));
        params.put("pickuplon",String.valueOf(pickLatLng.longitude));
        params.put("droplat",String.valueOf(dropLatLng.latitude));
        params.put("droplon",String.valueOf(dropLatLng.longitude));

        Call<ResponseBody> call = api.getTypeList(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                ProjectUtil.pauseProgressDialog();

                Log.e("kghkljsdhkljf","response = " + response);

                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    Log.e("kjagsdkjgaskjd","stringResponse = " + response);
                    Log.e("kjagsdkjgaskjd","stringResponse = " + stringResponse);

                    if (jsonObject.getString("status").equals("1")) {

                        ModelTaxiType modelTaxiType = new Gson().fromJson(stringResponse, ModelTaxiType.class);

                        AdapterCarTypes adapterCarTypes = new AdapterCarTypes(mContext, modelTaxiType.getResult(),TaxiRideOtpActivity.this::updateCarType);
                        binding.rvCarTypes.setAdapter(adapterCarTypes);

                    } else {
                        Toast.makeText(mContext, getString(R.string.no_car_found), Toast.LENGTH_SHORT).show();
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

    public void updateCarType(String carIdNew,String amount) {
        carId = carIdNew;
        estiCost = amount;
        binding.tvEstiTime.setText(AppConstant.DOLLAR + amount);
        Log.e("hjsdgfhjds","carId = " + carId);
    }

    private void addBookingRequest() {
        ProjectUtil.showProgressDialog(mContext,false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        Log.e("sfddsfdsfdsf","user_id = " + modelLogin.getResult().getId());

        HashMap<String,String> params = new HashMap<>();
        params.put("user_id",modelLogin.getResult().getId());
        params.put("picuplat",String.valueOf(pickLatLng.latitude));
        params.put("pickuplon",String.valueOf(pickLatLng.longitude));
        params.put("droplat",String.valueOf(dropLatLng.latitude));
        params.put("droplon",String.valueOf(dropLatLng.longitude));
        params.put("timezone",timeZone);
        params.put("device_type","android");
        params.put("car_type_id",carId);
        params.put("picuplocation",pickAdd);
        params.put("dropofflocation",dropAdd);
        params.put("booktype","NOW");
        params.put("picklatertime",currentTime);
        params.put("picklaterdate",currentDate);
        params.put("payment_type","");
        params.put("estimate_charge_amount",estiCost);

        Log.e("hjadkjshakjdhkjas","params = " + params);

        Call<ResponseBody> call = api.taxiBookingRequest(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                ProjectUtil.pauseProgressDialog();

                Log.e("kghkljsdhkljf","response = " + response);

                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    Log.e("kjagsdkjgaskjd","stringResponse = " + response);
                    Log.e("kjagsdkjgaskjd","stringResponse = " + stringResponse);

                    if (jsonObject.getString("status").equals("1")) {
                         searchDriverDialog();
                    } else if(jsonObject.getString("status").equals("2")) {
                         alreadyTripDialog();
                    } else {
                         Toast.makeText(mContext,getString(R.string.no_car_found), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Log.e("kjagsdkjgaskjd","stringResponse = " + t.getMessage());
            }

        });

    }

    private void alreadyTripDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.you_are_already_in_trip));
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                startActivity(new Intent(mContext,TripHistoryAct.class));
            }
        }).create().show();
    }

    private void addPickDropMarkerOnMap(LatLng pickUpLatLng, LatLng dropOffLatLng) {
        MarkerOptions markerOptionsPick = new MarkerOptions()
                .position(new LatLng(pickUpLatLng.latitude, pickUpLatLng.longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker))
                .title(ProjectUtil.getCompleteAddressString(mContext, pickUpLatLng.latitude, pickUpLatLng.longitude));

        MarkerOptions markerOptionsDrop = new MarkerOptions()
                .position(new LatLng(dropOffLatLng.latitude, dropOffLatLng.longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_marker))
                .title(ProjectUtil.getCompleteAddressString(mContext, dropOffLatLng.latitude, dropOffLatLng.longitude));

        mMap.addMarker(markerOptionsPick);
        mMap.addMarker(markerOptionsDrop);
        ArrayList<LatLng> latlngList = new ArrayList<>();
        latlngList.add(pickUpLatLng);
        latlngList.add(dropOffLatLng);

        zoomRoute(mMap, latlngList);
    }

    public void zoomRoute(GoogleMap googleMap, List<LatLng> lstLatLngRoute) {
        if (googleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 150;
        LatLngBounds latLngBounds = boundsBuilder.build();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
    }

    private void addPolyLinesOnMap(ArrayList<LatLng> latLngs) {
        polylineOptions = new PolylineOptions();
        polylineOptions.addAll(latLngs);
        polylineOptions.color(Color.BLUE);
        polylineOptions.width(10);
        polylineOptions.startCap(new SquareCap());
        polylineOptions.endCap(new SquareCap());
        polylineOptions.jointType(ROUND);
        polylineOptions.geodesic(true);
        polylineOptions.zIndex(5f);
        mMap.addPolyline(polylineOptions);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        addPickDropMarkerOnMap(pickLatLng,dropLatLng);
        addPolyLinesOnMap(polyLineLatLngs);
    }

}