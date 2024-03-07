package com.example.lidertrade.seller.adapters;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.lidertrade.R;
import com.example.lidertrade.admin.models.AdminProductModel;
import com.example.lidertrade.seller.activities.SellerProductDetailActivity;
import com.example.lidertrade.seller.models.SellerShoppingCartModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SellerProductListAdapter extends RecyclerView.Adapter<SellerProductListAdapter.ViewHolder> {
    private ArrayList<AdminProductModel> dataModalArrayList;
    private Context context;
    FirebaseAuth firebaseAuth;


    // constructor class for our Adapter
    public SellerProductListAdapter(ArrayList<AdminProductModel> dataModalArrayList, Context context) {
        this.dataModalArrayList = dataModalArrayList;
        this.context = context;
    }

    public SellerProductListAdapter() {

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.seller_product_list_activity_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // setting data to our views in Recycler view items.
        AdminProductModel modal = dataModalArrayList.get(position);
        holder.productName.setText(modal.getProductName());
        DecimalFormat decim = new DecimalFormat("#,###.##");
        holder.price.setText(String.format("%s so'm", decim.format(modal.getCashPrice())));

//        holder.productPrice.setText(modal.getImgUrl());
        List<SlideModel> slideModels = new ArrayList<>();
        for (String i :modal.getImageUrl()){
            slideModels.add(new SlideModel(i, ScaleTypes.CENTER_CROP));
        }
        holder.prodPic.setImageList(slideModels, ScaleTypes.CENTER_CROP);
        holder.productListCardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SellerProductDetailActivity.class);
            intent.putExtra("productModal", modal);
            context.startActivity(intent);
        });
        holder.sPLAddTheCartCard.setOnClickListener(view -> {
//                db = FirebaseFirestore.getInstance();
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.seller_product_list_activity_item_quantity_dialog);
            final int[] prodQuantity = new int[1];

            //Initializing the views of the dialog.
            final NumberPicker sPLAIQDNumberPicker = dialog.findViewById(R.id.sPLAIQDNumberPicker);
            if (sPLAIQDNumberPicker != null) {
                sPLAIQDNumberPicker.setMinValue(0);
                sPLAIQDNumberPicker.setMaxValue(100);
                sPLAIQDNumberPicker.setWrapSelectorWheel(true);
                sPLAIQDNumberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> prodQuantity[0] = newVal);

                CardView userListAddressUpdateCompleteCard = dialog.findViewById(R.id.sPLAIQDCompleteCard);
                userListAddressUpdateCompleteCard.setOnClickListener(v -> {
                    if (prodQuantity[0] > 0){
                        holder.sPLAIQuantityCard.setVisibility(View.VISIBLE);
                        createShoppingCartCollection(modal, prodQuantity[0], holder.sPLAIQuantity );
                        holder.sPLAIQuantity.setText(String.valueOf(prodQuantity[0]));
                    }else {
                        holder.sPLAIQuantity.setText(String.valueOf(0));
                        holder.sPLAIQuantityCard.setVisibility(View.INVISIBLE);
                    }
                    dialog.dismiss();

                });

                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            }
        });

    }

    private void createShoppingCartCollection(AdminProductModel modal, int i, TextView sPLAIQuantity) {
        int qty = i;
        int cashPrice = (int) modal.getCashPrice();
        int creditPrice = (int) modal.getCreditPrice();
        int ptp = (int) (qty*cashPrice);
        int creptp = (int) (qty*creditPrice);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String sellerId;
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            sellerId = user.getUid();
        }else{
            sellerId = "null";
        }
        String id = (modal.getProductId()+sellerId);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference shoppingCartCollection = db.collection("ShoppingCart");
        DocumentReference shoppingCartDocumentReferences = shoppingCartCollection.document(id);
        String imageUrl = modal.getImageUrl().get(0);


        shoppingCartDocumentReferences.get().addOnCompleteListener(task -> {
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
                    sPLAIQuantity.setText(String.valueOf(receivedQuantity));
                    shoppingCartCollection.document(id).update("totalCashPrice", receivedTotalPrice);
                    shoppingCartCollection.document(id).update("totalCreditPrice", receivedTotalCreditPrice);
                }
                else {
                    SellerShoppingCartModel sellerShoppingCartModel = new SellerShoppingCartModel(id,sellerId,modal.getProductId(),
                            modal.getProductName(),imageUrl, cashPrice, creditPrice, qty, ptp, creptp );

                    shoppingCartCollection.document(id).set(sellerShoppingCartModel);
                }
            } else {
                Log.d(TAG, "Failed with: ", task.getException());
            }
        });
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setContentText("Savatga " + qty + " ta maxsulot muvafaqqiyatli qo'shilda")
                .setConfirmText("OK!")
                .setConfirmClickListener(sweetAlertDialog2 -> sweetAlertDialog2.cancel())
                .show();
    }

    @Override
    public int getItemCount() {
        // returning the size of array list.
        return dataModalArrayList.size();
    }

    public void setFilteredList(ArrayList<AdminProductModel> filteredList) {
        this.dataModalArrayList = filteredList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our
        // views of recycler items.
        private TextView productName, price, sPLAIQuantity;
        private ImageSlider prodPic;
        private CardView sPLAddTheCartCard, productListCardView, sPLAIQuantityCard;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing the views of recycler views.
            productName = itemView.findViewById(R.id.productName);
            sPLAIQuantity = itemView.findViewById(R.id.sPLAIQuantity);
            prodPic = itemView.findViewById(R.id.prodPic);
            price = itemView.findViewById(R.id.price);
            sPLAddTheCartCard = itemView.findViewById(R.id.sPLAddTheCartCard);
            productListCardView = itemView.findViewById(R.id.productListCardView);
            sPLAIQuantityCard = itemView.findViewById(R.id.sPLAIQuantityCard);
        }
    }
}
