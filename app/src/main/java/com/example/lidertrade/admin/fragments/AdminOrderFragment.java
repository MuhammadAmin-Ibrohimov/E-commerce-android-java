package com.example.lidertrade.admin.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.adapters.AdminOrderFragmentAdapter;
import com.example.lidertrade.admin.adapters.AdminUserFragmentAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class AdminOrderFragment extends Fragment {

    View v;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    AdminOrderFragmentAdapter adminUserFragmentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.admin_order_fragment, container, false);
        tabLayout = (TabLayout) v.findViewById(R.id.aOFTabLayout);
        viewPager2 = (ViewPager2) v.findViewById(R.id.aOFViewPager);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.newcomer));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.history_order));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.atm));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.debt));


        adminUserFragmentAdapter = (AdminOrderFragmentAdapter) new AdminOrderFragmentAdapter(requireActivity());
        viewPager2.setAdapter(adminUserFragmentAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position).select();
            }
        });

        return v;
    }
}