package com.cityone.shipping.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.cityone.R;
import com.cityone.databinding.AdapterMySendingsBinding;
import com.cityone.databinding.AdapterShipingParcelsBinding;
import com.cityone.databinding.AddFeedbackDialogBinding;
import com.cityone.models.ModelLogin;
import com.cityone.shipping.ShipChatingActivity;
import com.cityone.shipping.ShippDetailsActivity;
import com.cityone.shipping.models.ModelMySendings;
import com.cityone.shipping.models.ModelShipRequest;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.AppConstant;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterShipStatus extends RecyclerView.Adapter<AdapterShipStatus.StoreHolder> {

    Context mContext;
    ArrayList<ModelMySendings.Result> sendingList;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    public AdapterShipStatus(Context mContext, ArrayList<ModelMySendings.Result> sendingList) {
        this.mContext = mContext;
        this.sendingList = sendingList;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    @NonNull
    @Override
    public AdapterShipStatus.StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterMySendingsBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.adapter_my_sendings,parent,false);
        return new AdapterShipStatus.StoreHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterShipStatus.StoreHolder holder, int position) {

        ModelMySendings.Result data = sendingList.get(position);

        holder.binding.setParcel(data);

        if("Accept".equals(data.getBid_status())) {
            holder.binding.btChat.setVisibility(View.VISIBLE);
        } else if("Pickup".equals(data.getBid_status())) {
            holder.binding.btChat.setVisibility(View.VISIBLE);
        } else if("Delivered".equals(data.getBid_status())) {
            holder.binding.btChat.setVisibility(View.VISIBLE);
            if("true".equals(data.getRating())) {
                holder.binding.btFeedback.setVisibility(View.GONE);
            } else {
                holder.binding.btFeedback.setVisibility(View.VISIBLE);
            }
        } else {
            holder.binding.btChat.setVisibility(View.GONE);
            holder.binding.btFeedback.setVisibility(View.GONE);
        }

        holder.binding.btChat.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, ShipChatingActivity.class)
                    .putExtra("sender_id",modelLogin.getResult().getId())
                    .putExtra("receiver_id",data.getDriver_id())
                    .putExtra("name",data.getUser_name())
            );
        });

        holder.binding.btParcel.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, ShippDetailsActivity.class)
                    .putExtra("parcelid",data.getId())
            );
        });

        holder.binding.btFeedback.setOnClickListener(v -> {
            addFeedbackDialog(data);
        });

    }

    private void addFeedbackDialog(ModelMySendings.Result data) {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);

        AddFeedbackDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
        ,R.layout.add_feedback_dialog,null,false);

        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.tvDrivername.setText(data.getUser_name());
        Picasso.get().load(data.getUser_image())
                .error(R.drawable.default_profile_icon)
                .placeholder(R.drawable.default_profile_icon)
                .into(dialogBinding.driveUserProfile);

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.btSubmit.setOnClickListener(v -> {
            addFeedbackApi(data,dialog,String.valueOf(dialogBinding.ratingBar.getRating())
                    ,dialogBinding.tvComment.getText().toString().trim());
        });

        dialog.show();
    }

    private void addFeedbackApi(ModelMySendings.Result data,Dialog dialog,String rating,String comment) {
        ProjectUtil.showProgressDialog(mContext,false,mContext.getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> params = new HashMap<>();
        params.put("user_id",modelLogin.getResult().getId());
        params.put("shipping_id",data.getShipping_id());
        params.put("driver_id",data.getDriver_id());
        params.put("rating",rating);
        params.put("review",comment);

        Log.e("dfsfsdf","params = " + params);

        Call<ResponseBody> call = api.addFeedbackApiCall(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {
                        data.setRating("true");
                        dialog.dismiss();
                        notifyDataSetChanged();
                        Log.e("responseStringdfsdfsd","responseString = " + responseString);

                    } else {
                        //Toast.makeText(mContext, getString(R.string.no_request_found), Toast.LENGTH_SHORT).show();
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
            }

        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return sendingList == null?0:sendingList.size();
    }

    public class StoreHolder extends RecyclerView.ViewHolder{

        AdapterMySendingsBinding binding;

        public StoreHolder(AdapterMySendingsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}



