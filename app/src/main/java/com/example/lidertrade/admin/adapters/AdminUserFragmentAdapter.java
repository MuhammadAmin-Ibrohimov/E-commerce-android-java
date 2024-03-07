package com.example.lidertrade.admin.adapters;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.lidertrade.admin.fragments.AdminUserFragmentAddUser;
import com.example.lidertrade.admin.fragments.AdminUserFragmentUserList;


public class AdminUserFragmentAdapter extends FragmentStateAdapter {


    public AdminUserFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return  new AdminUserFragmentUserList();
            case 1:
                return new AdminUserFragmentAddUser();
            default:
                return new AdminUserFragmentUserList();

        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

