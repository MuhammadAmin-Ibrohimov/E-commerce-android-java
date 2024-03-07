package com.example.lidertrade.admin.adapters;

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
import com.example.lidertrade.admin.models.AdminTotalStatsModel;
import com.example.lidertrade.seller.activities.SellerAllProductsActivity;
import com.example.lidertrade.seller.activities.SellerSubcategoryActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminReportFragmentTotalAdapter extends RecyclerView.Adapter<AdminReportFragmentTotalAdapter.ViewHolder> {
    ArrayList<AdminTotalStatsModel> modelArrayList;
    Context context;

    DecimalFormat decim = new DecimalFormat("#,###.##");

    public AdminReportFragmentTotalAdapter(ArrayList<AdminTotalStatsModel> homeModelArrayList, Context context) {
        this.modelArrayList = homeModelArrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public AdminReportFragmentTotalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.admin_report_fragment_total_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdminReportFragmentTotalAdapter.ViewHolder holder, int position) {
        AdminTotalStatsModel modal = modelArrayList.get(position);
        long tBP = modal.getBoughtPrice();
        holder.selectedDate.setText(modal.getId());
        holder.totalQuantity.setText(String.format(decim.format(modal.getCreditSoldQuantity()+modal.getCashSoldQuantity())));
        holder.totalPrice.setText(String.format("%s so'm",decim.format(modal.getCreditPrice()+modal.getCashPrice())));
        holder.totalCashPrice.setText(String.format("%s so'm",decim.format(modal.getCashPrice())));
        holder.totalCreditPrice.setText(String.format("%s so'm",decim.format(modal.getCreditPrice())));
        holder.totalBoughtPrice.setText(String.format("%s so'm",decim.format(modal.getBoughtPrice())));
        holder.totalDifferencePrice.setText(String.format("%s so'm",decim.format(modal.getCreditPrice()+modal.getCashPrice()-modal.getBoughtPrice())));
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView selectedDate, totalQuantity, totalCashPrice, totalCreditPrice, totalPrice, totalBoughtPrice, totalDifferencePrice;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            selectedDate = itemView.findViewById(R.id.selectedDate);
            totalQuantity = itemView.findViewById(R.id.totalQuantity);
            totalCashPrice = itemView.findViewById(R.id.totalCashPrice);
            totalCreditPrice = itemView.findViewById(R.id.totalCreditPrice);
            totalPrice = itemView.findViewById(R.id.totalPrice);
            totalBoughtPrice = itemView.findViewById(R.id.totalBoughtPrice);
            totalDifferencePrice = itemView.findViewById(R.id.totalDifferencePrice);
        }


    }
}

