package com.cityone.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.ImageView;

import com.braintreepayments.cardform.view.CardForm;
import com.cityone.R;
import com.cityone.databinding.ActivityPaymentMethodBinding;
import com.cityone.databinding.AddCartDialogBinding;

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

        addCartDialogBinding.cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .cardholderName(CardForm.FIELD_REQUIRED)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                .setup(PaymentMethodActivity.this);

        addCartDialogBinding.cardForm.getCvvEditText()
                .setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        addCartDialogBinding.ivBack.setOnClickListener(v -> {
            finish();
        });

        addCartDialogBinding.btAdd.setOnClickListener(v -> {
            finish();
        });

        dialog.show();

    }


}