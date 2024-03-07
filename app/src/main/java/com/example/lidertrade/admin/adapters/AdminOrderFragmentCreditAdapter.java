package com.example.lidertrade.admin.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.activities.AdminOrderCreditActivity;
import com.example.lidertrade.deliverer.models.DelivererCreditModel;

import java.util.ArrayList;

public class AdminOrderFragmentCreditAdapter extends RecyclerView.Adapter<AdminOrderFragmentCreditAdapter.ViewHolder>{
    private ArrayList<DelivererCreditModel> dataModalArrayList;
    private Context context;


    // constructor class for our Adapter
    public AdminOrderFragmentCreditAdapter(ArrayList<DelivererCreditModel> dataModalArrayList, Context context) {
        this.dataModalArrayList = dataModalArrayList;
        this.context = context;
    }

    public AdminOrderFragmentCreditAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.deliverer_sending_fragment_item, parent, false));
    }

    public void setFilteredList(ArrayList<DelivererCreditModel> filteredList) {
        this.dataModalArrayList = filteredList;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // setting data to our views in Recycler view items.
        DelivererCreditModel modal = dataModalArrayList.get(position);
        holder.orderSendingListCustomerAddress.setText(String.format("%s, %s, %s, %s", modal.getCustomerHouse(), modal.getCustomerStreet(), modal.getCustomerVillage(), modal.getCustomerDistrict()));
        holder.orderSendingListCustomerName.setText(modal.getCustomerName());
        holder.orderSendingListCustomerPhone.setText(modal.getCustomerPhoneNumber1());
        if (modal.getPackageStatus()==2){
            holder.linearLayout.setBackgroundColor(context.getColor(R.color.CompletedOrders));
            holder.completeSelected.setText("Buyurtma yetkazilgan");
        }else if (modal.getPackageStatus()==-2){
            holder.completeSelected.setText("Buyurtma Bekor qilingan");
            holder.linearLayout.setBackgroundColor(context.getColor(R.color.CanceledOrders));
        }
        else  if (modal.getPackageStatus()==-0){
            holder.completeSelected.setText("Buyurtma Yetkazilmoqda");
            holder.linearLayout.setBackgroundColor(context.getColor(R.color.PendingOrders));
        }

        holder.completeSelectedOrder.setOnClickListener(view -> {
            Intent intent = new Intent(context, AdminOrderCreditActivity.class);
            intent.putExtra("orderModel", modal);
            context.startActivity(intent);
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
        private TextView orderSendingListCustomerAddress, orderSendingListCustomerName, orderSendingListCustomerPhone, completeSelected;
        private CardView completeSelectedOrder;
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing the views of recycler views.
            orderSendingListCustomerAddress = itemView.findViewById(R.id.orderSendingListCustomerAddress);
            orderSendingListCustomerName = itemView.findViewById(R.id.orderSendingListCustomerName);
            orderSendingListCustomerPhone = itemView.findViewById(R.id.orderSendingListCustomerPhone);
            completeSelectedOrder = itemView.findViewById(R.id.completeSelectedOrder);
            completeSelected = itemView.findViewById(R.id.completeSelected);
            linearLayout = itemView.findViewById(R.id.linearLayout);
        }
    }
}

