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
import com.cityone.entertainment.movies.models.ModelTheaterDetails;
import com.cityone.utils.AppConstant;

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
    ModelTheaterDetails modelTheaterDetails;
    String seatType = null;

    public void successExl(int pso) {
        seatType = AppConstant.EXCLUSIVE;
      // Toast.makeText(mContext,""+exclList.get(pso).isSelected(),Toast.LENGTH_SHORT).show();
      exclList.get(pso).setSelected(!exclList.get(pso).isSelected());
      for (int i=0;i<normalList.size();i++) {
          normalList.get(i).setSelected(false);
      }
        for (int i=0;i<classicList.size();i++) {
            classicList.get(i).setSelected(false);
        }
        adapterExclutiveSeats.notifyDataSetChanged();
        adapterNormalSeats.notifyDataSetChanged();
        adapterClassicSeats.notifyDataSetChanged();
    }

    public void successNor(int pso) {
        seatType = AppConstant.NORMAL;
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
        seatType = AppConstant.CLASSIC;
        classicList.get(pso).setSelected(!classicList.get(pso).isSelected);
        for (int i=0;i<exclList.size();i++) {
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
        modelTheaterDetails = (ModelTheaterDetails) getIntent().getSerializableExtra("data");
        init();
    }

    private void init() {

        exclList = new ArrayList<>();
        classicList = new ArrayList<>();
        normalList = new ArrayList<>();

        binding.tvExclusive.setText("Exclusive: $"+modelTheaterDetails.getResult().getExclusive_price());
        binding.tvNormal.setText("Normal: $"+modelTheaterDetails.getResult().getNormal_price());
        binding.tvClassic.setText("Classic: $"+modelTheaterDetails.getResult().getClassic_price());

        int exSeats = Integer.parseInt(modelTheaterDetails.getResult().getExclusive_seat());
        int normSeats = Integer.parseInt(modelTheaterDetails.getResult().getNormal_seat());
        int classicSeats = Integer.parseInt(modelTheaterDetails.getResult().getClassic_seat());

        for (int i=0;i<exSeats;i++)
            exclList.add(new Model(i + 1, false));
        for (int i=0;i<normSeats;i++)
            normalList.add(new Model(i + 1, false));
        for (int i=0;i<classicSeats;i++)
            classicList.add(new Model(i + 1, false));

        adapterExclutiveSeats = new AdapterExclutiveSeats(mContext,exclList,this::successExl);
        binding.rvExclutive.setAdapter(adapterExclutiveSeats);

        adapterNormalSeats = new AdapterExclutiveSeats(mContext,normalList,this::successNor);
        binding.rvNormal.setAdapter(adapterNormalSeats);

        adapterClassicSeats = new AdapterExclutiveSeats(mContext,classicList,this::successClic);
        binding.rvClassic.setAdapter(adapterClassicSeats);

        binding.btDone.setOnClickListener(v -> {
            StringBuilder builder = new StringBuilder();
            if(seatType.equals(AppConstant.EXCLUSIVE)){
                for(int i=0;i<exclList.size();i++) if(exclList.get(i).isSelected()) builder.append((i+1)+",");
            } else if(seatType.equals(AppConstant.NORMAL)) {
                for(int i=0;i<normalList.size();i++) if(normalList.get(i).isSelected()) builder.append((i+1)+",");
            } else if(seatType.equals(AppConstant.CLASSIC)) {
                for(int i=0;i<classicList.size();i++) if(classicList.get(i).isSelected()) builder.append((i+1)+",");
            }

            Log.e("SelectedSets","SelectedSets = " + builder);

            Log.e("SelectedSets","===>" + builder.toString().substring(0,builder.length()-1));
            // startActivity(new Intent(mContext,PaymentMethodActivity.class));

        });

    }


}