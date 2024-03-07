
package com.example.lidertrade.admin.activities;

        import androidx.appcompat.app.AppCompatActivity;
        import androidx.appcompat.widget.Toolbar;
        import androidx.cardview.widget.CardView;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import android.content.Intent;
        import android.os.Bundle;
        import android.view.View;
        import android.view.Window;
        import android.view.WindowManager;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.lidertrade.R;
        import com.example.lidertrade.admin.adapters.AdminOrderHistoryActivityAdapter;
        import com.example.lidertrade.admin.models.AdminOrderModel;
        import com.example.lidertrade.admin.models.AdminSoldProductModel;
        import com.google.firebase.firestore.CollectionReference;
        import com.google.firebase.firestore.DocumentSnapshot;
        import com.google.firebase.firestore.FirebaseFirestore;

        import java.text.DecimalFormat;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.List;
        import java.util.Objects;

public class AdminOrderHistoryActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    DecimalFormat decim = new DecimalFormat("#,###.##");
    private CardView cancelTheOrderCard, trackTheOrderCard;
    private ArrayList<AdminSoldProductModel> pendingOrderDetailActivityModel;
    private AdminOrderHistoryActivityAdapter pendingOrderDetailActivityAdapter;
    RecyclerView pendingOrderDetailRecyclerView;
    Toolbar toolbar;
    ImageView deliverer_toolbar_logo;
    TextView orderListCustomerName, orderListCustomerAddress, orderListCustomerPhone, orderListPlacedTime,
            orderListTotalPrice, orderListSellerName, setSendingStatus, orderListPaymentStatus;
    private AdminOrderModel oPLFModel;
    CollectionReference orders, soldProducts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.admin_order_history_activity);


        deliverer_toolbar_logo = findViewById(R.id.deliverer_toolbar_logo);
        toolbar = findViewById(R.id.adminToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        deliverer_toolbar_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
// FireStore Collections
        db = FirebaseFirestore.getInstance();
        orders = db.collection("Orders");
        soldProducts = db.collection("SoldProducts");
        setSendingStatus = findViewById(R.id.setSendingStatus);

        Intent intent = getIntent();
        oPLFModel = (AdminOrderModel) intent.getSerializableExtra("orderModel");

        orderListCustomerName = findViewById(R.id.orderListCustomerName);
        orderListCustomerName.setText(oPLFModel.getCustomerName());
        orderListSellerName = findViewById(R.id.orderListSellerName);
        db.collection("Users").document(oPLFModel.getSellerId().toString()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.get("userName") != null){
                orderListSellerName.setText(Objects.requireNonNull(documentSnapshot.get("userName")).toString());
            }
            else{
                orderListSellerName.setText("Sotuvchi nomi berilmagan");
            }
        });
        orderListCustomerAddress = findViewById(R.id.orderListCustomerHouse);
        orderListCustomerAddress.setText(oPLFModel.getCustomerAddress());
        orderListCustomerPhone = findViewById(R.id.orderListCustomerPhone);
        orderListCustomerPhone.setText(oPLFModel.getCustomerPhone());
        orderListPlacedTime = findViewById(R.id.orderListPlacedTime);
        orderListPlacedTime.setText(getDate(oPLFModel.getOrderPlacedTime(), "HH:mm dd/MM/yyyy"));
        orderListTotalPrice = findViewById(R.id.orderListTotalPrice);

        orderListTotalPrice.setText(String.valueOf(oPLFModel.getCartTotalPrice())+" so'm");

        orderListPaymentStatus = findViewById(R.id.orderListPaymentStatus);
        if(oPLFModel.getPaymentStatus()>0){
            orderListPaymentStatus.setText("Qarzdorlik: "+decim.format(oPLFModel.getPaymentStatus())+" so'm");
        }
        else{
            orderListPaymentStatus.setText("To'lov amalga oshirilgan");
        }

        cancelTheOrderCard = findViewById(R.id.cancelTheOrderCard);
        trackTheOrderCard = findViewById(R.id.trackTheOrderCard);

        pendingOrderDetailRecyclerView = (RecyclerView) findViewById(R.id.pendingOrderDetailRecyclerView);
        pendingOrderDetailActivityModel = new ArrayList<>();
        pendingOrderDetailRecyclerView.setHasFixedSize(true);
        pendingOrderDetailRecyclerView.setLayoutManager(new LinearLayoutManager(AdminOrderHistoryActivity.this, LinearLayoutManager.VERTICAL, false));
        pendingOrderDetailActivityAdapter = new AdminOrderHistoryActivityAdapter(pendingOrderDetailActivityModel, this);
        pendingOrderDetailRecyclerView.setAdapter(pendingOrderDetailActivityAdapter);

        loadSoldProductData();


    }

    //    Method to load Sold Products
    private void loadSoldProductData() {
        soldProducts.whereEqualTo("orderId", oPLFModel.getOrderId()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot d : list) {
                    if (d.getData() != null){
                        AdminSoldProductModel dataModal = d.toObject(AdminSoldProductModel.class);
                        pendingOrderDetailActivityModel.add(dataModal);
                        int sum=0,i;
                        for(i=0;i< pendingOrderDetailActivityModel.size();i++){
                            sum=sum+(pendingOrderDetailActivityModel.get(i).getSoldProductPrice()*pendingOrderDetailActivityModel.get(i).getSoldProductQuantity());
                        }
                        orderListTotalPrice.setText((decim.format(sum)+" so'm"));
                        pendingOrderDetailActivityAdapter.notifyDataSetChanged();
                        db.collection("SoldProducts").document(d.getId()).update("soldProductQuantity",dataModal.getSoldProductQuantity());
                    }else{
                        Toast.makeText(AdminOrderHistoryActivity.this, "XATOOOOOO", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    // Method to cancel the order



    // Method to get Date from long
    public static String getDate(long milliSeconds, String dateFormat)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}