package com.cityone.stores.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.cityone.databinding.AdapterStoreItemsBinding;
import com.cityone.databinding.ItemDetailDialogBinding;
import com.cityone.models.ModelLogin;
import com.cityone.stores.activities.StoreDetailsActivity;
import com.cityone.stores.models.ModelStoreDetails;
import com.cityone.stores.models.ModelStoreItems;
import com.cityone.stores.models.ModelStores;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.AppConstant;
import com.cityone.utils.CustomAlertDialog;
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

public class AdapterStoreItems extends RecyclerView.Adapter<AdapterStoreItems.StoreCatHolder> {

    Context mContext;
    ArrayList<ModelStoreItems.Result> storeList;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    double itemTotal = 0.0;
    int itemCount = 0;

    public AdapterStoreItems(Context mContext, ArrayList<ModelStoreItems.Result> storeList) {
        this.mContext = mContext;
        this.storeList = storeList;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    @NonNull
    @Override
    public AdapterStoreItems.StoreCatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterStoreItemsBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.adapter_store_items,parent,false);
        return new AdapterStoreItems.StoreCatHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterStoreItems.StoreCatHolder holder, int position) {

        ModelStoreItems.Result data = storeList.get(position);

        holder.binding.tvName.setText(data.getName());
        holder.binding.tvPrice.setText(AppConstant.DOLLAR + data.getAmount());
        if(data.getDiscount() == null || data.getDiscount().equals("") || data.getDiscount().equals("0")) {
            holder.binding.tvDiscount.setVisibility(View.GONE);
        } else {
            holder.binding.tvDiscount.setVisibility(View.VISIBLE);
            holder.binding.tvDiscount.setText(data.getDiscount()+"% Off");
        }
        Picasso.get().load(data.getImage()).into(holder.binding.ivImage);

        holder.binding.btAdd.setOnClickListener(v -> {
            openItemDetailDialog(data);
        });

    }

    private void openItemDetailDialog(ModelStoreItems.Result data) {

        itemTotal = 0.0;
        itemCount = 1;

        ItemDetailDialogBinding dialogBinding = DataBindingUtil.
                inflate(LayoutInflater.from(mContext),R.layout.item_detail_dialog,null,false);

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(dialogBinding.getRoot());

        Picasso.get().load(data.getImage()).into(dialogBinding.ivItemImage);
        dialogBinding.tvItemName.setText(data.getName());
        dialogBinding.tvPrice.setText(AppConstant.DOLLAR + data.getAmount());

        itemTotal = Double.parseDouble(data.getAmount());

        dialogBinding.tvAddItem.setOnClickListener(v -> {
            addToCartApi(dialog,data,String.valueOf(itemCount));
        });

        dialogBinding.ivPlus.setOnClickListener(v -> {
            itemCount++;
            dialogBinding.tvQuantity.setText(String.valueOf(itemCount));
            itemTotal = Double.parseDouble(data.getAmount())  * itemCount;
            dialogBinding.tvPrice.setText(AppConstant.DOLLAR + itemTotal);
        });

        dialogBinding.ivMinus.setOnClickListener(v -> {
            if(itemCount > 1) {
                itemCount--;
                dialogBinding.tvQuantity.setText(String.valueOf(itemCount));
                itemTotal = Double.parseDouble(data.getAmount())  * itemCount;
                dialogBinding.tvPrice.setText(AppConstant.DOLLAR + itemTotal);
            }
        });

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    private void addToCartApi(Dialog dialog,ModelStoreItems.Result data,String quantity) {
        ProjectUtil.showProgressDialog(mContext,false,mContext.getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> param = new HashMap<>();
        param.put("user_id",modelLogin.getResult().getId());
        param.put("restaurant_id",data.getRestaurant_id());
        param.put("item_id",data.getId());
        param.put("quantity",quantity);

        Log.e("paramparam","param = " + param.toString());

        Call<ResponseBody> call = api.addToCartApiCall(param);
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
                        ((StoreDetailsActivity)mContext).updateCartCount(jsonObject.getInt("count"));
                        dialog.dismiss();
                    } else if(jsonObject.getString("status").equals("2")) {
                        Log.e("fsdfdsfdsf","responseString = " + responseString);
                        Log.e("fsdfdsfdsf","response = " + response);
                        new CustomAlertDialog(mContext).Message(mContext.getString(R.string.cart_remove_text))
                        .Show(() -> {
                           // Toast.makeText(mContext,"Click",Toast.LENGTH_SHORT).show();
                        });
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
        return storeList == null?0:storeList.size();
    }

    public class StoreCatHolder extends RecyclerView.ViewHolder{

        AdapterStoreItemsBinding binding;

        public StoreCatHolder(AdapterStoreItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
