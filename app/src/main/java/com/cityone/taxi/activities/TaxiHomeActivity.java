package com.cityone.taxi.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.cityone.R;
import com.cityone.databinding.ActivityTaxiHomeBinding;
import com.cityone.models.ModelLogin;
import com.cityone.utils.AppConstant;
import com.cityone.utils.DrawPollyLine;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static com.google.android.gms.maps.model.JointType.ROUND;

public class TaxiHomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE = 101;
    private static final int AUTOCOMPLETE_PICK_REQUEST_CODE = 102;
    private static final int AUTOCOMPLETE_DROP_REQUEST_CODE = 103;
    Context mContext = TaxiHomeActivity.this;
    ArrayList<LatLng> polyLineLatlng = null;
    ActivityTaxiHomeBinding binding;
    GoogleMap mMap;
    ModelLogin modelLogin;
    SharedPref sharedPref;
    LatLng pickUpLatLng, dropOffLatLng;
    FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    PolylineOptions polylineOptions;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 3000;
    private long FASTEST_INTERVAL = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_taxi_home);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        ProjectUtil.changeStatusBarColor(TaxiHomeActivity.this);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(),getString(R.string.places_api_key));
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);

        fetchLocation();
        
        init();
        
    }

    private void init() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(TaxiHomeActivity.this);

        binding.tvPickUp.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this);
            startActivityForResult(intent, AUTOCOMPLETE_PICK_REQUEST_CODE);
        });

        binding.tvDropOff.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this);
            startActivityForResult(intent, AUTOCOMPLETE_DROP_REQUEST_CODE);
        });

        binding.btNext.setOnClickListener(v -> {
            if(TextUtils.isEmpty(binding.tvPickUp.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_select_pickup_address), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.tvDropOff.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_select_dropoff_address), Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(mContext,TaxiRideOtpActivity.class)
                        .putExtra("polylines",polyLineLatlng)
                        .putExtra("picklatlon",pickUpLatLng)
                        .putExtra("droplatlon",dropOffLatLng)
                );
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchLocation();
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TaxiHomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // isCurrentLocation = true;
                    currentLocation = location;
                    Log.e("ivCurrentLocation", "location = " + location);

                    String address = ProjectUtil
                            .getCompleteAddressString(mContext, currentLocation.getLatitude()
                                    , currentLocation.getLongitude());

                    if (TextUtils.isEmpty(binding.tvPickUp.getText().toString().trim())) {
                        binding.tvPickUp.setText(address);
                        pickUpLatLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                        setCurrentLocationMap(currentLocation);
                    }

                } else {
                    startLocationUpdates();
                    Log.e("ivCurrentLocation", "location = " + location);
                }
            }
        });

    }

    private void setCurrentLocationMap(Location currentLocation) {
        if (mMap != null) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker))
                    .title(ProjectUtil.getCompleteAddressString(mContext, currentLocation.getLatitude(), currentLocation.getLongitude()));
            mMap.addMarker(markerOptions);
            LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
        }
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        Log.e("hdasfkjhksdf", "Location = ");

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null) {
                            Log.e("hdasfkjhksdf", "Location = " + locationResult.getLastLocation());
                            currentLocation = locationResult.getLastLocation();
                            String address = ProjectUtil.getCompleteAddressString(mContext, currentLocation.getLatitude(), currentLocation.getLongitude());

                            if (TextUtils.isEmpty(binding.tvPickUp.getText().toString().trim())) {
                                binding.tvPickUp.setText(address);
                                setCurrentLocationMap(currentLocation);
                            }
                        } else {
                            fetchLocation();
                        }

                    }
                },
                Looper.myLooper());

    }

    private void drawRoute(LatLng pickUpLatLng, LatLng dropOffLatLng) {
        DrawPollyLine.get(this).setOrigin(pickUpLatLng)
                .setDestination(dropOffLatLng).execute(new DrawPollyLine.onPolyLineResponse() {
            @Override
            public void Success(ArrayList<LatLng> latLngs) {
                mMap.clear();
                polyLineLatlng = latLngs;
                addPickDropMarkerOnMap(pickUpLatLng, dropOffLatLng);
                addPolyLinesOnMap(latLngs);
            }
        });
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AUTOCOMPLETE_PICK_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                pickUpLatLng = place.getLatLng();
                String address = ProjectUtil.getCompleteAddressString(mContext,pickUpLatLng.latitude,pickUpLatLng.longitude);
                binding.tvPickUp.setText(address);
            }
        } else if(requestCode == AUTOCOMPLETE_DROP_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                dropOffLatLng = place.getLatLng();
                String address = ProjectUtil.getCompleteAddressString(mContext,dropOffLatLng.latitude,dropOffLatLng.longitude);
                binding.tvDropOff.setText(address);

                if(TextUtils.isEmpty(binding.tvPickUp.getText().toString().trim())) {
                    Toast.makeText(mContext, getString(R.string.select_pickup_address), Toast.LENGTH_SHORT).show();
                } else {
                    drawRoute(pickUpLatLng,dropOffLatLng);
                }

            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

}