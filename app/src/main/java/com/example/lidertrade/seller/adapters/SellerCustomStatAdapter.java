package com.example.lidertrade.seller.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.lidertrade.seller.models.SellerCustomStatModel;
import com.example.lidertrade.seller.activities.SellerAllProductsActivity;
import com.example.lidertrade.seller.activities.SellerSubcategoryActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SellerCustomStatAdapter extends RecyclerView.Adapter<SellerCustomStatAdapter.ViewHolder> {
    ArrayList<SellerCustomStatModel> modelArrayList;
    Context context;

    DecimalFormat decim = new DecimalFormat("#,###.##");

    public SellerCustomStatAdapter(ArrayList<SellerCustomStatModel> homeModelArrayList, Context context) {
        this.modelArrayList = homeModelArrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public SellerCustomStatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.seller_profile_fragment_custom_stat_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SellerCustomStatAdapter.ViewHolder holder, int position) {
        SellerCustomStatModel modal = modelArrayList.get(position);
        holder.selectedDate.setText(modal.getId());

        holder.totalCashQuantity.setText(String.format(decim.format(modal.getTotalCashQuantity())));
        holder.totalCashSalary.setText(String.format("%s so'm",decim.format(modal.getTotalCashSalary())));

        holder.totalCreditQuantity.setText(String.format(decim.format(modal.getTotalCreditQuantity())));
        holder.totalCreditSalary.setText(String.format("%s so'm",decim.format(modal.getTotalCreditSalary())));

        holder.totalSalary.setText(String.format("%s so'm",decim.format(modal.getTotalCashSalary()+modal.getTotalCreditSalary())));
     }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView selectedDate, totalCashQuantity, totalCashSalary, totalCreditQuantity,
                totalCreditSalary, totalSalary;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            selectedDate = itemView.findViewById(R.id.selectedDate);
            totalCashQuantity = itemView.findViewById(R.id.totalCashQuantity);
            totalCashSalary = itemView.findViewById(R.id.totalCashSalary);
            totalCreditQuantity = itemView.findViewById(R.id.totalCreditQuantity);
            totalCreditSalary = itemView.findViewById(R.id.totalCreditSalary);
            totalSalary = itemView.findViewById(R.id.totalSalary);
        }


    }
}

