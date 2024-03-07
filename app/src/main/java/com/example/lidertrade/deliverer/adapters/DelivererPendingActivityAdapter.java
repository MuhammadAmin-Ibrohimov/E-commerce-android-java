package com.example.lidertrade.deliverer.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lidertrade.R;
import com.example.lidertrade.admin.models.AdminProductModel;
import com.example.lidertrade.deliverer.activities.DelivererPendingDetailActivity;
import com.example.lidertrade.deliverer.models.DelivererPendingActivityModel;
import com.example.lidertrade.seller.adapters.SCFCartAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DelivererPendingActivityAdapter extends RecyclerView.Adapter<DelivererPendingActivityAdapter.ViewHolder> {
    private ArrayList<DelivererPendingActivityModel> dataModalArrayList;
    private Context context;
    DecimalFormat decim = new DecimalFormat("#,###.##");
    FirebaseFirestore db;
    DelivererPendingDetailActivity activity;
    private TextView orderListTotalPrice;

    // constructor class for our Adapter
    public DelivererPendingActivityAdapter(DelivererPendingDetailActivity activity, ArrayList<DelivererPendingActivityModel> dataModalArrayList, Context context,
                                           TextView orderListTotalPrice) {
        this.dataModalArrayList = dataModalArrayList;
        this.activity = activity;
        this.context = context;
        this.orderListTotalPrice = orderListTotalPrice;
    }
    public DelivererPendingActivityAdapter() {
    }

    @NonNull
    @Override
    public DelivererPendingActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.deliverer_pending_detail_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DelivererPendingActivityAdapter.ViewHolder holder, int position) {
        // setting data to our views in Recycler view items.
        DelivererPendingActivityModel modal = dataModalArrayList.get(position);
        db = FirebaseFirestore.getInstance();
        final int[] prodQuan = new int[1];
        DocumentReference productDoc = db.collection("products").document(modal.getProductId());
        DocumentReference soldProductDoc = db.collection("SoldProducts").document(modal.getSoldProductId());

        productDoc.addSnapshotListener((d, error) -> {
            if(d.exists()){
                if(d.get("brand")!=null  && d.get("subCategory")!=null){
                    prodQuan[0] = Integer.parseInt(d.get("productQuantity").toString());
                    if(prodQuan[0] < modal.getSoldProductQuantity()){
                        modal.setSoldProductQuantity(prodQuan[0]);
                        soldProductDoc.update("soldProductQuantity",prodQuan[0]);
                    }
                    holder.prodQuantity.setText(String.valueOf(modal.getSoldProductQuantity()));

                    db.collection("brands").document(d.get("brand").toString()).get().addOnSuccessListener(dd -> {
                        if(dd.exists()){  holder.cartActivityBrandName.setText(dd.get("brandName").toString());}});
                    db.collection("SubCategories").document(d.get("subCategory").toString()).get().addOnSuccessListener(dd -> {
                        if(dd.exists()){  holder.cartActivitySubcategoryName.setText(dd.get("subcategoryName").toString());;}});
                }
                if(d.get("imageUrl")!=null){
                    ArrayList<String> urls = (ArrayList<String>) d.get("imageUrl");
                    String imageUrl = urls.get(0);
                    Glide.with(activity.getApplicationContext())
                            .load(Uri.parse(imageUrl))
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(holder.prodPic);
                }
            }
        });
        holder.prodName.setText(modal.getProductName());
        holder.prodPrice.setText(String.format("%s so'm",decim.format(modal.getSoldProductPrice())));


        holder.cartActivityPlusCard.setOnClickListener(view -> {
            int qnt=modal.getSoldProductQuantity();
            if(qnt<prodQuan[0]){
                qnt++;
                modal.setSoldProductQuantity(qnt);
                soldProductDoc.update(
                        "soldProductQuantity", qnt);
                notifyDataSetChanged();
                updateprice(position);
            }else {
                qnt = prodQuan[0];
                new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Bazada yetarli mahsulot mavjud emas!")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(SweetAlertDialog::cancel)
                        .show();
                modal.setSoldProductQuantity(qnt);
                soldProductDoc.update(
                        "soldProductQuantity", qnt);
                notifyDataSetChanged();
                updateprice(position);
            }
        });
        holder.cartActivityPlusCard.setOnLongClickListener(view -> {
            int qnt=modal.getSoldProductQuantity();
            if(qnt+10<prodQuan[0]){
                qnt += 10;
                modal.setSoldProductQuantity(qnt);
                soldProductDoc.update(
                        "soldProductQuantity", qnt);
                notifyDataSetChanged();
                updateprice(position);
            }else {
                qnt = prodQuan[0];
                new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Bazada yetarli mahsulot mavjud emas!")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(SweetAlertDialog::cancel)
                        .show();
                modal.setSoldProductQuantity(qnt);
                soldProductDoc.update("soldProductQuantity", qnt);
                FirebaseFirestore.getInstance().collection("ShoppingCart").document(modal.getSoldProductId()).update(
                        "soldProductQuantity", qnt);
                notifyDataSetChanged();
                updateprice(position);
            }

            return true;
        });
        holder.cartActivityMinusCard.setOnClickListener(view -> {

            int qnt=modal.getSoldProductQuantity();
            if (qnt > 0){
                qnt--;
                modal.setSoldProductQuantity(qnt);
                soldProductDoc.update(
                        "soldProductQuantity", qnt);
                notifyDataSetChanged();
                updateprice(position);
            }
        });
        holder.cartActivityMinusCard.setOnLongClickListener(view -> {
            int qnt=modal.getSoldProductQuantity();
            if (qnt - 10 > 0){
                qnt-=10;
                modal.setSoldProductQuantity(qnt);
                soldProductDoc.update(
                        "soldProductQuantity", qnt);
                notifyDataSetChanged();
                updateprice(position);
            }
            return true;
        });
    }

    @SuppressLint("DefaultLocale")
    private void updateprice(int position) {
        int sum=0,i, quantity=0;
        for(i=0;i< dataModalArrayList.size();i++){

            if(dataModalArrayList.size()>1){
                if(dataModalArrayList.get(i).getSoldProductQuantity()<=0){
                    int finalI = i;
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setContentText("Mahsulot miqdori 0 ga teng bo'ldi. O'chirib tashlansinmi?")
                            .setCancelText("Yo'q!")
                            .setConfirmText("Ha!")
                            .showCancelButton(true)
                            .setCancelClickListener(SweetAlertDialog::cancel)
                            .setConfirmClickListener(sweetAlertDialog1 -> {
                                db.collection("SoldProducts").document(dataModalArrayList.get(finalI).getSoldProductId()).delete();
                                db.collection("Orders").document(dataModalArrayList.get(finalI).getOrderId())
                                        .update("soldProductsList", FieldValue.arrayRemove(dataModalArrayList.get(finalI).getSoldProductId()),
                                                "productsList", FieldValue.arrayRemove(dataModalArrayList.get(finalI).getProductId()
                                                ));

                                new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                        .setContentText("O'chirib tashlandi'!")
                                        .setConfirmText("OK!")
                                        .setConfirmClickListener(sweetAlertDialog2 -> {
                                            sweetAlertDialog2.cancel();
                                        })
                                        .show();
                                dataModalArrayList.remove(position);
                                notifyItemRemoved(position);
                                sweetAlertDialog1.cancel();
                            })
                            .show();

                }
            }
            int s = (dataModalArrayList.get(i).getSoldProductPrice()*dataModalArrayList.get(i).getSoldProductQuantity());
            sum=sum+s;
            quantity += dataModalArrayList.get(i).getSoldProductQuantity();
            notifyDataSetChanged();
        }
        orderListTotalPrice.setText(String.format("%s so'm",decim.format(sum)));
        db.collection("Orders").document(dataModalArrayList.get(position).
                getOrderId()).update("cartTotalPrice", sum, "cartTotalQuantity", quantity);
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
        private ImageView prodPic, cartActivityMinusCard, cartActivityPlusCard, cartActivityProductDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing the views of recycler views.
            prodName = itemView.findViewById(R.id.cartActivityProductName);
            prodPrice = itemView.findViewById(R.id.cartActivityProductPrice);
            cartActivitySubcategoryName = itemView.findViewById(R.id.cartActivitySubcategoryName);
            cartActivityBrandName = itemView.findViewById(R.id.cartActivityBrandName);
            prodQuantity = itemView.findViewById(R.id.cartActivityQuantity);
            cartActivityProductDelete = itemView.findViewById(R.id.cartActivityProductDelete);
            prodPic = itemView.findViewById(R.id.cartActivityProductPic);
            cartActivityPlusCard = itemView.findViewById(R.id.cartActivityPlus);
            cartActivityMinusCard = itemView.findViewById(R.id.cartActivityMinus);


        }
    }

}
