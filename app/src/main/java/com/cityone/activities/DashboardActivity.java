package com.cityone.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import android.Manifest;
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
import android.widget.Toast;
import com.cityone.R;
import com.cityone.databinding.ActivityDashboardBinding;
import com.cityone.entertainment.activities.EntHomeActivity;
import com.cityone.meals.MealsHomeActivity;
import com.cityone.models.ModelLogin;
import com.cityone.shipping.ShippingActivity;
import com.cityone.stores.activities.StoresActivity;
import com.cityone.taxi.activities.TaxiHomeActivity;
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

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class DashboardActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_dashboard);
        ProjectUtil.changeStatusBarColor(DashboardActivity.this);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        Log.e("fsdfdsfds","modelLogin = " + modelLogin.getResult().getId());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);

        init();

    }

    private void init() {

        if(checkPermissions()) {
            if(isLocationEnabled()) {
                setCurrentLocation();
            }
        } else {
            requestPermissions();
        }

        binding.cvTaxi.setOnClickListener(v -> {
            startActivity(new Intent(mContext, TaxiHomeActivity.class));
        });

        binding.tvName.setText(modelLogin.getResult().getUser_name());
        binding.navItems.tvName.setText(modelLogin.getResult().getUser_name());

        binding.navItems.tvMybooking.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext,MyBookingActivity.class));
        });

        binding.navItems.tvMoveiBooks.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            // startActivity(new Intent(mContext,MyBookingActivity.class));
        });

        binding.navItems.tvAccount.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext,AccountActivity.class));
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

        binding.cvMeals.setOnClickListener(v -> {
            startActivity(new Intent(mContext, MealsHomeActivity.class));
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

    @Override
    public void onResume() {
        super.onResume();

        if(checkPermissions()) {
            if(isLocationEnabled()) {
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
                || locationManager.isProviderEnabled (
                LocationManager.NETWORK_PROVIDER
        );
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions (
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
                if(isLocationEnabled()) {
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
        if (ActivityCompat.checkSelfPermission (
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

        SettingsClient settingsClient = LocationServices.getSettingsClient(DashboardActivity.this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        getFusedLocationProviderClient(DashboardActivity.this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
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


}