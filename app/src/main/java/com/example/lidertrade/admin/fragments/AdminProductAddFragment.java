package com.example.lidertrade.admin.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.adapters.AdminDebtFragmentAdapter;
import com.google.android.material.tabs.TabLayout;

public class AdminProductAddFragment extends Fragment {

    View v;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    AdminDebtFragmentAdapter adminUserFragmentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.admin_order_fragment, container, false);
        tabLayout = (TabLayout) v.findViewById(R.id.aOFTabLayout);
        viewPager2 = (ViewPager2) v.findViewById(R.id.aOFViewPager);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.product));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.baseline_add_to_queue_24));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.returned_product));


        adminUserFragmentAdapter = (AdminDebtFragmentAdapter) new AdminDebtFragmentAdapter(requireActivity());
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