package com.example.lidertrade.seller.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lidertrade.R;
import com.example.lidertrade.seller.models.SellerShoppingCartModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class SCFCartAdapter extends RecyclerView.Adapter<SCFCartAdapter.ViewHolder> {
    private ArrayList<SellerShoppingCartModel> dataModalArrayList;
    private Context context;
    DecimalFormat decim = new DecimalFormat("#,###.##");
    private TextView shoppingCartTotalPrice;
    private OnItemClickListener listener;

    // constructor class for our Adapter
    public SCFCartAdapter(ArrayList<SellerShoppingCartModel> dataModalArrayList, Context context, TextView shoppingCartTotalPrice) {
        this.dataModalArrayList = dataModalArrayList;
        this.context = context;
        this.shoppingCartTotalPrice = shoppingCartTotalPrice;
    }
    public SCFCartAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.seller_cart_fragment_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // setting data to our views in Recycler view items.
        SellerShoppingCartModel modal = dataModalArrayList.get(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference shoppingCartDoc = db.collection("ShoppingCart").document(modal.getCartId());
        DocumentReference productDoc = db.collection("products").document(modal.getProductId());
        final int[] prodQuan = new int[1];
        productDoc.addSnapshotListener((d, error) -> {
            if(d.exists()){
                if(d.get("brand")!=null  && d.get("subCategory")!=null){
                    prodQuan[0] = Integer.parseInt(d.get("productQuantity").toString());
                    if(prodQuan[0] < modal.getProdQuantity()){
                        modal.setProdQuantity(prodQuan[0]);
                        shoppingCartDoc.update("prodQuantity",prodQuan[0]);
                    }
                    holder.prodQuantity.setText(String.valueOf(modal.getProdQuantity()));

                    db.collection("brands").document(d.get("brand").toString()).get().addOnSuccessListener(dd -> {
                        if(dd.exists()){  holder.cartActivityBrandName.setText(dd.get("brandName").toString());}});
                    db.collection("SubCategories").document(d.get("subCategory").toString()).get().addOnSuccessListener(dd -> {
                        if(dd.exists()){  holder.cartActivitySubcategoryName.setText(dd.get("subcategoryName").toString());;}});
                }


            }
        });
        holder.prodName.setText(modal.getProductName());

        holder.prodPrice.setText(String.format("%s so'm",decim.format(modal.getCashPrice())));
        Glide.with(context)
                .load(modal.getProdPic())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.prodPic);

        holder.cartActivityPlusCard.setOnClickListener(view -> {
            int qnt=modal.getProdQuantity();
            int price=modal.getCashPrice();
            if(qnt<prodQuan[0]){
                qnt++;
                modal.setProdQuantity(qnt);
                shoppingCartDoc.update(
                        "prodQuantity", qnt, "totalCashPrice", qnt*price);
                notifyDataSetChanged();
                updateprice();
            }else {
                qnt = prodQuan[0];
                new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Bazada yetarli mahsulot mavjud emas!")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(SweetAlertDialog::cancel)
                        .show();
                modal.setProdQuantity(qnt);
                shoppingCartDoc.update(
                        "prodQuantity", qnt, "totalCashPrice", qnt*price);
                notifyDataSetChanged();
                updateprice();
            }
        });
        holder.cartActivityPlusCard.setOnLongClickListener(view -> {
            int qnt=modal.getProdQuantity();
            int price=modal.getCashPrice();
            if(qnt+10<prodQuan[0]){
                qnt += 10;
                modal.setProdQuantity(qnt);
                shoppingCartDoc.update(
                        "prodQuantity", qnt, "totalCashPrice", qnt*price);
                notifyDataSetChanged();
                updateprice();
            }else {
                qnt = prodQuan[0];
                new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Bazada yetarli mahsulot mavjud emas!")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(SweetAlertDialog::cancel)
                        .show();
                modal.setProdQuantity(qnt);
                shoppingCartDoc.update(
                        "prodQuantity", qnt, "totalCashPrice", qnt*price);
                notifyDataSetChanged();
                updateprice();
            }

            return true;
        });
        holder.cartActivityMinusCard.setOnClickListener(view -> {
            int qnt=modal.getProdQuantity();
            int price=modal.getCashPrice();
            if (qnt > 1){
                qnt--;
                modal.setProdQuantity(qnt);
                shoppingCartDoc.update(
                        "prodQuantity", qnt, "totalCashPrice", qnt*price);
                notifyDataSetChanged();
                updateprice();
            }
        });
        holder.cartActivityMinusCard.setOnLongClickListener(view -> {
            int qnt=modal.getProdQuantity();
            int price=modal.getCashPrice();
            if (qnt - 10 > 1){
                qnt-=10;
                modal.setProdQuantity(qnt);
                shoppingCartDoc.update(
                        "prodQuantity", qnt, "totalCashPrice", qnt*price);
                notifyDataSetChanged();
                updateprice();
            }
            return true;
        });

    }

    @SuppressLint("DefaultLocale")
    private void updateprice() {
        int sum=0,i;
        DecimalFormat decim = new DecimalFormat("#,###.##");
        for(i=0;i< dataModalArrayList.size();i++)
            sum=sum+(dataModalArrayList.get(i).getCashPrice()*dataModalArrayList.get(i).getProdQuantity());

        shoppingCartTotalPrice.setText(String.format("%s so'm",decim.format(sum)));
    }

    @Override
    public int getItemCount() {
        // returning the size of array list.
        return dataModalArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our
        // views of recycler items.
        private TextView prodName, prodPrice, prodQuantity, cartActivitySubcategoryName ,cartActivityBrandName;
        private ImageView prodPic, cartActivityMinusCard, cartActivityPlusCard;
        private CardView  cartActivityProductDelete;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            // initializing the views of recycler views.
            prodName = itemView.findViewById(R.id.cartActivityProductName);
            prodPrice = itemView.findViewById(R.id.cartActivityProductPrice);
            cartActivitySubcategoryName = itemView.findViewById(R.id.cartActivitySubcategoryName);
            cartActivityBrandName = itemView.findViewById(R.id.cartActivityBrandName);
            prodQuantity = itemView.findViewById(R.id.cartActivityQuantity);
            prodPic = itemView.findViewById(R.id.cartActivityProductPic);
            cartActivityProductDelete = itemView.findViewById(R.id.cartActivityProductDelete);
            cartActivityPlusCard = itemView.findViewById(R.id.cartActivityPlus);
            cartActivityMinusCard = itemView.findViewById(R.id.cartActivityMinus);
            cartActivityProductDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public  void setOnItemClickListener(OnItemClickListener clickListener){
        listener = clickListener;
    }
}
