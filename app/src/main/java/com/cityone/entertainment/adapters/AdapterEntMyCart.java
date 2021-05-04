package com.cityone.entertainment.adapters;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.cityone.R;
import com.cityone.databinding.AdapterEntCartItemsBinding;
import com.cityone.databinding.AdapterMyCartStoreBinding;
import com.cityone.databinding.EntDetailDialogBinding;
import com.cityone.databinding.ItemDetailDialogBinding;
import com.cityone.entertainment.activities.EntCartActivity;
import com.cityone.entertainment.models.ModelEntMyCart;
import com.cityone.entertainment.models.UpdateCartEntInterf;
import com.cityone.models.ModelLogin;
import com.cityone.stores.activities.MyCartActivity;
import com.cityone.stores.adapters.AdapterMyCart;
import com.cityone.stores.models.ModelMyStoreCart;
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

public class AdapterEntMyCart extends RecyclerView.Adapter<AdapterEntMyCart.StoreCatHolder> {

    Context mContext;
    ArrayList<ModelEntMyCart.Result> itemsList;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    double itemTotal = 0.0;
    int itemCount = 0;
    UpdateCartEntInterf updateCartInterf;

    public AdapterEntMyCart(Context mContext, ArrayList<ModelEntMyCart.Result> itemsList) {
        this.mContext = mContext;
        this.updateCartInterf = (UpdateCartEntInterf) mContext;
        this.itemsList = itemsList;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    @NonNull
    @Override
    public AdapterEntMyCart.StoreCatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterEntCartItemsBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext),R.layout.adapter_ent_cart_items,parent,false);
        return new AdapterEntMyCart.StoreCatHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterEntMyCart.StoreCatHolder holder, int position) {

        ModelEntMyCart.Result data = itemsList.get(position);

        holder.binding.tvName.setText(data.getName());
        holder.binding.tvPrice.setText(AppConstant.DOLLAR + data.getPrice() +" x "+ data.getQuantity());

        holder.binding.ivEdit.setOnClickListener(v -> {
            editItemDialog(data);
        });

        holder.binding.ivDelete.setOnClickListener(v -> {
            Log.e("fsffddsfds","data.getId() = " + data.getId());
            deleteApiCall(data.getCart_id(),position);
        });

    }

    private void deleteApiCall(String id,int position) {
        ProjectUtil.showProgressDialog(mContext,true,mContext.getString(R.string.please_wait));

        HashMap<String,String> param = new HashMap<>();
        param.put("cart_id",id);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.deleteEntItemApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        Log.e("responseString","response = " + response);
                        Log.e("responseString","responseString = " + responseString);

                        itemsList.remove(position);
                        notifyDataSetChanged();

                        if(itemsList == null || itemsList.size() == 0) {
                            ((EntCartActivity)mContext).finish();
                        }

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

    private void editItemDialog(ModelEntMyCart.Result data) {

        itemTotal = 0.0;
        itemCount = 1;

        EntDetailDialogBinding dialogBinding = DataBindingUtil.
                inflate(LayoutInflater.from(mContext),R.layout.ent_detail_dialog,null,false);

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(dialogBinding.getRoot());

        Picasso.get().load(data.getEntertentment_image()).into(dialogBinding.ivImage);
        dialogBinding.tvTicketName.setText(data.getName());
        dialogBinding.tvPrice.setText(AppConstant.DOLLAR + data.getPrice());

        itemCount = Integer.parseInt(data.getQuantity());
        itemTotal = Double.parseDouble(data.getPrice()) * itemCount;
        dialogBinding.tvPrice.setText(AppConstant.DOLLAR + itemTotal);
        dialogBinding.tvQuantity.setText(String.valueOf(itemCount));
        dialogBinding.tvAddTicket.setText(mContext.getString(R.string.edit_ticket));

        dialogBinding.tvAddTicket.setOnClickListener(v -> {
            addToCartApi(dialog,data,String.valueOf(itemCount));
        });

        dialogBinding.ivPlus.setOnClickListener(v -> {
            itemCount++;
            dialogBinding.tvQuantity.setText(String.valueOf(itemCount));
            itemTotal = Double.parseDouble(data.getPrice()) * itemCount;
            dialogBinding.tvPrice.setText(AppConstant.DOLLAR + itemTotal);
        });

        dialogBinding.ivMinus.setOnClickListener(v -> {
            if(itemCount > 1) {
                itemCount--;
                dialogBinding.tvQuantity.setText(String.valueOf(itemCount));
                itemTotal = Double.parseDouble(data.getPrice()) * itemCount;
                dialogBinding.tvPrice.setText(AppConstant.DOLLAR + itemTotal);
            }
        });

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void addToCartApi(Dialog dialog,ModelEntMyCart.Result data, String quantity) {
        ProjectUtil.showProgressDialog(mContext,false,mContext.getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> param = new HashMap<>();
        param.put("user_id",modelLogin.getResult().getId());
        param.put("entertentment_id",data.getEntertentment_category_id());
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
                        updateCartInterf.onSuccess();
                        dialog.dismiss();
                    } else if(jsonObject.getString("status").equals("2")) {
                        Log.e("fsdfdsfdsf","responseString = " + responseString);
                        Log.e("fsdfdsfdsf","response = " + response);
                        new CustomAlertDialog(mContext).Message(mContext.getString(R.string.cart_remove_text_ent))
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
        return itemsList == null?0:itemsList.size();
    }

    public class StoreCatHolder extends RecyclerView.ViewHolder{

        AdapterEntCartItemsBinding binding;
        public StoreCatHolder(AdapterEntCartItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}

