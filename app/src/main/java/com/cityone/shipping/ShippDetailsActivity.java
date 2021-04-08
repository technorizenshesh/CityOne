package com.cityone.shipping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;

import com.cityone.R;
import com.cityone.databinding.ActivityShippDetailsBinding;
import com.cityone.databinding.AddShippDialogBinding;
import com.cityone.databinding.BidDetailDialogBinding;
import com.cityone.shipping.adapters.AdapterShipBids;
import com.cityone.shipping.adapters.AdapterShipRequest;
import com.cityone.shipping.models.ModelShipBid;
import com.cityone.shipping.models.ModelShipDetail;
import com.cityone.shipping.models.ModelShipRequest;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.DataParser;
import com.cityone.utils.ProjectUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShippDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    Context mContext = ShippDetailsActivity.this;
    ActivityShippDetailsBinding binding;
    String parcelId = "";
    GoogleMap gMap;
    List<List<HashMap<String,String>>> result;
    SupportMapFragment mapFragment;
    MarkerOptions originOption,dropOffOption;
    LatLng originLatLng,dropLatLng;
    AdapterShipBids adapterShipBids;
    ModelShipBid modelShipBid;
    String shippmentUserId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_shipp_details);
        parcelId = getIntent().getStringExtra("parcelid");

        init();

        shipDetailApi();
        getAllBidsNotLoader();
    }

    private void init() {

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(ShippDetailsActivity.this);

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(getIntent() != null){
                parcelId = getIntent().getStringExtra("parcelid");
                getAllBidsNotLoader();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver,new IntentFilter("shipdetail"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private void getAllBidsNotLoader() {
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> param = new HashMap<>();
        param.put("shipping_id",parcelId);

        Call<ResponseBody> call = api.getBidApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringRes = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringRes);
                    if(jsonObject.getString("status").equals("1")) {
                        modelShipBid = new Gson().fromJson(stringRes, ModelShipBid.class);
                        adapterShipBids = new AdapterShipBids(mContext,modelShipBid.getResult(),shippmentUserId,ShippDetailsActivity.this::updateBid);
                        binding.rvBids.setHasFixedSize(true);
                        binding.rvBids.setAdapter(adapterShipBids);
                    } else {

                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }
        });
    }

    public void updateBid(String status) {
        getAllBidsNotLoader();
        if("Accept".equals(status)) {
            binding.tvStatus.setText(status);
        }
    }

    private void shipDetailApi() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        HashMap<String,String> params = new HashMap<>();
        params.put("shipping_id",parcelId);

        Call<ResponseBody> call = api.getShipDetailApiCall(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        ModelShipDetail modelShipDetail = new Gson().fromJson(responseString,ModelShipDetail.class);
                        binding.setShip(modelShipDetail.getResult());
                        shippmentUserId = modelShipDetail.getResult().getUser_id();

                        originLatLng = new LatLng(Double.parseDouble(modelShipDetail.getResult().getPickup_lat())
                        ,Double.parseDouble(modelShipDetail.getResult().getPickup_lon()));

                        dropLatLng = new LatLng(Double.parseDouble(modelShipDetail.getResult().getDrop_lat())
                        ,Double.parseDouble(modelShipDetail.getResult().getDrop_lon()));

                        originOption = new MarkerOptions().position(originLatLng).title(modelShipDetail.getResult().getPickup_location());
                        dropOffOption = new MarkerOptions().position(dropLatLng).title(modelShipDetail.getResult().getDrop_location());

                        drawRoute();

                        Log.e("responseString","responseString = " + responseString);

                    } else {
                        // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    private void drawRoute() {
        gMap.addMarker(originOption);
        gMap.addMarker(dropOffOption);

        ArrayList<LatLng> listLatLon = new ArrayList<>();
        listLatLon.add(originOption.getPosition());
        listLatLon.add(dropOffOption.getPosition());

        zoomRoute(gMap,listLatLon);
        drawDirection();
    }

    private void drawDirection() {
        String url = getDirectionsUrl(originLatLng,dropLatLng);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    public void zoomRoute(GoogleMap googleMap, List<LatLng> lstLatLngRoute) {
        if (googleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 100;
        LatLngBounds latLngBounds = boundsBuilder.build();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        //setting transportation mode
        String mode = "mode=driving";

        String sensor = "mode=driving";

        // Building the parameters to the web service
        // String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        String parameters = str_origin + "&" + str_dest + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.places_api_key);

        return url;

    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
                // Log.e("aslfkghfjasasfd","data = " + data);
                JSONObject jsonObject = new JSONObject(data);

                JSONArray array = jsonObject.getJSONArray("routes");

                JSONObject routes = array.getJSONObject(0);

                JSONArray legs = routes.getJSONArray("legs");

                JSONObject steps = legs.getJSONObject(0);

                JSONObject distance = steps.getJSONObject("distance");

                /* Log.e("Distance", distance.toString());
                   Log.e("Distance",distance.getString("text")); */

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }

            return data;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("aslfkghfjasasfd","result = " + result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }

    }

    /**
     * A class to parse the JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DataParser parser = new DataParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ProjectUtil.pauseProgressDialog();
            ArrayList points = new ArrayList();
            PolylineOptions lineOptions = new PolylineOptions();

            for (int i = 0; i < result.size(); i++) {

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);

                }

                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.BLUE);
                lineOptions.geodesic(true);

            }

            if(points.size() != 0){
                gMap.addPolyline(lineOptions);
            }

        }

    }

}