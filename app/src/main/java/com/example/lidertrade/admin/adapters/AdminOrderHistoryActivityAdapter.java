package com.example.lidertrade.admin.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.lidertrade.R;
import com.example.lidertrade.admin.models.AdminProductModel;
import com.example.lidertrade.admin.models.AdminSoldProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminOrderHistoryActivityAdapter extends RecyclerView.Adapter<AdminOrderHistoryActivityAdapter.ViewHolder> {
    private ArrayList<AdminSoldProductModel> dataModalArrayList;
    private Context context;
    FirebaseFirestore db;

    // constructor class for our Adapter
    public AdminOrderHistoryActivityAdapter(ArrayList<AdminSoldProductModel> dataModalArrayList, Context context) {
        this.dataModalArrayList = dataModalArrayList;
        this.context = context;
    }
    public AdminOrderHistoryActivityAdapter() {
    }


    @NonNull
    @Override
    public AdminOrderHistoryActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.deliverer_sending_detail_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminOrderHistoryActivityAdapter.ViewHolder holder, int position) {
        // setting data to our views in Recycler view items.
        AdminSoldProductModel modal = dataModalArrayList.get(position);
        db = FirebaseFirestore.getInstance();
        holder.orderSendingDetailProductName.setText(modal.getProductName());
        holder.orderSendingDetailProductQuantity.setText(String.valueOf(modal.getSoldProductQuantity())+" dona");
        db.collection("products").document(modal.getProductId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot d = task.getResult();
                    if(d.exists()){
                        ArrayList<String> driverPermissions = (ArrayList<String>) d.get("imageUrl");
                        String imageUrl = driverPermissions.get(0);
                        Glide.with(context)
                                .load(Uri.parse(imageUrl))
                                .into(holder.orderSendingDetailProductImage);

                    }else {
                        Glide.with(context)
                                .load(R.drawable.baseline_not_interested_24)
                                .into(holder.orderSendingDetailProductImage);
                        holder.orderSendingDetailProductName.setText(modal.getProductName()+" bazada mavjud emas");
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        // returning the size of array list.
        return dataModalArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our
        // views of recycler items.
        private TextView orderSendingDetailProductQuantity, orderSendingDetailProductName;
        private ImageView orderSendingDetailProductImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing the views of recycler views.
            orderSendingDetailProductQuantity = itemView.findViewById(R.id.orderSendingDetailProductQuantity);
            orderSendingDetailProductName = itemView.findViewById(R.id.orderSendingDetailProductName);
            orderSendingDetailProductImage = itemView.findViewById(R.id.orderSendingDetailProductImage);

        }
    }
}
