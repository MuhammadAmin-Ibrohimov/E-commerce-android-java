package com.example.lidertrade.admin.fragments;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.adapters.AdminReportFragmentTotalAdapter;
import com.example.lidertrade.admin.models.AdminTotalStatsModel;
import com.example.lidertrade.seller.adapters.SHFCategoryAdapter;
import com.example.lidertrade.seller.models.SHFSaleModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class AdminReportFragmentTotal extends Fragment {

    View view;
    int delivered=0, canceled=0, pending=0;
    TextView tvR, tvPython, tvCPP, tvJava, dailyStatText, dailyStatTextLine;
    LineChart lineChart;
    LineData lineData;
    LineDataSet lineDataSet;
    ArrayList lineEntries;
    CardView sellerStatDataPickerCard;
    private float[] yData, yData2;
    String idForTodayStats, idForYesterdayStats, myFormat, idForStartDate, idForEndDate;
    SimpleDateFormat dateFormat;
    Date date, yesterDay, aMonthBefore;
    private String[] xData = {"Naqd", "Kredit"};
    PieChart pieChart, idPieChart2;
    CollectionReference statProducts;
    private ArrayList<AdminTotalStatsModel>  adminTotalStatsModel;
    AdminReportFragmentTotalAdapter adminReportFragmentTotalAdapter;

    private FirebaseFirestore db;
    RecyclerView totalStatsRW;







    private BarChart chart, chart2;



    final long[] toBoPf = {0};
    final long[] toCaPf = {0};
    final long[] toCaQf = {0};
    final long[] toCrPf = {0};
    final long[] toCrQf = {0};
    public AdminReportFragmentTotal() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.admin_report_fragment_total, container, false);
        db = FirebaseFirestore.getInstance();
        statProducts = db.collection("StatProducts");
//        Get Current date
        myFormat="dd-MM-yyyy";
        dateFormat = new SimpleDateFormat(myFormat, Locale.US);

        date = new Date();
        yesterDay = new Date(date.getTime() - 24*60*60*1000);
        aMonthBefore = new Date(date.getTime() - 30L *24*60*60*1000);
        idForYesterdayStats = String.valueOf((dateFormat.format(yesterDay)));
        idForTodayStats = String.valueOf((dateFormat.format(date)));
        idForStartDate = String.valueOf((dateFormat.format(aMonthBefore)));
        idForEndDate = String.valueOf((dateFormat.format(date)));
        dailyStatTextLine = (TextView) view.findViewById(R.id.dailyStatTextLine);
        dailyStatTextLine.setText(idForStartDate.toString()+" - "+idForTodayStats.toString()+" Statistikasi");
        loadDataToPieChart();

//        PieChart
        dailyStatText = (TextView) view.findViewById(R.id.dailyStatText);
        pieChart = (PieChart) view.findViewById(R.id.idPieChart);
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
        chart = view.findViewById(R.id.idPieChart2);
        chart2 = view.findViewById(R.id.idPieChart3);
        pieChart.setTransparentCircleAlpha(0);
//        End

        sellerStatDataPickerCard = view.findViewById(R.id.sellerStatDataPickerCard);
        MaterialDatePicker<Pair<Long, Long>> materialDatePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setSelection(Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds())).build();
        sellerStatDataPickerCard.setOnClickListener(view -> materialDatePicker.show(requireActivity().getSupportFragmentManager(), "Tag_picker"));
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            addDataToTable(selection.first, selection.second);

            dailyStatTextLine.setText(dateFormat.format(selection.first)+" - "+dateFormat.format(selection.second)+" Statistikasi");
        });

        totalStatsRW = view.findViewById(R.id.totalStatsRW);
        adminTotalStatsModel = new ArrayList<>();
        totalStatsRW.setHasFixedSize(true);
        totalStatsRW.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adminReportFragmentTotalAdapter = new AdminReportFragmentTotalAdapter(adminTotalStatsModel, getContext());
        addDefaultDataToTable();
        totalStatsRW.setAdapter(adminReportFragmentTotalAdapter);
        adminReportFragmentTotalAdapter.notifyDataSetChanged();

        return view;
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
    private void addDataToTable(Long first, Long second) {
        long nDay = ((second - first)/24/60/60/1000);
        adminTotalStatsModel.clear();
        for(int ii=0;ii<=nDay;ii++){
            toBoPf[0] =0;
            toCaPf[0] =0;
            toCaQf[0] =0;
            toCrPf[0] =0;
            toCrQf[0] =0;
            String idForAnyDay = String.valueOf((dateFormat.format(new Date(second- (long) ii *24*60*60*1000))));
            statProducts.document(idForAnyDay).addSnapshotListener((d, error) -> {
                if(d.exists() && d.getData()!=null){
                    System.out.println(d.getId());
                    System.out.println(d.getData());
                    Map<String, Object> map = d.getData();
                    long toBoP = 0;
                    long toCaP = 0;
                    long toCaQ = 0;
                    long toCrP = 0;
                    long toCrQ = 0;
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        Map<String, Object> map2 = (Map<String, Object>) entry.getValue();
                        final long[] totalBP = {0};
                        final long[] totalCaP = {0};
                        final long[] totalCaQ = {0};
                        final long[] totalCrP = {0};
                        final long[] totalCrQ = {0};
                        int i=0;
                        for (Map.Entry<String, Object> entry2 : map2.entrySet()){
                            if(!map2.containsValue(null)){
                                if(Objects.equals(entry2.getKey().toString(), "boughtPrice")){
                                    totalBP[0] = Long.parseLong(entry2.getValue().toString());
                                    i++;
                                }
                                if(Objects.equals(entry2.getKey().toString(), "cashPrice")){
                                    totalCaP[0] =  Long.parseLong(entry2.getValue().toString());
                                    i++;
                                }
                                if(Objects.equals(entry2.getKey().toString(), "creditPrice")){
                                    totalCrP[0] =  Long.parseLong(entry2.getValue().toString());
                                    i++;
                                }
                                if(Objects.equals(entry2.getKey().toString(), "creditSoldQuantity")){
                                    totalCrQ[0] =  Long.parseLong(entry2.getValue().toString());
                                    i++;
                                }
                                if(Objects.equals(entry2.getKey().toString(), "cashSoldQuantity")){
                                    totalCaQ[0] =  Long.parseLong(entry2.getValue().toString());
                                    i++;
                                }
                                if(Objects.equals(entry2.getKey().toString(), "productId")){
                                    i++;
                                }
                                if(Objects.equals(entry2.getKey().toString(), "productName")){
                                    i++;
                                }
                                if(i==map2.size()){
                                    toBoP += totalBP[0];
                                    toCaP += totalCaP[0];
                                    toCaQ += totalCaQ[0];
                                    toCrP += totalCrP[0];
                                    toCrQ += totalCrQ[0];
                                }
                            }
                        }
                    }

                    AdminTotalStatsModel dataModel = new AdminTotalStatsModel();
                    dataModel.setId(d.getId());
                    dataModel.setBoughtPrice(toBoP);
                    dataModel.setCashPrice(toCaP);
                    dataModel.setCashSoldQuantity(toCaQ);
                    dataModel.setCreditPrice(toCrP);
                    dataModel.setCreditSoldQuantity(toCrQ);
                    adminTotalStatsModel.add(dataModel);
                    adminReportFragmentTotalAdapter.notifyDataSetChanged();
                    toBoPf[0] +=toBoP;
                    toCaPf[0] +=toCaP;
                    toCaQf[0] +=toCaQ;
                    toCrPf[0] +=toCrP;
                    toCrQf[0] +=toCrQ;

                    BarData data = createChartData(toBoPf[0],toCaPf[0], toCrPf[0] );
                    BarData data2 = createChartData2( toCaQf[0], toCrQf[0] );
                    configureChartAppearance();
                    prepareChartData(data);
                    prepareChartData2( data2);
                }
                else{
                    toBoPf[0] +=0;
                    toCaPf[0] +=0;
                    toCaQf[0] +=0;
                    toCrPf[0] +=0;
                    toCrQf[0] +=0;
                    BarData data = createChartData(toBoPf[0],toCaPf[0], toCrPf[0] );
                    BarData data2 = createChartData2( toCaQf[0], toCrQf[0] );
                    configureChartAppearance();
                    prepareChartData(data);
                    prepareChartData2( data2);
                }
            });
        }
    }

    private BarData createChartData(long l, long l1, long l3) {
        ArrayList<BarEntry> values = new ArrayList<>();

        values.add(new BarEntry(1, new float[]{(float) l1, (float) l3}));
        values.add(new BarEntry(2, new float[]{(float) l}));
        DecimalFormat decim = new DecimalFormat("#,###.##");
        BarDataSet set1 = new BarDataSet(values, "Daromad: "+ String.valueOf(decim.format(l1+l3-l))+" so'm");

        set1.setColors(new int[] {getContext().getColor(R.color.SendingOrders), getContext().getColor(R.color.PendingOrders), ColorTemplate.MATERIAL_COLORS[2]});
        set1.setStackLabels(new String[] {"Daromad", "Kredit", "Xarajat"});
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



    private void addDefaultDataToTable() {
        long nDay = 30;
        adminTotalStatsModel.clear();
        for(int ii=0;ii<=nDay;ii++){
            String idForAnyDay = String.valueOf((dateFormat.format(new Date(new Date().getTime() - (long) ii *24*60*60*1000))));
            statProducts.document(idForAnyDay).addSnapshotListener((d, error) -> {
                try {
                    if(d.exists() && d.getData()!=null){
                        Map<String, Object> map = d.getData();
                        long toBoP = 0;
                        long toCaP = 0;
                        long toCaQ = 0;
                        long toCrP = 0;
                        long toCrQ = 0;

                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            Map<String, Object> map2 = (Map<String, Object>) entry.getValue();
                            final long[] totalBP = {0};
                            final long[] totalCaP = {0};
                            final long[] totalCaQ = {0};
                            final long[] totalCrP = {0};
                            final long[] totalCrQ = {0};
                            int i=0;
                            for (Map.Entry<String, Object> entry2 : map2.entrySet()){
                                if(!map2.containsValue(null)){
                                    if(Objects.equals(entry2.getKey().toString(), "boughtPrice")){
                                        totalBP[0] = Long.parseLong(entry2.getValue().toString());
                                        i++;
                                    }
                                    if(Objects.equals(entry2.getKey().toString(), "cashPrice")){
                                        totalCaP[0] =  Long.parseLong(entry2.getValue().toString());
                                        i++;
                                    }
                                    if(Objects.equals(entry2.getKey().toString(), "creditPrice")){
                                        totalCrP[0] =  Long.parseLong(entry2.getValue().toString());
                                        i++;
                                    }
                                    if(Objects.equals(entry2.getKey().toString(), "creditSoldQuantity")){
                                        totalCrQ[0] =  Long.parseLong(entry2.getValue().toString());
                                        i++;
                                    }
                                    if(Objects.equals(entry2.getKey().toString(), "cashSoldQuantity")){
                                        totalCaQ[0] =  Long.parseLong(entry2.getValue().toString());
                                        i++;
                                    }
                                    if(Objects.equals(entry2.getKey().toString(), "productId")){
                                        i++;
                                    }
                                    if(Objects.equals(entry2.getKey().toString(), "productName")){
                                        i++;
                                    }
                                }

                                if(i==map2.size()){
                                    toBoP += totalBP[0];
                                    toCaP += totalCaP[0];
                                    toCaQ += totalCaQ[0];
                                    toCrP += totalCrP[0];
                                    toCrQ += totalCrQ[0];
                                }
                            }
                        }
                        AdminTotalStatsModel dataModel = new AdminTotalStatsModel();
                        dataModel.setId(d.getId());
                        dataModel.setBoughtPrice(toBoP);
                        dataModel.setCashPrice(toCaP);
                        dataModel.setCashSoldQuantity(toCaQ);
                        dataModel.setCreditPrice(toCrP);
                        dataModel.setCreditSoldQuantity(toCrQ);
                        adminTotalStatsModel.add(dataModel);
                        adminReportFragmentTotalAdapter.notifyDataSetChanged();
                        toBoPf[0] +=toBoP;
                        toCaPf[0] +=toCaP;
                        toCaQf[0] +=toCaQ;
                        toCrPf[0] +=toCrP;
                        toCrQf[0] +=toCrQ;

                        BarData data = createChartData(toBoPf[0],toCaPf[0], toCrPf[0] );
                        BarData data2 = createChartData2( toCaQf[0], toCrQf[0] );
                        configureChartAppearance();
                        prepareChartData(data);
                        prepareChartData2( data2);
                    }
                }catch (NullPointerException e){

                }
            });
        }
    }

    private void loadDataToPieChart() {
        statProducts.document(idForTodayStats).addSnapshotListener((d, error) -> {
            try {
                if(d.exists() && d.getData()!=null){
                    Map<String, Object> map = d.getData();
                    long toCaP = 0;
                    long toCrP = 0;
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        Map<String, Object> map2 = (Map<String, Object>) entry.getValue();
                        final long[] totalBP = {0};
                        final long[] totalCaP = {0};
                        final long[] totalCaQ = {0};
                        final long[] totalCrP = {0};
                        final long[] totalCrQ = {0};
                        int i=0;
                        for (Map.Entry<String, Object> entry2 : map2.entrySet()){
                            if(!map2.containsValue(null)){
                                if(Objects.equals(entry2.getKey().toString(), "boughtPrice")){
                                    totalBP[0] = Long.parseLong(entry2.getValue().toString());
                                    i++;
                                }
                                if(Objects.equals(entry2.getKey().toString(), "cashPrice")){
                                    totalCaP[0] =  Long.parseLong(entry2.getValue().toString());
                                    i++;
                                }
                                if(Objects.equals(entry2.getKey().toString(), "creditPrice")){
                                    totalCrP[0] =  Long.parseLong(entry2.getValue().toString());
                                    i++;
                                }
                                if(Objects.equals(entry2.getKey().toString(), "creditSoldQuantity")){
                                    totalCrQ[0] =  Long.parseLong(entry2.getValue().toString());
                                    i++;
                                }
                                if(Objects.equals(entry2.getKey().toString(), "cashSoldQuantity")){
                                    totalCaQ[0] =  Long.parseLong(entry2.getValue().toString());
                                    i++;
                                }
                                if(Objects.equals(entry2.getKey().toString(), "productId")){
                                    i++;
                                }
                                if(Objects.equals(entry2.getKey().toString(), "productName")){
                                    i++;
                                }
                                if(i==map2.size()){
                                    toCaP += (totalCaP[0]-totalBP[0])*totalCaQ[0];
                                    toCrP += (totalCrP[0]-totalBP[0])*totalCrQ[0];
                                }
                            }

                        }
                    }
                    dailyStatText.setText(idForTodayStats + " sanadagi umumiy statistika");
                    yData = new float[]{toCaP, toCrP};
                    pieChart.setCenterText(idForTodayStats);
                    addDataSet();

                }
                else{
                    statProducts.document(idForYesterdayStats).get().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            DocumentSnapshot dd = task.getResult();
                            if(dd.exists() && dd.getData()!=null){
                                Map<String, Object> map = dd.getData();
                                long toCaP = 0;
                                long toCrP = 0;
                                for (Map.Entry<String, Object> entry : map.entrySet()) {
                                    Map<String, Object> map2 = (Map<String, Object>) entry.getValue();
                                    final long[] totalBP = {0};
                                    final long[] totalCaP = {0};
                                    final long[] totalCaQ = {0};
                                    final long[] totalCrP = {0};
                                    final long[] totalCrQ = {0};
                                    int i=0;
                                    for (Map.Entry<String, Object> entry2 : map2.entrySet()){
                                        if(!map2.containsValue(null)){
                                            if(Objects.equals(entry2.getKey().toString(), "boughtPrice")){
                                                totalBP[0] = Long.parseLong(entry2.getValue().toString());
                                                i++;
                                            }
                                            if(Objects.equals(entry2.getKey().toString(), "cashPrice")){
                                                totalCaP[0] =  Long.parseLong(entry2.getValue().toString());
                                                i++;
                                            }
                                            if(Objects.equals(entry2.getKey().toString(), "creditPrice")){
                                                totalCrP[0] =  Long.parseLong(entry2.getValue().toString());
                                                i++;
                                            }
                                            if(Objects.equals(entry2.getKey().toString(), "creditSoldQuantity")){
                                                totalCrQ[0] =  Long.parseLong(entry2.getValue().toString());
                                                i++;
                                            }
                                            if(Objects.equals(entry2.getKey().toString(), "cashSoldQuantity")){
                                                totalCaQ[0] =  Long.parseLong(entry2.getValue().toString());
                                                i++;
                                            }
                                            if(Objects.equals(entry2.getKey().toString(), "productId")){
                                                i++;
                                            }
                                            if(Objects.equals(entry2.getKey().toString(), "productName")){
                                                i++;
                                            }
                                            if(i==map2.size()){

                                                toCaP += (totalCaP[0]-totalBP[0])*totalCaQ[0];
                                                toCrP += (totalCrP[0]-totalBP[0])*totalCrQ[0];

                                            }
                                        }
                                    }
                                }
                                yData = new float[]{toCaP, toCrP};
                                pieChart.setCenterText(idForYesterdayStats);
                                dailyStatText.setText(String.format("%s sanadagi umumiy statistika", idForYesterdayStats));
                                addDataSet();

                            }
                        }
                    });
                }
            }catch (NullPointerException e){

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