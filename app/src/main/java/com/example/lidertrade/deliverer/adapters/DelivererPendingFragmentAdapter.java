package com.example.lidertrade.deliverer.adapters;

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

import com.example.lidertrade.R;
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

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DelivererPendingFragmentAdapter extends RecyclerView.Adapter<DelivererPendingFragmentAdapter.ViewHolder>{

    private ArrayList<AdminOrderModel> dataModalArrayList;
    private DelivererPendingFragmentAdapter orderPendingListFragmentAdapter;
    private Context context;
    FirebaseFirestore db;
    CollectionReference order, soldProducts;


    // constructor class for our Adapter
    public DelivererPendingFragmentAdapter(ArrayList<AdminOrderModel> dataModalArrayList, Context context) {
        this.dataModalArrayList = dataModalArrayList;
        this.context = context;
    }

    public DelivererPendingFragmentAdapter() {

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.deliverer_pending_fragment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // setting data to our views in Recycler view items.
        AdminOrderModel modal = dataModalArrayList.get(position);
        holder.orderListCustomerAddress.setText(modal.getCustomerAddress());
        holder.orderListCustomerName.setText(modal.getCustomerName());
        holder.orderListCustomerPhone.setText(modal.getCustomerPhone());
        DecimalFormat decim = new DecimalFormat("#,###.##");
        holder.orderListTotalPrice.setText(String.format("%s so'm",decim.format((modal.getCartTotalPrice()))));
        holder.orderListPlacedTime.setText(getDate(modal.getOrderPlacedTime(), "hh:mm dd/MM/yyyy"));
        loadSoldProductsData(modal.getOrderId());
        holder.checkTheOrderDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, DelivererPendingDetailActivity.class);
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


    @Override
    public int getItemCount() {
        // returning the size of array list.
        return dataModalArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our
        // views of recycler items.
        private TextView orderListCustomerAddress, orderListCustomerName, orderListCustomerPhone, orderListPlacedTime, orderListTotalPrice;
        private CardView checkTheOrderDetails;
        ImageView locationIcon99;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing the views of recycler views.
            orderListCustomerAddress = itemView.findViewById(R.id.orderListCustomerHouse);
            orderListCustomerName = itemView.findViewById(R.id.orderListCustomerName);
            orderListCustomerPhone = itemView.findViewById(R.id.orderListCustomerPhone);
            orderListPlacedTime = itemView.findViewById(R.id.orderListPlacedTime);
            orderListTotalPrice = itemView.findViewById(R.id.orderListTotalPrice);
            checkTheOrderDetails = itemView.findViewById(R.id.checkTheOrderDetails);
            locationIcon99 = itemView.findViewById(R.id.homeIcon99);
        }
    }

    public void loadSoldProductsData(String orderId){
        db = FirebaseFirestore.getInstance();
        db.collection("soldProducts").whereEqualTo("orderId", orderId).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot d : list) {
                    if (d.exists()) {
                        System.out.println(d.getData());
                    }
                }
            }
        });
    }
    public void dialogToAssignOrderToTrack(AdminOrderModel modal){
        Task ss = db.collection("Orders").document(modal.getOrderId()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.get("sellerGeoPoint") != null){
                        new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText("Eslatma!")
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
                                .setContentText("Buyurtma qabul qilingan joy belgilanmagan.")
                                .setConfirmText("OK!")
                                .setConfirmClickListener(SweetAlertDialog::cancel)
                                .show();
                    }
                });

    }
}

