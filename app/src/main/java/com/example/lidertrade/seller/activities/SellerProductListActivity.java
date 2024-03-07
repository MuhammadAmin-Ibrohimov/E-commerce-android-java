package com.example.lidertrade.seller.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.models.AdminProductModel;
import com.example.lidertrade.seller.adapters.SellerProductListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SellerProductListActivity extends AppCompatActivity {
    TextView productListSubcategoryId, product_not_found, badge_notification_1;
    ShapeableImageView seller_inner_toolbar_logo;
    Toolbar toolbar;
    private FirebaseFirestore db;
    private CollectionReference productBase;

    CardView seller_toolbar_cart_card;
    private RecyclerView productListRecycler;
    FirebaseUser user;
    String sellerId;
    private ArrayList<AdminProductModel> productListModel;
    private SellerProductListAdapter productListAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.seller_product_list_activity);

        final Object productIdList = getIntent().getSerializableExtra("products_list");
        final Object subcatID = getIntent().getSerializableExtra("subcategory_name");
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){assert user != null;sellerId = user.getUid();}else{sellerId = "null";}

        // initializing our variables.
        productListRecycler = findViewById(R.id.productListRecycler);
        badge_notification_1 = findViewById(R.id.badge_notification_1);
        productListSubcategoryId = findViewById(R.id.productListSubcategoryId);
        productListSubcategoryId.setText(subcatID.toString());
        toolbar = findViewById(R.id.sellerInnerToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // initializing our variable for firebase
        // firestore and getting its instance.
        db = FirebaseFirestore.getInstance();
        productBase = db.collection("products");

        // creating our new array list
        productListModel = new ArrayList<>();
        productListRecycler.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager=new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL);
        productListRecycler.setLayoutManager(layoutManager);
        updateCartCount();
        // adding our array list to our recycler view adapter class.
        productListAdapter = new SellerProductListAdapter(productListModel, this);
        seller_toolbar_cart_card = findViewById(R.id.seller_toolbar_cart_card);
        seller_toolbar_cart_card.setOnClickListener(view -> {
            Intent intent1 = new Intent(SellerProductListActivity.this, SellerCartActivity.class);
            startActivity(intent1);
        });
        seller_inner_toolbar_logo = findViewById(R.id.seller_inner_toolbar_logo);
        seller_inner_toolbar_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // setting adapter to our recycler view.
        productListRecycler.setAdapter(productListAdapter);
        for (Object prod : (ArrayList) productIdList){
            if (prod != ""){
                loadrecyclerViewData(prod);
            }else{
                Toast.makeText(this, "Nothing to show", Toast.LENGTH_SHORT).show();
            }
        }
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

    //    Firebase FireStore connection
    private void loadrecyclerViewData(Object prod) {
        productBase.document(prod.toString()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    AdminProductModel dataModal = document.toObject(AdminProductModel.class);
                    productListModel.add(dataModal);
                    productListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(SellerProductListActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(SellerProductListActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
            }
        });
    }


}

