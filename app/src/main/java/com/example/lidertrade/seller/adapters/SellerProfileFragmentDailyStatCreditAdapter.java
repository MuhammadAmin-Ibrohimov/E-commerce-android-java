package com.example.lidertrade.seller.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lidertrade.R;
import com.example.lidertrade.seller.models.SellerProfileFragmentDailyStatCreditModel;

import java.util.ArrayList;
import java.util.Objects;

public class SellerProfileFragmentDailyStatCreditAdapter extends RecyclerView.Adapter<SellerProfileFragmentDailyStatCreditAdapter.ViewHolder> {
    private ArrayList<SellerProfileFragmentDailyStatCreditModel> dataModalArrayList;
    private Context context;


    // constructor class for our Adapter
    public SellerProfileFragmentDailyStatCreditAdapter(ArrayList<SellerProfileFragmentDailyStatCreditModel> dataModalArrayList, Context context) {
        this.dataModalArrayList = dataModalArrayList;
        this.context = context;
    }

    public SellerProfileFragmentDailyStatCreditAdapter() {

    }

    @NonNull
    @Override
    public SellerProfileFragmentDailyStatCreditAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.seller_profile_fragment_daily_stat_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SellerProfileFragmentDailyStatCreditAdapter.ViewHolder holder, int position) {
        // setting data to our views in Recycler view items.
        SellerProfileFragmentDailyStatCreditModel modal = dataModalArrayList.get(position);
        holder.customerName.setText(modal.getCustomerName());
        holder.customerPhone.setText(modal.getCustomerPhoneNumber1());
        if (Integer.parseInt(String.valueOf(modal.getPackageStatus())) == 2){
            holder.packageStatus.setText("Yetkazildi");
        }else if (Integer.parseInt(String.valueOf(modal.getPackageStatus())) == 0){
            holder.packageStatus.setText("Kutilmoqda");
        }else if (Integer.parseInt(String.valueOf(modal.getPackageStatus())) == -2){
            holder.packageStatus.setText("Bekor qilindi");
        }
    }

    @Override
    public int getItemCount() {
        // returning the size of array list.
        return dataModalArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our
        // views of recycler items.
        private TextView customerName;
        private TextView customerPhone;
        private TextView packageStatus;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing the views of recycler views.
            customerName = itemView.findViewById(R.id.sellingDailName);
            customerPhone = itemView.findViewById(R.id.sellingDailyPhoneNumber);
            packageStatus = itemView.findViewById(R.id.sellingDailyPackageStatus);
        }
    }
}
