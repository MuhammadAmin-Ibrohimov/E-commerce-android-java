package com.example.lidertrade.admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lidertrade.R;
import com.example.lidertrade.admin.models.AdminBrandModel;
import com.example.lidertrade.admin.models.AdminProductModel;
import com.example.lidertrade.admin.models.AdminProductModelImage;

import java.util.ArrayList;

public class AdminProductFragmentProductAdapterImage extends RecyclerView.Adapter<AdminProductFragmentProductAdapterImage.MyViewHolder> {

    Context context;
    ArrayList<AdminProductModelImage> modelArrayList;
    OnItemClickListener mListener;

    public void setFilteredList(ArrayList<AdminProductModelImage> filteredList) {
        this.modelArrayList = filteredList;
    }


    public interface OnItemClickListener {
        void onDeleteClick(String url);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public AdminProductFragmentProductAdapterImage(Context context, ArrayList<AdminProductModelImage> modelArrayList) {
        this.context = context;
        this.modelArrayList = modelArrayList;
    }

    @NonNull
    @Override
    public AdminProductFragmentProductAdapterImage.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_product_fragment_product_list_dialog_image, parent, false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminProductFragmentProductAdapterImage.MyViewHolder holder, int position) {
        AdminProductModelImage model = modelArrayList.get(position);
        holder.cartActivityProductName.setText(model.getImageUrl());
        Glide.with(context).load(model.getImageUrl()).centerCrop().into(holder.cartActivityProductPic);



        int pos = holder.getAdapterPosition();
        String id = modelArrayList.get(pos).getImageUrl();
        holder.imageDeleteBtn.setOnClickListener(view -> {
            if (mListener != null){
                if (pos != RecyclerView.NO_POSITION){
                    mListener.onDeleteClick(id);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView cartActivityProductName;
        ImageView cartActivityProductPic, imageDeleteBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cartActivityProductName = itemView.findViewById(R.id.cartActivityProductName);
            cartActivityProductPic = itemView.findViewById(R.id.cartActivityProductPic);
            imageDeleteBtn = itemView.findViewById(R.id.imageDeleteBtn);
        }
    }
}
