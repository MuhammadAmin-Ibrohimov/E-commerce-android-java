package com.example.lidertrade.deliverer.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.models.AdminOrderModel;
import com.example.lidertrade.deliverer.adapters.DelivererPendingActivityAdapter;
import com.example.lidertrade.deliverer.models.DelivererPendingActivityModel;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class DelivererPendingDetailActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    ProgressBar progressBar;
    String idForStats, myFormat, mySalaryFormat, idForSalary;
    SimpleDateFormat dateFormat, salaryDateFormat;
    Date date;
    private CardView cancelTheOrderCard, trackTheOrderCard;
    private ArrayList<DelivererPendingActivityModel> pendingOrderDetailActivityModel;
    private DelivererPendingActivityAdapter pendingOrderDetailActivityAdapter;
    RecyclerView pendingOrderDetailRecyclerView;
    int cashPricePercent, creditPricePercent;
    TextView orderListCustomerName, orderListCustomerAddress, orderListCustomerPhone, orderListPlacedTime,
            orderListTotalPrice;
    private AdminOrderModel oPLFModel;
    CollectionReference orders, soldProducts, products, statProducts, statSeller, sellersSalary;
    DocumentReference statProductsDoc, statSellerDoc, sellerSalaryDoc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.deliverer_pending_detail_activity);
// FireStore Collections
        db = FirebaseFirestore.getInstance();
        orders = db.collection("Orders");
        soldProducts = db.collection("SoldProducts");
        products = db.collection("products");
        statProducts = db.collection("StatProducts");
        statSeller = db.collection("StatSellers");
        sellersSalary = db.collection("SellersSalary");

        myFormat="dd-MM-yyyy";
        mySalaryFormat="MM-yyyy";

        dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        salaryDateFormat = new SimpleDateFormat(mySalaryFormat, Locale.US);
        date = new Date();
        idForStats = String.valueOf((dateFormat.format(date)));
        idForSalary = String.valueOf((salaryDateFormat.format(date)));
        statProductsDoc = statProducts.document(idForStats);
        statSellerDoc = statSeller.document(idForStats);
        sellerSalaryDoc = sellersSalary.document(idForSalary);

        Intent intent = getIntent();
        oPLFModel = (AdminOrderModel) intent.getSerializableExtra("orderModel");
        orderListCustomerName = findViewById(R.id.orderListCustomerName);
        orderListCustomerName.setText(oPLFModel.getCustomerName());
        orderListCustomerAddress = findViewById(R.id.orderListCustomerHouse);
        orderListCustomerAddress.setText(oPLFModel.getCustomerAddress());
        orderListCustomerPhone = findViewById(R.id.orderListCustomerPhone);
        orderListCustomerPhone.setText(oPLFModel.getCustomerPhone());
        orderListPlacedTime = findViewById(R.id.orderListPlacedTime);
        orderListPlacedTime.setText(getDate(oPLFModel.getOrderPlacedTime(), "HH:mm dd/MM/yyyy"));
        orderListTotalPrice = findViewById(R.id.orderListTotalPrice);
        orderListTotalPrice.setText(String.valueOf(oPLFModel.getCartTotalPrice())+" so'm");

        cancelTheOrderCard = findViewById(R.id.cancelTheOrderCard);
        trackTheOrderCard = findViewById(R.id.trackTheOrderCard);
        progressBar = findViewById(R.id.progressBar);

        pendingOrderDetailRecyclerView = (RecyclerView) findViewById(R.id.pendingOrderDetailRecyclerView);
        pendingOrderDetailActivityModel = new ArrayList<>();
        pendingOrderDetailRecyclerView.setHasFixedSize(true);
        pendingOrderDetailRecyclerView.setLayoutManager(new LinearLayoutManager(DelivererPendingDetailActivity.this, LinearLayoutManager.VERTICAL, false));
        pendingOrderDetailActivityAdapter = new DelivererPendingActivityAdapter(this,pendingOrderDetailActivityModel, this, orderListTotalPrice);
        pendingOrderDetailRecyclerView.setAdapter(pendingOrderDetailActivityAdapter);

        getSellerBenefit();
        loadSoldProductData();
        cancelTheOrder();
        trackTheOrder();

    }

    private void getSellerBenefit() {
        db.collection("Users").document(oPLFModel.getSellerId()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot d = task.getResult();
                if(d.exists()){
                    cashPricePercent = Integer.parseInt(d.get("cashSalaryPercent").toString());
                    creditPricePercent = Integer.parseInt(d.get("creditSalaryPercent").toString());
                }
            }
        });
    }

    private void loadSoldProductData() {
        soldProducts.whereEqualTo("orderId", oPLFModel.getOrderId()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot d : list) {
                    if (d.getData() != null){
                        DelivererPendingActivityModel dataModal = d.toObject(DelivererPendingActivityModel.class);
                        pendingOrderDetailActivityModel.add(dataModal);
                        int sum=0,i;
                        for(i=0;i< pendingOrderDetailActivityModel.size();i++){
                            sum=sum+(pendingOrderDetailActivityModel.get(i).getSoldProductPrice()*pendingOrderDetailActivityModel.get(i).getSoldProductQuantity());
                        }
                        DecimalFormat decim = new DecimalFormat("#,###.##");
                        orderListTotalPrice.setText(String.format("%s so'm",decim.format(sum)));
                        pendingOrderDetailActivityAdapter.notifyDataSetChanged();
                        db.collection("SoldProducts").document(d.getId()).update("soldProductQuantity",dataModal.getSoldProductQuantity());
                    }else{
                        Toast.makeText(DelivererPendingDetailActivity.this, "XATOOOOOO", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void trackTheOrder() {
        trackTheOrderCard.setOnClickListener(view -> {
            new SweetAlertDialog(DelivererPendingDetailActivity.this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("So'rovnoma")
                    .setContentText("Buyurtmani yetkazish uchun belgilaysizmi?")
                    .setCancelText("Yo'q!")
                    .setConfirmText("Ha!")
                    .showCancelButton(true)
                    .setCancelClickListener(SweetAlertDialog::cancel)
                    .setConfirmClickListener(sweetAlertDialog1 -> {
                        ArrayList<String> spList = (oPLFModel.getSoldProductsList());
                        ArrayList<String> pList = (oPLFModel.getProductsList());
                        System.out.println(oPLFModel.getProductsList() );
                        addToProductStatistics(spList, pList);
                        orders.document(oPLFModel.getOrderId()).update("packageStatus", 1);
                        new SweetAlertDialog(DelivererPendingDetailActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setContentText("Buyurtma yetkazish uchun belgilandi!")
                                .setConfirmText("OK!")
                                .setConfirmClickListener(sweetAlertDialog2 -> {
                                    sweetAlertDialog2.cancel();
                                    Intent intent = new Intent(DelivererPendingDetailActivity.this, DelivererMainActivity.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .show();
                        sweetAlertDialog1.cancel();
                    })
                    .show();
            pendingOrderDetailActivityAdapter.notifyDataSetChanged();
        });
    }

    private void addToProductStatistics(ArrayList<String> spList, ArrayList<String> pList) {
        if(spList.size()>0 && spList.size()==pList.size()){
            for(String spId:spList){
                soldProducts.document(spId).get().addOnSuccessListener(d -> {
                    if(d.exists()){
                        if(Integer.parseInt(d.get("soldProductQuantity").toString())>0){
                            prodStat(d);

                        }
                        else{
                            orders.document(oPLFModel.getOrderId()).update("soldProductsList", FieldValue.arrayRemove(d.get("soldProductId")),
                                    "productsList", FieldValue.arrayRemove(d.get("productId")));
                            d.getReference().delete();
                            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                                    .setContentText("Miqdori berilmagan maxsulot buyurtmalar ro'yxatidan o'chirib tashlandi'!")
                                    .setConfirmText("OK!")
                                    .setConfirmClickListener(sweetAlertDialog2 -> {
                                        sweetAlertDialog2.cancel();
                                    })
                                    .show();
                        }
                    }
                });
            }
            sellerStat(pList,spList);
        }

    }

    private void sellerStat(ArrayList<String> pList, ArrayList<String> spList) {
        if (pList.size()>0 && spList.size()==pList.size()){
            statSellerDoc.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    DocumentSnapshot d = task.getResult();
                    if(d.exists()){
                        Log.d(TAG, "d exists!");
                        if(d.getData().containsKey(oPLFModel.getSellerId())){
                            Log.d(TAG, "id contains!");
                            Map<String, Object> map = d.getData();
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                if(Objects.equals(oPLFModel.getSellerId(), entry.getKey())){
                                    Log.d(TAG, "id equals!");
                                    Map<String, Object> map2 = (Map<String, Object>) entry.getValue();
                                    final long[] totalCaBP = {0};
                                    final long[] totalCaS = {0};
                                    final long[] totalCrBP = {0};
                                    final long[] totalCrS = {0};
                                    final long[] totalCaP = {0};
                                    final long[] totalCrP = {0};
                                    final long[] totalCrQ = {0};
                                    final long[] totalCaQ = {0};
                                    int i=0;
                                    for (Map.Entry<String, Object> entry2 : map2.entrySet()){
                                        if(Objects.equals(entry2.getKey().toString(), "cashSalaryPercent")){
                                            i++;
                                        }
                                        if(Objects.equals(entry2.getKey().toString(), "totalCashSalary")){
                                            totalCaS[0] = Long.parseLong(entry2.getValue().toString());
                                            i++;
                                        }
                                        if(Objects.equals(entry2.getKey().toString(), "creditSalaryPercent")){
                                            i++;
                                        }
                                        if(Objects.equals(entry2.getKey().toString(), "totalCreditSalary")){
                                            totalCrS[0] = Long.parseLong(entry2.getValue().toString());
                                            i++;
                                        }
                                        if(Objects.equals(entry2.getKey().toString(), "totalCashBoughtPrice")){
                                            totalCaBP[0] = Long.parseLong(entry2.getValue().toString());
                                            i++;
                                        }
                                        if(Objects.equals(entry2.getKey().toString(), "totalCreditBoughtPrice")){
                                            totalCrBP[0] = Long.parseLong(entry2.getValue().toString());
                                            i++;
                                        }
                                        if(Objects.equals(entry2.getKey().toString(), "totalCashQuantity")){
                                            totalCaQ[0] = Long.parseLong(entry2.getValue().toString());
                                            i++;
                                        }
                                        if(Objects.equals(entry2.getKey().toString(), "totalCreditQuantity")){
                                            totalCrQ[0] = Long.parseLong(entry2.getValue().toString());
                                            i++;
                                        }
                                        if(Objects.equals(entry2.getKey().toString(), "totalCashPrice")){
                                            totalCaP[0] =  Long.parseLong(entry2.getValue().toString());
                                            i++;
                                        }
                                        if(Objects.equals(entry2.getKey().toString(), "totalCreditPrice")){
                                            totalCrP[0] =  Long.parseLong(entry2.getValue().toString());
                                            i++;
                                        }

                                        if((totalCaP[0]!=0 && totalCaBP[0]!=0 && totalCaQ[0]!=0 && i==map2.size())||
                                                totalCrP[0]!=0 && totalCrBP[0]!=0 && totalCrQ[0]!=0 && i==map2.size()){
                                            createSellerStat(spList, totalCaBP[0], totalCaP[0], totalCrP[0],totalCaQ[0],totalCrQ[0], totalCrBP[0], totalCaS[0],totalCrS[0]);
                                        }
                                    }
                                }
                            }
                        }
                        else{
                            Log.d(TAG, "id not exists!");
                            createSellerStat(spList,0,0,0,0,0,0,0,0);
                        }
                    }
                    else{

                        Log.d(TAG, "d not exists!");
                        createSellerStat(spList,0,0,0,0,0,0,0,0);
                    }
                }
            });
        }
    }
    private void createSellerStat(ArrayList<String> spList, long l, long l1, long l2, long l3, long l4,long l5,long l6, long l7) {
        final long[] finalTotalCaBP = {l};
        final long[] finalTotalCaP = {l1};
        final long[] finalTotalCrP = {l2};
        final long[] finalTotalCaQ= {l3};
        final long[] finalTotalCrQ= {l4};
        final long[] finalTotalCrBP = {l5};
        final long[] finalTotalCaS= {l6};
        final long[] finalTotalCrS= {l7};
        final int[] i = {0};
        final int[] iii = {0};
        final long[] tspp = {0};
        for(String spId:spList){
            System.out.println(oPLFModel.getSellerId());
            soldProducts.document(spId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                    {
                        if(task1.isSuccessful()) {
                            DocumentSnapshot dd = task1.getResult();
                            if (dd.exists()) {
                                iii[0]++;
                                if (Integer.parseInt(dd.get("soldProductQuantity").toString()) > 0) {
                                    tspp[0] += Long.parseLong(dd.get("soldProductPrice").toString())*
                                            Long.parseLong(dd.get("soldProductQuantity").toString());
                                    if(iii[0]==spList.size()){
                                        products.document(dd.get("productId").toString()).get().addOnCompleteListener(task2 -> {
                                            if(task2.isSuccessful()){
                                                DocumentSnapshot ddd = task2.getResult();
                                                if(ddd.exists()){
                                                    int spq = Integer.parseInt(dd.get("soldProductQuantity").toString());
                                                    long spBp = Long.parseLong(ddd.get("boughtPrice").toString());
                                                    long spCap = Long.parseLong(ddd.get("cashPrice").toString());
                                                    finalTotalCaQ[0] +=spq;
                                                    finalTotalCaBP[0] += spq*spBp;
                                                    finalTotalCaP[0] += spq*spCap;
                                                    finalTotalCaS[0] +=spq*spCap*cashPricePercent/100;
                                                    i[0]++;
                                                    if(i[0]==spList.size()){
                                                        Map<String, Object> outerMap1 = new HashMap<>();
                                                        Map<String, Object> innerMap1 = new HashMap<>();
                                                        innerMap1.put("totalCashQuantity", finalTotalCaQ[0]);
                                                        innerMap1.put("totalCashSalary", finalTotalCaS[0]);
                                                        innerMap1.put("totalCreditSalary", finalTotalCrS[0]);
                                                        innerMap1.put("totalCreditQuantity", finalTotalCrQ[0]);
                                                        innerMap1.put("totalCashBoughtPrice", finalTotalCaBP[0]);
                                                        innerMap1.put("totalCreditBoughtPrice", finalTotalCrBP[0]);
                                                        innerMap1.put("totalCashPrice",finalTotalCaP[0]);
                                                        innerMap1.put("totalCreditPrice", finalTotalCrP[0]);
                                                        innerMap1.put("cashSalaryPercent", cashPricePercent);
                                                        innerMap1.put("creditSalaryPercent", creditPricePercent);
                                                        outerMap1.put(oPLFModel.getSellerId(), innerMap1);
                                                        statSellerDoc.set(outerMap1, SetOptions.merge());
                                                    }
                                                }
                                            }
                                        });


                                        sellerSalaryDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    DocumentSnapshot ddd = task.getResult();
                                                    if(ddd.exists()){
                                                        Map<String, Object> outerMap = ddd.getData();
                                                        if(ddd.getData().containsKey(oPLFModel.getSellerId())){
                                                            for(Map.Entry<String, Object> innerMap:outerMap.entrySet()) {
                                                                if (innerMap.getKey().toString().equals(oPLFModel.getSellerId())) {

                                                                    Map<String, Object> inMap = (Map<String, Object>) innerMap.getValue();
                                                                    long caS = 0;
                                                                    long gCaS = 0;
                                                                    long crS = 0;
                                                                    long gCrS = 0;

                                                                    int i=0;
                                                                    for(Map.Entry<String, Object> in:inMap.entrySet()){
                                                                        if(Objects.equals(in.getKey(), "creditSalary")){
                                                                            crS = Long.parseLong(in.getValue().toString());
                                                                            i++;
                                                                        }
                                                                        if(Objects.equals(in.getKey(), "cashSalary")){
                                                                            caS = Long.parseLong(in.getValue().toString());
                                                                            i++;
                                                                        }
                                                                        if(Objects.equals(in.getKey(), "sellerId")){
                                                                            i++;
                                                                        }
                                                                        if(Objects.equals(in.getKey(), "givenCreditSalary")){
                                                                            gCaS = Long.parseLong(in.getValue().toString());
                                                                            i++;
                                                                        }
                                                                        if(Objects.equals(in.getKey(), "givenCashSalary")){
                                                                            gCrS = Long.parseLong(in.getValue().toString());
                                                                            i++;
                                                                        }
                                                                        if(i==inMap.size()){
                                                                            System.out.println("cas"+caS);
                                                                            System.out.println("tspp[0]"+tspp[0]);
                                                                            updateSellersSalary(ddd,tspp[0],crS, caS, gCrS,gCaS);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }else{
                                                            updateSellersSalary(ddd,tspp[0],0,0,0,0);
                                                        }

                                                    }
                                                    else{
                                                        updateSellersSalary(ddd, tspp[0],0,0,0,0);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                                else{
                                    orders.document(oPLFModel.getOrderId()).update("soldProductsList", FieldValue.arrayRemove(dd.get("soldProductId")),
                                            "productsList", FieldValue.arrayRemove(dd.get("productId")));
                                    dd.getReference().delete();
                                    new SweetAlertDialog(DelivererPendingDetailActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setContentText("Miqdori berilmagan maxsulot buyurtmalar ro'yxatidan o'chirib tashlandi'!")
                                            .setConfirmText("OK!")
                                            .setConfirmClickListener(sweetAlertDialog2 -> {
                                                sweetAlertDialog2.cancel();
                                            })
                                            .show();
                                }
                            }
                        }}
                }
            });
        }


    }
    private void updateSellersSalary(DocumentSnapshot d, long totalCaP,
                                     long crS, long caS,long gCrS, long gCaS) {
        Map<String,Object> iMap = new HashMap<>();
        Map<String,Object> oMap = new HashMap<>();
        System.out.println("totalCaP"+totalCaP);
        System.out.println("caS"+caS);
        iMap.put("sellerId", oPLFModel.getSellerId());
        iMap.put("creditSalary", crS);
        iMap.put("givenCreditSalary", gCrS);
        iMap.put("cashSalary", caS+(totalCaP*cashPricePercent/100));
        iMap.put("givenCashSalary", gCaS);
        oMap.put( oPLFModel.getSellerId(), iMap);
        d.getReference().set(oMap, SetOptions.merge());
    }


    private void prodStat(DocumentSnapshot d) {
        db.collection("products").document(d.get("productId").toString()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot dd = task.getResult();
                statProductsDoc.get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        DocumentSnapshot ddd = task1.getResult();
                        if (ddd.exists()) {
                            if(ddd.getData().containsKey((dd.getId()))){
                                Map<String, Object> map = ddd.getData();
                                for (Map.Entry<String, Object> entry : map.entrySet()) {
                                    if(Objects.equals( dd.getId(), entry.getKey())){
                                        Log.d(TAG, "field3 exists!");
                                        Map<String, Object> map2 = (Map<String, Object>) entry.getValue();
                                        final long[] caSQ = {0};
                                        final long[] crSQ = {0};
                                        int i=0;
                                        for (Map.Entry<String, Object> entry2 : map2.entrySet()){
                                            if(!map2.containsValue(null)){
                                                if(Objects.equals(entry2.getKey().toString(), "cashSoldQuantity")){
                                                    caSQ[0] = Long.parseLong(entry2.getValue().toString());
                                                    i++;
                                                }
                                                if(Objects.equals(entry2.getKey().toString(), "creditSoldQuantity")){
                                                    crSQ[0] = Long.parseLong(entry2.getValue().toString());
                                                    i++;
                                                }
                                                if(Objects.equals(entry2.getKey().toString(), "boughtPrice")){
                                                    i++;
                                                }
                                                if(Objects.equals(entry2.getKey().toString(), "cashPrice")){
                                                    i++;
                                                }
                                                if(Objects.equals(entry2.getKey().toString(), "creditPrice")){
                                                    i++;
                                                }
                                                if(Objects.equals(entry2.getKey().toString(), "productId")){
                                                    i++;
                                                }
                                                if(Objects.equals(entry2.getKey().toString(), "productName")){
                                                    i++;
                                                }
                                                if((crSQ[0]!=0 && i==map2.size()) || (caSQ[0]!=0 && i==map2.size())){
                                                    createProductStat(dd,d, caSQ[0], crSQ[0]);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else{
//                                Log.d(TAG, "field does not exist!");
                                createProductStat(dd,d,0,0);
                            }
                        } else {
//                            Log.d(TAG, "StatProdDoc does not exist!");
                            createProductStat(dd,d,0,0);

                        }
                    } else {
//                        Log.d(TAG, "Failed with: ", task1.getException());
                    }
                });
            }
        });
    }

    private void createProductStat(DocumentSnapshot dd, DocumentSnapshot d, long l, long l1) {
        Map<String, Object> outerMap = new HashMap<>();
        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put("boughtPrice", dd.get("boughtPrice"));
        innerMap.put("cashPrice", dd.get("cashPrice"));
        innerMap.put("creditPrice", dd.get("creditPrice"));
        innerMap.put("productId", dd.getId());
        innerMap.put("productName", dd.get("productName"));
        innerMap.put("creditSoldQuantity", l1);
        innerMap.put("cashSoldQuantity", Integer.parseInt(d.get("soldProductQuantity").toString())+l);
        outerMap.put(d.get("productId").toString(),innerMap);
        statProductsDoc.set(outerMap, SetOptions.merge());
    }
    //    Method to load Sold Products



    // Method to cancel the order
    public void cancelTheOrder(){
        cancelTheOrderCard.setOnClickListener(view -> {
            new SweetAlertDialog(DelivererPendingDetailActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Eslatma")
                    .setContentText("Buyurtmani bekor qilishni xohlaysizmi?")
                    .setCancelText("Yo'q!")
                    .setConfirmText("Ha!")
                    .showCancelButton(true)
                    .setCancelClickListener(SweetAlertDialog::cancel)
                    .setConfirmClickListener(sweetAlertDialog1 -> {
                        orders.document(oPLFModel.getOrderId()).update("packageStatus", -2);
                        new SweetAlertDialog(DelivererPendingDetailActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setContentText("Buyurtma bekor qilindi!")
                                .setConfirmText("OK!")
                                .setConfirmClickListener(sweetAlertDialog2 -> {
                                    sweetAlertDialog2.cancel();
                                    Intent intent = new Intent(DelivererPendingDetailActivity.this, DelivererMainActivity.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .show();
                        sweetAlertDialog1.cancel();
                    })
                    .show();
            pendingOrderDetailActivityAdapter.notifyDataSetChanged();
        });

    }


    // Method to get Date from long
    public static String getDate(long milliSeconds, String dateFormat)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}