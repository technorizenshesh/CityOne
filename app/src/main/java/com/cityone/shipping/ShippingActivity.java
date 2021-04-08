package com.cityone.shipping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.Toast;
import com.cityone.R;
import com.cityone.activities.DashboardActivity;
import com.cityone.databinding.ActivityShippingBinding;
import com.cityone.databinding.AddShippDialogBinding;
import com.cityone.models.ModelLogin;
import com.cityone.shipping.adapters.AdapterShipRequest;
import com.cityone.shipping.models.ModelShipRequest;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.AppConstant;
import com.cityone.utils.DataParser;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShippingActivity extends AppCompatActivity {

    Context mContext = ShippingActivity.this;
    ActivityShippingBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    LatLng pickUpLatLon,dropOffLatLon;
    int AUTOCOMPLETE_REQUEST_CODE_PICK_UP = 1,
            AUTOCOMPLETE_REQUEST_CODE_DESTINATION = 2;
    long pickUpMilliseconds;
    AddShippDialogBinding dialogBinding;
    Calendar date = Calendar.getInstance();
    Calendar currentDate = Calendar.getInstance();
    ModelShipRequest modelShipRequest = null;
    GoogleMap gMap;
    HashMap<String,String> paramHash;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_shipping);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        if (!Places.isInitialized()) {
            Places.initialize(mContext, getString(R.string.places_api_key));
        }

        init();

    }

    private void init() {

        binding.navItems.tvName.setText(modelLogin.getResult().getUser_name());

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllShipRequest();
            }
        });

        getAllShipRequest();

        binding.navItems.tvMySending.setOnClickListener(v -> {
            startActivity(new Intent(mContext,ShippStatusActivity.class));
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.navItems.tvMessages.setOnClickListener(v -> {
            startActivity(new Intent(mContext,ShipChatListActivity.class));
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.ivMenu.setOnClickListener(v -> {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        });

        binding.ivAddAdress.setOnClickListener(v -> {
            openAddParcelDialog();
        });

    }

    private void openAddParcelDialog() {
        dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext),R.layout.add_shipp_dialog,null,false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.etDropAdd.setOnClickListener(v -> {
            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(mContext);

            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_DESTINATION);
        });

        dialogBinding.etPickAdd.setOnClickListener(v -> {
            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(mContext);

            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_PICK_UP);
        });

        dialogBinding.etPickDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    date.set(year, monthOfYear, dayOfMonth);
                    String dateString = new SimpleDateFormat("dd-MMM-yyyy").format(date.getTime());
                    dialogBinding.etPickDate.setText(dateString);
                    pickUpMilliseconds = ProjectUtil.convertDateIntoMillisecond(dateString);
                }
            }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));
            datePickerDialog.getDatePicker().setMinDate(currentDate.getTimeInMillis());
            datePickerDialog.show();
        });

        dialogBinding.etDropDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    date.set(year, monthOfYear, dayOfMonth);
                    String dateString = new SimpleDateFormat("dd-MMM-yyyy").format(date.getTime());
                    dialogBinding.etDropDate.setText(dateString);
                    pickUpMilliseconds = ProjectUtil.convertDateIntoMillisecond(dateString);
                }
            }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));
            datePickerDialog.getDatePicker().setMinDate(pickUpMilliseconds);
            datePickerDialog.show();
        });

        dialogBinding.btUpload.setOnClickListener(v -> {
            if(TextUtils.isEmpty(dialogBinding.etPickAdd.getText().toString().trim())) {
                Toast.makeText(mContext,getString(R.string.all_fields_are_mandatory), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(dialogBinding.etDropAdd.getText().toString().trim())) {
                Toast.makeText(mContext,getString(R.string.all_fields_are_mandatory), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(dialogBinding.etPickDate.getText().toString().trim())) {
                Toast.makeText(mContext,getString(R.string.all_fields_are_mandatory), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(dialogBinding.etDropDate.getText().toString().trim())) {
                Toast.makeText(mContext,getString(R.string.all_fields_are_mandatory), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(dialogBinding.etRecipName.getText().toString().trim())) {
                Toast.makeText(mContext,getString(R.string.all_fields_are_mandatory), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(dialogBinding.etMobile.getText().toString().trim())) {
                Toast.makeText(mContext,getString(R.string.all_fields_are_mandatory), Toast.LENGTH_SHORT).show();
            } else if(dialogBinding.spQuantity.getSelectedItemPosition() == 0) {
                Toast.makeText(mContext,getString(R.string.please_select_item_quantity), Toast.LENGTH_SHORT).show();
            } else if(dialogBinding.spItemCat.getSelectedItemPosition() == 0) {
                Toast.makeText(mContext,getString(R.string.please_select_item_category), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(dialogBinding.etItemDetail.getText().toString().trim())) {
                Toast.makeText(mContext,getString(R.string.all_fields_are_mandatory), Toast.LENGTH_SHORT).show();
            }  else if(TextUtils.isEmpty(dialogBinding.etDevInstruction.getText().toString().trim())) {
                Toast.makeText(mContext,getString(R.string.all_fields_are_mandatory), Toast.LENGTH_SHORT).show();
            } else {
                paramHash = new HashMap<>();
                paramHash.put("user_id",modelLogin.getResult().getId());
                paramHash.put("pickup_location", dialogBinding.etPickAdd.getText().toString().trim());
                paramHash.put("pickup_lat",String.valueOf(pickUpLatLon.latitude));
                paramHash.put("pickup_lon",String.valueOf(pickUpLatLon.longitude));
                paramHash.put("drop_location",dialogBinding.etDropAdd.getText().toString().trim());
                paramHash.put("drop_lat",String.valueOf(dropOffLatLon.latitude));
                paramHash.put("drop_lon",String.valueOf(pickUpLatLon.longitude));
                paramHash.put("pickup_date",dialogBinding.etPickDate.getText().toString().trim());
                paramHash.put("dropoff_date",dialogBinding.etDropDate.getText().toString().trim());
                paramHash.put("recipient_name",dialogBinding.etRecipName.getText().toString().trim());
                paramHash.put("mobile_no",dialogBinding.etMobile.getText().toString().trim());
                paramHash.put("parcel_quantity",dialogBinding.spQuantity.getSelectedItem().toString());
                paramHash.put("parcel_category",dialogBinding.spItemCat.getSelectedItem().toString());
                paramHash.put("item_detail",dialogBinding.etItemDetail.getText().toString().trim());
                paramHash.put("dev_instruction",dialogBinding.etDevInstruction.getText().toString().trim());
                paramHash.put("direction_json","");

                addShippingRequest(paramHash);

                // getRoute();

            }

        });

        dialog.show();

    }

    private void getAllShipRequest() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> params = new HashMap<>();
        params.put("user_id",modelLogin.getResult().getId());

        Call<ResponseBody> call = api.getAllShippingRequestApiCall(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        modelShipRequest = new Gson().fromJson(responseString,ModelShipRequest.class);

                        AdapterShipRequest adapterShipRequest = new AdapterShipRequest(mContext,modelShipRequest.getResult());
                        binding.rvRequest.setAdapter(adapterShipRequest);

                        Log.e("responseStringdfsdfsd","responseString = " + responseString);

                    } else {
                        Toast.makeText(mContext, getString(R.string.no_request_found), Toast.LENGTH_SHORT).show();
                        AdapterShipRequest adapterShipRequest = new AdapterShipRequest(mContext,null);
                        binding.rvRequest.setAdapter(adapterShipRequest);
                        // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
            }

        });
    }

    private void addShippingRequest(HashMap<String,String> params) {
        Log.e("responseString","params = " + params.toString());

        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.addShippingRequestApiCall(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        dialog.dismiss();
                        pickUpMilliseconds = 0;

                        if(modelShipRequest != null) {
                            ModelShipRequest.Result data = new ModelShipRequest().new Result();
                            data.setId(jsonObject.getJSONObject("result").getString("id"));
                            data.setStatus(jsonObject.getJSONObject("result").getString("status"));
                            data.setPickup_location(jsonObject.getJSONObject("result").getString("pickup_location"));
                            data.setDrop_location(jsonObject.getJSONObject("result").getString("drop_location"));

                            modelShipRequest.getResult().add(data);

                            AdapterShipRequest adapterShipRequest = new AdapterShipRequest(mContext,modelShipRequest.getResult());
                            binding.rvRequest.setAdapter(adapterShipRequest);

                        } else {
                            getAllShipRequest();
                        }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE_PICK_UP) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                pickUpLatLon = place.getLatLng();
                try {
                    String address = ProjectUtil.getCompleteAddressString(mContext,
                            place.getLatLng().latitude,
                            place.getLatLng().longitude);
                    dialogBinding.etPickAdd.setText(address);
                } catch (Exception e) {
                    e.printStackTrace();
                    //setMarker(latLng);
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else {
            if (resultCode == RESULT_OK) {

                Place place = Autocomplete.getPlaceFromIntent(data);
                dropOffLatLon = place.getLatLng();

                try {
                    String address = ProjectUtil.getCompleteAddressString(mContext,
                            place.getLatLng().latitude,
                            place.getLatLng().longitude);
                    dialogBinding.etDropAdd.setText(address);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }

        }

    }

    private void getRoute() {

        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(pickUpLatLon,dropOffLatLon);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);

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

        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(mContext, DashboardActivity.class));
    }

}