package com.cityone.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.cityone.R;
import com.cityone.databinding.ActivityPaypalWebviewBinding;
import com.cityone.models.ModelLogin;
import com.cityone.stores.activities.StorePaymentActivity;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.AppConstant;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaypalWebviewAct extends AppCompatActivity {

    Context mContext = PaypalWebviewAct.this;
    ActivityPaypalWebviewBinding binding;
    HashMap<String,String> param = new HashMap<>();
    String type = "",amount = "",id="",taxiPayType="";
    SharedPref sharedPref;
    ModelLogin modelLogin;
    String loadPaymentURL = "";
    String timeZone = TimeZone.getDefault().getID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_paypal_webview);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        param = (HashMap<String, String>) getIntent().getSerializableExtra("param");

        amount = param.get("total_amount");
        id = getIntent().getStringExtra("id");

        loadPaymentURL = "https://equipmeapp.co.nz/CityOne/Products/buy?request_id="+modelLogin.getResult().getId()+"&amount="+amount;

        Log.e("sfasdfdassdsf","loadPaymentURL = " + loadPaymentURL);

        init();

    }

    private void init() {

        binding.webView.getSettings().setJavaScriptEnabled(true);// enable javascript
        binding.webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(PaypalWebviewAct.this, description, Toast.LENGTH_SHORT).show();
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("paypal_web_url", url);
                String title = binding.webView.getTitle();
                Log.d("title", title);
                String completed = "PayPal checkout - Payment complete.";
                if (url.contains("success")) {
                      callBookingApiFromAdapter("");
//                    Toast.makeText(PaypalWebviewAct.this, "Successful", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent();
//                    setResult(1234,intent);
//                    finish();
                    // doPayment();
                }
                if (title.contains("cancel")) {
                    Toast.makeText(mContext, "Payment Cancel", Toast.LENGTH_SHORT).show();
                }

            }

        });

        binding.webView.loadUrl(loadPaymentURL);

    }

    private void doPayment() {

    }

    public void callBookingApiFromAdapter(String token) {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        Call<ResponseBody> call = api.bookingStoreApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringRes = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringRes);
                    if(jsonObject.getString("status").equals("1")) {

                        JSONObject resultJson = jsonObject.getJSONObject("result");

                        HashMap<String,String> param = new HashMap<>();

                        Toast.makeText(PaypalWebviewAct.this, getString(R.string.order_placed), Toast.LENGTH_SHORT).show();
                        finishAffinity();
                        startActivity(new Intent(mContext, DashboardActivity.class));

                        param.put("transaction_type","transaction_type");
                        param.put("payment_type","Card");
                        param.put("amount",resultJson.getString("total_amount"));
                        param.put("user_id",modelLogin.getResult().getId());
                        param.put("order_id",resultJson.getString("id"));
                        param.put("restaurant_id",resultJson.getString("restaurant_id"));
                        param.put("time_zone",timeZone);
                        param.put("token",token);
                        param.put("currency","INR");
                        param.put("tip","0");

                        Log.e("paramparam","param = " + param.toString());

                        // paymentApiCall(token,resultJson.getString("total_amount"));

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
    public void onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack();
        } else {
            finish();
        }
    }


}
