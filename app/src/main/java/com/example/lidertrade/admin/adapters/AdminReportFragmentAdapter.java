package com.example.lidertrade.admin.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.lidertrade.admin.fragments.AdminProductFragmentCategory;
import com.example.lidertrade.admin.fragments.AdminProductFragmentProductAdd;
import com.example.lidertrade.admin.fragments.AdminProductFragmentBrandList;
import com.example.lidertrade.admin.fragments.AdminProductFragmentSubCategory;
import com.example.lidertrade.admin.fragments.AdminReportFragmentProducts;
import com.example.lidertrade.admin.fragments.AdminReportFragmentSellers;
import com.example.lidertrade.admin.fragments.AdminReportFragmentTotal;

public class AdminReportFragmentAdapter extends FragmentStateAdapter {


    public AdminReportFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return  new AdminReportFragmentTotal();
            case 1:
                return new AdminReportFragmentProducts();
            case 2:
                return new AdminReportFragmentSellers();


        }
        return new AdminProductFragmentProductAdd();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
