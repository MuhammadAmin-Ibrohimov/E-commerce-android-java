package com.example.lidertrade.deliverer.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.models.AdminOrderModel;
import com.example.lidertrade.deliverer.activities.DelivererSendingDetailActivity;
import com.example.lidertrade.deliverer.models.DelivererPendingFragmentModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DelivererSendingFragmentAdapter extends RecyclerView.Adapter<DelivererSendingFragmentAdapter.ViewHolder>{

    private ArrayList<AdminOrderModel> dataModalArrayList;
    private Context context;


    // constructor class for our Adapter
    public DelivererSendingFragmentAdapter(ArrayList<AdminOrderModel> dataModalArrayList, Context context) {
        this.dataModalArrayList = dataModalArrayList;
        this.context = context;
    }

    public DelivererSendingFragmentAdapter() {

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.deliverer_sending_fragment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // setting data to our views in Recycler view items.
        AdminOrderModel modal = dataModalArrayList.get(position);
        holder.orderSendingListCustomerAddress.setText(modal.getCustomerAddress());
        holder.orderSendingListCustomerName.setText(modal.getCustomerName());
        holder.orderSendingListCustomerPhone.setText(modal.getCustomerPhone());
        holder.completeSelectedOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DelivererSendingDetailActivity.class);
                intent.putExtra("orderModel", modal);
                context.startActivity(intent);
            }
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
        private TextView orderSendingListCustomerAddress, orderSendingListCustomerName, orderSendingListCustomerPhone;
        private CardView completeSelectedOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing the views of recycler views.
            orderSendingListCustomerAddress = itemView.findViewById(R.id.orderSendingListCustomerAddress);
            orderSendingListCustomerName = itemView.findViewById(R.id.orderSendingListCustomerName);
            orderSendingListCustomerPhone = itemView.findViewById(R.id.orderSendingListCustomerPhone);
            completeSelectedOrder = itemView.findViewById(R.id.completeSelectedOrder);
        }
    }
}

