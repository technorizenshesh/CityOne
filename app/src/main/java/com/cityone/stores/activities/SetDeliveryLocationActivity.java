package com.cityone.stores.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;
import com.cityone.R;
import com.cityone.activities.DashboardActivity;
import com.cityone.activities.PaymentMethodActivity;
import com.cityone.activities.PaypalWebviewAct;
import com.cityone.databinding.ActivitySetDeliveryLocationBinding;
import com.cityone.databinding.AddCardDialogBinding;
import com.cityone.databinding.CardListDialogBinding;
import com.cityone.models.ModelLogin;
import com.cityone.stores.adapters.AdapterCards;
import com.cityone.stores.models.ModelCards;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.AppConstant;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetDeliveryLocationActivity extends AppCompatActivity {

    private static final int AUTOCOMPLETE_REQUEST_CODE = 101;
    Context mContext = SetDeliveryLocationActivity.this;
    ActivitySetDeliveryLocationBinding binding;
    private LatLng latLng;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    AlertDialog.Builder alertBuilder;
    HashMap<String,String> bookingParams = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_set_delivery_location);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        bookingParams = (HashMap<String,String>) getIntent().getSerializableExtra(AppConstant.STORE_BOOKING_PARAMS);

        if (!Places.isInitialized()) {
            Places.initialize(mContext,getString(R.string.places_api_key));
        }

        init();

    }

    private void init() {

        binding.btNext.setOnClickListener(v -> {
            if(TextUtils.isEmpty(binding.etAddress.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_select_add), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.etLandMark.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enterlandmark_add), Toast.LENGTH_SHORT).show();
            } else {

//              bookingAPicall(binding.etAddress.getText().toString().trim()
//                        +" "+binding.etLandMark.getText().toString().trim());

                bookingParams.put("address",binding.etAddress.getText().toString().trim()
                        +" "+binding.etLandMark.getText().toString().trim());
                bookingParams.put("lat", String.valueOf(latLng.latitude));
                bookingParams.put("lon", String.valueOf(latLng.longitude));

//                startActivity(new Intent(mContext,PaypalWebviewAct.class)
//                    .putExtra("param",bookingParams)
//                );

                startActivity(new Intent(mContext,StorePaymentActivity.class)
                    .putExtra("param",bookingParams)
                );

            }
        });

        binding.etAddress.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }

    private void getAvailableCardApi() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));

        HashMap<String,String> param = new HashMap<>();
        param.put("user_id",modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getCardApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringRes = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringRes);
                    if(jsonObject.getString("status").equals("1")) {
                        ModelCards modelCards = new Gson().fromJson(stringRes,ModelCards.class);
                        getCardListDialog(modelCards);
                    } else {
                        Toast.makeText(SetDeliveryLocationActivity.this,getString(R.string.please_add_card), Toast.LENGTH_SHORT).show();
                        getCardListDialog(null);
                    }
                } catch (Exception e) {}
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }
        });

    }

    private void getCardListDialog(ModelCards modelCards) {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);

        CardListDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext),R.layout.card_list_dialog,null,false);
        dialog.setContentView(dialogBinding.getRoot());

//      AdapterCards adapterCards = new AdapterCards(mContext,modelCards.getResult(),"");
//      dialogBinding.rvCards.setAdapter(adapterCards);

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.ivAddCard.setOnClickListener(v -> {
            // openCarddetailDialog();
        });

        dialog.show();

    }

    //    private void openCarddetailDialog() {
//
//        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
//
//        AddCardDialogBinding dialogBinding = DataBindingUtil
//                .inflate(LayoutInflater.from(mContext),R.layout.add_card_dialog,null,false);
//        dialog.setContentView(dialogBinding.getRoot());
//
//        dialogBinding.cardForm.cardRequired(true)
//                .expirationRequired(true)
//                .cvvRequired(true)
//                .postalCodeRequired(false)
//                .mobileNumberRequired(false)
//                .setup(SetDeliveryLocationActivity.this);
//
//        dialogBinding.cardForm.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
//
//        dialogBinding.btMakePayment.setOnClickListener(v -> {
//            if (dialogBinding.cardForm.isValid()) {
//                alertBuilder = new AlertDialog.Builder(mContext);
//                alertBuilder.setTitle("Confirm before purchase");
//                alertBuilder.setMessage("Card number: " + dialogBinding.cardForm.getCardNumber() + "\n" +
//                                "Card expiry date: " + dialogBinding.cardForm.getExpirationDateEditText().getText().toString() + "\n" +
//                                "Card CVV: " + dialogBinding.cardForm.getCvv() + "\n");
//                alertBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//
//                        Card.Builder card = new Card.Builder(dialogBinding.cardForm.getCardNumber(),
//                                Integer.valueOf(dialogBinding.cardForm.getExpirationMonth()),
//                                Integer.valueOf(dialogBinding.cardForm.getExpirationYear()),
//                                dialogBinding.cardForm.getCvv());
//
//                        if (!card.build().validateCard()) {
//                            Toast.makeText(mContext, "Card Not Valid", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        // Stripe stripe = new Stripe(mContext, "pk_test_51HGVYvEA8txfY5aoKS3kcgQ7PsKWlkRpwf0gB6R9hERR2iQyS7qNf1Gx5IfRkAOAf5SLBYrpdrRg3LadRPyl0fjo005Z3Z8G3Z");
//                        Stripe stripe = new Stripe(mContext, getString(R.string.stripe_test_key));
//
//                        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
//                        stripe.createCardToken(
//                                card.build(), new ApiResultCallback<Token>() {
//                                    @Override
//                                    public void onSuccess(Token token) {
//                                        ProjectUtil.pauseProgressDialog();
//                                        dialog.dismiss();
//                                        Log.e("stripeToken","token = " + token.getId());
//
//                                    }
//
//                                    @Override
//                                    public void onError(@NotNull Exception e) {
//                                        ProjectUtil.pauseProgressDialog();
//                                    }
//                                });
//                    }
//                });
//                alertBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                });
//
//                AlertDialog alertDialog = alertBuilder.create();
//                alertDialog.show();
//
//            } else {
//                Toast.makeText(mContext, "Please complete the form", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        dialogBinding.ivBack.setOnClickListener(v -> {
//            dialog.dismiss();
//        });
//
//        dialog.show();
//
//    }
//
//    private void addCardDialog() {
//        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
//
//        AddCardDialogBinding dialogBinding = DataBindingUtil
//                .inflate(LayoutInflater.from(mContext),R.layout.add_card_dialog,null,false);
//        dialog.setContentView(dialogBinding.getRoot());
//
//        dialogBinding.cardForm.cardRequired(true)
//                .expirationRequired(true)
//                .cvvRequired(true)
//                .postalCodeRequired(false)
//                .mobileNumberRequired(false)
//                .setup(SetDeliveryLocationActivity.this);
//
//        dialogBinding.cardForm.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
//
//        dialogBinding.btMakePayment.setOnClickListener(v -> {
//            if (dialogBinding.cardForm.isValid()) {
//                alertBuilder = new AlertDialog.Builder(mContext);
//                alertBuilder.setTitle("Confirm before purchase");
//                alertBuilder.setMessage("Card number: " + dialogBinding.cardForm.getCardNumber() + "\n" +
//                                "Card expiry date: " + dialogBinding.cardForm.getExpirationDateEditText().getText().toString() + "\n" +
//                                "Card CVV: " + dialogBinding.cardForm.getCvv() + "\n"
//                        // "Postal code: " + binding.cardForm.getPostalCode() + "\n" +
//                        /* "Phone number: " + binding.cardForm.getMobileNumber() */ );
//                alertBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//
//                        Card.Builder card = new Card.Builder(dialogBinding.cardForm.getCardNumber(),
//                                Integer.valueOf(dialogBinding.cardForm.getExpirationMonth()),
//                                Integer.valueOf(dialogBinding.cardForm.getExpirationYear()),
//                                dialogBinding.cardForm.getCvv());
//
//                        if (!card.build().validateCard()) {
//                            Toast.makeText(mContext, "Card Not Valid", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        // Stripe stripe = new Stripe(mContext, "pk_test_51HGVYvEA8txfY5aoKS3kcgQ7PsKWlkRpwf0gB6R9hERR2iQyS7qNf1Gx5IfRkAOAf5SLBYrpdrRg3LadRPyl0fjo005Z3Z8G3Z");
//                        Stripe stripe = new Stripe(mContext, getString(R.string.stripe_test_key));
//
//                        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
//                        stripe.createCardToken(
//                                card.build(), new ApiResultCallback<Token>() {
//                                    @Override
//                                    public void onSuccess(Token token) {
//                                        ProjectUtil.pauseProgressDialog();
//                                        dialog.dismiss();
//                                        Log.e("stripeToken","token = " + token.getId());
//                                        getAvailableCardApi();
//                                    }
//
//                                    @Override
//                                    public void onError(@NotNull Exception e) {
//                                        ProjectUtil.pauseProgressDialog();
//                                    }
//                                });
//
//                    }
//                });
//                alertBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                });
//
//                AlertDialog alertDialog = alertBuilder.create();
//                alertDialog.show();
//
//            } else {
//                Toast.makeText(mContext, "Please complete the form", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        dialogBinding.ivBack.setOnClickListener(v -> {
//            dialog.dismiss();
//        });
//
//        dialog.show();
//
//    }

    private void addCardApi(String cardNo,String expDate,String expMonth,String cvv,String tokenId) {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> param = new HashMap<>();
        param.put("user_id",modelLogin.getResult().getId());
        param.put("card_number",cardNo);
        param.put("expiry_date",expDate);
        param.put("expiry_month",expMonth);
        param.put("cvc_code",cvv);

        Log.e("sdfgdsfd","param = " + param.toString());

        Call<ResponseBody> call = api.addCardApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringRes = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringRes);

                    Log.e("dfsdfdsf","stringRes = " + stringRes);

                    if(jsonObject.getString("status").equals("1")) {
                        getAvailableCardApi();
//                        bookingAPicall(binding.etAddress.getText().toString().trim()
//                        +" "+binding.etLandMark.getText().toString().trim());
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

    private void bookingAPicall(String address) {

        bookingParams.put("address",address);
        bookingParams.put("lat", String.valueOf(latLng.latitude));
        bookingParams.put("lon", String.valueOf(latLng.longitude));

        Log.e("bookingParams","bookingParams = " + bookingParams.toString());

        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.bookingStoreApiCall(bookingParams);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringRes = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringRes);
                    if(jsonObject.getString("status").equals("1")) {
                        Toast.makeText(mContext, getString(R.string.success), Toast.LENGTH_SHORT).show();
                        finishAffinity();
                        startActivity(new Intent(mContext, DashboardActivity.class));
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