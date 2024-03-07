package com.example.lidertrade.admin.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lidertrade.R;
import com.example.lidertrade.admin.models.AdminSellerStatsModel;
import com.example.lidertrade.admin.models.AdminSellersSalaryModel;
import com.example.lidertrade.seller.adapters.SHFCategoryAdapter;
import com.example.lidertrade.seller.models.SellerCategoryModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminReportFragmentSellersAdapter extends RecyclerView.Adapter<AdminReportFragmentSellersAdapter.ViewHolder> {
    ArrayList<AdminSellerStatsModel> modelArrayList;
    ArrayList<AdminSellersSalaryModel> adminSellersSalaryModel;
    AdminReportFragmentSellersSalaryAdapter adminSellersSalaryAdapter;
    Context context;
    LineChart lineChart;
    String idForStats, myFormat;
    SimpleDateFormat dateFormat;
    Date date;
    LineData lineData;
    LineDataSet lineDataSet, lineDataSet2, lineDataSet3,lineDataSet4;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    DecimalFormat decim = new DecimalFormat("#,###.##");

    public AdminReportFragmentSellersAdapter(ArrayList<AdminSellerStatsModel> homeModelArrayList, Context context) {
        this.modelArrayList = homeModelArrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public AdminReportFragmentSellersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.admin_report_fragment_sellers_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdminReportFragmentSellersAdapter.ViewHolder holder, int position) {
        AdminSellerStatsModel modal = modelArrayList.get(position);
        db.collection("Users").document(modal.getId()).get().addOnSuccessListener(d -> {
            if(d.exists()){
                holder.sellerName.setText(d.get("userName").toString());
                String s = new String();
                if(d.get("userImage")!=null){
                    Glide.with(context)
                            .load(d.get("userImage").toString())
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .into(holder.sellerStatDataPicker);
                }else {
                    Glide.with(context)
                            .load(context.getDrawable(R.drawable.logo))
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .into(holder.sellerStatDataPicker);
                }
            }});


        ArrayList lineEntries = new ArrayList<>();
        ArrayList lineEntries2 = new ArrayList<>();
        ArrayList lineEntries3 = new ArrayList<>();
        ArrayList lineEntries4 = new ArrayList<>();

        CollectionReference sellerStatHelper = db.collection("SellerStatHelper");
        CollectionReference statSellers = db.collection("StatSellers");
        createEachProdStat(modal, sellerStatHelper, statSellers, lineEntries, lineEntries2, lineEntries3,lineEntries4);

        holder.creditQuantity.setText(String.format(decim.format(modal.getTotalCreditQuantity())));
        holder.creditBoughtPrice.setText(String.format("%s so'm",decim.format(modal.getTotalCreditBoughtPrice())));
        holder.creditSoldPrice.setText(String.format("%s so'm",decim.format(modal.getTotalCreditPrice())));
        holder.creditPercent.setText(String.valueOf(modal.getCreditSalaryPercent()));
        holder.creditSalary.setText(String.format("%s so'm",decim.format(modal.getTotalCreditPrice()*modal.getCreditSalaryPercent()/100)));

        holder.cashQuantity.setText(String.format(decim.format(modal.getTotalCashQuantity())));
        holder.cashBoughtPrice.setText(String.format("%s so'm",decim.format(modal.getTotalCashBoughtPrice())));
        holder.cashSoldPrice.setText(String.format("%s so'm",decim.format(modal.getTotalCashPrice())));
        holder.cashPercent.setText(String.valueOf(modal.getCashSalaryPercent()));
        holder.cashSalary.setText(String.format("%s so'm",decim.format(modal.getTotalCashPrice()*modal.getCashSalaryPercent()/100)));

        holder.percentEditCard.setOnClickListener(view -> updatePercents(modal));
        holder.percentStatCard.setOnClickListener(view -> updateProductData(modal, lineEntries, lineEntries2, lineEntries3,lineEntries4));
        holder.sellerSalaryCard.setOnClickListener(view -> sellersSalaryCalculation(modal));
    }

    private void sellersSalaryCalculation(AdminSellerStatsModel modal) {
        db = FirebaseFirestore.getInstance();
        CollectionReference statSellers = db.collection("StatSellers");
        CollectionReference sellersSalary = db.collection("SellersSalary");
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.admin_report_fragment_seller_salary_dialog);
        dialog.setCancelable(true);
        myFormat="MM-yyyy";
        date = new Date();
        dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        idForStats = String.valueOf((dateFormat.format(date)));

        final RecyclerView monthlySalaryRW = dialog.findViewById(R.id.monthlySalaryRW);
        final Button reset = dialog.findViewById(R.id.reset);
        adminSellersSalaryModel = new ArrayList<>();
        monthlySalaryRW.setHasFixedSize(true);
        monthlySalaryRW.setLayoutManager(new LinearLayoutManager(dialog.getContext(), LinearLayoutManager.VERTICAL, false));
        adminSellersSalaryAdapter = new AdminReportFragmentSellersSalaryAdapter(adminSellersSalaryModel, dialog.getContext());
        monthlySalaryRW.setAdapter(adminSellersSalaryAdapter);
        adminSellersSalaryAdapter.notifyDataSetChanged();
        sellersSalary.addSnapshotListener((queryDocumentSnapshots, error) -> {
            if(!queryDocumentSnapshots.isEmpty()){
                adminSellersSalaryModel.clear();
                for(DocumentSnapshot d:queryDocumentSnapshots.getDocuments()){
                    if (d.exists()){
                        Map<String, Object> outerMap = d.getData();
                        for(Map.Entry<String, Object> innerMap:outerMap.entrySet()) {
                            if (innerMap.getKey().toString().equals(modal.getId())) {
                                System.out.println(innerMap.getValue());
                                Map<String, Object> inMap = (Map<String, Object>) innerMap.getValue();
                                long caS = 0;
                                long crS = 0;
                                long gCrS = 0;
                                long gCaS = 0;
                                for(Map.Entry<String, Object> in:inMap.entrySet()){
                                    if(Objects.equals(in.getKey(), "creditSalary")){
                                        crS = Long.parseLong(in.getValue().toString());
                                    }
                                    if(Objects.equals(in.getKey(), "cashSalary")){
                                        caS = Long.parseLong(in.getValue().toString());
                                    }
                                    if(Objects.equals(in.getKey(), "givenCreditSalary")){
                                        gCrS = Long.parseLong(in.getValue().toString());
                                    }
                                    if(Objects.equals(in.getKey(), "givenCashSalary")){
                                        gCaS = Long.parseLong(in.getValue().toString());
                                    }

                                }
                                AdminSellersSalaryModel dataModal = d.toObject(AdminSellersSalaryModel.class);
                                dataModal.setSellerId(modal.getId());
                                dataModal.setId(d.getId());
                                dataModal.setCashSalary(caS);
                                dataModal.setCreditSalary(crS);
                                dataModal.setGivenCashSalary(gCaS);
                                dataModal.setGivenCreditSalary(gCrS);
                                adminSellersSalaryModel.add(dataModal);
                                adminSellersSalaryAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        });
        reset.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }


    private void updateProductData(AdminSellerStatsModel modal, ArrayList lineEntries, ArrayList lineEntries2,ArrayList lineEntries3,ArrayList lineEntries4) {
        db = FirebaseFirestore.getInstance();
        CollectionReference statMaker = db.collection("StatMaker");
        CollectionReference statProducts = db.collection("StatProducts");
        CollectionReference statProduct = db.collection("StatProduct");
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        dialog.setContentView(R.layout.admin_report_fragment_seller_stat_dialog);
        lineChart = dialog.findViewById(R.id.lineChart);
//        Collections.reverse(lineEntries);
        lineDataSet = new LineDataSet(lineEntries, "Naqd Xarajat");
        lineDataSet2 = new LineDataSet(lineEntries2, "Kredit Xarajat");
        lineDataSet3 = new LineDataSet(lineEntries3, "Naqd Savdo");
        lineDataSet4 = new LineDataSet(lineEntries4, "Kredit Savdo");
        lineDataSet.setColor(context.getColor(R.color.CanceledOrders));
        lineDataSet2.setColors(context.getColor(R.color.PendingOrders));
        lineDataSet3.setColors(context.getColor(R.color.CardBorderColor));
        lineDataSet4.setColors(context.getColor(R.color.CardInnerColor));
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet2.setValueTextColor(Color.BLACK);
        lineDataSet3.setValueTextColor(Color.BLACK);
        lineDataSet4.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(12f);
        lineDataSet2.setValueTextSize(12f);
        lineDataSet3.setValueTextSize(12f);
        lineDataSet4.setValueTextSize(12f);
        lineData = new LineData(lineDataSet, lineDataSet2, lineDataSet3, lineDataSet4);
        lineChart.setData(lineData);



        final TextView customStatText = dialog.findViewById(R.id.customStatText);

        statMaker.document(modal.getId()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot d = task.getResult();
                if(d.exists()) {
                    customStatText.setText(d.get("dateRange").toString());
                }}});


        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void createEachProdStat(AdminSellerStatsModel modal, CollectionReference sellerStatHelper, CollectionReference statSellers,
                                    ArrayList lineEntries, ArrayList lineEntries2, ArrayList lineEntries3, ArrayList lineEntries4) {
        sellerStatHelper.document(modal.getId()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot d = task.getResult();
                if(d.exists()){
                    String string = d.get("dateRange").toString();
                    String[] parts = string.split(" ");
                    String endDateString = parts[0]; // 004
                    String startDateString = parts[1]; // 034556
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                    try {
                        Date endDate = format.parse(endDateString);
                        Date startDate = format.parse(startDateString);
                        Long endLong = endDate.getTime();
                        Long startLong = startDate.getTime();
                        long nDay = ((endLong - startLong)/24/60/60/1000);
                        final long[] tCaBP = {0};
                        final long[] tCrBP = {0};
                        final long[] tCaP = {0};
                        final long[] tCrP = {0};
                        final long[] tCaQ = {0};
                        final long[] tCrQ = {0};
                        for(int ii=0;ii<=nDay;ii++) {
                            lineEntries.clear();
                            String idForAnyDay = format.format(new Date(endLong - (long) ii * 24 * 60 * 60 * 1000));
                            final int[] finalIi = {ii};
                            statSellers.document(idForAnyDay).get().addOnCompleteListener(task2 -> {
                                if(task2.isSuccessful()){
                                    DocumentSnapshot dd = task2.getResult();
                                    if(dd.exists() && dd.getData()!=null){
                                        if(dd.contains(d.getId())){
                                            Map<String, Object> map = dd.getData();
                                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                                if(Objects.equals(entry.getKey(), d.getId().toString())){
                                                    Map<String, Object> map2 = (Map<String, Object>) entry.getValue();
                                                    for (Map.Entry<String, Object> item : map2.entrySet()) {
                                                        if(Objects.equals(item.getKey().toString(), "totalCashBoughtPrice")){
                                                            tCaBP[0] = Long.parseLong(item.getValue().toString());
                                                        }
                                                        if(Objects.equals(item.getKey().toString(), "totalCreditBoughtPrice")){
                                                            tCrBP[0] = Long.parseLong(item.getValue().toString());
                                                        }
                                                        if(Objects.equals(item.getKey().toString(), "totalCreditPrice")){
                                                            tCaP[0] = Long.parseLong(item.getValue().toString());
                                                        }
                                                        if(Objects.equals(item.getKey().toString(), "totalCashPrice")){
                                                            tCrP[0] = Long.parseLong(item.getValue().toString());
                                                        }
                                                        if(Objects.equals(item.getKey().toString(), "totalCashQuantity")){
                                                            tCaQ[0] = Long.parseLong(item.getValue().toString());
                                                        }
                                                        if(Objects.equals(item.getKey().toString(), "totalCreditQuantity")){
                                                            tCrQ[0] = Long.parseLong(item.getValue().toString());
                                                        }
                                                    }
                                                }

                                            }

                                        }
                                        lineEntries.add(new Entry(finalIi[0], tCaBP[0]));
                                        lineEntries2.add(new Entry(finalIi[0], tCrBP[0]));
                                        lineEntries3.add(new Entry(finalIi[0], tCaP[0]));
                                        lineEntries4.add(new Entry(finalIi[0], tCaP[0]));
                                    }
                                }

                            });

                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }








    private void updatePercents(AdminSellerStatsModel modal) {
        db = FirebaseFirestore.getInstance();
        CollectionReference users = db.collection("Users");
        DocumentReference usersDoc = users.document(modal.getId());
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.admin_report_fragment_sellers_percent_edit_dialog);

        final EditText cashPercentDialog = dialog.findViewById(R.id.cashPercentDialog);
        final EditText creditPercentDialog = dialog.findViewById(R.id.creditPercentDialog);
        usersDoc.get().addOnSuccessListener(d -> {
            if(d.exists()){
                if(d.get("cashSalaryPercent")!=null && d.get("creditSalaryPercent")!=null){
                    cashPercentDialog.setText(String.valueOf(d.get("cashSalaryPercent")));
                    creditPercentDialog.setText(String.valueOf(d.get("creditSalaryPercent")));
                }
            }
        });
        final Button submit = dialog.findViewById(R.id.submit);
        submit.setOnClickListener(view -> {
            String cashPerText = cashPercentDialog.getText().toString().trim();
            String creditPerText = creditPercentDialog.getText().toString().trim();
            if (cashPerText.isEmpty() || creditPerText.isEmpty()){
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setContentText("Iltimos, avval maydonni to'ldiring!")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(SweetAlertDialog::cancel)
                        .show();
            }
            else{
                usersDoc.update("cashSalaryPercent", Integer.parseInt(cashPerText),
                        "creditSalaryPercent",Integer.parseInt(creditPerText))
                        .addOnSuccessListener(unused -> new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                .setContentText("Muvafaqqiyatli o'zgartirildi!")
                                .setConfirmText("OK!")
                                .setConfirmClickListener(SweetAlertDialog::cancel)
                                .show()).addOnFailureListener(e -> {
                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Jarayonda xatolik!")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(SweetAlertDialog::cancel)
                            .show();
                });
                dialog.dismiss();
            }
        });
        final Button reset = dialog.findViewById(R.id.reset);
        reset.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView sellerName, cashQuantity, cashBoughtPrice, cashSoldPrice, creditQuantity, creditBoughtPrice, creditSoldPrice,
                cashPercent, cashSalary, creditPercent, creditSalary;
        ImageView sellerStatDataPicker;
        CardView percentEditCard, percentStatCard, sellerSalaryCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sellerName = itemView.findViewById(R.id.sellerName);
            cashQuantity = itemView.findViewById(R.id.cashQuantity);
            cashBoughtPrice = itemView.findViewById(R.id.cashBoughtPrice);
            cashSoldPrice = itemView.findViewById(R.id.cashSoldPrice);
            creditQuantity = itemView.findViewById(R.id.creditQuantity);
            creditBoughtPrice = itemView.findViewById(R.id.creditBoughtPrice);
            creditSoldPrice = itemView.findViewById(R.id.creditSoldPrice);
            sellerStatDataPicker = itemView.findViewById(R.id.sellerStatDataPicker);
            cashPercent = itemView.findViewById(R.id.cashPercent);
            cashSalary = itemView.findViewById(R.id.cashSalary);
            creditPercent = itemView.findViewById(R.id.creditPercent);
            creditSalary = itemView.findViewById(R.id.creditSalary);
            percentEditCard = itemView.findViewById(R.id.percentEditCard);
            percentStatCard = itemView.findViewById(R.id.percentStatCard);
            sellerSalaryCard = itemView.findViewById(R.id.sellerSalaryCard);
        }


    }
}

