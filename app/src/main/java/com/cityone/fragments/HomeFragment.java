package com.cityone.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cityone.R;
import com.cityone.activities.DashboardActivity;
import com.cityone.databinding.FragmentHomeBinding;
import com.cityone.entertainment.activities.EntHomeActivity;
import com.cityone.models.ModelLogin;
import com.cityone.shipping.ShippingActivity;
import com.cityone.stores.activities.StoresActivity;
import com.cityone.utils.AppConstant;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class HomeFragment extends Fragment {

    Context mContext;
    FragmentHomeBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 3000;  /* 5 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    final int REQUEST_LOCATION_PERMISSION_CODE = 101;
    Location currentLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home, container, false);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);

        init();

        // Inflate the layout for this fragment
        return binding.getRoot();

    }

    @Override
    public void onResume() {
        super.onResume();
        if(checkPermissions()) {
            if(isLocationEnabled()) {
                setCurrentLocation();
            } else {
                Toast.makeText(getActivity(), getString(R.string.turn_on_loc), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private void init() {

        if(checkPermissions()) {
            if(isLocationEnabled()) {
                setCurrentLocation();
            }
        } else {
           requestPermissions();
        }

        binding.tvName.setText(modelLogin.getResult().getUser_name());
        Picasso.get().load(modelLogin.getResult().getImage())
                .placeholder(R.drawable.default_profile_icon)
                .error(R.drawable.default_profile_icon)
                .into(binding.cvProfile);

        binding.cvProfile.setOnClickListener(v -> {
            // ((DashboardActivity)getActivity()).loadFragment(new AccountFragment());
        });

        binding.cvStores.setOnClickListener(v -> {
            startActivity(new Intent(mContext, StoresActivity.class));
        });

        binding.cvEnt.setOnClickListener(v -> {
            startActivity(new Intent(mContext, EntHomeActivity.class));
        });

        binding.cvShipping.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ShippingActivity.class));
        });

        binding.btFood.setOnClickListener(v -> {
            binding.btGrocery.setBackgroundResource(R.drawable.gray_back_10);
            binding.btPharmacy.setBackgroundResource(R.drawable.gray_back_10);
            binding.btFood.setBackgroundResource(R.drawable.orange_back_10);
        });

        binding.btGrocery.setOnClickListener(v -> {
            binding.btFood.setBackgroundResource(R.drawable.gray_back_10);
            binding.btPharmacy.setBackgroundResource(R.drawable.gray_back_10);
            binding.btGrocery.setBackgroundResource(R.drawable.orange_back_10);
        });

        binding.btPharmacy.setOnClickListener(v -> {
            binding.btGrocery.setBackgroundResource(R.drawable.gray_back_10);
            binding.btPharmacy.setBackgroundResource(R.drawable.orange_back_10);
            binding.btFood.setBackgroundResource(R.drawable.gray_back_10);
        });

    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled (
                LocationManager.NETWORK_PROVIDER
        );
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions (
                getActivity(),
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
                if(isLocationEnabled()) {
                    setCurrentLocation();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.turn_on_loc), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            }
        }
    }

    private void setCurrentLocation() {
        if (ActivityCompat.checkSelfPermission (
                mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
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
                        String address = ProjectUtil.getCompleteAddressString(mContext,currentLocation.getLatitude(), currentLocation.getLongitude());

                        if(TextUtils.isEmpty(binding.tvAddress.getText().toString().trim())){
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

        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        getFusedLocationProviderClient(getActivity()).requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                if(currentLocation != null) {
                    try {
                        String address = ProjectUtil.getCompleteAddressString(mContext,currentLocation.getLatitude(), currentLocation.getLongitude());

                        if(TextUtils.isEmpty(binding.tvAddress.getText().toString().trim())){
                            binding.tvAddress.setText(address);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if(locationResult != null) {
                        Log.e("hdasfkjhksdf", "Location = " + locationResult.getLastLocation());
                        currentLocation = locationResult.getLastLocation();
                        try {
                            String address = ProjectUtil.getCompleteAddressString(mContext,currentLocation.getLatitude(), currentLocation.getLongitude());

                            if(TextUtils.isEmpty(binding.tvAddress.getText().toString().trim())){
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

}