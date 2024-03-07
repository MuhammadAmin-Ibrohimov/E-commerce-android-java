package com.example.lidertrade.admin.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.lidertrade.admin.fragments.AdminProductFragmentCategory;
import com.example.lidertrade.admin.fragments.AdminProductFragmentProductAdd;
import com.example.lidertrade.admin.fragments.AdminProductFragmentBrandList;
import com.example.lidertrade.admin.fragments.AdminProductFragmentProductList;
import com.example.lidertrade.admin.fragments.AdminProductFragmentSubCategory;

public class AdminProductFragmentAdapter extends FragmentStateAdapter {


    public AdminProductFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AdminProductFragmentBrandList();
            case 1:
                return new AdminProductFragmentCategory();
            case 2:
                return  new AdminProductFragmentSubCategory();


        }
        return new AdminProductFragmentProductAdd();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
