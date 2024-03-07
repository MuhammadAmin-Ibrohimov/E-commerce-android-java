package com.example.lidertrade.seller.activities;


import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.lidertrade.R;
import com.example.lidertrade.admin.models.AdminProductModel;
import com.example.lidertrade.admin.models.AdminProductSpecificationsModel;
import com.example.lidertrade.seller.adapters.SPDAdapter;
import com.example.lidertrade.seller.adapters.SellerSubcategoryAdapter;
import com.example.lidertrade.seller.models.SellerSubcategoryModel;
import com.example.lidertrade.seller.models.SellerShoppingCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nex3z.notificationbadge.NotificationBadge;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SellerProductDetailActivity extends AppCompatActivity {
    TextView productDetailProductName, productDetailQuantity, productDetailProductPrice, productDetailProductTotalPrice,
            productDetailProductDescription, badge_notification_1, innerToolbarTitle;
    RecyclerView productDetailProductSpecification;
    Toolbar toolbar;
    ImageSlider productPicList;
    FirebaseUser user;
    String sellerId;
    SPDAdapter spdAdapter;
    String  imageUrl;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    CardView productDetailAddToCart, seller_toolbar_cart_card;
    ImageView productDetailPlusCard, productDetailMinusCard;
    ShapeableImageView seller_inner_toolbar_logo;
    NotificationBadge badge;

    private FirebaseFirestore db;
    private CollectionReference subcategoryBase, shoppingCartCollection, productsCollection;
    DocumentReference shoppingCartDocumentRefernces;

    private RecyclerView subcategoryRecycler;
    private ArrayList<SellerSubcategoryModel> subcategoryModel;
    private SellerSubcategoryAdapter subcategoryAdapter;

    public SellerProductDetailActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.seller_product_detail_activity);

        Intent intent = getIntent();
        AdminProductModel productListModel = (AdminProductModel) intent.getSerializableExtra("productModal");
        String productId = productListModel.getProductId();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){assert user != null;sellerId = user.getUid();}else{sellerId = "null";}

        db = FirebaseFirestore.getInstance();
        subcategoryBase = db.collection("SubCategories");
        shoppingCartCollection = db.collection("ShoppingCart");
        productsCollection = db.collection("products");
        tabLayout = (TabLayout) findViewById(R.id.sPDTabLayout);
        viewPager2 = (ViewPager2) findViewById(R.id.sPDViewPager);
        tabLayout.addTab(tabLayout.newTab().setText("Xususiyatlari"));
        tabLayout.addTab(tabLayout.newTab().setText("Kredit haqida"));
        DecimalFormat decim = new DecimalFormat("#,###.##");
        spdAdapter = new SPDAdapter(SellerProductDetailActivity.this);
        spdAdapter.setData(productId);
        viewPager2.setAdapter(spdAdapter);


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





        // initializing our variables in XML.

//       Price
        productDetailProductPrice = findViewById(R.id.productDetailProductPrice);
        productDetailProductPrice.setText(String.format("%s so'm",decim.format(productListModel.getCashPrice())));


        ArrayList<AdminProductSpecificationsModel> arrDetails = productListModel.getProductSpecification();
        AdminProductSpecificationsModel hashDetails = arrDetails.get(0); // Use this index accordingly
        badge_notification_1 = findViewById(R.id.badge_notification_1);
        imageUrl = productListModel.getImageUrl().get(0);

//      Total  Price

        productDetailProductTotalPrice = findViewById(R.id.productDetailProductTotalPrice);
        productDetailProductTotalPrice.setText(String.format(decim.format(productListModel.getCashPrice())));
        long cleanedPTP = Long.parseLong(productDetailProductTotalPrice.getText().toString().trim().replaceAll("[^0-9]", ""));
        long cleanedPT = Long.parseLong(productDetailProductPrice.getText().toString().trim().replaceAll("[^0-9]", ""));
//        Name
        productDetailProductName = findViewById(R.id.productDetailProductName);
        productDetailProductName.setText((String) productListModel.getProductName());
//        Pictures
        productPicList = findViewById(R.id.productPicList);
        List<SlideModel> slideModels = new ArrayList<>();
        for (String i :productListModel.getImageUrl()){
            slideModels.add(new SlideModel(i, ScaleTypes.CENTER_INSIDE));
        }
        productPicList.setImageList(slideModels, ScaleTypes.CENTER_INSIDE);
        updateCartCount();

//      Quantity
        productDetailQuantity = findViewById(R.id.productDetailQuantity);
        productDetailQuantity.setText("1");

//+++++++++++++++++++++++++         Plus Button
        productDetailPlusCard = findViewById(R.id.productDetailPlus);
        productDetailPlusCard.setOnClickListener(view -> {
            long qt = Long.parseLong(productDetailQuantity.getText().toString());
            long price = cleanedPT;
            if (qt<productListModel.getProductQuantity()){
                qt = qt+1;
                price = price*qt;
                productDetailQuantity.setText(String.valueOf(qt));
                productDetailProductTotalPrice.setText(String.format(decim.format(price)));
            }else{
                new SweetAlertDialog(SellerProductDetailActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Bazada yetarli mahsulot mavjud emas")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(sweetAlertDialog2 -> sweetAlertDialog2.cancel())
                        .show();
            }
        });
// _________________________       Plus Button End
//+++++++++++++++++++++++++        Minus Button START
        productDetailMinusCard = findViewById(R.id.productDetailMinus);
        productDetailMinusCard.setOnClickListener(view -> {
            long qt = Long.parseLong(productDetailQuantity.getText().toString());
            long price = cleanedPT;
            if(qt == 1){
                new SweetAlertDialog(SellerProductDetailActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Uzr, Bundan kam mahsulot kiritolmaymiz")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(sweetAlertDialog2 -> sweetAlertDialog2.cancel())
                        .show();
            }
            else{
                qt = qt-1;
                price = price*qt;
                productDetailQuantity.setText(String.valueOf(qt));
                productDetailProductTotalPrice.setText(String.format(decim.format(price)));
            }
        });
// _________________________       Minus Button End


//++++++++++++++++++        Toolbar Start
        toolbar = findViewById(R.id.sellerInnerToolbar);
        innerToolbarTitle = findViewById(R.id.innerToolbarTitle);
        db.collection("SubCategories").document(productListModel.getSubCategory()).get()
                .addOnSuccessListener(d -> {
                    if(d.exists()){
                        innerToolbarTitle.setText(d.get("subcategoryName").toString());
                    }
                });

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        seller_inner_toolbar_logo = findViewById(R.id.seller_inner_toolbar_logo);
        seller_inner_toolbar_logo = findViewById(R.id.seller_inner_toolbar_logo);
        seller_inner_toolbar_logo.setOnClickListener(v -> onBackPressed());
        seller_toolbar_cart_card = findViewById(R.id.seller_toolbar_cart_card);
        seller_toolbar_cart_card.setOnClickListener(view -> {
            Intent intent1 = new Intent(SellerProductDetailActivity.this, SellerCartActivity.class);
            startActivity(intent1);
        });

//________________________ Toolbar END
//    ++++++++++++++++++++++++++++ Add To Cart Start
        productDetailAddToCart = findViewById(R.id.productDetailAddToCart);
        productDetailAddToCart.setOnClickListener(view -> createShoppingCartCollection(productListModel));
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


    private void createShoppingCartCollection(AdminProductModel productListModel) {
        int qty = Integer.parseInt(productDetailQuantity.getText().toString().trim().replaceAll("[^0-9]", ""));
        int cashPrice = Integer.parseInt(productDetailProductPrice.getText().toString().trim().replaceAll("[^0-9]", ""));
        int creditPrice = (productListModel.getCreditPrice());
        int ptp = qty*cashPrice;
        int creptp = (int) (qty*creditPrice);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String sellerId;
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            assert user != null;
            sellerId = user.getUid();
        }else{
            sellerId = "null";
        }
        String id = (productListModel.getProductId()+sellerId);
        shoppingCartDocumentRefernces = shoppingCartCollection.document(id);
        shoppingCartDocumentRefernces.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    int receivedQuantity = Integer.parseInt(Objects.requireNonNull(document.get("prodQuantity")).toString());
                    int receivedTotalPrice;
                    int receivedTotalCreditPrice;
                    int receivedPrice = Integer.parseInt(Objects.requireNonNull(document.get("cashPrice")).toString());
                    int receivedCreditPrice = Integer.parseInt(Objects.requireNonNull(document.get("creditPrice")).toString());
                    receivedQuantity += qty;
                    receivedTotalPrice = receivedPrice * receivedQuantity;
                    receivedTotalCreditPrice = receivedCreditPrice * receivedQuantity;
                    shoppingCartCollection.document(id).update("prodQuantity", receivedQuantity);
                    shoppingCartCollection.document(id).update("totalCashPrice", receivedTotalPrice);
                    shoppingCartCollection.document(id).update("totalCreditPrice", receivedTotalCreditPrice);
                } else {
                    SellerShoppingCartModel sellerShoppingCartModel = new SellerShoppingCartModel(id,sellerId,productListModel.getProductId(),
                            productListModel.getProductName(),imageUrl, cashPrice, creditPrice, qty, ptp, creptp );
                    shoppingCartCollection.document(id).set(sellerShoppingCartModel);
                }
            } else {
                Log.d(TAG, "Failed with: ", task.getException());
            }
        });
        new SweetAlertDialog(SellerProductDetailActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                .setContentText("Savatga " + qty + " ta maxsulot muvafaqqiyatli qo'shilda")
                .setConfirmText("OK!")
                .setConfirmClickListener(sweetAlertDialog2 -> sweetAlertDialog2.cancel())
                .show();
    }
}