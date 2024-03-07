package com.example.lidertrade.seller.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lidertrade.R;
import com.example.lidertrade.seller.models.SHFSaleModel;

import java.util.ArrayList;

public class SHFSaleAdapter extends RecyclerView.Adapter<SHFSaleAdapter.ViewHolder> {

    ArrayList<SHFSaleModel> homeSaleModelArrayList;
    Context context;

    public SHFSaleAdapter(ArrayList<SHFSaleModel> homeSaleModelArrayList, Context context) {
        this.homeSaleModelArrayList = homeSaleModelArrayList;
        this.context = context;
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.seller_home_fragment_sale_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SHFSaleModel modal = homeSaleModelArrayList.get(position);
        holder.saleName.setText(modal.getSaleName());
        holder.saleDescription.setText(modal.getDescription());
        Glide.with(context)
                .load(modal.getSalePic())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.salePic);

        holder.saleSeeAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setting on click listener
                // for our items of recycler items.
                Toast.makeText(context, "Clicked item is " + modal.getSaleName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return homeSaleModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView saleName;
        TextView saleDescription;
        ImageView salePic;
        CardView saleSeeAllBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            saleName = itemView.findViewById(R.id.saleName);
            saleDescription = itemView.findViewById(R.id.saleDescription);
            salePic = itemView.findViewById(R.id.salePic);
            saleSeeAllBtn = itemView.findViewById((R.id.saleSeeAllBtn));

        }
    }
}
