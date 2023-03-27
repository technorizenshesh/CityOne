package com.cityone.shipping.fragment;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.cityone.R;
import com.cityone.databinding.AddShippDialogBinding;
import com.cityone.databinding.FragmentStartBinding;
import com.cityone.models.ModelLogin;
import com.cityone.shipping.adapters.AdapterShipRequest;
import com.cityone.shipping.models.ModelShipRequest;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.AppConstant;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShippingStartFragment extends Fragment {
    FragmentStartBinding binding;
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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_start, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPref = SharedPref.getInstance(getActivity());
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);


        binding.etDropAdd.setOnClickListener(v -> {
            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(getActivity());

            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_DESTINATION);
        });

        binding.etPickAdd.setOnClickListener(v -> {
            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(getActivity());

            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_PICK_UP);
        });

        binding.etPickDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    date.set(year, monthOfYear, dayOfMonth);
                    String dateString = new SimpleDateFormat("dd-MMM-yyyy").format(date.getTime());
                    binding.etPickDate.setText(dateString);
                    pickUpMilliseconds = ProjectUtil.convertDateIntoMillisecond(dateString);
                }
            }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));
            datePickerDialog.getDatePicker().setMinDate(currentDate.getTimeInMillis());
            datePickerDialog.show();
        });

        binding.etDropDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    date.set(year, monthOfYear, dayOfMonth);
                    String dateString = new SimpleDateFormat("dd-MMM-yyyy").format(date.getTime());
                    binding.etDropDate.setText(dateString);
                    pickUpMilliseconds = ProjectUtil.convertDateIntoMillisecond(dateString);
                }
            }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));
            datePickerDialog.getDatePicker().setMinDate(pickUpMilliseconds);
            datePickerDialog.show();
        });

        binding.btUpload.setOnClickListener(v -> {
            if(TextUtils.isEmpty(binding.etPickAdd.getText().toString().trim())) {
                Toast.makeText(getActivity(),getString(R.string.all_fields_are_mandatory), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.etDropAdd.getText().toString().trim())) {
                Toast.makeText(getActivity(),getString(R.string.all_fields_are_mandatory), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.etPickDate.getText().toString().trim())) {
                Toast.makeText(getActivity(),getString(R.string.all_fields_are_mandatory), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.etDropDate.getText().toString().trim())) {
                Toast.makeText(getActivity(),getString(R.string.all_fields_are_mandatory), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.etRecipName.getText().toString().trim())) {
                Toast.makeText(getActivity(),getString(R.string.all_fields_are_mandatory), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.etMobile.getText().toString().trim())) {
                Toast.makeText(getActivity(),getString(R.string.all_fields_are_mandatory), Toast.LENGTH_SHORT).show();
            } else if(binding.spQuantity.getSelectedItemPosition() == 0) {
                Toast.makeText(getActivity(),getString(R.string.please_select_item_quantity), Toast.LENGTH_SHORT).show();
            }
            else if(binding.spVecType.getSelectedItemPosition() == 0) {
                Toast.makeText(getActivity(),getString(R.string.please_select_vehicle), Toast.LENGTH_SHORT).show();
            }

            else if(binding.spItemCat.getSelectedItemPosition() == 0) {
                Toast.makeText(getActivity(),getString(R.string.please_select_item_category), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.etItemDetail.getText().toString().trim())) {
                Toast.makeText(getActivity(),getString(R.string.all_fields_are_mandatory), Toast.LENGTH_SHORT).show();
            }  else if(TextUtils.isEmpty(binding.etDevInstruction.getText().toString().trim())) {
                Toast.makeText(getActivity(),getString(R.string.all_fields_are_mandatory), Toast.LENGTH_SHORT).show();
            } else {
                paramHash = new HashMap<>();
                paramHash.put("user_id",modelLogin.getResult().getId());
                paramHash.put("pickup_location", binding.etPickAdd.getText().toString().trim());
                paramHash.put("pickup_lat",String.valueOf(pickUpLatLon.latitude));
                paramHash.put("pickup_lon",String.valueOf(pickUpLatLon.longitude));
                paramHash.put("drop_location",binding.etDropAdd.getText().toString().trim());
                paramHash.put("drop_lat",String.valueOf(dropOffLatLon.latitude));
                paramHash.put("drop_lon",String.valueOf(pickUpLatLon.longitude));
                paramHash.put("pickup_date",binding.etPickDate.getText().toString().trim());
                paramHash.put("dropoff_date",binding.etDropDate.getText().toString().trim());
                paramHash.put("recipient_name",binding.etRecipName.getText().toString().trim());
                paramHash.put("mobile_no",binding.etMobile.getText().toString().trim());
                paramHash.put("parcel_quantity",binding.spQuantity.getSelectedItem().toString());
                paramHash.put("parcel_category",binding.spItemCat.getSelectedItem().toString());
                paramHash.put("vehicle_id",binding.spVecType.getSelectedItem().toString());
                paramHash.put("item_detail",binding.etItemDetail.getText().toString().trim());
                paramHash.put("dev_instruction",binding.etDevInstruction.getText().toString().trim());
                paramHash.put("direction_json","");

                addShippingRequest(paramHash);

                // getRoute();

            }

        });
    }

    private void addShippingRequest(HashMap<String,String> params) {
        Log.e("responseString","params = " + params.toString());

        ProjectUtil.showProgressDialog(getActivity(),false,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(getActivity()).create(Api.class);
        Call<ResponseBody> call = api.addShippingRequestApiCall(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        pickUpMilliseconds = 0;

                        if(modelShipRequest != null) {
                            ModelShipRequest.Result data = new ModelShipRequest().new Result();
                            data.setId(jsonObject.getJSONObject("result").getString("id"));
                            data.setStatus(jsonObject.getJSONObject("result").getString("status"));
                            data.setPickup_location(jsonObject.getJSONObject("result").getString("pickup_location"));
                            data.setDrop_location(jsonObject.getJSONObject("result").getString("drop_location"));

                            modelShipRequest.getResult().add(data);
                            Toast.makeText(getActivity(), "Shipping request send successfully", Toast.LENGTH_SHORT).show();
                         //   AdapterShipRequest adapterShipRequest = new AdapterShipRequest(mContext,modelShipRequest.getResult());
                         //   binding.rvRequest.setAdapter(adapterShipRequest);

                        } else {
                          //  getAllShipRequest();
                        }
                        Toast.makeText(getActivity(), "Shipping request send successfully", Toast.LENGTH_SHORT).show();
                        Log.e("responseString","responseString = " + responseString);

                    } else {
                         Toast.makeText(getActivity(), "Provider not available", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                //    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    String address = ProjectUtil.getCompleteAddressString(getActivity(),
                            place.getLatLng().latitude,
                            place.getLatLng().longitude);
                    binding.etPickAdd.setText(address);
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
                    String address = ProjectUtil.getCompleteAddressString(getActivity(),
                            place.getLatLng().latitude,
                            place.getLatLng().longitude);
                    binding.etDropAdd.setText(address);
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
    
}
