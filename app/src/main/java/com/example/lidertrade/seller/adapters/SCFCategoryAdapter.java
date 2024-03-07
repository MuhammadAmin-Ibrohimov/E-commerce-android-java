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
import com.example.lidertrade.seller.activities.SellerSubcategoryActivity;
import com.example.lidertrade.seller.models.SellerCategoryModel;

import java.util.ArrayList;

public class SCFCategoryAdapter extends RecyclerView.Adapter<SCFCategoryAdapter.ViewHolder> {


    private ArrayList<SellerCategoryModel> sellerCategoryModelArrayList;
    Context context;

    public SCFCategoryAdapter(ArrayList<SellerCategoryModel> sellerCategoryModelArrayList, Context context) {
        this.sellerCategoryModelArrayList = sellerCategoryModelArrayList;
        this.context = context;
    }

    public SCFCategoryAdapter() {

    }

    @NonNull
    @Override
    public SCFCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.seller_category_fragment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SellerCategoryModel modal = sellerCategoryModelArrayList.get(position);
        holder.categoryName.setText(modal.getCategoryName());
        Glide.with(context)
                .load(modal.getCategoryPic())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.categoryPic);
        if(modal.getSubcategories()!=null){
            if (modal.getSubcategories().toArray().length != 0){
                if (modal.getSubcategories().get(0) != "" ){
                    holder.sCFCategoriesCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, SellerSubcategoryActivity.class);
                            intent.putExtra("category_modal", modal);
                            context.startActivity(intent);
                        }
                    });
                }else{
                    holder.sCFCategoriesCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "Nomlanmagan bo'lim mavjud. Iltimos avval to'g'rilang!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            else{
                holder.sCFCategoriesCard.setOnClickListener(v -> Toast.makeText(context, "Bu katalogda hech qanday ma'lumot yo'q!", Toast.LENGTH_SHORT).show());
            }
        }

    }


    @Override
    public int getItemCount() {
        return sellerCategoryModelArrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our
        // views of recycler items.
        private TextView categoryName;
        private ImageView categoryPic;
        private CardView sCFCategoriesCard;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing the views of recycler views.
            categoryName = itemView.findViewById(R.id.categoryName);
            categoryPic = itemView.findViewById(R.id.categoryPic);
            sCFCategoriesCard = itemView.findViewById((R.id.sCFCategoriesCard));
        }
    }
}


