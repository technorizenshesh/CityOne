package com.cityone.taxi.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.cityone.R;
import com.cityone.activities.DashboardActivity;
import com.cityone.databinding.ActivityTrackTaxiBinding;
import com.cityone.databinding.TaxiPaymentDialogBinding;
import com.cityone.databinding.TaxiPaymentHistoryDialogBinding;
import com.cityone.models.ModelLogin;
import com.cityone.taxi.models.ModelTaxiBookingDetail;
import com.cityone.taxi.models.ModelTripHistory;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.AppConstant;
import com.cityone.utils.DrawPollyLine;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackTaxiAct extends AppCompatActivity implements OnMapReadyCallback {

    Context mContext = TrackTaxiAct.this;
    ActivityTrackTaxiBinding binding;
    String request_id = "";
    private LatLng pickUpLatLng, dropOffLatLng;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private Marker driverCarMarker;
    private long UPDATE_INTERVAL = 3000;
    private long FASTEST_INTERVAL = 2000;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    GoogleMap map;
    Timer timer = new Timer();
    ModelTaxiBookingDetail data;
    AlertDialog.Builder builder1;
    String driverStatus = "",driverId=null,driverName="",driverImage="",driverMobile =null,driverLat,driverLon;
    private PolylineOptions lineOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_track_taxi);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);

        if (getIntent() != null)
            request_id = getIntent().getStringExtra("request_id");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        init();

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("object") != null) {
                try {
                    JSONObject jsonObject = new JSONObject(intent.getStringExtra("object"));
                    binding.ivCancel.setVisibility(View.GONE);
                    DriverArriveDialog(String.valueOf(jsonObject.get("status")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void openPaymentSummaryDialog(ModelTaxiBookingDetail data) {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        TaxiPaymentDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                ,R.layout.taxi_payment_dialog,null,false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.setPayment(data.getResult());
        dialogBinding.tvEstiPrice.setText(AppConstant.DOLLAR + data.getResult().getEstimateChargeAmount());

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(mContext,DashboardActivity.class));
            finishAffinity();
        });

        dialogBinding.btPayNow.setOnClickListener(v -> {
            
        });

        dialog.show();

    }

    private void cancelByDriverDialog() {
        builder1 = new AlertDialog.Builder(TrackTaxiAct.this);
        builder1.setMessage(getResources()
                .getString(R.string.are_your_sure_you_want_to_cancel_the_trip));
        builder1.setCancelable(false);

        builder1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        cancelByUserApi();
                    }
                });

        builder1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void cancelByUserApi() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> param = new HashMap<>();
        param.put("request_id", request_id);

        Call<ResponseBody> call = api.cancelTripByUser(param);

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

//                        ModelTaxiBookingDetail data = new Gson().fromJson(stringResponse,ModelTaxiBookingDetail.class);
//
//                        driverStatus = data.getResult().getStatus();
//                        Log.e("kjagsdkjgaskjd","status = " + driverStatus);

                        finishAffinity();
                        startActivity(new Intent(mContext, DashboardActivity.class));
                    } else {
                        // Toast.makeText(mContext, getString(R.string.no_chat_found), Toast.LENGTH_SHORT).show();
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

    private void init() {

        binding.ivCancel.setOnClickListener(v -> {
            cancelByDriverDialog();
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.icCall.setOnClickListener(v -> {
            if(driverMobile != null)
                callDriver(driverMobile);
        });

        binding.ivChat.setOnClickListener(v -> {
            startActivity(new Intent(mContext,TaxiChatingActivity.class)
            .putExtra("sender_id",modelLogin.getResult().getId())
            .putExtra("receiver_id",driverId)
            .putExtra("name",driverName)
            .putExtra("request_id",request_id)
            );
        });

    }

    private void callDriver(String number) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + number)); //change the number.
        startActivity(callIntent);
    }

    public void updateDriverLatLon(String driverId) {

        Api api = ApiFactory.getClientWithoutHeader(this).create(Api.class);

        HashMap<String,String> param = new HashMap<>();
        param.put("user_id",driverId);

        Call<ResponseBody> call = api.getDriverLocation(param);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("fasfasdfasd","responseString = " + responseString);
                    Log.e("fasfasdfasd","response = " + response);

                    if(jsonObject.getString("status").equals("1")) {
                        double lat = Double.parseDouble(jsonObject.getString("lat"));
                        double lon = Double.parseDouble(jsonObject.getString("lon"));

                        Location currentLocation = new Location("");
                        currentLocation.setLatitude(lat);
                        currentLocation.setLongitude(lon);

                        if (map != null) {
                            if(driverCarMarker == null) {
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_top))
                                        .title("Driver Location");
                                driverCarMarker = map.addMarker(markerOptions);
                                driverCarMarker.setRotation(currentLocation.getBearing());
                                LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                                // map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                                // Toast.makeText(mContext, "marker added if ", Toast.LENGTH_SHORT).show();
                            } else {
                                LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                                driverCarMarker.setPosition(latLng);
                                driverCarMarker.setRotation(currentLocation.getBearing());
                                // map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                                // Toast.makeText(mContext, "Location update else ", Toast.LENGTH_SHORT).show();
                            }
                        }

                        // String address = ProjectUtil.getCompleteAddressString(MyService.this,Double.parseDouble(lat),Double.parseDouble(lon));

                        // Toast.makeText(MyService.this, "Location update", Toast.LENGTH_SHORT).show();
                    } else {
                        // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure","onFailure = " + t.getMessage());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(driverId != null) updateDriverLatLon(driverId);
            }
        }, 0, 5000);
        registerReceiver(broadcastReceiver,new IntentFilter("Job_Status_Action"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
        unregisterReceiver(broadcastReceiver);
    }

    public void DriverArriveDialog(String status) {
        final Dialog dialog = new Dialog(TrackTaxiAct.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_driver_arrived);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        TextView tvTitle = dialog.findViewById(R.id.tvTitle);

        if(status.equals("Arrived")) {
            binding.titler.setText(getString(R.string.driver_arrived));
            tvTitle.setText(getString(R.string.driver_arrived));
        } else if(status.equals("Start")) {
            tvTitle.setText(getString(R.string.your_trip_has_begin));
            binding.titler.setText(getString(R.string.your_trip_has_begin));
        } else if(status.equals("Finish")) {
            tvTitle.setText(getString(R.string.your_trip_is_finished));
            binding.titler.setText(getString(R.string.your_trip_is_finished));
        } else if(status.equals("Cancel_by_driver")) {
            binding.titler.setText(getString(R.string.your_trip_is_cancelled_by_driver));
            Toast.makeText(mContext,getString(R.string.your_trip_is_cancelled_by_driver), Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(mContext,DashboardActivity.class));
        }

        TextView btnOk = dialog.findViewById(R.id.btnOk);

        btnOk.setOnClickListener(v -> {
            if(status.equals("Finish")) {
              openPaymentSummaryDialog(data);
            }
            //TripStartDialog();
            dialog.dismiss();
        });

        dialog.show();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        getBookingDetail(request_id);
    }

    private void getBookingDetail(String request_id) {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        HashMap<String,String> param = new HashMap<>();
        param.put("request_id",request_id);

        Call<ResponseBody> call = api.bookingDetails(param);

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

                        data = new Gson()
                                .fromJson(stringResponse,ModelTaxiBookingDetail.class);

                        if("Arrived".equals(data.getResult().getStatus())) {
                            binding.titler.setText(getString(R.string.driver_arrived));
                            binding.ivCancel.setVisibility(View.GONE);
                        } else if("Start".equals(data.getResult().getStatus())) {
                            binding.titler.setText(getString(R.string.your_trip_has_begin));
                            binding.ivCancel.setVisibility(View.GONE);
                        } else if("Finish".equals(data.getResult().getStatus())) {
                            binding.titler.setText(getString(R.string.your_trip_is_finished));
                            binding.ivCancel.setVisibility(View.GONE);
                            openPaymentSummaryDialog(data);
                        } else {
                            binding.ivCancel.setVisibility(View.VISIBLE);
                        }

                        driverId = data.getResult().getDriverDetails().getId();
                        driverName = data.getResult().getDriverDetails().getUserName();
                        driverImage = data.getResult().getDriverDetails().getImage();
                        driverMobile = data.getResult().getDriverDetails().getMobile();
                        driverLat = data.getResult().getDriverDetails().getLat();
                        driverLon = data.getResult().getDriverDetails().getLon();
                        binding.tvName.setText(driverName);
                        binding.tvCaraNumber.setText("Car No : " + data.getResult().getDriverDetails().getCarNumber());
                        binding.tvCarModel.setText(data.getResult().getDriverDetails().getCarModel());

                        updateDriverLatLon(driverId);

                        try {
                            Picasso.get().load(driverImage)
                                    .placeholder(R.drawable.default_profile_icon)
                                    .error(R.drawable.default_profile_icon).into(binding.ivDriverPropic);
                        } catch (Exception e){}

                        pickUpLatLng = new LatLng(Double.parseDouble(data.getResult().getPicuplat()), Double.parseDouble(data.getResult().getPickuplon()));
                        dropOffLatLng = new LatLng(Double.parseDouble(data.getResult().getDroplat()), Double.parseDouble(data.getResult().getDroplon()));

                        Log.e("fgdfgdfgdfg","pickUpLatLng = " + pickUpLatLng);
                        Log.e("fgdfgdfgdfg","dropOffLatLng = " + dropOffLatLng);

                        if (checkPermissions()) {
                            if (isLocationEnabled()) {
                                Log.e("dropOffLatLng","pickUpLatLng = " + pickUpLatLng);
                                Log.e("dropOffLatLng","dropOffLatLng = " + dropOffLatLng);
                                setPickUpDropOffRoute();
                            } else {
                                Toast.makeText(TrackTaxiAct.this, "Turn on location", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        } else {
                            requestPermissions();
                        }

                    } else {
                        // Toast.makeText(mContext, getString(R.string.no_chat_found), Toast.LENGTH_SHORT).show();
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

    private void setPickUpDropOffRoute() {
        DrawPolyLine();
    }

    private void DrawPolyLine() {
        DrawPollyLine.get(this).setOrigin(pickUpLatLng)
                .setDestination(dropOffLatLng)
                .execute(new DrawPollyLine.onPolyLineResponse() {
            @Override
            public void Success(ArrayList<LatLng> latLngs) {
                // map.clear();
                lineOptions = new PolylineOptions();
                lineOptions.addAll(latLngs);
                lineOptions.width(10);
                lineOptions.color(R.color.black);
                ArrayList<LatLng> latlonList = new ArrayList<>();
                latlonList.add(pickUpLatLng);
                latlonList.add(dropOffLatLng);
                if(driverCarMarker == null) {
                    double lat = Double.parseDouble(driverLat);
                    double lon = Double.parseDouble(driverLon);
                    String driverAddress = ProjectUtil.getCompleteAddressString(mContext, lat, lon);
                    MarkerOptions pickMarker = new MarkerOptions().title("Driver Location")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_top))
                            .position(new LatLng(lat,lon));
                    driverCarMarker = map.addMarker(pickMarker);
                }
                // latlonList.add(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
                zoomRoute(map,latlonList);
                setPickDropMarker();
            }
        });
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

    private void setPickDropMarker() {
        if (map != null) {
            // map.clear();
            if (lineOptions != null)
                map.addPolyline(lineOptions);
            if (pickUpLatLng != null) {
                String pickAdd = ProjectUtil.getCompleteAddressString(mContext,pickUpLatLng.latitude,pickUpLatLng.longitude);
                MarkerOptions pickMarker = new MarkerOptions().title("PickUp Address : " + pickAdd)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker))
                        .position(pickUpLatLng);
                map.addMarker(pickMarker);
                // mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(PickUpLatLng)));
            }
            if (dropOffLatLng != null) {
                String dropAdd = ProjectUtil.getCompleteAddressString(mContext,dropOffLatLng.latitude,dropOffLatLng.longitude);
                MarkerOptions dropMarker = new MarkerOptions().title("Drop Address : " + dropAdd)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_marker))
                        .position(dropOffLatLng);
                map.addMarker(dropMarker);
            }
        }
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // setCurrentLoc();
            }
        }
    }

    //    public void TripStartDialog() {
//        final Dialog dialog = new Dialog(TrackTaxiAct.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_driver_arrived);
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.setCancelable(true);
//        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
//
//        tvTitle.setText(getString(R.string.your_trip_has_begin));
//        binding.titler.setText("Arriving");
//        dialog.setCanceledOnTouchOutside(true);
//        TextView btnOk = dialog.findViewById(R.id.btnOk);
//
//        btnOk.setOnClickListener(v -> {TripFinishDialog();
//            dialog.dismiss();});
//
//        dialog.show();
//    }
//
//    public void TripFinishDialog() {
//
//        final Dialog dialog = new Dialog(TrackTaxiAct.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_driver_arrived);
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.setCancelable(true);
//        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
//
//        tvTitle.setText(getString(R.string.your_trip_is_finished));
//        binding.titler.setText("Arriving");
//        dialog.setCanceledOnTouchOutside(true);
//        TextView btnOk = dialog.findViewById(R.id.btnOk);
//
//        btnOk.setOnClickListener(v -> {
//            dialog.dismiss();
//            //   binding.titler.setText("Send Feedback");
//           // binding.btnBack.setVisibility(View.GONE);
//            binding.rlDriver.setVisibility(View.GONE);
//            //  binding.rlFeedback.setVisibility(View.VISIBLE);
////            Intent i = new Intent(TrackTaxiAct.this,TripEndAct.class);
////            startActivityForResult(i,1);
//        });
//
//        dialog.show();
//
//    }

}