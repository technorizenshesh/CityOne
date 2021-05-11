package com.cityone.fragments;
import android.content.Context;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cityone.R;
import com.cityone.databinding.FragmentFavoriteBinding;

public class FavoriteFragment extends Fragment {

    Context mContext;
    FragmentFavoriteBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_favorite, container, false);

        init();

        // Inflate the layout for this fragment
        return binding.getRoot();

    }

    private void init() {

        binding.btFood.setOnClickListener(v -> {
            binding.btGrocery.setBackgroundResource(R.drawable.gray_back_10);
            binding.btPharmacy.setBackgroundResource(R.drawable.gray_back_10);
            binding.btFood.setBackgroundResource(R.drawable.orange_back_10);
        });

        binding.btGrocery.setOnClickListener(v -> {
            binding.btFood.setBackgroundResource(R.drawable.gray_back_10);
            binding.btPharmacy.setBackgroundResource(R.drawable.gray_back_10);
            binding.btGrocery.setBackgroundResource(R.drawable.orange_back_10);
        });

        binding.btPharmacy.setOnClickListener(v -> {
            binding.btGrocery.setBackgroundResource(R.drawable.gray_back_10);
            binding.btPharmacy.setBackgroundResource(R.drawable.orange_back_10);
            binding.btFood.setBackgroundResource(R.drawable.gray_back_10);
        });

    }


}