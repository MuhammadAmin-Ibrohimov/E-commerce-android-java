package com.example.lidertrade.seller.fragments;


import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.adapters.AdminReportFragmentTotalAdapter;
import com.example.lidertrade.admin.models.AdminTotalStatsModel;
import com.example.lidertrade.seller.adapters.SellerCustomStatAdapter;
import com.example.lidertrade.seller.models.SellerCategoryModel;
import com.example.lidertrade.seller.models.SellerCustomStatModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class SellerProfileFragmentCustomStat extends Fragment {
    FirebaseUser user;
    String sellerId;
    final long[] toCaSf = {0};
    final long[] toCaQf = {0};
    final long[] toCrSf = {0};
    final long[] toCrQf = {0};
    CollectionReference sellersSalaryCol,statSeller;
    DecimalFormat decim = new DecimalFormat("#,###.##");
    FirebaseFirestore db;
    ArrayList<SellerCustomStatModel> adminTotalStatsModel;

    private String[] xData = {"Naqd", "Kredit"};
    private float[] yData;
    private BarChart chart, chart2;
    String idForMonthlyStats, idForDailyStats, myFormat, myFormat2;
    SimpleDateFormat dateFormat,dateFormat2;
    Date date;
    RecyclerView totalStatsRW;
    CardView sellerStatDataPickerCard;
    SellerCustomStatAdapter sellerCustomStatAdapter;
    PieChart pieChart;
    PieData pieData;
    TextView dailyStatTextLine, sellersSalaryTextView;
    PieDataSet pieDataSet;
    ArrayList pieEntries;
    ArrayList PieEntryLabels;
    String idForTodayStats, idForYesterdayStats,  idForStartDate, idForEndDate;
    Date  yesterDay, aMonthBefore;
    public SellerProfileFragmentCustomStat() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.seller_profile_fragment_custom_stat, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){assert user != null;sellerId = user.getUid();}else{sellerId = "null";}
        pieEntries = new ArrayList<>();
        myFormat="MM-yyyy";
        myFormat2="dd-MM-yyyy";
        date = new Date();
        dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        dateFormat2 = new SimpleDateFormat(myFormat2, Locale.US);
        idForMonthlyStats = String.valueOf((dateFormat.format(date)));
        idForDailyStats = String.valueOf((dateFormat2.format(date)));
        yesterDay = new Date(date.getTime() - 24*60*60*1000);
        aMonthBefore = new Date(date.getTime() - 30L *24*60*60*1000);
        idForYesterdayStats = String.valueOf((dateFormat2.format(yesterDay)));
        idForTodayStats = String.valueOf((dateFormat2.format(date)));
        idForStartDate = String.valueOf((dateFormat2.format(aMonthBefore)));
        idForEndDate = String.valueOf((dateFormat2.format(date)));
//        System.out.println(idForStartDate);
//        System.out.println(idForEndDate);
        db = FirebaseFirestore.getInstance();
        sellersSalaryCol = db.collection("SellersSalary");
        statSeller = db.collection("StatSellers");


        pieChart = (PieChart) v.findViewById(R.id.idPieChart);
        getEntries();
        Description descChartDescription = new Description();
        descChartDescription.setEnabled(false);
        descChartDescription.setText("Savdodan tushgan kunlik foyda");
        descChartDescription.setTextColor(Color.argb(200,0,0,0));
        descChartDescription.setEnabled(true);
        descChartDescription.setTextSize(12);
        pieChart.setBackgroundColor(Color.argb(20,0,0,0));
        pieChart.setEntryLabelColor(Color.argb(255,255,255,255));
        pieChart.setNoDataText("Oxirgi ikki kun ichida buyurtmalar topilmadi");
        pieChart.setDescription(descChartDescription);
        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(40);
        pieChart.setTransparentCircleAlpha(0);

        chart = v.findViewById(R.id.idPieChart2);
        chart2 = v.findViewById(R.id.idPieChart3);

        dailyStatTextLine = v.findViewById(R.id.dailyStatTextLine);
        dailyStatTextLine.setText(idForStartDate.toString()+" - "+idForTodayStats.toString()+" Statistikasi");
        sellerStatDataPickerCard = v.findViewById(R.id.sellerStatDataPickerCard);
        MaterialDatePicker<Pair<Long, Long>> materialDatePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setSelection(Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds())).build();
        sellerStatDataPickerCard.setOnClickListener(view -> materialDatePicker.show(requireActivity().getSupportFragmentManager(), "Tag_picker"));
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            addDataToTable(selection.first, selection.second);

            dailyStatTextLine.setText(dateFormat2.format(selection.first)+" - "+dateFormat2.format(selection.second)+" Statistikasi");
        });

        totalStatsRW = v.findViewById(R.id.totalStatsRW);
        adminTotalStatsModel = new ArrayList<>();
        totalStatsRW.setHasFixedSize(true);
        totalStatsRW.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        sellerCustomStatAdapter = new SellerCustomStatAdapter(adminTotalStatsModel, getContext());
        addDefaultDataToTable();
        totalStatsRW.setAdapter(sellerCustomStatAdapter);
        sellerCustomStatAdapter.notifyDataSetChanged();

        sellersSalaryTextView = v.findViewById(R.id.sellersSalaryTextView);
        getSellersSalary();
        return v;
    }

    private void getSellersSalary() {
        db.collection("SellersSalary").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentSnapshot d:value.getDocuments()){
                    if(d.getData().containsKey(sellerId)){
                        Map<String, Object> outerMap = d.getData();
                        for(Map.Entry<String, Object> entry:outerMap.entrySet()){
                            if(Objects.equals(entry.getKey(), sellerId)){
                                long caSa = 0;
                                long giCaSa = 0;
                                long crSa = 0;
                                long giCrSa = 0;
                                Map<String,Object> innerMap = (Map<String, Object>) entry.getValue();
                                for(Map.Entry<String, Object> entry2:innerMap.entrySet()){
                                    if(Objects.equals(entry2.getKey(), "cashSalary")){
                                        caSa = Long.parseLong(entry2.getValue().toString());
                                    }
                                    if(Objects.equals(entry2.getKey(), "creditSalary")){
                                        crSa = Long.parseLong(entry2.getValue().toString());
                                    }
                                    if(Objects.equals(entry2.getKey(), "givenCashSalary")){
                                        giCaSa = Long.parseLong(entry2.getValue().toString());
                                    }
                                    if(Objects.equals(entry2.getKey(), "givenCreditSalary\n")){
                                        giCrSa = Long.parseLong(entry2.getValue().toString());
                                    }
                                }


                                sellersSalaryTextView.setText(String.format("%s so'm", String.valueOf(decim.format((crSa + caSa) - (giCrSa + giCaSa)))));
                            }
                        }

                    }
                }
            }
        });
    }

    private void addDataToTable(Long first, Long second) {
        long nDay = ((second - first)/24/60/60/1000);
        adminTotalStatsModel.clear();
        for(int ii=0;ii<=nDay;ii++){
            String idForAnyDay = String.valueOf((dateFormat2.format(new Date(second- (long) ii *24*60*60*1000))));
            toCaSf[0] =0;
            toCaQf[0] =0;
            toCrSf[0] =0;
            toCrQf[0] =0;
            statSeller.document(idForAnyDay).addSnapshotListener((d, error) -> {
                if(d.exists() && d.getData()!=null){
                    Map<String, Object> map = d.getData();
                    long toCaS = 0;
                    long toCaQ = 0;
                    long toCrS = 0;
                    long toCrQ = 0;
                    for (Map.Entry<String, Object> entry : map.entrySet()) {

                        if(Objects.equals(entry.getKey(), sellerId)) {
                            Map<String, Object> map2 = (Map<String, Object>) entry.getValue();
                            final long[] totalCaS = {0};
                            final long[] totalCaQ = {0};
                            final long[] totalCrS = {0};
                            final long[] totalCrQ = {0};
                            int i = 0;

                            for (Map.Entry<String, Object> entry2 : map2.entrySet()) {
                            if (entry2.getValue() != null) {
                                if (Objects.equals(entry2.getKey().toString(), "totalCashSalary")) {
                                    totalCaS[0] = Long.parseLong(entry2.getValue().toString());
                                    i++;
                                }
                                if (Objects.equals(entry2.getKey().toString(), "totalCreditSalary")) {
                                    totalCrS[0] = Long.parseLong(entry2.getValue().toString());
                                    i++;
                                }
                                if (Objects.equals(entry2.getKey().toString(), "totalCreditQuantity")) {
                                    totalCrQ[0] = Long.parseLong(entry2.getValue().toString());
                                    i++;
                                }
                                if (Objects.equals(entry2.getKey().toString(), "totalCashQuantity")) {
                                    totalCaQ[0] = Long.parseLong(entry2.getValue().toString());
                                    i++;
                                }
                                if (Objects.equals(entry2.getKey().toString(), "totalCashBoughtPrice")) {
                                    i++;
                                }
                                if (Objects.equals(entry2.getKey().toString(), "totalCashPrice")) {
                                    i++;
                                }
                                if (Objects.equals(entry2.getKey().toString(), "totalCreditBoughtPrice")) {
                                    i++;
                                }
                                if (Objects.equals(entry2.getKey().toString(), "totalCreditPrice")) {
                                    i++;
                                }
                                if (Objects.equals(entry2.getKey().toString(), "creditSalaryPercent")) {
                                    i++;
                                }
                                if (Objects.equals(entry2.getKey().toString(), "cashSalaryPercent")) {
                                    i++;
                                }
                            }

                            if (i == map2.size()) {
                                toCaS += totalCaS[0];
                                toCaQ += totalCaQ[0];
                                toCrS += totalCrS[0];
                                toCrQ += totalCrQ[0];
                            }
//                            }
                        }
                        }
                    }
                    SellerCustomStatModel dataModel = new SellerCustomStatModel();
                    dataModel.setId(d.getId());
                    dataModel.setSellerId(sellerId);
                    dataModel.setTotalCashSalary(toCaS);
                    dataModel.setTotalCreditSalary(toCrS);
                    dataModel.setTotalCashQuantity((int) toCaQ);
                    dataModel.setTotalCreditQuantity((int) toCrQ);
                    adminTotalStatsModel.add(dataModel);
                    sellerCustomStatAdapter.notifyDataSetChanged();
//
                    toCaSf[0] +=toCaS;
                    toCaQf[0] +=toCaQ;
                    toCrSf[0] +=toCrS;
                    toCrQf[0] +=toCrQ;

                    BarData data = createChartData(toCaSf[0], toCrSf[0] );
                    BarData data2 = createChartData2( toCaQf[0], toCrQf[0] );
                    configureChartAppearance();
                    prepareChartData(data);
                    prepareChartData2( data2);
                }
                else{
                    toCaSf[0] +=0;
                    toCaQf[0] +=0;
                    toCrSf[0] +=0;
                    toCrQf[0] +=0;

                    sellerCustomStatAdapter.notifyDataSetChanged();
                    BarData data = createChartData(toCaSf[0], toCrSf[0] );
                    BarData data2 = createChartData2( toCaQf[0], toCrQf[0] );
                    configureChartAppearance();
                    prepareChartData(data);
                    prepareChartData2( data2);
                }
            });
        }
    }
    private void addDefaultDataToTable() {
        long nDay = 30;
        adminTotalStatsModel.clear();
        for(int ii=0;ii<=nDay;ii++){
            String idForAnyDay = String.valueOf((dateFormat2.format(new Date(new Date().getTime() - (long) ii *24*60*60*1000))));

            statSeller.document(idForAnyDay).addSnapshotListener((d, error) -> {
                if(d.exists() && d.getData()!=null){
                    Map<String, Object> map = d.getData();
                    long toCaS = 0;
                    long toCaQ = 0;
                    long toCrS = 0;
                    long toCrQ = 0;
                    for (Map.Entry<String, Object> entry : map.entrySet()) {

                        if(Objects.equals(entry.getKey(), sellerId)) {
                            Map<String, Object> map2 = (Map<String, Object>) entry.getValue();
                            final long[] totalCaS = {0};
                            final long[] totalCaQ = {0};
                            final long[] totalCrS = {0};
                            final long[] totalCrQ = {0};
                            int i = 0;

                            for (Map.Entry<String, Object> entry2 : map2.entrySet()) {
                                if (entry2.getValue() != null) {
                                    if (Objects.equals(entry2.getKey().toString(), "totalCashSalary")) {
                                        totalCaS[0] = Long.parseLong(entry2.getValue().toString());
                                        i++;
                                    }
                                    if (Objects.equals(entry2.getKey().toString(), "totalCreditSalary")) {
                                        totalCrS[0] = Long.parseLong(entry2.getValue().toString());
                                        i++;
                                    }
                                    if (Objects.equals(entry2.getKey().toString(), "totalCreditQuantity")) {
                                        totalCrQ[0] = Long.parseLong(entry2.getValue().toString());
                                        i++;
                                    }
                                    if (Objects.equals(entry2.getKey().toString(), "totalCashQuantity")) {
                                        totalCaQ[0] = Long.parseLong(entry2.getValue().toString());
                                        i++;
                                    }
                                    if (Objects.equals(entry2.getKey().toString(), "totalCashBoughtPrice")) {
                                        i++;
                                    }
                                    if (Objects.equals(entry2.getKey().toString(), "totalCashPrice")) {
                                        i++;
                                    }
                                    if (Objects.equals(entry2.getKey().toString(), "totalCreditBoughtPrice")) {
                                        i++;
                                    }
                                    if (Objects.equals(entry2.getKey().toString(), "totalCreditPrice")) {
                                        i++;
                                    }
                                    if (Objects.equals(entry2.getKey().toString(), "creditSalaryPercent")) {
                                        i++;
                                    }
                                    if (Objects.equals(entry2.getKey().toString(), "cashSalaryPercent")) {
                                        i++;
                                    }
                                }

                                if (i == map2.size()) {
                                    toCaS += totalCaS[0];
                                    toCaQ += totalCaQ[0];
                                    toCrS += totalCrS[0];
                                    toCrQ += totalCrQ[0];
                                }
//                            }
                            }
                        }
                    }
                    SellerCustomStatModel dataModel = new SellerCustomStatModel();
                    dataModel.setId(d.getId());
                    dataModel.setSellerId(sellerId);
                    dataModel.setTotalCashSalary(toCaS);
                    dataModel.setTotalCreditSalary(toCrS);
                    dataModel.setTotalCashQuantity((int) toCaQ);
                    dataModel.setTotalCreditQuantity((int) toCrQ);
                    adminTotalStatsModel.add(dataModel);
                    sellerCustomStatAdapter.notifyDataSetChanged();
//
                    toCaSf[0] +=toCaS;
                    toCaQf[0] +=toCaQ;
                    toCrSf[0] +=toCrS;
                    toCrQf[0] +=toCrQ;

                    BarData data = createChartData(toCaSf[0], toCrSf[0] );
                    BarData data2 = createChartData2( toCaQf[0], toCrQf[0] );
                    configureChartAppearance();
                    prepareChartData(data);
                    prepareChartData2( data2);
                }
                else{
                }
            });
        }
    }
    private void configureChartAppearance() {
        chart.setDrawGridBackground(false);
        chart2.setDrawGridBackground(false);
        chart.setDrawValueAboveBar(false);
        chart2.setDrawValueAboveBar(false);
        chart.setBackgroundColor(Color.argb(20,0,0,0));
        chart2.setBackgroundColor(Color.argb(20,0,0,0));
        chart.getDescription().setEnabled(false);
        chart2.getDescription().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        XAxis xAxis2 = chart2.getXAxis();
        xAxis.setGranularity(1f);
        xAxis2.setGranularity(1f);

        YAxis leftAxis = chart.getAxisLeft();
        YAxis leftAxis2 = chart2.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis2.setDrawGridLines(false);

        YAxis rightAxis = chart.getAxisRight();
        YAxis rightAxis2 = chart2.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis2.setDrawGridLines(false);
    }
    private void prepareChartData(BarData data) {
        data.setValueTextSize(12f);
        chart.setData(data);
        chart.invalidate();
    }
    private void prepareChartData2(BarData data) {
        data.setValueTextSize(12f);
        chart2.setData(data);
        chart2.invalidate();
    }
    private BarData createChartData(long l, long l1) {
        ArrayList<BarEntry> values = new ArrayList<>();

        values.add(new BarEntry(1, new float[]{(float) l}));
        values.add(new BarEntry(2, new float[]{(float) l1}));
        DecimalFormat decim = new DecimalFormat("#,###.##");
        BarDataSet set1 = new BarDataSet(values, "Daromad: "+ String.valueOf(decim.format(l1+l))+" so'm");

        set1.setColors(new int[] {getContext().getColor(R.color.SendingOrders), getContext().getColor(R.color.PendingOrders)});

        set1.setValueTextColor(getContext().getColor(R.color.white));
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        return new BarData(dataSets);
    }
    private BarData createChartData2(long l, long l1) {
        ArrayList<BarEntry> values2 = new ArrayList<>();

        values2.add(new BarEntry(1, new float[]{(int) l}));
        values2.add(new BarEntry(2, new float[]{(int) l1}));
        BarDataSet set2 = new BarDataSet(values2, "Jami: "+String.valueOf((int) l + (int) l1)+" ta mahsulot sotilgan");
        set2.setColors(new int[] {getContext().getColor(R.color.SendingOrders),getContext().getColor(R.color.PendingOrders)});

        set2.setValueTextColor(getContext().getColor(R.color.white));
        ArrayList<IBarDataSet> dataSets2 = new ArrayList<>();
        dataSets2.add(set2);

        return new BarData(dataSets2);
    }
    private void getEntries() {
        statSeller.document(idForDailyStats).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot d = task.getResult();
                if(d.exists()){
                    Map<String, Object> outerMap = d.getData();
                    long tCaS = 0;
                    long tCrS = 0;
                    pieEntries = new ArrayList<>();
                    for(Map.Entry<String, Object> entry: outerMap.entrySet()){
                        if(!Objects.equals(entry.getKey(), sellerId)) {
                            Map<String, Object> innerMap = (Map<String, Object>) entry.getValue();
                            for(Map.Entry<String, Object> entry2:innerMap.entrySet()){
                                if(Objects.equals(entry2.getKey(), "totalCashSalary")){
                                    tCaS = Long.parseLong(entry2.getValue().toString());
                                }
                                if(Objects.equals(entry2.getKey(), "totalCreditSalary")){
                                    tCrS = Long.parseLong(entry2.getValue().toString());
                                }
                            }
                        }
                    }
                    yData = new float[]{tCaS, tCrS};
                    addDataSet();
                }
                else {
                }
            }
        });
    }
    private void addDataSet() {
        Log.d(TAG, "addDataSet started");
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();
        ArrayList<String > allLabes = new ArrayList<>();
        allLabes.add("Naqd savdodan tushgan foyda");
        allLabes.add("Kredit savdodan tushgan foyda");
        for(int i = 0; i < yData.length; i++){
            yEntrys.add(new PieEntry(yData[i] ,xData[i]));
        }

        for(int i = 1; i < xData.length; i++){
            xEntrys.add(xData[i]);
        }

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "Savdodan tushgan foyda");
        pieDataSet.setSliceSpace(5);
        pieDataSet.setValueTextSize(12);
        pieDataSet.setValueTextColor(Color.argb(255,255,255,255));

        //add colors to dataset
        ArrayList<Integer> colors = new ArrayList<>();

        colors.add(getContext().getColor(R.color.SendingOrders));
        colors.add(getContext().getColor(R.color.PendingOrders));


        pieDataSet.setColors(colors);

        //add legend to chart
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
//        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}