package com.cityone.entertainment.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cityone.R;
import com.cityone.databinding.AdapterStoreItemsBinding;
import com.cityone.databinding.AdapterTicketsBinding;
import com.cityone.databinding.EntDetailDialogBinding;
import com.cityone.databinding.ItemDetailDialogBinding;
import com.cityone.entertainment.models.ModelEntTicket;
import com.cityone.models.ModelLogin;
import com.cityone.stores.activities.StoreDetailsActivity;
import com.cityone.stores.adapters.AdapterStoreItems;
import com.cityone.stores.models.ModelStoreItems;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.AppConstant;
import com.cityone.utils.CustomAlertDialog;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterTickets extends RecyclerView.Adapter<AdapterTickets.StoreCatHolder> {

    Context mContext;
    ArrayList<ModelEntTicket.Result> ticketList;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    String image;
    double itemTotal = 0.0;
    int itemCount = 0;
    String entId = "";
    UpdateItemCountIterf updateItemCountIterf;

    public AdapterTickets(Context mContext, ArrayList<ModelEntTicket.Result> ticketList,String image,UpdateItemCountIterf updateItemCountIterf,String entId) {
        this.mContext = mContext;
        this.ticketList = ticketList;
        this.image = image;
        this.updateItemCountIterf = updateItemCountIterf;
        this.entId = entId;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    @NonNull
    @Override
    public AdapterTickets.StoreCatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterTicketsBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.adapter_tickets,parent,false);
        return new AdapterTickets.StoreCatHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTickets.StoreCatHolder holder, int position) {

        ModelEntTicket.Result data = ticketList.get(position);

        holder.binding.setData(data);

        holder.binding.btAdd.setOnClickListener(v -> {
            ticketDetailDialog(image,data);
        });

    }

    public interface UpdateItemCountIterf{
        void onSuccess(int count);
    }

    private void ticketDetailDialog(String image,ModelEntTicket.Result data) {

        itemTotal = 0.0;
        itemCount = 1;

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        EntDetailDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                ,R.layout.ent_detail_dialog,null,false);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();

        itemTotal = Double.parseDouble(data.getPrice());

        try {
           Picasso.get().load(image).into(dialogBinding.ivImage);
        } catch (Exception e) {}

        dialogBinding.tvTicketName.setText(data.getName());
        dialogBinding.tvIncludedDesp.setText(data.getDescription());
        dialogBinding.tvPrice.setText(AppConstant.DOLLAR + data.getPrice());

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.ivPlus.setOnClickListener(v -> {
            itemCount++;
            dialogBinding.tvQuantity.setText(String.valueOf(itemCount));
            itemTotal = Double.parseDouble(data.getPrice())  * itemCount;
            dialogBinding.tvPrice.setText(AppConstant.DOLLAR + itemTotal);
        });

        dialogBinding.ivMinus.setOnClickListener(v -> {
            if(itemCount > 1) {
                itemCount--;
                dialogBinding.tvQuantity.setText(String.valueOf(itemCount));
                itemTotal = Double.parseDouble(data.getPrice())  * itemCount;
                dialogBinding.tvPrice.setText(AppConstant.DOLLAR + itemTotal);
            }
        });

        dialogBinding.tvAddTicket.setOnClickListener(v -> {
            addToCartApi(dialog,data,String.valueOf(itemCount));
        });

    }

    private void addToCartApi(Dialog dialog,ModelEntTicket.Result data,String quantity) {
        ProjectUtil.showProgressDialog(mContext,false,mContext.getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> param = new HashMap<>();
        param.put("user_id",modelLogin.getResult().getId());
        param.put("entertentment_id",entId);
        param.put("ticket_id",data.getId());
        param.put("quantity",quantity);

        Log.e("paramparam","param = " + param.toString());

        Call<ResponseBody> call = api.addToCartEntApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {
                        Log.e("fsdfdsfdsf","responseString = " + responseString);
                        Log.e("fsdfdsfdsf","response = " + response);
                        Log.e("fsdfdsfdsf","count = " + jsonObject.getInt("count"));
                        updateItemCountIterf.onSuccess(jsonObject.getInt("count"));
                        dialog.dismiss();
                    } else if(jsonObject.getString("status").equals("2")) {
                        Log.e("fsdfdsfdsf","responseString = " + responseString);
                        Log.e("fsdfdsfdsf","response = " + response);
                        removeCartDialog();
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("fsdfdsfdsf","response = " + t.getMessage());
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    private void removeCartDialog() {
        AlertDialog.Builder builder =  new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setMessage(mContext.getString(R.string.cart_remove_text_ent));
        builder.setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removerCartApiCall();
                dialog.dismiss();
            }
        }).create().show();
    }

    private void removerCartApiCall() {
        ProjectUtil.showProgressDialog(mContext,false,mContext.getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> param = new HashMap<>();
        param.put("user_id",modelLogin.getResult().getId());

        Log.e("paramparam","param = " + param.toString());

        Call<ResponseBody> call = api.removeEntCartApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {
                        updateItemCountIterf.onSuccess(1234);
                    } else if(jsonObject.getString("status").equals("2")) {
                        Log.e("fsdfdsfdsf","responseString = " + responseString);
                        Log.e("fsdfdsfdsf","response = " + response);
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("fsdfdsfdsf","response = " + t.getMessage());
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    @Override
    public int getItemCount() {
        return ticketList == null?0:ticketList.size();
    }

    public class StoreCatHolder extends RecyclerView.ViewHolder {

        AdapterTicketsBinding binding;

        public StoreCatHolder(AdapterTicketsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
