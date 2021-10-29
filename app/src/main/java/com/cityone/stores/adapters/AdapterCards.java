package com.cityone.stores.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.cityone.R;
import com.cityone.databinding.AdapterStoresBinding;
import com.cityone.databinding.AdaptersCardsBinding;
import com.cityone.models.ModelLogin;
import com.cityone.parentmodels.ModelPayCards;
import com.cityone.parentmodels.ModelPayCardsPro;
import com.cityone.stores.activities.StoreDetailsActivity;
import com.cityone.stores.activities.StorePaymentActivity;
import com.cityone.stores.models.ModelCards;
import com.cityone.stores.models.ModelStores;
import com.cityone.utils.AppConstant;
import com.cityone.utils.ProjectUtil;
import com.cityone.utils.SharedPref;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterCards extends RecyclerView.Adapter<AdapterCards.StoreHolder> {

    Context mContext;
    ArrayList<ModelPayCardsPro.Cards> cardsList;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    String type = "";

    public AdapterCards(Context mContext, ArrayList<ModelPayCardsPro.Cards> cardsList,String type) {
        this.mContext = mContext;
        this.cardsList = cardsList;
        this.type = type;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    @NonNull
    @Override
    public AdapterCards.StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdaptersCardsBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext),R.layout.adapters_cards,parent,false);
        return new AdapterCards.StoreHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCards.StoreHolder holder, int position) {

        ModelPayCardsPro.Cards data = cardsList.get(position);

        holder.binding.card.setCardNumber(data.getBin());
        holder.binding.card.setExpiryDate(data.getExpiry_month() +
                "/" + data.getExpiry_year());
        holder.binding.card.setCardName(data.getHolder_name());

        holder.binding.card.setOnClickListener(v -> {
            if(type.equals("store")) {
                ((StorePaymentActivity)mContext).callBookingApiFromAdapter(data.getToken());
            }
        });

        holder.binding.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ShowDeleteAlert(position,data.getToken());
                return true;
            }
        });

    }

    private void ShowDeleteAlert(int position,String token) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getString(R.string.delete_card_text));
        builder.setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCardApi(position,token);
            }
        }).setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    private void deleteCardApi(int position,String token) {
        ProjectUtil.showProgressDialog(mContext,false,mContext.getString(R.string.please_wait));

//        HashMap<String,String> param = new HashMap<>();
//        param.put("user_id",modelLogin.getResult().getId());

      //  https://equipmeapp.co.nz/CityOne/delete_card.php?token=8927240109141827007&user_id=3

        AndroidNetworking.post(AppConstant.PAY_DELETE_CARD + "token="+token+"&user_id=" + modelLogin.getResult().getId())
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        ProjectUtil.pauseProgressDialog();
                        try {
                            String stringRes = response;
                            JSONObject jsonObject = new JSONObject(stringRes);
                            if(jsonObject.getString("status").equals("1")) {
                                Log.e("onResponseonResponse","response = " + response);
                                cardsList.remove(position);
                                notifyDataSetChanged();
                            } else {}
                        } catch (Exception e) {}
                    }

                    @Override
                    public void onError(ANError anError) {
                        ProjectUtil.pauseProgressDialog();
                    }
                });

    }

    @Override
    public int getItemCount() {
        return cardsList == null?0:cardsList.size();
    }

    public class StoreHolder extends RecyclerView.ViewHolder{

        AdaptersCardsBinding binding;

        public StoreHolder(AdaptersCardsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}

