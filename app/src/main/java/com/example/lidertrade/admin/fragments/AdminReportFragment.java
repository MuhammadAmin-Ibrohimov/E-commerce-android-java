package com.example.lidertrade.admin.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.adapters.AdminProductFragmentAdapter;
import com.example.lidertrade.admin.adapters.AdminReportFragmentAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class AdminReportFragment extends Fragment {
    public AdminReportFragment() {
    }

    View v;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    AdminReportFragmentAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.admin_report_fragment, container, false);

        tabLayout = (TabLayout) v.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.analysis));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.product));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.users));


        viewPager2 = (ViewPager2) v.findViewById(R.id.viewPager);
        adapter = new AdminReportFragmentAdapter(requireActivity());
        viewPager2.setAdapter(adapter);

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
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });



        return v;
    }
}