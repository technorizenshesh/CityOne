package com.cityone.stores.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
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
    static double itemTotal = 0.0;
    static int itemCount = 0;
    static Dialog dialog;
    static int toppingTotal;

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
                .inflate(LayoutInflater.from(mContext), R.layout.adapter_store_items, parent, false);
        return new AdapterStoreItems.StoreCatHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterStoreItems.StoreCatHolder holder, int position) {

        ModelStoreItems.Result data = storeList.get(position);

        holder.binding.tvName.setText(data.getName());
        holder.binding.tvPrice.setText(AppConstant.DOLLAR + data.getAmount());

        if (data.getDiscount() == null ||
                data.getDiscount().equals("") ||
                data.getDiscount().equals("0")) {
            holder.binding.tvDiscount.setVisibility(View.GONE);
        } else {
            holder.binding.tvDiscount.setVisibility(View.VISIBLE);
            holder.binding.tvDiscount.setText(data.getProduct_point() + " Points");
        }

        if (data.getAdditional_discount() == null || data.getAdditional_discount().equals("")) {
            holder.binding.tvCashBack.setVisibility(View.GONE);
        } else {
            double additionalDiscount = 0.0;
            try {
                additionalDiscount = Double.parseDouble(data.getAdditional_discount());
                Log.e("sdadasdasdasd","additionalDiscount Before = " + additionalDiscount);
                additionalDiscount = additionalDiscount / 2.0;
                Log.e("sdadasdasdasd","additionalDiscount After = " + additionalDiscount);
            } catch (Exception e) {}
            holder.binding.tvCashBack.setVisibility(View.VISIBLE);
            holder.binding.tvCashBack.setText("Cashback : "  + additionalDiscount + "%");
        }

        Picasso.get().load(data.getImage()).into(holder.binding.ivImage);

        holder.binding.btAdd.setOnClickListener(v -> {
            openItemDetailDialog(data);
        });

    }

    public static void updatePrice(String toppingTotal, String price) {

        Log.e("sdfsfsdf", "Topping updatePrice");
        Log.e("sdfsfsdf", "toppingTotal = " + toppingTotal);
        Log.e("sdfsfsdf", "itemTotal = " + itemTotal);
        Log.e("sdfsfsdf", "count = " + itemCount);
        // Log.e("sdfsfsdf", "currentPosition = " + currentPosition);

        itemTotal = Double.parseDouble(price) * itemCount;
        itemTotal = itemTotal + (itemCount * Double.parseDouble(toppingTotal));
        // itemTotal = itemTotal + (itemCount * Double.parseDouble(optionPrice));

        // Log.e("sdfsfsdf","itemList.get(currentPosition).getItem_price() = "
        // + itemList.get(currentPosition).getItem_price());

        // Log.e("sdfsfsdf", "currentPosition = " + currentPosition);
        Log.e("sdfsfsdf", "itemTotal after update = " + itemTotal);

        TextView textPrice = dialog.findViewById(R.id.tvPrice);
        textPrice.setText(AppConstant.CURRENCY + " " + itemTotal);

    }

    private void openItemDetailDialog(ModelStoreItems.Result data) {

        itemTotal = 0.0;
        itemCount = 1;

        try {
            for (int i = 0; i < data.getExtra_options_item().size(); i++) {
                data.getExtra_options_item().get(i).setChecked(false);
            }
        } catch (Exception e) {

        }

        ItemDetailDialogBinding dialogBinding = DataBindingUtil.
                inflate(LayoutInflater.from(mContext), R.layout.item_detail_dialog, null, false);

        dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(dialogBinding.getRoot());

        Picasso.get().load(data.getImage()).into(dialogBinding.ivItemImage);
        dialogBinding.tvItemName.setText(data.getName());

        if(data.getItem_description() == null || data.getItem_description().equals("")) {
            dialogBinding.tvItemDesp.setVisibility(View.GONE);
        } else {
            dialogBinding.tvItemDesp.setVisibility(View.VISIBLE);
            dialogBinding.tvItemDesp.setText(data.getItem_description());
        }

        if(data.getAdditional_discount() == null || data.getAdditional_discount().equals("")) {
            dialogBinding.tvAdditionalDiscount.setVisibility(View.GONE);
        } else {
            double additionalDiscount = 0.0;
            try {
                additionalDiscount = Double.parseDouble(data.getAdditional_discount());
                Log.e("sdadasdasdasd","additionalDiscount Before = " + additionalDiscount);
                additionalDiscount = additionalDiscount / 2.0;
                Log.e("sdadasdasdasd","additionalDiscount After = " + additionalDiscount);
            } catch (Exception e) {}
            dialogBinding.tvAdditionalDiscount.setVisibility(View.VISIBLE);
            dialogBinding.tvAdditionalDiscount.setText("Cashback : " + additionalDiscount + "%");
        }

        dialogBinding.tvPrice.setText(AppConstant.DOLLAR + data.getAmount());
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                    dialog.dismiss();
                return false;
            }
        });

        AdapterOptions adapterOptions = new AdapterOptions(mContext, data.getExtra_options_item(), data.getAmount());
        dialogBinding.optionListView.setAdapter(adapterOptions);

        itemTotal = Double.parseDouble(data.getAmount());

        dialogBinding.tvAddItem.setOnClickListener(v -> {
            Log.e("adsasdasdasd", "Total Option Price = " + getToppingTotal(data));
            Log.e("adsasdasdasd", "Options Ids = " + getSelectedToppingIds(data));
            addToCartApi(dialog, data, String.valueOf(itemCount), getSelectedToppingIds(data));
        });

        dialogBinding.ivPlus.setOnClickListener(v -> {
            itemCount++;
            dialogBinding.tvQuantity.setText(String.valueOf(itemCount));
            itemTotal = Double.parseDouble(data.getAmount()) * itemCount;
            itemTotal = itemTotal + (getToppingTotal(data) * itemCount);
            dialogBinding.tvPrice.setText(AppConstant.DOLLAR + itemTotal);
        });

        dialogBinding.ivMinus.setOnClickListener(v -> {
            if (itemCount > 1) {
                itemCount--;
                dialogBinding.tvQuantity.setText(String.valueOf(itemCount));
                itemTotal = Double.parseDouble(data.getAmount()) * itemCount;
                itemTotal = itemTotal + (getToppingTotal(data) * itemCount);
                dialogBinding.tvPrice.setText(AppConstant.DOLLAR + itemTotal);
            }
        });

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    private int getToppingTotal(ModelStoreItems.Result data) {
        toppingTotal = 0;
        if (data.getExtra_options_item() == null || data.getExtra_options_item().size() == 0) {
            return 0;
        } else {
            for (int i = 0; i < data.getExtra_options_item().size(); i++) {
                Log.e("sdfsfsdf", "inside For = " + i);
                Log.e("sdfsfsdf", "sdfsfsdf isChecked = " + data.getExtra_options_item().get(i).isChecked());
                if (data.getExtra_options_item().get(i).isChecked()) {
                    int price = 0;
                    if(data.getExtra_options_item().get(i).getExtra_price().equalsIgnoreCase("")) {
                        price = 0;
                        toppingTotal = toppingTotal + price;

                    }
                    else {
                        toppingTotal = toppingTotal + Integer.parseInt(data.getExtra_options_item().get(i).getExtra_price());

                    }
                }
            }

            return toppingTotal;

        }

    }

    private String getSelectedToppingIds(ModelStoreItems.Result data) {
        ArrayList<String> tempList = new ArrayList<>();
        if (data.getExtra_options_item() == null || data.getExtra_options_item().size() == 0) {
            return "";
        } else {
            for (int i = 0; i < data.getExtra_options_item().size(); i++) {
                Log.e("sdfsfsdf", "inside For = " + i);
                Log.e("sdfsfsdf", "sdfsfsdf isChecked = " + data.getExtra_options_item().get(i).isChecked());
                if (data.getExtra_options_item().get(i).isChecked()) {
                    tempList.add(data.getExtra_options_item().get(i).getId());
                }
            }
            return TextUtils.join(",", tempList);
        }
    }

    private void addToCartApi(Dialog dialog, ModelStoreItems.Result data, String quantity, String optionIds) {
        ProjectUtil.showProgressDialog(mContext, false, mContext.getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        param.put("restaurant_id", data.getRestaurant_id());
        param.put("item_id", data.getId());
        param.put("quantity", quantity);
        param.put("extra_item_id", optionIds);

        Log.e("paramparam", "param = " + param.toString());

        Call<ResponseBody> call = api.addToCartApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        Log.e("fsdfdsfdsf", "responseString = " + responseString);
                        Log.e("fsdfdsfdsf", "response = " + response);
                        Log.e("fsdfdsfdsf", "count = " + jsonObject.getInt("count"));
                        ((StoreDetailsActivity) mContext).updateCartCount(jsonObject.getInt("count"));
                        dialog.dismiss();
                    } else if (jsonObject.getString("status").equals("2")) {
                        Log.e("fsdfdsfdsf", "responseString = " + responseString);
                        Log.e("fsdfdsfdsf", "response = " + response);
                        removeCartDialog();
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("fsdfdsfdsf", "response = " + t.getMessage());
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    private void removeCartDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getString(R.string.another_res_added_cart_text))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeCartApi();
                        dialog.dismiss();
                    }
                }).setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    private void removeCartApi() {
        ProjectUtil.showProgressDialog(mContext, false, mContext.getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());

        Log.e("paramparam", "param = " + param.toString());

        Call<ResponseBody> call = api.removeCartApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("fsdfdsfdsf", "response = " + t.getMessage());
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    @Override
    public int getItemCount() {
        return storeList == null ? 0 : storeList.size();
    }

    public class StoreCatHolder extends RecyclerView.ViewHolder {

        AdapterStoreItemsBinding binding;

        public StoreCatHolder(AdapterStoreItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
