package com.cityone.stores.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.cityone.R;
import com.cityone.stores.models.ModelMyStoreCart;
import com.cityone.utils.AppConstant;

import java.util.ArrayList;

public class AdapterOptionsEdit extends BaseAdapter {

    Context mContext;
    ArrayList<ModelMyStoreCart.Result.Extra_options_item> optionsList;
    String itemPrice = "0.0";

    public AdapterOptionsEdit(Context mContext, ArrayList<ModelMyStoreCart.Result.Extra_options_item> optionsList, String price) {
        this.mContext = mContext;
        this.optionsList = optionsList;
        this.itemPrice = price;
    }

    @Override
    public int getCount() {
        return optionsList == null ? 0 : optionsList.size();
    }

    @Override
    public Object getItem(int position) {
        return optionsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_items_options, parent, false);

        ModelMyStoreCart.Result.Extra_options_item data = optionsList.get(position);

        CheckBox cbTopping = view.findViewById(R.id.cbOption);
        TextView tvToppingName = view.findViewById(R.id.tvOptionName);
        TextView tvToppingPrice = view.findViewById(R.id.tvOptionPrice);

        tvToppingName.setText(data.getExtra_item());
        tvToppingPrice.setText(AppConstant.CURRENCY + " " + data.getExtra_price());

        if (data.getStatus().equals("true")) {
            cbTopping.setChecked(true);
        }

        cbTopping.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("sdfsfsdf", "Topping");

                int toppingTotal = 0;

                if (isChecked) {
                    optionsList.get(position).setChecked(true);
                    Log.e("sdfsfsdf", "cbTopping true = " + position);
                    // notifyDataSetChanged();
                } else {
                    optionsList.get(position).setChecked(false);
                    Log.e("sdfsfsdf", "cbTopping false = " + position);
                    // notifyDataSetChanged();
                }

                Log.e("sdfsfsdf", "optionsList.size() = " + optionsList.size());
                for (int i = 0; i < optionsList.size(); i++) {
                    Log.e("sdfsfsdf", "inside For = " + i);
                    Log.e("sdfsfsdf", "sdfsfsdf isChecked = " + optionsList.get(i).isChecked());
                    if (optionsList.get(i).isChecked()) {
                        toppingTotal = toppingTotal + Integer.parseInt(optionsList.get(i).getExtra_price());
                    }
                }

                AdapterMyCart.updatePrice(String.valueOf(toppingTotal), itemPrice);

            }

        });

        return view;

    }

}
