package com.cityone.entertainment.movies.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cityone.R;
import com.cityone.activities.LoginActivity;
import com.cityone.activities.PaymentMethodActivity;
import com.cityone.databinding.ActivityAvailSeatsBinding;
import com.cityone.entertainment.movies.adapters.AdapterClassicSeats;
import com.cityone.entertainment.movies.adapters.AdapterExclutiveSeats;
import com.cityone.entertainment.movies.adapters.AdapterNormalSeats;

import java.util.ArrayList;

public class AvailSeatsActivity extends AppCompatActivity {

    Context mContext = AvailSeatsActivity.this;
    ActivityAvailSeatsBinding binding;
    ArrayList<Model> exclList;
    ArrayList<Model> normalList;
    ArrayList<Model> classicList;
    AdapterExclutiveSeats adapterExclutiveSeats;
    AdapterExclutiveSeats adapterNormalSeats;
    AdapterExclutiveSeats adapterClassicSeats;

    public void successExl(int pso) {
      // Toast.makeText(mContext, ""+exclList.get(pso).isSelected(), Toast.LENGTH_SHORT).show();
      exclList.get(pso).setSelected(!exclList.get(pso).isSelected());
      for (int i=0;i<normalList.size();i++){
          normalList.get(i).setSelected(false);
      }
        for (int i=0;i<classicList.size();i++){
            classicList.get(i).setSelected(false);
        }
        adapterExclutiveSeats.notifyDataSetChanged();
        adapterNormalSeats.notifyDataSetChanged();
        adapterClassicSeats.notifyDataSetChanged();
    }

    public void successNor(int pso) {
        normalList.get(pso).setSelected(!normalList.get(pso).isSelected);
        for (int i=0;i<exclList.size();i++) {
            exclList.get(i).setSelected(false);
        }
        for (int i=0;i<classicList.size();i++){
            classicList.get(i).setSelected(false);
        }
        adapterExclutiveSeats.notifyDataSetChanged();
        adapterNormalSeats.notifyDataSetChanged();
        adapterClassicSeats.notifyDataSetChanged();
    }

    public void successClic(int pso) {
        classicList.get(pso).setSelected(!classicList.get(pso).isSelected);
        for (int i=0;i<exclList.size();i++){
            exclList.get(i).setSelected(false);
        }
        for (int i=0;i<normalList.size();i++){
            normalList.get(i).setSelected(false);
        }
        adapterExclutiveSeats.notifyDataSetChanged();
        adapterNormalSeats.notifyDataSetChanged();
        adapterClassicSeats.notifyDataSetChanged();
    }

    public class Model{
    int sets;
    boolean isSelected;

    public Model(int sets) {
        this.sets = sets;
    }

        public Model(int sets, boolean isSelected) {
            this.sets = sets;
            this.isSelected = isSelected;
        }

        public String getSets() {
        return ""+sets;
    }

    public void setSets(Integer sets) {
        this.sets = sets;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_avail_seats);

        init();

    }

    private void init() {

        exclList = new ArrayList<>();
        classicList = new ArrayList<>();
        normalList = new ArrayList<>();

        for (int i=0;i<35;i++) {
            exclList.add(new Model(i + 1, false));
            classicList.add(new Model(i + 1, false));
            normalList.add(new Model(i + 1, false));
        }

        adapterExclutiveSeats = new AdapterExclutiveSeats(mContext,exclList,this::successExl);
        binding.rvExclutive.setAdapter(adapterExclutiveSeats);

        adapterNormalSeats = new AdapterExclutiveSeats(mContext,normalList,this::successNor);
        binding.rvNormal.setAdapter(adapterNormalSeats);

        adapterClassicSeats = new AdapterExclutiveSeats(mContext,classicList,this::successClic);
        binding.rvClassic.setAdapter(adapterClassicSeats);

        binding.btDone.setOnClickListener(v -> {
          // Log.e("SelectedSets","===>"+builder.toString().substring(0,builder.length()-1));
          startActivity(new Intent(mContext,PaymentMethodActivity.class));
        });

    }


}