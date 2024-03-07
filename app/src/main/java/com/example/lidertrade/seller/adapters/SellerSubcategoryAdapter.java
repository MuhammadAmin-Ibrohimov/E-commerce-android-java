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
import com.example.lidertrade.seller.activities.SellerProductListActivity;
import com.example.lidertrade.seller.models.SellerSubcategoryModel;

import java.util.ArrayList;

public class SellerSubcategoryAdapter extends RecyclerView.Adapter<SellerSubcategoryAdapter.ViewHolder> {
    private ArrayList<SellerSubcategoryModel> subcategoryModelArrayList;
    private Context context;


    // constructor class for our Adapter
    public SellerSubcategoryAdapter(ArrayList<SellerSubcategoryModel> subcategoryModelArrayList, Context context) {
        this.subcategoryModelArrayList = subcategoryModelArrayList;
        this.context = context;
    }

    public SellerSubcategoryAdapter() {

    }

    @NonNull
    @Override
    public SellerSubcategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.seller_subcategory_activity_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SellerSubcategoryAdapter.ViewHolder holder, int position) {
        // setting data to our views in Recycler view items.
        SellerSubcategoryModel modal = subcategoryModelArrayList.get(position);
        holder.subcategoryName.setText(modal.getSubcategoryName());
        Glide.with(context)
                .load(modal.getSubcategoryPic())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.subcategoryPic);
        if (modal.getProducts().toArray().length != 0){
            if (modal.getProducts().get(0) != "" ){
                holder.subcategorySubCategoriesCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, SellerProductListActivity.class);
                        intent.putStringArrayListExtra("products_list", modal.getProducts());
                        intent.putExtra("subcategory_name", modal.getSubcategoryName());
                        context.startActivity(intent);
                    }
                });
            }else{
                holder.subcategorySubCategoriesCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "Nomlanmagan maxsulot mavjud. Iltimos avval to'g'rilang!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }else{
            holder.subcategorySubCategoriesCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Bu katalogda hech qanday maxsulot yo'q!", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        // returning the size of array list.
        return subcategoryModelArrayList.size();
    }

    public void setFilteredList(ArrayList<SellerSubcategoryModel> filteredList){
        this.subcategoryModelArrayList = filteredList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our
        // views of recycler items.
        private TextView subcategoryName;
        private ImageView subcategoryPic;
        private CardView subcategorySubCategoriesCardView;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing the views of recycler views.
            subcategoryName = itemView.findViewById(R.id.subcategoryName);
            subcategoryPic = itemView.findViewById(R.id.subcategoryPic);
            subcategorySubCategoriesCardView = itemView.findViewById(R.id.subcategorySubCategoriesCardView);
        }
    }
}