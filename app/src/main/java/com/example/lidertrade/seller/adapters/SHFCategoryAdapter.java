package com.example.lidertrade.seller.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.lidertrade.seller.activities.SellerAllProductsActivity;
import com.example.lidertrade.seller.activities.SellerSubcategoryActivity;
import com.example.lidertrade.seller.models.SellerCategoryModel;

import java.util.ArrayList;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SHFCategoryAdapter extends RecyclerView.Adapter<SHFCategoryAdapter.ViewHolder> {
    ArrayList<SellerCategoryModel> homeModelArrayList;
    Context context;

    public SHFCategoryAdapter(ArrayList<SellerCategoryModel> homeModelArrayList, Context context) {
        this.homeModelArrayList = homeModelArrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public SHFCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.seller_home_fragment_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SHFCategoryAdapter.ViewHolder holder, int position) {
        SellerCategoryModel modal = homeModelArrayList.get(position);
        holder.categoryName.setText(modal.getCategoryName());
        Glide.with(context)
                .load(modal.getCategoryPic())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.categoryPic);
        if (Objects.equals(modal.getCategoryName(), "Barchasi")){
            holder.sHFCategoriesCardView.setOnClickListener(v -> {
                Intent intent = new Intent(context, SellerAllProductsActivity.class);
                context.startActivity(intent);
            });
        }
        else{
            if(modal.getSubcategories()==null){
                holder.itemView.setOnClickListener(v -> new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Xatolik mavjud!")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(SweetAlertDialog::cancel)
                        .show());
            }
            else if (modal.getSubcategories().toArray().length != 0){
                if (modal.getSubcategories().get(0) != "" ){
                    holder.sHFCategoriesCardView.setOnClickListener(v -> {
                        Intent intent = new Intent(context, SellerSubcategoryActivity.class);
                        intent.putExtra("category_modal", modal);
                        context.startActivity(intent);
                    });
                }else{
                    holder.itemView.setOnClickListener(v -> new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Xatolik mavjud!")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(SweetAlertDialog::cancel)
                            .show());
                }
            }
            else{
                holder.itemView.setOnClickListener(v -> new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Hozircha ushbu katalogda ma'lumot mavjud emas!")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(sweetAlertDialog2 -> sweetAlertDialog2.cancel())
                        .show());

            }
        }
    }

    @Override
    public int getItemCount() {
        return homeModelArrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        ImageView categoryPic;
        CardView sHFCategoriesCardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryName = itemView.findViewById(R.id.categoryName);
            categoryPic = itemView.findViewById(R.id.categoryPic);
            sHFCategoriesCardView = itemView.findViewById(R.id.sHFCategoriesCardView);
        }


    }
}
