package com.example.lidertrade.deliverer.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.models.AdminOrderModel;
import com.example.lidertrade.deliverer.adapters.DelivererSendingActivityAdapter;
import com.example.lidertrade.deliverer.helpers.NumberTextWatcherForThousand;
import com.example.lidertrade.deliverer.models.DebtBookModel;
import com.example.lidertrade.deliverer.models.DelivererPendingActivityModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DelivererSendingDetailActivity extends AppCompatActivity {
    private CollectionReference orders, soldProducts, debtBook;
    private EditText cashTextInput, cardTextInput,clickTextInput, bankTextInput;
    private CardView completeTheOrderCard, cancelTheOrderCard;
    private ArrayList<DelivererPendingActivityModel> sendingOrderDetailActivityModel;
    private ArrayList<DebtBookModel> debtBookModel;
    private DelivererSendingActivityAdapter sendingOrderDetailActivityAdapter;
    private RecyclerView sendingOrderDetailRecyclerView;
    DecimalFormat decim = new DecimalFormat("#,###.##");
    private TextView orderTotalPrice, receivedTotalMoney, paymentDifference;
    private AdminOrderModel oSLFModel;
    private FirebaseFirestore db;
    private final long[] cash = {0};
    private final long[] card = {0};
    private final long[] click = {0};
    private final long[] bank = {0};
    private final long[] totalSum = {0};
    private long differenceSum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.deliverer_sending_detail_activity);

        db = FirebaseFirestore.getInstance();
        orders = db.collection("Orders");
        soldProducts = db.collection("SoldProducts");
        debtBook = db.collection("DebtBook");

        cashTextInput = findViewById(R.id.cashTextInput);
        cardTextInput = findViewById(R.id.cardTextInput);
        clickTextInput = findViewById(R.id.clickTextInput);
        bankTextInput = findViewById(R.id.bankTextInput);
        receivedTotalMoney = findViewById(R.id.receivedTotalMoney);
        paymentDifference = findViewById(R.id.paymentDifference);
        completeTheOrderCard = findViewById(R.id.completeTheOrderCard);
        cancelTheOrderCard = findViewById(R.id.cancelTheOrderCard);


        Intent intent = getIntent();
        oSLFModel = (AdminOrderModel) intent.getSerializableExtra("orderModel");
        orderTotalPrice = findViewById(R.id.orderTotalPrice);

        orderTotalPrice.setText(String.format("Umumiy summa: %s so'm", decim.format(oSLFModel.getCartTotalPrice())));

        sendingOrderDetailRecyclerView = (RecyclerView) findViewById(R.id.sendingOrderDetailRecyclerView);
        sendingOrderDetailActivityModel = new ArrayList<>();
        sendingOrderDetailRecyclerView.setHasFixedSize(true);
        sendingOrderDetailRecyclerView.setLayoutManager(new LinearLayoutManager(DelivererSendingDetailActivity.this, LinearLayoutManager.VERTICAL, false));
        sendingOrderDetailActivityAdapter = new DelivererSendingActivityAdapter(sendingOrderDetailActivityModel, this);
        sendingOrderDetailRecyclerView.setAdapter(sendingOrderDetailActivityAdapter);

        loadSoldProductData();
        paymentCalculation(oSLFModel.getCartTotalPrice());
        completeTheOrder();
        cancelTheOrder();
    }



    private void completeTheOrder() {
        completeTheOrderCard.setOnClickListener(view -> {
            if (differenceSum == 0 && totalSum[0] == 0){
                differenceSum = oSLFModel.getCartTotalPrice();
            }
            if (differenceSum>0){
                new SweetAlertDialog(DelivererSendingDetailActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Rozimisiz?")
                        .setContentText("Xaridorning to'lanmagan "+differenceSum+" so'm qarzi mavjud! JAVOBGARLIK SIZGAMI?")
                        .setCancelText("Yo'q!")
                        .setConfirmText("Ha!")
                        .showCancelButton(true)
                        .setCancelClickListener(SweetAlertDialog::cancel)
                        .setConfirmClickListener(sweetAlertDialog1 -> {
                            new SweetAlertDialog(DelivererSendingDetailActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Eslatma!")
                                    .setContentText("To'lov maksimum muddati 1oy!")
                                    .setConfirmText("Tushundim!")
                                    .setConfirmClickListener(sweetAlertDialog2 -> {
                                        sweetAlertDialog2.cancel();
                                        updateAllCollections(-1, differenceSum);
                                        Intent intent = new Intent(DelivererSendingDetailActivity.this, DelivererMainActivity.class);
                                        startActivity(intent);
                                    })
                                    .show();
                            sweetAlertDialog1.cancel();
                        })
                        .show();
            }else if(differenceSum<0){
                new SweetAlertDialog(DelivererSendingDetailActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Eslatma!")
                        .setContentText("Xaridor "+ (-1*differenceSum)+" so'm ortiqcha to'lov qildi! Qaytardingizmi?")
                        .setCancelText("Yo'q!")
                        .setConfirmText("Ha!")
                        .showCancelButton(true)
                        .setCancelClickListener(SweetAlertDialog::cancel)
                        .setConfirmClickListener(sweetAlertDialog1 -> {
                            sweetAlertDialog1.cancel();
                            updateAllCollections(2, differenceSum);
                            Intent intent = new Intent(DelivererSendingDetailActivity.this, DelivererMainActivity.class);
                            startActivity(intent);
                        })
                        .show();
            }else{
                new SweetAlertDialog(DelivererSendingDetailActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Tariklaymiz!")
                        .setContentText("To'lov muvafaqqiyatli amalga oshirildi! Buyurtmalarni yetkazishda davom etishingiz mumkin.")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(sweetAlertDialog2 -> {
                            sweetAlertDialog2.cancel();
                            updateAllCollections(2, differenceSum);
                            Intent intent = new Intent(DelivererSendingDetailActivity.this, DelivererMainActivity.class);
                            startActivity(intent);
                        })
                        .show();
            }
        });
    }
    private void updateAllCollections(int i, long ds) {
        HashMap<String, Long> paymentType = new HashMap<>();
        paymentType.put("Naqd pul", cash[0]);
        paymentType.put("Karta orqali", card[0]);
        paymentType.put("Click orqali", click[0]);
        paymentType.put("Hisob Raqam orqali", bank[0]);
        orders.document(oSLFModel.getOrderId()).update("paymentMap", paymentType,"packageStatus", i,
                "paymentStatus", ds, "orderCompletedTime", new Date().getTime(), "soldDate", FieldValue.serverTimestamp());
        if (i == -1){
            String id = new String(oSLFModel.getOrderId());
            DebtBookModel dBModel = new DebtBookModel(id, oSLFModel.getCustomerName(), oSLFModel.getCustomerAddress(),
                    oSLFModel.getCustomerPhone(), new Date().getTime(), 30, ds);
            debtBook.document(id).set(dBModel);
        }
    }
    private void paymentCalculation(long cartTP) {
        cashTextInput.addTextChangedListener(new NumberTextWatcherForThousand(cashTextInput));
        cashTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()!=0){
                    cash[0] = Long.parseLong(charSequence.toString().trim().replaceAll("[^A-Za-z0-9]", ""));
                }else{
                    cash[0] = 0;
                }
                totalSum[0] = cash[0] + card[0] + bank[0] + click[0];
                calculateTheDifference(totalSum[0], cartTP);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        cardTextInput.addTextChangedListener(new NumberTextWatcherForThousand(cardTextInput));
        cardTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()!=0){
                    card[0] =  Long.parseLong(charSequence.toString().trim().replaceAll("[^A-Za-z0-9]", ""));
                }else{
                    card[0] = 0;
                }
                totalSum[0] = cash[0] + card[0] + bank[0] + click[0];
                calculateTheDifference(totalSum[0], cartTP);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        clickTextInput.addTextChangedListener(new NumberTextWatcherForThousand(clickTextInput));
        clickTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()!=0){
                    click[0] = Long.parseLong(charSequence.toString().trim().replaceAll("[^A-Za-z0-9]", ""));
                }else{
                    click[0] = 0;
                }
                totalSum[0] = cash[0] + card[0] + bank[0] + click[0];
                calculateTheDifference(totalSum[0], cartTP);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        bankTextInput.addTextChangedListener(new NumberTextWatcherForThousand(bankTextInput));
        bankTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()!=0){
                    bank[0] =  Long.parseLong(charSequence.toString().trim().replaceAll("[^A-Za-z0-9]", ""));
                }else{
                    bank[0] = 0;
                }
                totalSum[0] = cash[0] + card[0] + bank[0] + click[0];
                calculateTheDifference(totalSum[0], cartTP);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
    private void calculateTheDifference(long tgs, long tcs){
        differenceSum = tcs - tgs;
        receivedTotalMoney.setText(String.format("Qabul qilingan mablag':    %s so'm", decim.format(tgs)));
        if (differenceSum < 0){
            paymentDifference.setText(String.format("Qaytariladigan mablag':    %s so'm", decim.format(differenceSum)));
        }else if (differenceSum > 0){
            paymentDifference.setText(String.format("Qarzdorlik:    %s so'm", decim.format(differenceSum)));
        }else{
            paymentDifference.setText("To'lov amalga oshirildi");
        }
    }

    // LOAD SOLD_PRODUCT COLLECTION
    private void loadSoldProductData() {
        soldProducts.whereEqualTo("orderId", oSLFModel.getOrderId()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot d : list) {
                    if (d.getData() != null){
                        DelivererPendingActivityModel dataModal = d.toObject(DelivererPendingActivityModel.class);
                        sendingOrderDetailActivityModel.add(dataModal);
                        sendingOrderDetailActivityAdapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(DelivererSendingDetailActivity.this, "XATOOOOOO", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // Method to cancel the order
    public void cancelTheOrder(){
        cancelTheOrderCard.setOnClickListener(view -> new SweetAlertDialog(DelivererSendingDetailActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Eslatma")
                .setContentText("Buyurtmani bekor qilishni xohlaysizmi?")
                .setCancelText("Yo'q!")
                .setConfirmText("Ha!")
                .showCancelButton(true)
                .setCancelClickListener(SweetAlertDialog::cancel)
                .setConfirmClickListener(sweetAlertDialog1 -> {
                    orders.document(oSLFModel.getOrderId()).update("packageStatus", -2);
                    new SweetAlertDialog(DelivererSendingDetailActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setContentText("Buyurtma bekor qilindi!")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(sweetAlertDialog2 -> {
                                sweetAlertDialog2.cancel();
                                Intent intent = new Intent(DelivererSendingDetailActivity.this, DelivererMainActivity.class);
                                startActivity(intent);
                            })
                            .show();
                    sweetAlertDialog1.cancel();
                })
                .show());

    }

}