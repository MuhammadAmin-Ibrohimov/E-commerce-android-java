package com.example.lidertrade.deliverer.adapters;

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
import com.example.lidertrade.deliverer.models.DelivererPendingActivityModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DelivererSendingActivityAdapter extends RecyclerView.Adapter<DelivererSendingActivityAdapter.ViewHolder> {
    private ArrayList<DelivererPendingActivityModel> dataModalArrayList;
    private Context context;
    FirebaseFirestore db;

    // constructor class for our Adapter
    public DelivererSendingActivityAdapter(ArrayList<DelivererPendingActivityModel> dataModalArrayList, Context context) {
        this.dataModalArrayList = dataModalArrayList;
        this.context = context;
    }
    public DelivererSendingActivityAdapter() {
    }


    @NonNull
    @Override
    public DelivererSendingActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.deliverer_sending_detail_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DelivererSendingActivityAdapter.ViewHolder holder, int position) {
        // setting data to our views in Recycler view items.
        DelivererPendingActivityModel modal = dataModalArrayList.get(position);
        db = FirebaseFirestore.getInstance();
        holder.orderSendingDetailProductName.setText(modal.getProductName());
        holder.orderSendingDetailProductQuantity.setText(String.valueOf(modal.getSoldProductQuantity())+" dona");
        System.out.println(modal.getProductId());

        db.collection("products").document(modal.getProductId()).get().addOnCompleteListener(task -> {
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
