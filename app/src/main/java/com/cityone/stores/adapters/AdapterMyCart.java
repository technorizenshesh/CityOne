package com.cityone.stores.adapters;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
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
import com.cityone.databinding.AdapterMyCartStoreBinding;
import com.cityone.databinding.ItemDetailDialogBinding;
import com.cityone.models.ModelLogin;
import com.cityone.stores.activities.MyCartActivity;
import com.cityone.stores.models.ModelMyStoreCart;
import com.cityone.utils.Api;
import com.cityone.utils.ApiFactory;
import com.cityone.utils.AppConstant;
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

public class AdapterMyCart extends RecyclerView.Adapter<AdapterMyCart.StoreCatHolder> {

    Context mContext;
    ArrayList<ModelMyStoreCart.Result> itemsList;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    static double itemTotal = 0.0;
    static int itemCount = 0;
    static Dialog dialog;
    static int toppingTotal;
    boolean isOption;
    Integer finalItemTotal;

    public AdapterMyCart(Context mContext, ArrayList<ModelMyStoreCart.Result> itemsList, Integer finalItemTotal) {
        this.mContext = mContext;
        this.itemsList = itemsList;
        this.finalItemTotal = finalItemTotal;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    @NonNull
    @Override
    public AdapterMyCart.StoreCatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterMyCartStoreBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.adapter_my_cart_store, parent, false);
        return new AdapterMyCart.StoreCatHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMyCart.StoreCatHolder holder, int position) {

        ModelMyStoreCart.Result data = itemsList.get(position);

        holder.binding.tvName.setText(data.getName());
        holder.binding.tvPrice.setText(AppConstant.DOLLAR + data.getAmount() + " x " + data.getQuantity());
        Picasso.get().load(data.getImage()).into(holder.binding.ivImage);

        itemTotal = finalItemTotal;

        // holder.binding.tvExtras.setText("Akash\nJayant\nharshit");

        String finalExtras = "";

        for (int i = 0; i < data.getExtra_options_item().size(); i++) {
            if (data.getExtra_options_item().get(i).getStatus().equals("true")) {
                Log.e("getExtra_options_item", "getExtra_options_item = " + i + " = ye true hai");
                Log.e("getExtra_options_item", "getExtra_options_item = " + i + " = " + data.getExtra_options_item().get(i).getExtra_item());
                finalExtras = finalExtras + data.getExtra_options_item().get(i).getExtra_item() + " " +
                        data.getExtra_options_item().get(i).getExtra_price() + "/-\n";
            }
        }
        if(finalExtras.equals("")) holder.binding.llExtra.setVisibility(View.GONE);
      else {
            holder.binding.llExtra.setVisibility(View.VISIBLE);
          holder.binding.tvExtras.setText(finalExtras);
        }

//        if (data.getDiscount() == null || data.getDiscount().equals("") || data.getDiscount().equals("0")) {
//            holder.binding.tvDiscount.setVisibility(View.GONE);
//        } else {
//            holder.binding.tvDiscount.setVisibility(View.VISIBLE);
//            holder.binding.tvDiscount.setText(data.getDiscount() + "% Off");
//        }

        holder.binding.ivEdit.setOnClickListener(v -> {
            editItemDialog(data);
        });

        holder.binding.ivDelete.setOnClickListener(v -> {
            Log.e("fsffddsfds", "data.getId() = " + data.getId());
            deleteApiCall(data.getItem_id(), position);
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

    private void deleteApiCall(String id, int position) {
        ProjectUtil.showProgressDialog(mContext, true, mContext.getString(R.string.please_wait));

        HashMap<String, String> param = new HashMap<>();
        param.put("cart_id", id);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.deleteStoreItemApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.getString("status").equals("1")) {

                        Log.e("responseString", "response = " + response);
                        Log.e("responseString", "responseString = " + responseString);

                        itemsList.remove(position);
                        notifyDataSetChanged();
                        ((MyCartActivity) mContext).updateCartId(itemsList);

                        if (itemsList == null || itemsList.size() == 0) {
                            ((MyCartActivity) mContext).finish();
                        }

                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });
    }

    private void editItemDialog(ModelMyStoreCart.Result data) {

        Log.e("asdlhjkjasdkljsla", "finalItemTotal = " + finalItemTotal);

        itemTotal = finalItemTotal;
        itemCount = Integer.parseInt(data.getQuantity());

        ItemDetailDialogBinding dialogBinding = DataBindingUtil.
                inflate(LayoutInflater.from(mContext), R.layout.item_detail_dialog, null, false);

        dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(dialogBinding.getRoot());

        for (int i = 0; i < data.getExtra_options_item().size(); i++) {
            if (data.getExtra_options_item().get(i).getStatus().equals("true")) {
                data.getExtra_options_item().get(i).setChecked(true);
            }
        }

        Picasso.get().load(data.getImage()).into(dialogBinding.ivItemImage);
        dialogBinding.tvItemName.setText(data.getName());
        dialogBinding.tvQuantity.setText(data.getQuantity());
        dialogBinding.tvPrice.setText(AppConstant.DOLLAR + finalItemTotal);

        AdapterOptionsEdit adapterOptions = new AdapterOptionsEdit(mContext, data.getExtra_options_item(), data.getAmount());
        dialogBinding.optionListView.setAdapter(adapterOptions);

//      itemCount = Integer.parseInt(data.getQuantity());
//      itemTotal = Double.parseDouble(data.getAmount()) * itemCount;
        dialogBinding.tvPrice.setText(AppConstant.DOLLAR + itemTotal);
        dialogBinding.tvQuantity.setText(String.valueOf(itemCount));
        dialogBinding.tvAddItem.setText(mContext.getString(R.string.edit_item));

        dialogBinding.tvAddItem.setOnClickListener(v -> {
            addToCartApi(dialog, data, String.valueOf(itemCount), getSelectedToppingIds(data));
        });

        dialogBinding.ivPlus.setOnClickListener(v -> {
            itemCount++;
            dialogBinding.tvQuantity.setText(String.valueOf(itemCount));
            itemTotal = Double.parseDouble(data.getAmount()) * itemCount;
            Log.e("gdfgdfgfdgdf", "itemTotal upper = " + itemTotal);
            itemTotal = itemTotal + (getToppingTotal(data) * itemCount);
            Log.e("gdfgdfgfdgdf", "itemTotal neeche = " + itemTotal);
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

    private int getToppingTotal(ModelMyStoreCart.Result data) {
        toppingTotal = 0;
        if (data.getExtra_options_item() == null || data.getExtra_options_item().size() == 0) {
            return 0;
        } else {
            for (int i = 0; i < data.getExtra_options_item().size(); i++) {
                Log.e("sdfsfsdf", "inside For = " + i);
                Log.e("sdfsfsdf", "sdfsfsdf isChecked = " + data.getExtra_options_item().get(i).isChecked());
                if (data.getExtra_options_item().get(i).isChecked()) {
                    toppingTotal = toppingTotal + Integer.parseInt(data.getExtra_options_item().get(i).getExtra_price());
                }
            }

            return toppingTotal;

        }

    }

    private String getSelectedToppingIds(ModelMyStoreCart.Result data) {
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

    private void addToCartApi(Dialog dialog, ModelMyStoreCart.Result data, String quantity, String optionIds) {
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
                        ((MyCartActivity) mContext).updateCartCount();
                        dialog.dismiss();
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
        return itemsList == null ? 0 : itemsList.size();
    }

    public class StoreCatHolder extends RecyclerView.ViewHolder {

        AdapterMyCartStoreBinding binding;

        public StoreCatHolder(AdapterMyCartStoreBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}

