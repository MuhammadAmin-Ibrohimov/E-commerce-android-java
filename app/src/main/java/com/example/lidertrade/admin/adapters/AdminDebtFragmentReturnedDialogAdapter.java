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

public class AdminDebtFragmentReturnedDialogAdapter extends RecyclerView.Adapter<AdminDebtFragmentReturnedDialogAdapter.MyViewHolder> {

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

    public AdminDebtFragmentReturnedDialogAdapter(Context context, ArrayList<AdminProductSpecificationsModel> modelArrayList) {
        this.context = context;
        this.modelArrayList = modelArrayList;
    }

    @NonNull
    @Override
    public AdminDebtFragmentReturnedDialogAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_debt_fragment_returned_dialog_item, parent, false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminDebtFragmentReturnedDialogAdapter.MyViewHolder holder, int position) {
        AdminProductSpecificationsModel model = modelArrayList.get(position);
        holder.dateOfComplaint.setText(model.getField());
        holder.causeOfReturning.setText(model.getName());

    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dateOfComplaint, causeOfReturning;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dateOfComplaint = itemView.findViewById(R.id.dateOfComplaint);
            causeOfReturning = itemView.findViewById(R.id.causeOfReturning);
        }
    }
}

