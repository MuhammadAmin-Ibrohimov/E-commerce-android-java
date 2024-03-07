package com.example.lidertrade.admin.adapters;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.lidertrade.admin.fragments.AdminDebtFragmentDebtBook;
import com.example.lidertrade.admin.fragments.AdminDebtFragmentReturned;
import com.example.lidertrade.admin.fragments.AdminOrderFragmentCompleted;
import com.example.lidertrade.admin.fragments.AdminOrderFragmentCredit;
import com.example.lidertrade.admin.fragments.AdminOrderFragmentUncompleted;
import com.example.lidertrade.admin.fragments.AdminProductFragmentProductAdd;
import com.example.lidertrade.admin.fragments.AdminProductFragmentProductList;
import com.example.lidertrade.admin.fragments.AdminUserFragmentAddUser;
import com.example.lidertrade.admin.fragments.AdminUserFragmentUserList;


public class AdminDebtFragmentAdapter extends FragmentStateAdapter {


    public AdminDebtFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AdminProductFragmentProductList();
            case 1:
                return new AdminProductFragmentProductAdd();
            case 2:
                return new AdminDebtFragmentReturned();
            default:
                return new AdminProductFragmentProductList();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

