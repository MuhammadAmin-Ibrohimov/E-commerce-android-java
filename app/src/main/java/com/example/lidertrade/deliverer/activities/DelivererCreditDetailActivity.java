package com.example.lidertrade.deliverer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DelivererCreditDetailActivity extends AppCompatActivity {
    private EditText cashTextInput, cardTextInput,clickTextInput, bankTextInput;
    private CardView completeTheOrderCard, cancelTheOrderCard;
    private ArrayList<SellerCreditProductsModel> sendingOrderDetailActivityModel;
    private DelivererCreditActivityAdapter sendingOrderDetailActivityAdapter;
    private RecyclerView sendingOrderDetailRecyclerView;
    int cashPricePercent, creditPricePercent;
    DecimalFormat decim = new DecimalFormat("#,###.##");
    private TextView orderTotalPrice ;
    private DelivererCreditModel oSLFModel;
    private FirebaseFirestore db;
    String idForStats, myFormat,mySalaryFormat, idForSalary;
    SimpleDateFormat dateFormat,salaryDateFormat;
    Date date;
    CollectionReference orders, creditProducts,debtBook, products, statProducts, statSeller, sellersSalary;
    DocumentReference statProductsDoc, statSellerDoc,sellerSalaryDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.deliverer_credit_detail_activity);

        db = FirebaseFirestore.getInstance();
        orders = db.collection("Credits");
        creditProducts = db.collection("CreditProducts");
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
        oSLFModel = (DelivererCreditModel) intent.getSerializableExtra("orderModel");
        orderTotalPrice = findViewById(R.id.orderTotalPrice);
        cancelTheOrderCard = findViewById(R.id.cancelTheOrderCard);
        completeTheOrderCard = findViewById(R.id.completeTheOrderCard);
        orderTotalPrice.setText(String.format("Umumiy summa: %s so'm", decim.format(oSLFModel.getCartTotalPrice())));

        sendingOrderDetailRecyclerView = (RecyclerView) findViewById(R.id.creditDetailRecyclerView);
        sendingOrderDetailActivityModel = new ArrayList<>();
        sendingOrderDetailRecyclerView.setHasFixedSize(true);
        sendingOrderDetailRecyclerView.setLayoutManager(new LinearLayoutManager(DelivererCreditDetailActivity.this, LinearLayoutManager.VERTICAL, false));
        sendingOrderDetailActivityAdapter = new DelivererCreditActivityAdapter(sendingOrderDetailActivityModel, this);
        sendingOrderDetailRecyclerView.setAdapter(sendingOrderDetailActivityAdapter);
        cancelTheOrder();
        getSellerBenefit();
        completeTheOrder();
        loadSoldProductData();

    }
    public void completeTheOrder(){
        completeTheOrderCard.setOnClickListener(view -> new SweetAlertDialog(DelivererCreditDetailActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setContentText("Buyurtma yetkazildimi?")
                .setCancelText("Yo'q!")
                .setConfirmText("Ha!")
                .showCancelButton(true)
                .setCancelClickListener(SweetAlertDialog::cancel)
                .setConfirmClickListener(sweetAlertDialog1 -> {
                    orders.document(oSLFModel.getOrderId()).update("packageStatus", 2, "orderCompletedTime", new Date().getTime());
                    ArrayList<String> spList = (oSLFModel.getSoldProductsList());
                    ArrayList<String> pList = (oSLFModel.getProductsList());
                    addToProductStatistics(spList, pList);
                    new SweetAlertDialog(DelivererCreditDetailActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setContentText("Buyurtma Muvafaqqiyatli yetkazildi!")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(sweetAlertDialog2 -> {
                                sweetAlertDialog2.cancel();
                                Intent intent = new Intent(DelivererCreditDetailActivity.this, DelivererMainActivity.class);
                                startActivity(intent);
                                finish();
                            })
                            .show();
                    sweetAlertDialog1.cancel();
                })
                .show());

    }






    private void addToProductStatistics(ArrayList<String> spList, ArrayList<String> pList) {
        if(spList.size()>0 && spList.size()==pList.size()){
            for(String spId:spList){
                creditProducts.document(spId).get().addOnSuccessListener(d -> {
                    if(d.exists()){
                        if(Integer.parseInt(d.get("creditProductQuantity").toString())>0){
                            prodStat(d);

                        }
                        else{
                            orders.document(oSLFModel.getOrderId()).update("soldProductsList", FieldValue.arrayRemove(d.get("creditProductId")),
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
                        if(d.getData().containsKey(oSLFModel.getSellerId())){
                            Map<String, Object> map = d.getData();
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                if(Objects.equals(oSLFModel.getSellerId(), entry.getKey())){
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
                            createSellerStat(spList,0,0,0,0,0,0,0,0);
                        }
                    }
                    else{
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
        System.out.println(finalTotalCrP);
        System.out.println(finalTotalCrQ);
        System.out.println(finalTotalCrBP);
        System.out.println(finalTotalCrS);
        final int[] i = {0};
        final int[] iii = {0};
        final long[] tspp = {0};
        for(String spId:spList){
            System.out.println(oSLFModel.getSellerId());
            creditProducts.document(spId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                    {
                        if(task1.isSuccessful()) {
                            DocumentSnapshot dd = task1.getResult();
                            if (dd.exists()) {
                                iii[0]++;
                                if (Integer.parseInt(dd.get("creditProductQuantity").toString()) > 0) {
                                    tspp[0] += Long.parseLong(dd.get("creditProductPrice").toString())*
                                            Long.parseLong(dd.get("creditProductQuantity").toString());
                                    products.document(dd.get("productId").toString()).get().addOnCompleteListener(task2 -> {
                                        if(task2.isSuccessful()){
                                            DocumentSnapshot ddd = task2.getResult();
                                            if(ddd.exists()){
                                                int spq = Integer.parseInt(dd.get("creditProductQuantity").toString());
                                                long spBp = Long.parseLong(ddd.get("boughtPrice").toString());
                                                long spCap = Long.parseLong(ddd.get("creditPrice").toString());
                                                finalTotalCrQ[0] +=spq;
                                                finalTotalCrBP[0] += spq*spBp;
                                                finalTotalCrP[0] += spq*spCap;
                                                finalTotalCrS[0] +=spq*spCap*creditPricePercent/100;
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
                                                    outerMap1.put(oSLFModel.getSellerId(), innerMap1);
                                                    statSellerDoc.set(outerMap1, SetOptions.merge());
                                                }
                                            }
                                        }
                                    });
                                    if(iii[0]==spList.size()){

                                        sellerSalaryDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    DocumentSnapshot ddd = task.getResult();
                                                    if(ddd.exists()){
                                                        Map<String, Object> outerMap = ddd.getData();
                                                        if(ddd.getData().containsKey(oSLFModel.getSellerId())){
                                                            for(Map.Entry<String, Object> innerMap:outerMap.entrySet()) {
                                                                if (innerMap.getKey().toString().equals(oSLFModel.getSellerId())) {

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
                                    orders.document(oSLFModel.getOrderId()).update("soldProductsList", FieldValue.arrayRemove(dd.get("soldProductId")),
                                            "productsList", FieldValue.arrayRemove(dd.get("productId")));
                                    dd.getReference().delete();
                                    new SweetAlertDialog(DelivererCreditDetailActivity.this, SweetAlertDialog.WARNING_TYPE)
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

        iMap.put("sellerId", oSLFModel.getSellerId());
        iMap.put("creditSalary", crS+(totalCaP*creditPricePercent/100));
        iMap.put("givenCreditSalary", gCrS);
        iMap.put("cashSalary", caS);
        iMap.put("givenCashSalary", gCaS);
        oMap.put( oSLFModel.getSellerId(), iMap);
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
                                        Map<String, Object> map2 = (Map<String, Object>) entry.getValue();
                                        final long[] caSQ = {0};
                                        final long[] crSQ = {0};
                                        int i=0;
                                        for (Map.Entry<String, Object> entry2 : map2.entrySet()){
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
                            else{
                                createProductStat(dd,d,0,0);
                            }
                        } else {
                            createProductStat(dd,d,0,0);

                        }
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
        innerMap.put("creditSoldQuantity", Integer.parseInt(d.get("creditProductQuantity").toString())+l1);
        innerMap.put("cashSoldQuantity", l);
        outerMap.put(d.get("productId").toString(),innerMap);
        statProductsDoc.set(outerMap, SetOptions.merge());
    }



























    private void getSellerBenefit() {
        db.collection("Users").document(oSLFModel.getSellerId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot d = task.getResult();
                    if(d.exists()){
                        cashPricePercent = Integer.parseInt(d.get("cashSalaryPercent").toString());
                        creditPricePercent = Integer.parseInt(d.get("creditSalaryPercent").toString());
                    }
                }
            }
        });
    }




    // Method to cancel the order
    public void cancelTheOrder(){
        cancelTheOrderCard.setOnClickListener(view -> new SweetAlertDialog(DelivererCreditDetailActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setContentText("Buyurtmani bekor qilishni xohlaysizmi?")
                .setCancelText("Yo'q!")
                .setConfirmText("Ha!")
                .showCancelButton(true)
                .setCancelClickListener(SweetAlertDialog::cancel)
                .setConfirmClickListener(sweetAlertDialog1 -> {
                    orders.document(oSLFModel.getOrderId()).update("packageStatus", -2);
                    new SweetAlertDialog(DelivererCreditDetailActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Buyurtma bekor qilindi!")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(sweetAlertDialog2 -> {
                                sweetAlertDialog2.cancel();
                                Intent intent = new Intent(DelivererCreditDetailActivity.this, DelivererMainActivity.class);
                                startActivity(intent);
                            })
                            .show();
                    sweetAlertDialog1.cancel();
                })
                .show());

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
                        Toast.makeText(DelivererCreditDetailActivity.this, "XATOOOOOO", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}