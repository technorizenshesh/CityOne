package com.cityone.shipping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cityone.R;
import com.cityone.databinding.ActivityShipChatListBinding;
import com.cityone.models.ModelLogin;
import com.cityone.shipping.adapters.AdapterChats;
import com.cityone.shipping.models.ModelChatList;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.App;
import com.cityone.utils.AppConstant;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShipChatListActivity extends AppCompatActivity {

    Context mContext = ShipChatListActivity.this;
    ActivityShipChatListBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    ModelChatList modelChat;
    AdapterChats adapterChats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_ship_chat_list);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        App.checkToken(mContext);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getChats();
            }
        });

        getChats();

    }

    private void getChats() {

        Log.e("kghkljsdhkljf","userIdId = " + modelLogin.getResult().getId());

        ProjectUtil.showProgressDialog(mContext,false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> param = new HashMap<>();
        param.put("receiver_id",modelLogin.getResult().getId());

        Call<ResponseBody> call = api.getConversation(param);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);

                Log.e("kghkljsdhkljf","response = " + response);

                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    Log.e("kjagsdkjgaskjd","stringResponse = " + response);
                    Log.e("kjagsdkjgaskjd","stringResponse = " + stringResponse);

                    if (jsonObject.getString("status").equals("1")) {

                        modelChat = new Gson().fromJson(stringResponse, ModelChatList.class);

                        adapterChats = new AdapterChats(mContext, modelChat.getResult());
                        binding.rvChats.setLayoutManager(new LinearLayoutManager(mContext));
                        binding.rvChats.setAdapter(adapterChats);

                        // Toast.makeText(mContext, getString(R.string.successful), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(mContext, getString(R.string.no_chat_found), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                binding.swipLayout.setRefreshing(false);
                ProjectUtil.pauseProgressDialog();
            }

        });


    }


}