package com.cityone.stores.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cityone.R;
import com.cityone.stores.models.ModelStoreItemFilterCat;

import java.util.ArrayList;

public class AdapterStoreItemCatFilter extends BaseAdapter {

    Context mContext;
    ArrayList<ModelStoreItemFilterCat.Result> catList;

    public AdapterStoreItemCatFilter(Context mContext, ArrayList<ModelStoreItemFilterCat.Result> catList) {
        this.mContext = mContext;
        this.catList = catList;
    }

    @Override
    public int getCount() {
        return catList == null?0:catList.size();
    }

    @Override
    public Object getItem(int position) {
        return catList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_cat,parent,false);

        TextView tvName = view.findViewById(R.id.tvName);
        tvName.setText(catList.get(position).getName());

        return view;
    }

}
