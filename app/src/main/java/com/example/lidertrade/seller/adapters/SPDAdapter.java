package com.example.lidertrade.seller.adapters;
        import android.os.Bundle;

        import androidx.annotation.NonNull;
        import androidx.fragment.app.Fragment;
        import androidx.fragment.app.FragmentActivity;
        import androidx.viewpager2.adapter.FragmentStateAdapter;
        import com.example.lidertrade.seller.fragments.SPDCreditFragment;
        import com.example.lidertrade.seller.fragments.SPDDescriptionFragment;
        import com.example.lidertrade.seller.fragments.SPDSpecificationFragment;


public class SPDAdapter extends FragmentStateAdapter {
    String prodId;

    public void setData(String productId) {
        prodId = productId;
    }

    public SPDAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                SPDSpecificationFragment fragment1 = new SPDSpecificationFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putString("productId", prodId);
                fragment1.setArguments(bundle1);
                return  fragment1;
            case 1:
                SPDCreditFragment fragment2 = new SPDCreditFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putString("productId", prodId);
                fragment2.setArguments(bundle2);
                return  fragment2;
            default:
                SPDSpecificationFragment fragment4 = new SPDSpecificationFragment();
                Bundle bundle4 = new Bundle();
                bundle4.putString("productId", prodId);
                fragment4.setArguments(bundle4);
                return  fragment4;

        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }



}

