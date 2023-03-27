package com.cityone.shipping;

import android.content.Context;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.cityone.R;
import com.cityone.databinding.ActivityShippingHomeBinding;
import com.cityone.models.ModelLogin;
import com.cityone.utils.AppConstant;
import com.cityone.utils.SharedPref;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShippingHomeAct extends AppCompatActivity {
    ActivityShippingHomeBinding binding;
    private AppBarConfiguration appBarConfiguration;
    NavController navController;
    BottomNavigationView navView;

    ImageView ivMenu;
    Context mContext = ShippingHomeAct.this;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding =  DataBindingUtil.setContentView(this,R.layout.activity_shipping_home);
      //  drawerLayout = findViewById(R.id.main_drawer);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.shippingStart, R.id.shippingServices, R.id.shippingProfile)
                .setDrawerLayout(binding.mainDrawer)
                .build();
        navView = findViewById(R.id.nav_view);
        ivMenu = findViewById(R.id.ivMenu);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment1);
      //  navigationView = findViewById(R.id.main_sidebar);

        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.mainSidebar, navController);
        NavigationUI.setupWithNavController(binding.navView, navController);


        binding.ivMenu.setOnClickListener(v -> {
            binding.mainDrawer.openDrawer(GravityCompat.START);
        });


        binding.mainSidebar.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.msg) {
                    startActivity(new Intent(mContext,ShipChatListActivity.class));
                    binding.mainDrawer.closeDrawer(GravityCompat.START);
                }

                return false;
            }
        });


       View v =  binding.mainSidebar.getHeaderView(0);
       TextView tvName = v.findViewById(R.id.tvName);
       tvName.setText(modelLogin.getResult().getUser_name());

        CircleImageView ivProfile = v.findViewById(R.id.ivProfile);

        Picasso.get().load(modelLogin.getResult().getImage())
                .placeholder(R.drawable.default_profile_icon)
                .error(R.drawable.default_profile_icon)
                .into(ivProfile);

    }
}
