package com.example.lidertrade.seller.activities;


import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.models.AdminSubCategoryModel;
import com.example.lidertrade.seller.adapters.SellerSubcategoryAdapter;
import com.example.lidertrade.seller.models.SellerCategoryModel;
import com.example.lidertrade.seller.models.SellerSubcategoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SellerSubcategoryActivity extends AppCompatActivity {
    TextView subcategoryCategoryId, subcategory_not_found, badge_notification_1;
    Toolbar toolbar;

    ShapeableImageView seller_inner_toolbar_logo;
    CardView seller_toolbar_cart_card;
    private FirebaseFirestore db;
    FirebaseUser user;
    String sellerId;
    private CollectionReference subcategoryBase;
    NotificationBadge badge;
    private RecyclerView subcategoryRecycler;
    private ArrayList<SellerSubcategoryModel> subcategoryModel;
    private SellerSubcategoryAdapter subcategoryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.seller_subcategory_activity);

        final SellerCategoryModel category_modal = (SellerCategoryModel) getIntent().getSerializableExtra("category_modal");

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){assert user != null;sellerId = user.getUid();}else{sellerId = "null";}
        // initializing our variables.
        subcategoryRecycler = findViewById(R.id.sellerSubcategoryRecycler);
        subcategoryCategoryId = findViewById(R.id.subcategoryCategoryId);
        subcategoryCategoryId.setText(category_modal.getCategoryName().toString());
        badge_notification_1 = findViewById(R.id.badge_notification_1);
        toolbar = findViewById(R.id.sellerInnerToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // firestore and getting its instance.
        db = FirebaseFirestore.getInstance();
        subcategoryBase = db.collection("SubCategories");

        // creating our new array list
        subcategoryModel = new ArrayList<>();
        subcategoryRecycler.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager=new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL);
        subcategoryRecycler.setLayoutManager(layoutManager);

        // adding our array list to our recycler view adapter class.
        subcategoryAdapter = new SellerSubcategoryAdapter(subcategoryModel, this);
        updateCartCount();
        // setting adapter to our recycler view.
        subcategoryRecycler.setAdapter(subcategoryAdapter);
        seller_toolbar_cart_card = findViewById(R.id.seller_toolbar_cart_card);
        seller_toolbar_cart_card.setOnClickListener(view -> {
            Intent intent1 = new Intent(SellerSubcategoryActivity.this, SellerCartActivity.class);
            startActivity(intent1);
        });
        seller_inner_toolbar_logo = findViewById(R.id.seller_inner_toolbar_logo);
        seller_inner_toolbar_logo.setOnClickListener(v -> onBackPressed());
        for (Object subCat :  category_modal.getSubcategories()){
            if (subCat != ""){
                loadRecyclerViewData(subCat);
            }else{
                Toast.makeText(this, "Nothing to show", Toast.LENGTH_SHORT).show();
            }

        }

    }
    //    Firebase FireStore connection
    private void loadRecyclerViewData(Object subCat) {
        subcategoryBase.document(subCat.toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        SellerSubcategoryModel dataModal = document.toObject(SellerSubcategoryModel.class);
                        subcategoryModel.add(dataModal);
                        subcategoryAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(SellerSubcategoryActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateCartCount() {
        db.collection("ShoppingCart").whereEqualTo("sellerId", sellerId).addSnapshotListener((value, error) -> {
            if (!value.isEmpty() || value.size() != 0) {
                int tProd = 0;
                List<DocumentSnapshot> list = value.getDocuments();
                for (DocumentSnapshot d : list) {
                    tProd += Integer.parseInt(Objects.requireNonNull(d.get("prodQuantity")).toString());
                }
                if (tProd != 0){
                    badge_notification_1.setVisibility(View.VISIBLE);
                    badge_notification_1.setText(String.valueOf(tProd));
                    badge_notification_1.setText(String.valueOf(tProd));
                    badge_notification_1.setBackgroundColor(Color.RED);
                }else{
                    badge_notification_1.setText("");
                    badge_notification_1.setVisibility(View.GONE);
                }
            }
            else{
                badge_notification_1.setText("");
                badge_notification_1.setVisibility(View.GONE);
            }
        });

    }


    private void filterList(String text) {
        subcategory_not_found = findViewById(R.id.subcategory_not_found);
        ArrayList<SellerSubcategoryModel> filteredList = new ArrayList<>();
        for(SellerSubcategoryModel recyclerData: subcategoryModel){
            if(recyclerData.getSubcategoryName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(recyclerData);
            }
        }
        if(filteredList.isEmpty()){
            subcategory_not_found.setVisibility(View.VISIBLE);
            this.subcategoryAdapter.setFilteredList(filteredList);
            this.subcategoryRecycler.setAdapter(this.subcategoryAdapter);
        }else {
            subcategory_not_found.setVisibility(View.INVISIBLE);
            this.subcategoryAdapter.setFilteredList(filteredList);
            this.subcategoryRecycler.setAdapter(this.subcategoryAdapter);
        }
    }
}
