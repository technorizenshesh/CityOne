package com.cityone.utils;

import android.app.Application;

import com.cityone.R;
import com.google.android.libraries.places.api.Places;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(),getString(R.string.places_api_key));
        }
    }

}
