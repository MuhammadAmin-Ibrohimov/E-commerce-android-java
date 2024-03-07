package com.example.lidertrade.deliverer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import com.example.lidertrade.R;
import com.example.lidertrade.deliverer.fragments.DelivererCreditFragment;
import com.example.lidertrade.deliverer.fragments.DelivererPendingFragment;
import com.example.lidertrade.deliverer.fragments.DelivererSendingFragment;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class DelivererMainActivity extends AppCompatActivity {
    Toolbar toolbar;
    CardView deliverer_toolbar_notification_card;
    SmoothBottomBar smoothBottomBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.deliverer_main_activity);
        fragmentReplace(new DelivererPendingFragment());
        smoothBottomBar = findViewById(R.id.bottomBar);
        smoothBottomBar.setItemTextSize(24);
        smoothBottomBar.setItemIconSize(36);
        toolbar = findViewById(R.id.delivererToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        deliverer_toolbar_notification_card = findViewById(R.id.deliverer_toolbar_map_card);
        deliverer_toolbar_notification_card.setOnClickListener(view -> {
            Intent intent = new Intent(DelivererMainActivity.this, DelivererMapActivity.class);
            startActivity(intent);
        });

        smoothBottomBar.setOnItemSelectedListener((OnItemSelectedListener) i -> {
            if (i==0) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, new DelivererPendingFragment());
                fragmentTransaction.commit();
            }
            if (i==1) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, new DelivererSendingFragment());
                fragmentTransaction.commit();
            }
            if (i==2) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, new DelivererCreditFragment());
                fragmentTransaction.commit();
            }
            return true;
        });
    }

    private void fragmentReplace(Fragment orderPendingListFragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, orderPendingListFragment);
        transaction.commit();
    }
}