package com.example.lidertrade.admin.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.fragments.AdminProductAddFragment;
import com.example.lidertrade.admin.fragments.AdminOrderFragment;
import com.example.lidertrade.admin.fragments.AdminProductFragment;
import com.example.lidertrade.admin.fragments.AdminReportFragment;
import com.example.lidertrade.admin.fragments.AdminUserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminMainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.admin_main_activity);

        loadFragment(new AdminReportFragment());

        bottomNavigationView = findViewById(R.id.adminBottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.adminReportBottomBar);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.adminReportBottomBar:
                    loadFragment(new AdminReportFragment());
                    return true;
                case R.id.adminProductAddBottomBar:
                    loadFragment(new AdminProductAddFragment());
                    return true;
                case R.id.adminOrderBottomBar:
                    loadFragment(new AdminOrderFragment());
                    return true;
                case R.id.adminProductBottomBar:
                    loadFragment(new AdminProductFragment());
                    return true;
                case R.id.adminUserBottomBar:
                    loadFragment(new AdminUserFragment());
                    return true;
            }
            return true;
        });

    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tr = fm.beginTransaction();
        tr.replace(R.id.baseFragment, fragment);
        tr.commit();
    }


}