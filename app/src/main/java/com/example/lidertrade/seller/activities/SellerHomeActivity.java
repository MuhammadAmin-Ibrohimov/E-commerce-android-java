package com.example.lidertrade.seller.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ThemedSpinnerAdapter;

import com.example.lidertrade.R;
import com.example.lidertrade.seller.fragments.SellerCartFragment;
import com.example.lidertrade.seller.fragments.SellerCategoryFragment;
import com.example.lidertrade.seller.fragments.SellerHomeFragment;
import com.example.lidertrade.seller.fragments.SellerProfileFragment;
import com.example.lidertrade.seller.fragments.SellerSearchFragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

public class SellerHomeActivity extends AppCompatActivity {
    int tProd;
    String sellerId;
    FirebaseUser user;
    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    CardView seller_toolbar_cart_card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.seller_home_activity);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){assert user != null;sellerId = user.getUid();}else{sellerId = "null";}
        toolbar = findViewById(R.id.sellerToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        seller_toolbar_cart_card = findViewById(R.id.seller_toolbar_cart_card);
        loadFragment(new SellerHomeFragment());
        bottomNavigationView = findViewById(R.id.sellerBottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.seller_home_fragment_menu);
        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.seller_cart_fragment_menu);
//        bottomNavigationView.removeBadge(R.id.seller_cart_fragment_menu);
        FirebaseFirestore.getInstance().collection("ShoppingCart").whereEqualTo("sellerId", sellerId).addSnapshotListener((value, error) -> {
            if (!value.isEmpty() || value.size() != 0) {
                tProd = 0;
                List<DocumentSnapshot> list = value.getDocuments();
                for (DocumentSnapshot d : list) {
                    tProd += Integer.parseInt(Objects.requireNonNull(d.get("prodQuantity")).toString());
                }
                if (tProd != 0){
                    badge.setVisible(true);
                    badge.setNumber(tProd);
                    badge.setBackgroundColor(Color.RED);
                    badge.setBadgeTextColor(Color.WHITE);
                }else{
                    badge.clearNumber();
                    badge.setVisible(false);
                }
            }
            else{
                badge.clearNumber();
                badge.setVisible(false);
            }
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.seller_home_fragment_menu:
                    loadFragment(new SellerHomeFragment());
                    return true;
                case R.id.seller_category_fragment_menu:
                    loadFragment(new SellerCategoryFragment());
                    return true;
                case R.id.seller_cart_fragment_menu:
                    loadFragment(new SellerCartFragment());
                    return true;
                case R.id.seller_search_fragment_menu:
                    loadFragment(new SellerSearchFragment());
                    return true;
                case R.id.seller_profile_fragment_menu:
                    loadFragment(new SellerProfileFragment());
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