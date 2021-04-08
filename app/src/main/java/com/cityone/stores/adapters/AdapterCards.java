package com.cityone.stores.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cityone.R;
import com.cityone.databinding.AdapterStoresBinding;
import com.cityone.databinding.AdaptersCardsBinding;
import com.cityone.stores.activities.StoreDetailsActivity;
import com.cityone.stores.activities.StorePaymentActivity;
import com.cityone.stores.models.ModelCards;
import com.cityone.stores.models.ModelStores;
import com.cityone.utils.ProjectUtil;
import com.squareup.picasso.Picasso;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdapterCards extends RecyclerView.Adapter<AdapterCards.StoreHolder> {

    Context mContext;
    ArrayList<ModelCards.Result> storeList;

    public AdapterCards(Context mContext, ArrayList<ModelCards.Result> storeList) {
        this.mContext = mContext;
        this.storeList = storeList;
    }

    @NonNull
    @Override
    public AdapterCards.StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdaptersCardsBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.adapters_cards,parent,false);
        return new AdapterCards.StoreHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCards.StoreHolder holder, int position) {
        ModelCards.Result data = storeList.get(position);

        holder.binding.card.setCardNumber(data.getCard_number());
        holder.binding.card.setExpiryDate(data.getExpiry_date());
        holder.binding.card.setCardName(data.getHolder_name());

        holder.binding.card.setOnClickListener(v -> {
            Card.Builder card = new Card.Builder(data.getCard_number(),
                    Integer.valueOf(data.getExpiry_month()),
                    Integer.valueOf(data.getExpiry_date().split("/")[1]),
                    data.getCvc_code());

            Stripe stripe = new Stripe(mContext,mContext.getString(R.string.stripe_test_key));

            ProjectUtil.showProgressDialog(mContext, false,mContext.getString(R.string.please_wait));
            stripe.createCardToken(
                    card.build(), new ApiResultCallback<Token>() {
                        @Override
                        public void onSuccess(Token token) {
                            ProjectUtil.pauseProgressDialog();
                            Log.e("stripeToken","token = " + token.getId());
                            ((StorePaymentActivity)mContext).callBookingApiFromAdapter(token.getId());
                        }

                        @Override
                        public void onError(@NotNull Exception e) {
                            ProjectUtil.pauseProgressDialog();
                        }
                    });

        });

    }

    @Override
    public int getItemCount() {
        return storeList == null?0:storeList.size();
    }

    public class StoreHolder extends RecyclerView.ViewHolder{

        AdaptersCardsBinding binding;

        public StoreHolder(AdaptersCardsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}

