package com.cityone.taxi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_taxi_ride_otp);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        polyLineLatLngs = (ArrayList<LatLng>) getIntent().getSerializableExtra("polylines");
        pickLatLng = getIntent().getExtras().getParcelable("picklatlon");
        dropLatLng = getIntent().getExtras().getParcelable("droplatlon");

        init();
    }

    private void init() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(TaxiRideOtpActivity.this);

        getAllCarTypes();

        binding.btRequestNow.setOnClickListener(v -> {
            searchDriverDialog();
        });

    }

    private void searchDriverDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);

        SerachDriverDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.serach_driver_dialog,null,false);
        dialog.setContentView(dialogBinding.getRoot());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        },3000);

        dialog.dismiss();
    }

    private void getAllCarTypes () {
        ProjectUtil.showProgressDialog(mContext,false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        Call<ResponseBody> call = api.getCarTypesApi();

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
                        Toast.makeText(mContext, getString(R.string.no_chat_found), Toast.LENGTH_SHORT).show();
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

    public void updateCarType(String carIdNew) {
        carId = carIdNew;
        Log.e("hjsdgfhjds","carId = " + carId);
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