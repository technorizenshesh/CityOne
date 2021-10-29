package com.cityone.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.ImageView;

import com.braintreepayments.cardform.view.CardForm;
import com.cityone.R;
import com.cityone.databinding.ActivityPaymentMethodBinding;
import com.cityone.databinding.AddCartDialogBinding;
import com.redeban.payment.model.Card;

import java.util.HashMap;

public class PaymentMethodActivity extends AppCompatActivity {

    Context mContext = PaymentMethodActivity.this;
    ActivityPaymentMethodBinding binding;
    HashMap<String,String> param = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_payment_method);

        init();

    }

    private void init() {

        binding.ivAddCard.setOnClickListener(v -> {
            addCardDialog();
        });

    }

    private void addCardDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);

        AddCartDialogBinding addCartDialogBinding = DataBindingUtil.
                inflate(LayoutInflater.from(mContext),R.layout.add_cart_dialog,null,false);
        dialog.setContentView(addCartDialogBinding.getRoot());

        addCartDialogBinding.ivBack.setOnClickListener(v -> {
            finish();
        });

        addCartDialogBinding.btAdd.setOnClickListener(v -> {
            Card cardToSave = addCartDialogBinding.cardMulticarWedjet.getCard();
            if (cardToSave == null) {
                showAlert("Invalid card informations");
                // Toast.makeText(mContext,"Invalid card informations", Toast.LENGTH_SHORT).show();
                return;
            }
            addCardApiCall(cardToSave);
        });

        dialog.show();

    }

    private void addCardApiCall(Card cardToSave) {

    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }



}