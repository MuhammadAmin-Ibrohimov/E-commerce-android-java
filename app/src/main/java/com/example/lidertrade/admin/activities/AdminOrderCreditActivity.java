package com.example.lidertrade.admin.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lidertrade.R;
import com.example.lidertrade.deliverer.adapters.DelivererCreditActivityAdapter;
import com.example.lidertrade.deliverer.models.DelivererCreditModel;
import com.example.lidertrade.seller.models.SellerCreditProductsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminOrderCreditActivity extends AppCompatActivity {
    private EditText cashTextInput, cardTextInput,clickTextInput, bankTextInput;
    private CardView completeTheOrderCard, cancelTheOrderCard;

    TextView CustomerHouse, CustomerStreet, CustomerVillage, CustomerDistrict, CustomerName, CustomerPhone,
    CustomerPhone2, CustomerBirthDay, CustomerPassport, CustomerPassportGivenDate, CustomerGivenPlace,
    CustomerWorkPlace, CustomerStatus, CustomerSalary, CustomerCreditType, CustomerCreditDate,
            CustomerCreditInitialPayment;
    private ArrayList<SellerCreditProductsModel> sendingOrderDetailActivityModel;
    private DelivererCreditActivityAdapter sendingOrderDetailActivityAdapter;
    private RecyclerView sendingOrderDetailRecyclerView;
    int cashPricePercent, creditPricePercent;
    DecimalFormat decim = new DecimalFormat("#,###.##");
    private TextView orderTotalPrice ;
    private DelivererCreditModel oSLFModel;
    private FirebaseFirestore db;
    String idForStats, myFormat;
    SimpleDateFormat dateFormat;
    Date date;
    CollectionReference orders, creditProducts,debtBook, products, statProducts, statSeller;
    DocumentReference statProductsDoc, statSellerDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.admin_order_fragment_credit_activity);

        db = FirebaseFirestore.getInstance();
        orders = db.collection("Credits");
        creditProducts = db.collection("CreditProducts");
        products = db.collection("products");
        statProducts = db.collection("StatProducts");
        statSeller = db.collection("StatSellers");
        myFormat="dd-MM-yyyy";
        dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        date = new Date();
        idForStats = String.valueOf((dateFormat.format(date)));
        statProductsDoc = statProducts.document(idForStats);
        statSellerDoc = statSeller.document(idForStats);


        Intent intent = getIntent();
        oSLFModel = (DelivererCreditModel) intent.getSerializableExtra("orderModel");
        orderTotalPrice = findViewById(R.id.orderTotalPrice);
        cancelTheOrderCard = findViewById(R.id.cancelTheOrderCard);
        completeTheOrderCard = findViewById(R.id.completeTheOrderCard);

        CustomerHouse = findViewById(R.id.CustomerHouse);
        CustomerHouse.setText(oSLFModel.getCustomerHouse());
        CustomerBirthDay = findViewById(R.id.CustomerBirthDay);
        CustomerBirthDay.setText(oSLFModel.getCustomerBirthDay());
        CustomerStreet = findViewById(R.id.CustomerStreet);
        CustomerStreet.setText(oSLFModel.getCustomerStreet());
        CustomerVillage = findViewById(R.id.CustomerVillage);
        CustomerVillage.setText(oSLFModel.getCustomerVillage());
        CustomerDistrict = findViewById(R.id.CustomerDistrict);
        CustomerDistrict.setText(oSLFModel.getCustomerDistrict());
        CustomerName = findViewById(R.id.CustomerName);
        CustomerName.setText(oSLFModel.getCustomerName());
        CustomerPhone = findViewById(R.id.CustomerPhone);
        CustomerPhone.setText(oSLFModel.getCustomerPhoneNumber1());
        CustomerPhone2 = findViewById(R.id.CustomerPhone2);
        CustomerPhone2.setText(oSLFModel.getCustomerPhoneNumber2());
        CustomerPassport = findViewById(R.id.CustomerPassport);
        CustomerPassport.setText(oSLFModel.getCustomerPassport());
        CustomerPassportGivenDate = findViewById(R.id.CustomerPassportGivenDate);
        CustomerPassportGivenDate.setText(oSLFModel.getCustomerPassportGivenDate());
        CustomerGivenPlace = findViewById(R.id.CustomerGivenPlace);
        CustomerGivenPlace.setText(oSLFModel.getCustomerPassportGiven());
        CustomerWorkPlace = findViewById(R.id.CustomerWorkPlace);
        CustomerWorkPlace.setText(oSLFModel.getCustomerJob());
        CustomerStatus = findViewById(R.id.CustomerStatus);
        CustomerStatus.setText(oSLFModel.getCustomerStatus());
        CustomerSalary = findViewById(R.id.CustomerSalary);
        CustomerSalary.setText(oSLFModel.getCustomerSalary());
        CustomerCreditType = findViewById(R.id.CustomerCreditType);
        CustomerCreditType.setText(oSLFModel.getCreditType());
        CustomerCreditInitialPayment = findViewById(R.id.CustomerCreditInitialPayment);
        CustomerCreditInitialPayment.setText(String.format(" %s so'm", decim.format(oSLFModel.getCustomerInitialPayment())));

        CustomerCreditDate = findViewById(R.id.CustomerCreditDate);
        CustomerCreditDate.setText(getDate(oSLFModel.getOrderCompletedTime(), "hh:mm dd/MM/yyyy"));
        orderTotalPrice.setText(String.format("Umumiy summa: %s so'm", decim.format(oSLFModel.getCartTotalPrice())));

        sendingOrderDetailRecyclerView = (RecyclerView) findViewById(R.id.creditDetailRecyclerView);


        sendingOrderDetailActivityModel = new ArrayList<>();
        sendingOrderDetailRecyclerView.setHasFixedSize(true);
        sendingOrderDetailRecyclerView.setLayoutManager(new LinearLayoutManager(AdminOrderCreditActivity.this, LinearLayoutManager.VERTICAL, false));
        sendingOrderDetailActivityAdapter = new DelivererCreditActivityAdapter(sendingOrderDetailActivityModel, this);
        sendingOrderDetailRecyclerView.setAdapter(sendingOrderDetailActivityAdapter);

        completeTheOrderCard.setVisibility(View.INVISIBLE);
        cancelTheOrderCard.setVisibility(View.INVISIBLE);

        loadSoldProductData();

    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }





    // LOAD SOLD_PRODUCT COLLECTION
    private void loadSoldProductData() {
        creditProducts.whereEqualTo("orderId", oSLFModel.getOrderId()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot d : list) {
                    if (d.getData() != null){
                        SellerCreditProductsModel dataModal = d.toObject(SellerCreditProductsModel.class);
                        sendingOrderDetailActivityModel.add(dataModal);
                        sendingOrderDetailActivityAdapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(AdminOrderCreditActivity.this, "XATOOOOOO", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}