package com.cityone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cityone.databinding.ActivityMainBinding;
import com.cityone.models.ModelLogin;
import com.cityone.utils.AppConstant;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.redeban.payment.Payment;
import com.redeban.payment.model.Card;
import com.redeban.payment.rest.TokenCallback;
import com.redeban.payment.rest.model.RedebanError;

public class MainActivity extends AppCompatActivity {

    Context mContext = MainActivity.this;
    ActivityMainBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

//        /*
//         *
//         * Init library
//         *
//         * @param test_mode false to use production environment
//         * @param redeban_client_app_code provided by Redeban.
//         * @param redeban_client_app_key provided by Redeban.
//         */
//        Payment.setEnvironment(AppConstant.REDEBAN_IS_TEST_MODE,
//                 AppConstant.REDEBAN_CLIENT_APP_CODE,
//                 AppConstant.REDEBAN_CLIENT_APP_KEY);

        inti();

        Log.e("adasdasdasds","user_id = " + modelLogin.getResult().getId());
        Log.e("adasdasdasds","email = " + modelLogin.getResult().getEmail());

    }

    private void inti() {

        binding.btPayNow.setOnClickListener(v -> {
            Card cardToSave = binding.cardMultilineWidget.getCard();
            if (cardToSave == null) {
                Toast.makeText(mContext,"Invalid card informations", Toast.LENGTH_SHORT).show();
                return;
            } createCardToken(modelLogin.getResult().getId(),
                    modelLogin.getResult().getEmail(),
                    cardToSave);
        });

    }

    private void createCardToken(String userId,String email,Card cardToSave) {

        ProjectUtil.showProgressDialog(mContext,false,"Please wait...");
        Payment.addCard(mContext, userId, email, cardToSave, new TokenCallback() {

            public void onSuccess(Card card) {
                ProjectUtil.pauseProgressDialog();
                if(card != null) {
                    if(card.getStatus().equals("valid")) {

                        Log.e("onSuccess","status = " +"status: " + card.getStatus());
                        Log.e("onSuccess","globalpayment = " +"Card Token: " + card.getToken());
                        Log.e("onSuccess","transaction_reference = " +"status: " + card.getTransactionReference());
                        Toast.makeText(mContext, "status = " +"status: " + card.getStatus(), Toast.LENGTH_SHORT).show();
//                        Alert.show(mContext,
//                                "Card Successfully Added",
//                                "status: " + card.getStatus() + "\n" +
//                                        "Card Token: " + card.getToken() + "\n" +
//                                        "transaction_reference: " + card.getTransactionReference());

                    } else if (card.getStatus().equals("review")) {
                        Log.e("reviewreview","status = " +"status: " + card.getStatus());
                        Log.e("reviewreview","globalpayment = " +"Card Token: " + card.getToken());
                        Log.e("reviewreview","transaction_reference = " +"status: " + card.getTransactionReference());
                        Toast.makeText(mContext, "status = " +"status: " + card.getStatus(), Toast.LENGTH_SHORT).show();

//                      Alert.show(mContext,
//                              "Card Under Review",
//                              "status: " + card.getStatus() + "\n" +
//                                        "Card Token: " + card.getToken() + "\n" +
//                                        "transaction_reference: " + card.getTransactionReference());

                    } else {
                        Toast.makeText(mContext, "status = " +"status: " + card.getStatus(), Toast.LENGTH_SHORT).show();
                        Log.e("CardError","status = " +"status: " + card.getStatus());
                        Log.e("CardError","message: " + card.getMessage());
//                        Alert.show(mContext,
//                                "Error",
//                                "status: " + card.getStatus() + "\n" +
//                                        "message: " + card.getMessage());
                    }

                }

                // TODO: Create charge or Save Token to your backend

            }

            public void onError(RedebanError error) {
                ProjectUtil.pauseProgressDialog();
                Log.e("RedebanError","Type: " + error.getType());
                Log.e("RedebanError","Help: " + error.getHelp());
                Log.e("RedebanError","Description: " + error.getDescription());
//                Alert.show(mContext,
//                        "Error",
//                        "Type: " + error.getType() + "\n" +
//                                "Help: " + error.getHelp() + "\n" +
//                                "Description: " + error.getDescription());

                // TODO: Handle error
            }

        });

    }


}