package com.example.lidertrade.admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.models.AdminProductSpecificationsModel;

import java.util.ArrayList;

public class AdminProductFragmentProductAdapterSpec extends RecyclerView.Adapter<AdminProductFragmentProductAdapterSpec.MyViewHolder> {

    Context context;
    ArrayList<AdminProductSpecificationsModel> modelArrayList;
    OnItemClickListener mListener;

    public void setFilteredList(ArrayList<AdminProductSpecificationsModel> filteredList) {
        this.modelArrayList = filteredList;
    }


    public interface OnItemClickListener {
        void onDeleteClick(String url);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public AdminProductFragmentProductAdapterSpec(Context context, ArrayList<AdminProductSpecificationsModel> modelArrayList) {
        this.context = context;
        this.modelArrayList = modelArrayList;
    }

    @NonNull
    @Override
    public AdminProductFragmentProductAdapterSpec.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_product_fragment_product_list_dialog_spec, parent, false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminProductFragmentProductAdapterSpec.MyViewHolder holder, int position) {
        AdminProductSpecificationsModel model = modelArrayList.get(position);
        holder.specName.setText(model.getField());
        holder.specValue.setText(model.getName());

        int pos = holder.getAdapterPosition();
        String id = modelArrayList.get(pos).getField();
        holder.specListDelete.setOnClickListener(view -> {
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
        TextView specName, specValue;
        ImageView specListDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            specName = itemView.findViewById(R.id.specName);
            specValue = itemView.findViewById(R.id.specValue);
            specListDelete = itemView.findViewById(R.id.specListDelete);
        }
    }
}
