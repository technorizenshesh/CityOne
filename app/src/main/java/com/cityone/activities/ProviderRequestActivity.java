package com.cityone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.cityone.R;
import com.cityone.databinding.ActivityProviderRequestBinding;
import com.cityone.utils.AppConstant;
import com.cityone.utils.Compress;
import com.cityone.utils.ProjectUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ProviderRequestActivity extends AppCompatActivity {

    private static final int PERMISSION_ID = 101;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 102;
    Context mContext = ProviderRequestActivity.this;
    ActivityProviderRequestBinding binding;
    private LatLng latLng;
    private String registerId;
    File fileImage,doc1Img,doc2Img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_provider_request);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {

                        if (!task.isSuccessful()) {
                            return;
                        }

                        String token = task.getResult().getToken();
                        registerId = token;

                    }

                });

        if (!Places.isInitialized()) {
            Places.initialize(mContext,getString(R.string.places_api_key));
        }

        init();


    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions (
                this,
                new String[] {
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE },
                PERMISSION_ID
        );
    }

    private void init() {

        binding.ivDoc1.setOnClickListener(v -> {
            if(checkPermissions()) {
                final PickImageDialog dialog = PickImageDialog.build(new PickSetup());
                dialog.setOnPickCancel(new IPickCancel() {
                    @Override
                    public void onCancelClick() {
                        dialog.dismiss();
                    }
                }).setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        if (r.getError() == null) {

                            String path = r.getPath();
                            doc1Img = new File(path);

                            Compress.get(mContext).setQuality(40)
                                    .execute(new Compress.onSuccessListener() {
                                        @Override
                                        public void response(boolean status, String message, File file) {
                                            Log.e("kjsgdfjklgdkjasf","file = " + file.length()/1024 + "kb      "+ message );
                                            doc1Img = file;
                                            binding.ivDoc1.setImageURI(r.getUri());
                                        }
                                    }).CompressedImage(path);

                        } else {
                            // Handle possible errors
                            // TODO: do what you have to do with r.getError();
                            Toast.makeText(mContext, r.getError().getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }

                }).show(ProviderRequestActivity.this);
            } else {
                requestPermissions();
            }
        });

        binding.ivDoc2.setOnClickListener(v -> {
            if(checkPermissions()) {
                final PickImageDialog dialog = PickImageDialog.build(new PickSetup());
                dialog.setOnPickCancel(new IPickCancel() {
                    @Override
                    public void onCancelClick() {
                        dialog.dismiss();
                    }
                }).setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        if (r.getError() == null) {

                            String path = r.getPath();
                            doc2Img = new File(path);

                            Compress.get(mContext).setQuality(40)
                                    .execute(new Compress.onSuccessListener() {
                                        @Override
                                        public void response(boolean status, String message, File file) {
                                            Log.e("kjsgdfjklgdkjasf","file = " + file.length()/1024 + "kb      "+ message );
                                            doc2Img = file;
                                            binding.ivDoc2.setImageURI(r.getUri());
                                        }
                                    }).CompressedImage(path);

                        } else {
                            // Handle possible errors
                            // TODO: do what you have to do with r.getError();
                            Toast.makeText(mContext, r.getError().getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }

                }).show(ProviderRequestActivity.this);
            } else {
                requestPermissions();
            }
        });

        binding.etAddress.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });

        binding.btSubmit.setOnClickListener(v -> {
            if(TextUtils.isEmpty(binding.etUsername.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_username), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_email_add), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.etPhone.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_phone_add), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.etAddress.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_select_add), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.etLandmark.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enterlandmark_add), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.pass.getText().toString().trim())){
                Toast.makeText(mContext, getString(R.string.please_enter_pass), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.confirmPass.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_conf_pass), Toast.LENGTH_SHORT).show();
            } else if(!(binding.pass.getText().toString().trim().length() > 4 )) {
                Toast.makeText(mContext, getString(R.string.password_validation_text), Toast.LENGTH_SHORT).show();
            } else if(!(binding.pass.getText().toString().trim().equals(binding.confirmPass.getText().toString().trim()))){
                Toast.makeText(mContext, getString(R.string.password_not_match), Toast.LENGTH_SHORT).show();
            } else if(!ProjectUtil.isValidEmail(binding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            } else if(!validateUsing_libphonenumber(binding.etPhone.getText().toString().replace(" ","")
                    ,binding.ccp.getSelectedCountryCode())) {
                Toast.makeText(mContext, getString(R.string.invalid_number), Toast.LENGTH_SHORT).show();
            } else if(doc1Img == null || doc2Img == null) {
                Toast.makeText(mContext, getString(R.string.please_upload_both_doc), Toast.LENGTH_SHORT).show();
            } else {
                HashMap<String,String> params = new HashMap<>();

                params.put("user_name",binding.etUsername.getText().toString().trim());
                params.put("email",binding.etEmail.getText().toString().trim());
                params.put("mobile",binding.etPhone.getText().toString().trim());
                params.put("address",binding.etAddress.getText().toString().trim());
                params.put("land_mark",binding.etLandmark.getText().toString().trim());
                params.put("lat", String.valueOf(latLng.latitude));
                params.put("lon", String.valueOf(latLng.longitude));
                params.put("register_id",registerId);
                params.put("password",binding.pass.getText().toString().trim());
                params.put("type", AppConstant.PROVIDER);

                HashMap<String, File> fileHashMap = new HashMap<>();
                fileHashMap.put("document1",doc1Img);
                fileHashMap.put("document2",doc2Img);

                Log.e("fsfdsfdsfs","image = " + fileImage);
                Log.e("fsfdsfdsfs","document1 = " + doc1Img);
                Log.e("fsfdsfdsfs","document2 = " + doc2Img);

                // signUpApi(params,fileHashMap);

                String mobileWithCounCode = (binding.ccp.getSelectedCountryCodeWithPlus()
                        + binding.etPhone.getText().toString().trim()).replace(" ","");

                startActivity(new Intent(mContext,VerifyOtpActivity.class)
                        .putExtra("resgisterHashmap" , params)
                        .putExtra("fileHashmap" , fileHashMap)
                        .putExtra("mobile" , mobileWithCounCode)
                        .putExtra("request" , "request")
                );

                finish();

            }
        });

    }

    private boolean validateUsing_libphonenumber(String phNumber,String code) {

        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(code));
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            //phoneNumber = phoneNumberUtil.parse(phNumber, "IN");  //if you want to pass region code
            phoneNumber = phoneNumberUtil.parse(phNumber, isoCode);
        } catch (Exception e) {
            System.err.println(e);
        }

        boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
        if (isValid) {
            String internationalFormat = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            // Toast.makeText(this, "Phone Number is Valid " + internationalFormat, Toast.LENGTH_LONG).show();
            return true;
        } else {
            // Toast.makeText(this, "Phone Number is Invalid " + phoneNumber, Toast.LENGTH_LONG).show();
            return false;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                latLng = place.getLatLng();
                try {
                    String addresses = ProjectUtil.getCompleteAddressString(mContext,place.getLatLng().latitude, place.getLatLng().longitude);
                    binding.etAddress.setText(addresses);
                } catch (Exception e) {}
            }

        }

    }

}