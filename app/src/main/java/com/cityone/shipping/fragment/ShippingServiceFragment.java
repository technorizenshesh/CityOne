package com.cityone.shipping.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cityone.R;
import com.cityone.databinding.FragmentShippingServiceBinding;
import com.cityone.databinding.FragmentStartBinding;
import com.cityone.models.ModelLogin;
import com.cityone.shipping.adapters.AdapterShipRequest;
import com.cityone.shipping.models.ModelShipRequest;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.AppConstant;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShippingServiceFragment extends Fragment {
    FragmentShippingServiceBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    ModelShipRequest modelShipRequest = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_shipping_service, container, false);
        sharedPref = SharedPref.getInstance(getActivity());
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllShipRequest();
            }
        });

        getAllShipRequest();
        return binding.getRoot();
    }

    private void getAllShipRequest() {
        ProjectUtil.showProgressDialog(getActivity(),false,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(getActivity()).create(Api.class);

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
                        binding.tvNotFound.setVisibility(View.GONE);
                        modelShipRequest = new Gson().fromJson(responseString, ModelShipRequest.class);

                        AdapterShipRequest adapterShipRequest = new AdapterShipRequest(getActivity(),modelShipRequest.getResult());
                        binding.rvRequest.setAdapter(adapterShipRequest);

                        Log.e("responseStringdfsdfsd","responseString = " + responseString);

                    } else {
                      //  Toast.makeText(getActivity(), getString(R.string.no_request_found), Toast.LENGTH_SHORT).show();
                        binding.tvNotFound.setVisibility(View.VISIBLE);
                        AdapterShipRequest adapterShipRequest = new AdapterShipRequest(getActivity(),null);
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


}