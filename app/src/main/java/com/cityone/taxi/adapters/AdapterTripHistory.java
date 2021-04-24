package com.cityone.taxi.adapters;

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
import com.cityone.databinding.AdapterTripHistoryBinding;
import com.cityone.databinding.AddFeedbackDialogBinding;
import com.cityone.databinding.TaxiPaymentHistoryDialogBinding;
import com.cityone.models.ModelLogin;
import com.cityone.shipping.adapters.AdapterShipRequest;
import com.cityone.shipping.models.ModelMySendings;
import com.cityone.shipping.models.ModelShipRequest;
import com.cityone.taxi.activities.TrackTaxiAct;
import com.cityone.taxi.activities.TripHistoryAct;
import com.cityone.taxi.models.ModelTripHistory;
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

public class AdapterTripHistory extends RecyclerView.Adapter<AdapterTripHistory.MyTripView> {

    Context mContext;
    ArrayList<ModelTripHistory.Result> tripHisList;
    String status;
    PayNowInterface payNowInterface;
    String payMethod = null;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    public AdapterTripHistory(Context mContext, ArrayList<ModelTripHistory.Result> tripHisList,String status,PayNowInterface payNowInterface) {
        this.mContext = mContext;
        this.tripHisList = tripHisList;
        this.status = status;
        this.payNowInterface = payNowInterface;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER);
    }

    @NonNull
    @Override
    public MyTripView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterTripHistoryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.adapter_trip_history,parent,false);
        return new MyTripView(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyTripView holder, int position) {

        ModelTripHistory.Result data = tripHisList.get(position);

        holder.binding.tvDateTime.setText(data.getPicklaterdate());
        holder.binding.tvFrom.setText(data.getPicuplocation());
        holder.binding.tvDestination.setText(data.getDropofflocation());
        holder.binding.tvStatus.setText(data.getStatus());

        if(status.equals("Pending")) {
            holder.binding.GoDetail.setVisibility(View.GONE);
        } else {
            holder.binding.GoDetail.setVisibility(View.VISIBLE);
        }

        Log.e("fsdfssadaasd","data.getUser_rating_status() = " + data.getUser_rating_status());

        if(status.equals("Finish")) {
            holder.binding.btPayNow.setVisibility(View.VISIBLE);
            holder.binding.btGiveFeedback.setVisibility(View.GONE);
        } else if(status.equals("Finish")) {
            if(data.getStatus().equals("Paid")) {
                if("true".equals(data.getUser_rating_status())) {
                    holder.binding.btGiveFeedback.setVisibility(View.GONE);
                } else if("false".equals(data.getUser_rating_status())) {
                    holder.binding.btGiveFeedback.setVisibility(View.VISIBLE);
                } else {
                    holder.binding.btGiveFeedback.setVisibility(View.GONE);
                }
                holder.binding.btPayNow.setVisibility(View.GONE);
            } else {
                holder.binding.btPayNow.setVisibility(View.VISIBLE);
                holder.binding.btGiveFeedback.setVisibility(View.GONE);
            }
        }

        holder.binding.btGiveFeedback.setOnClickListener(v -> {
            addFeedbackDialog(data);
        });

        holder.binding.GoDetail.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, TrackTaxiAct.class)
            .putExtra("request_id",data.getId())
            );
        });

        holder.binding.btPayNow.setOnClickListener(v -> {
            openPaymentSummaryDialog(data);
        });

    }

    private void addFeedbackDialog(ModelTripHistory.Result data) {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);

        AddFeedbackDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                ,R.layout.add_feedback_dialog,null,false);

        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.tvDrivername.setText(data.getUser_name());
//        Picasso.get().load(data.get())
//                .error(R.drawable.default_profile_icon)
//                .placeholder(R.drawable.default_profile_icon)
//                .into(dialogBinding.driveUserProfile);

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.btSubmit.setOnClickListener(v -> {
            addFeedbackApi(data,dialog,String.valueOf(dialogBinding.ratingBar.getRating())
                    ,dialogBinding.tvComment.getText().toString().trim());
        });
        dialog.show();
    }

    private void addFeedbackApi(ModelTripHistory.Result data, Dialog dialog, String rating, String comment) {
        ProjectUtil.showProgressDialog(mContext,false,mContext.getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> params = new HashMap<>();
        params.put("user_id",modelLogin.getResult().getId());
        params.put("rating",rating);
        params.put("review",comment);

        Call<ResponseBody> call = api.addFeedbackByUserApiCall(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        data.setUser_rating_status("true");
                        notifyDataSetChanged();
                        dialog.dismiss();
                        // Log.e("responseStringdfsdfsd","responseString = " + responseString);

                    } else {

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

    private void openPaymentSummaryDialog(ModelTripHistory.Result data) {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        TaxiPaymentHistoryDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                ,R.layout.taxi_payment_history_dialog,null,false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.setPayment(data);
        dialogBinding.tvEstiPrice.setText(AppConstant.DOLLAR + data.getEstimate_charge_amount());

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.btPayNow.setOnClickListener(v -> {
            dialog.dismiss();
            if(dialogBinding.rbCash.isChecked()) {
                payMethod = "cash";
                payNowInterface.onSuccess(data,payMethod);
            } else if(dialogBinding.rbOnline.isChecked()) {
                payMethod = "online";
                payNowInterface.onSuccess(data,payMethod);
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.please_select_pay_method), Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();

    }

    public interface PayNowInterface {
        void onSuccess(ModelTripHistory.Result data,String payMethod);
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
        return tripHisList == null?0:tripHisList.size();
    }

    public class MyTripView extends RecyclerView.ViewHolder {

        AdapterTripHistoryBinding binding;
        public MyTripView(@NonNull AdapterTripHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}
