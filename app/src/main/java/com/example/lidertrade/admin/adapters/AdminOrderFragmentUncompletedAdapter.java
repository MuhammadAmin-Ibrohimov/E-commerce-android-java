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
import com.example.lidertrade.admin.models.AdminOrderModel;
import com.example.lidertrade.deliverer.activities.DelivererMapActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Calendar;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminOrderFragmentUncompletedAdapter extends RecyclerView.Adapter<AdminOrderFragmentUncompletedAdapter.ViewHolder>{

    private ArrayList<AdminOrderModel> dataModalArrayList;
    private AdminOrderFragmentUncompletedAdapter orderPendingListFragmentAdapter;
    private Context context;
    FirebaseFirestore db;
    CollectionReference order, soldProducts;


    // constructor class for our Adapter
    public AdminOrderFragmentUncompletedAdapter(ArrayList<AdminOrderModel> dataModalArrayList, Context context) {
        this.dataModalArrayList = dataModalArrayList;
        this.context = context;
    }

    public AdminOrderFragmentUncompletedAdapter() {

    }

    @NonNull
    @Override
    public AdminOrderFragmentUncompletedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new AdminOrderFragmentUncompletedAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.admin_order_fragment_uncompleted_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdminOrderFragmentUncompletedAdapter.ViewHolder holder, int position) {
        // setting data to our views in Recycler view items.
        AdminOrderModel modal = dataModalArrayList.get(position);
        holder.orderListCustomerAddress.setText(modal.getCustomerAddress());
        holder.orderListCustomerName.setText(modal.getCustomerName());

        if(modal.getPackageStatus() == 0){
            holder.orderListLinLay.setBackgroundColor(context.getColor(R.color.PendingOrders));
            holder.orderListStatus.setText("Kutilmoqda");
        }
        else if(modal.getPackageStatus() == 1) {
            holder.orderListLinLay.setBackgroundColor(context.getColor(R.color.SendingOrders));
            holder.orderListStatus.setText("Yetkazilmoqda");
        }
        FirebaseFirestore.getInstance().collection("Users").document(modal.getSellerId()).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists() && documentSnapshot.get("userName")!=null){
                holder.orderListSellerName.setText(Objects.requireNonNull(documentSnapshot.get("userName")).toString());
            }else {
                holder.orderListSellerName.setText("Sotuvchi nomi berilmagan");
            }
        });

        holder.orderListCustomerPhone.setText(modal.getCustomerPhone());
        DecimalFormat decim = new DecimalFormat("#,###.##");
        holder.orderListTotalPrice.setText(String.valueOf(decim.format(modal.getCartTotalPrice()))+" so'm");
        holder.orderListPlacedTime.setText(getDate(modal.getOrderPlacedTime(), "hh:mm dd/MM/yyyy"));
        System.out.println(modal.getPackageStatus());
        holder.checkTheOrderDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminOrderDetailActivity.class);
            intent.putExtra("orderModel", modal);
            context.startActivity(intent);
        });
        holder.locationIcon99.setOnClickListener(view -> dialogToAssignOrderToTrack(modal));
    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
    public void setFilteredList(ArrayList<AdminOrderModel> filteredList) {
        this.dataModalArrayList = filteredList;
    }

    @Override
    public int getItemCount() {
        // returning the size of array list.
        return dataModalArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our
        // views of recycler items.
        private TextView orderListCustomerAddress, orderListSellerName, orderListCustomerName,
                orderListCustomerPhone, orderListStatus, orderListPlacedTime, orderListTotalPrice;
        private CardView checkTheOrderDetails;
        private LinearLayout orderListLinLay;
        ImageView locationIcon99;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing the views of recycler views.
            orderListCustomerAddress = itemView.findViewById(R.id.orderListCustomerHouse);
            orderListCustomerName = itemView.findViewById(R.id.orderListCustomerName);
            orderListSellerName = itemView.findViewById(R.id.orderListSellerName);
            orderListCustomerPhone = itemView.findViewById(R.id.orderListCustomerPhone);
            orderListPlacedTime = itemView.findViewById(R.id.orderListPlacedTime);
            orderListTotalPrice = itemView.findViewById(R.id.orderListTotalPrice);
            checkTheOrderDetails = itemView.findViewById(R.id.checkTheOrderDetails);
            orderListStatus = itemView.findViewById(R.id.orderListStatus);
            orderListLinLay = itemView.findViewById(R.id.orderListLinLay);
            locationIcon99 = itemView.findViewById(R.id.homeIcon99);
        }
    }

    public void dialogToAssignOrderToTrack(AdminOrderModel modal){
        FirebaseFirestore.getInstance().collection("Orders").document(modal.getOrderId()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.get("sellerGeoPoint") != null){
                        new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                                .setContentText("Buyurtma qabul qilingan joyni ko'rishni xohlaysizmi")
                                .setCancelText("Yo'q!")
                                .setConfirmText("Ha!")
                                .showCancelButton(true)
                                .setCancelClickListener(SweetAlertDialog::cancel)
                                .setConfirmClickListener(sweetAlertDialog1 -> {
                                    Intent intent = new Intent(context, DelivererMapActivity.class);
                                    intent.putExtra("orderModel", modal);
                                    context.startActivity(intent);
                                })
                                .show();
                    }else {
                        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Eslatma!")
                                .setContentText("Buyurtma qabul qilingan joy belgilanmagan.")
                                .setConfirmText("OK!")
                                .setConfirmClickListener(SweetAlertDialog::cancel)
                                .show();
                    }
                });

    }
}

