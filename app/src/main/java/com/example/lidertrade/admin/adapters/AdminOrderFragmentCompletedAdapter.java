package com.example.lidertrade.admin.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.activities.AdminOrderDetailActivity;
import com.example.lidertrade.admin.activities.AdminOrderHistoryActivity;
import com.example.lidertrade.admin.models.AdminOrderModel;
import com.example.lidertrade.deliverer.activities.DelivererMapActivity;
import com.example.lidertrade.deliverer.activities.DelivererPendingDetailActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminOrderFragmentCompletedAdapter extends RecyclerView.Adapter<AdminOrderFragmentCompletedAdapter.ViewHolder>{

    private ArrayList<AdminOrderModel> dataModalArrayList;
    private AdminOrderFragmentCompletedAdapter orderPendingListFragmentAdapter;
    private Context context;
    DecimalFormat decim = new DecimalFormat("#,###.##");
    FirebaseFirestore db;
    CollectionReference order, soldProducts;

    // constructor class for our Adapter
    public AdminOrderFragmentCompletedAdapter(ArrayList<AdminOrderModel> dataModalArrayList, Context context) {
        this.dataModalArrayList = dataModalArrayList;
        this.context = context;
    }

    public void setFilteredList(ArrayList<AdminOrderModel> filteredList) {
        this.dataModalArrayList = filteredList;
    }

    public AdminOrderFragmentCompletedAdapter() {

    }

    @NonNull
    @Override
    public AdminOrderFragmentCompletedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new AdminOrderFragmentCompletedAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.admin_order_fragment_completed_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdminOrderFragmentCompletedAdapter.ViewHolder holder, int position) {
        // setting data to our views in Recycler view items.
        AdminOrderModel modal = dataModalArrayList.get(position);
        holder.orderListCustomerName.setText(modal.getCustomerName());

        if(modal.getPackageStatus() == 2){
            holder.orderListLinLay.setBackgroundColor(context.getColor(R.color.CompletedOrders));
            holder.orderListStatus.setText("To'lov Yakunlangan");
            holder.orderListPackageStatus.setText("Buyurtma Yetkazilgan");
        }
        else if(modal.getPackageStatus() == -2) {
            holder.orderListLinLay.setBackgroundColor(context.getColor(R.color.CanceledOrders));
            holder.orderListStatus.setText("Bekor Qilingan");
            holder.orderListPackageStatus.setText("Buyurtma Bekor Qilingan");
        }
        else if(modal.getPackageStatus() == -1) {
            holder.orderListLinLay.setBackgroundColor(context.getColor(R.color.PendingOrders));
            holder.orderListStatus.setText(String.valueOf(decim.format(modal.getPaymentStatus()))+" so'm");
            holder.orderListPackageStatus.setText("Qarzdorligi Bor");
        }
        FirebaseFirestore.getInstance().collection("Users").document(modal.getSellerId()).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists() && documentSnapshot.get("userName")!=null){
                holder.orderListSellerName.setText(Objects.requireNonNull(documentSnapshot.get("userName")).toString());
            }else {
                holder.orderListSellerName.setText("Sotuvchi nomi berilmagan");
            }
        });
        holder.orderListTotalPrice.setText(String.valueOf(decim.format(modal.getCartTotalPrice()))+" so'm");
        holder.orderListPlacedTime.setText(getDate(modal.getOrderPlacedTime(), "hh:mm dd/MM/yyyy"));
        holder.checkTheOrderDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminOrderHistoryActivity.class);
            intent.putExtra("orderModel", modal);
            context.startActivity(intent);
        });

    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


    @Override
    public int getItemCount() {
        // returning the size of array list.
        return dataModalArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our
        // views of recycler items.
        private TextView  orderListSellerName, orderListCustomerName, orderListPackageStatus,
                 orderListStatus, orderListPlacedTime, orderListTotalPrice;
        private CardView checkTheOrderDetails;
        private LinearLayout orderListLinLay;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing the views of recycler views.
            orderListCustomerName = itemView.findViewById(R.id.orderListCustomerName);
            orderListSellerName = itemView.findViewById(R.id.orderListSellerName);
            orderListPlacedTime = itemView.findViewById(R.id.orderListCompletedTime);
            orderListTotalPrice = itemView.findViewById(R.id.orderListTotalPrice);
            checkTheOrderDetails = itemView.findViewById(R.id.checkTheOrderDetails);
            orderListStatus = itemView.findViewById(R.id.orderListPaymentStatus);
            orderListPackageStatus = itemView.findViewById(R.id.orderListPackageStatus);
            orderListLinLay = itemView.findViewById(R.id.orderListLinLay);
        }
    }
}

