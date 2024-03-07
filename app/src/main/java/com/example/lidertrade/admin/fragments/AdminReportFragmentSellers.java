package com.example.lidertrade.admin.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.adapters.AdminReportFragmentSellersAdapter;
import com.example.lidertrade.admin.models.AdminSellerStatsModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class AdminReportFragmentSellers extends Fragment {

    View view;
    TextView  dailyStatTextLine;

    CardView sellerStatDataPickerCard;
    private float[] yData;
    String idForTodayStats, idForYesterdayStats, myFormat, idForStartDate, idForEndDate;
    SimpleDateFormat dateFormat;
    Date date, yesterDay, aMonthBefore;
    private String[] xData = {"Naqd", "Kredit"};
    PieChart pieChart;
    CollectionReference statProducts, sellerStatHelper;
    private ArrayList<AdminSellerStatsModel>  adminTotalStatsModel;
    AdminReportFragmentSellersAdapter adminReportFragmentTotalAdapter;

    private FirebaseFirestore db;
    RecyclerView totalStatsRW;


    public AdminReportFragmentSellers() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.admin_report_fragment_sellers, container, false);
        db = FirebaseFirestore.getInstance();
        statProducts = db.collection("StatSellers");
        sellerStatHelper = db.collection("SellerStatHelper");
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






        sellerStatDataPickerCard = view.findViewById(R.id.sellerStatDataPickerCard);
        MaterialDatePicker<Pair<Long, Long>> materialDatePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setSelection(Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds())).build();
        sellerStatDataPickerCard.setOnClickListener(view -> materialDatePicker.show(requireActivity().getSupportFragmentManager(), "Tag_picker"));
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            addDataToTable(selection.first, selection.second);
            String customStartDate = String.valueOf((dateFormat.format(selection.first)));
            String customEndDate = String.valueOf((dateFormat.format(selection.second)));
            addDataToRW(customEndDate, customStartDate);
            dailyStatTextLine.setText(dateFormat.format(selection.first)+" - "+dateFormat.format(selection.second)+" Statistikasi");
        });

        totalStatsRW = view.findViewById(R.id.totalStatsRW);
        adminTotalStatsModel = new ArrayList<>();
        totalStatsRW.setHasFixedSize(true);
        totalStatsRW.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adminReportFragmentTotalAdapter = new AdminReportFragmentSellersAdapter(adminTotalStatsModel, getContext());
        addDefaultDataToTable();
        totalStatsRW.setAdapter(adminReportFragmentTotalAdapter);
        adminReportFragmentTotalAdapter.notifyDataSetChanged();
        addDefaultDataToRW();

        return view;
    }

    private void addDataToRW(String end, String start) {
        sellerStatHelper.whereEqualTo("dateRange", end+" "+start).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if(!queryDocumentSnapshots.isEmpty()){
                for(DocumentSnapshot d:queryDocumentSnapshots.getDocuments()){
                    if(d.exists()){
                        AdminSellerStatsModel dataModal = d.toObject(AdminSellerStatsModel.class);
                        dataModal.setId(d.getId());
                        dataModal.setTotalCashBoughtPrice(Long.parseLong(d.get("totalCashBoughtPrice").toString()));
                        dataModal.setTotalCreditBoughtPrice(Long.parseLong(d.get("totalCreditBoughtPrice").toString()));
                        dataModal.setTotalCashQuantity(Integer.parseInt(d.get("totalCashQuantity").toString()));
                        dataModal.setTotalCreditQuantity(Integer.parseInt(d.get("totalCreditQuantity").toString()));
                        dataModal.setCashSalaryPercent(Integer.parseInt(d.get("cashSalaryPercent").toString()));
                        dataModal.setCreditSalaryPercent(Integer.parseInt(d.get("creditSalaryPercent").toString()));
                        dataModal.setTotalCashPrice(Long.parseLong(d.get("totalCashPrice").toString()));
                        dataModal.setTotalCreditPrice(Long.parseLong(d.get("totalCreditPrice").toString()));
                        adminTotalStatsModel.add(dataModal);
                        adminReportFragmentTotalAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void addDataToTable(Long first, Long second) {
        String customStartDate = String.valueOf((dateFormat.format(first)));
        String customEndDate = String.valueOf((dateFormat.format(second)));
        sellerStatHelper.whereEqualTo("dataRange",customEndDate+" "+customStartDate).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(DocumentSnapshot d:task.getResult()){
                            d.getReference().delete();
                        }
                    }
                });
        db.collection("Users").whereEqualTo("userStatus", "Sotuvchi").addSnapshotListener((value, error) -> {
            if(!value.isEmpty()){
                for(DocumentSnapshot d:value.getDocuments()){
                    if(d.exists()){
                        final long[] tCaBoPr = {0};
                        final long[] tCrBoPr = {0};
                        final long[] tCaPr = {0};
                        final long[] tCaQ = {0};
                        final long[] tCrPr = {0};
                        final long[] tCrQ = {0};
                        final long[] caSP = {0};
                        final long[] crSP = {0};
                        ArrayList<String> idsList= new ArrayList<>();
                        long nDay = ((second - first)/24/60/60/1000);
                        adminTotalStatsModel.clear();
                        for(int ii=0;ii<=nDay;ii++){
                            String idForAnyDay = String.valueOf((dateFormat.format(new Date(second- (long) ii *24*60*60*1000))));
                            statProducts.document(idForAnyDay).addSnapshotListener((dd, error1) -> {
                                if(dd.exists() && dd.getData()!=null){
                                    if(dd.contains(d.getId())){
                                        Map<String, Object> map = dd.getData();
                                        Map<String, Object> sellerHelperMap = new HashMap<>();
                                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                                            if(d.getId().equals(entry.getKey())){
                                                Map<String, Object> map2 = (Map<String, Object>) entry.getValue();
                                                for (Map.Entry<String, Object> item : map2.entrySet()) {
                                                    if(!map2.containsValue(null)){
                                                        if(Objects.equals(item.getKey().toString(), "totalCashBoughtPrice")){
                                                            tCaBoPr[0] += Long.parseLong(item.getValue().toString());
                                                        }
                                                        if(Objects.equals(item.getKey().toString(), "totalCashQuantity")){
                                                            tCaQ[0] += Long.parseLong(item.getValue().toString());
                                                        }
                                                        if(Objects.equals(item.getKey().toString(), "totalCreditBoughtPrice")){
                                                            tCrBoPr[0] += Long.parseLong(item.getValue().toString());
                                                        }
                                                        if(Objects.equals(item.getKey().toString(), "totalCreditQuantity")){
                                                            tCrQ[0] += Long.parseLong(item.getValue().toString());
                                                        }
                                                        if(Objects.equals(item.getKey().toString(), "totalCashPrice")){
                                                            tCaPr[0] += Long.parseLong(item.getValue().toString());
                                                        }
                                                        if(Objects.equals(item.getKey().toString(), "totalCreditPrice")){
                                                            tCrPr[0] += Long.parseLong(item.getValue().toString());
                                                        }
                                                        if(Objects.equals(item.getKey().toString(), "cashSalaryPercent")){
                                                            caSP[0] = Long.parseLong(item.getValue().toString());
                                                        }
                                                        if(Objects.equals(item.getKey().toString(), "creditSalaryPercent")){
                                                            crSP[0] = Long.parseLong(item.getValue().toString());
                                                        }
                                                    }

                                                }
                                            }

                                        }
                                        sellerHelperMap.put("totalCashBoughtPrice", tCaBoPr[0]);
                                        sellerHelperMap.put("totalCashQuantity", tCaQ[0]);
                                        sellerHelperMap.put("totalCreditBoughtPrice", tCrBoPr[0]);
                                        sellerHelperMap.put("totalCreditQuantity", tCrQ[0]);
                                        sellerHelperMap.put("totalCashPrice", tCaPr[0]);
                                        sellerHelperMap.put("totalCreditPrice", tCrPr[0]);
                                        sellerHelperMap.put("cashSalaryPercent", caSP[0]);
                                        sellerHelperMap.put("creditSalaryPercent", crSP[0]);
                                        sellerHelperMap.put("userId", d.getId());
                                        sellerHelperMap.put("dateRange", customEndDate+" "+customStartDate);
                                        sellerStatHelper.document(d.getId()).set(sellerHelperMap, SetOptions.merge());

                                    }
                                }
                            });
                        }

                    }
                }
            }
        });
    }

    private void addDefaultDataToTable() {
        db.collection("Users").whereEqualTo("userStatus", "Sotuvchi").addSnapshotListener((value, error) -> {
            if(!value.isEmpty()){
                for(DocumentSnapshot d:value.getDocuments()){
                    adminTotalStatsModel.clear();
                    if(d.exists()){
                        long nDay = 30;
                        final long[] tCaBoPr = {0};
                        final long[] tCrBoPr = {0};
                        final long[] tCaPr = {0};
                        final long[] tCaQ = {0};
                        final long[] tCrPr = {0};
                        final long[] tCrQ = {0};
                        final long[] caSP = {0};
                        final long[] crSP = {0};
                        ArrayList<String> idsList= new ArrayList<>();
                        ArrayList<Map<String, Object>> allDataList = new ArrayList<>();
                        for(int ii=0;ii<=nDay;ii++) {
                            String idForAnyDay = String.valueOf((dateFormat.format(new Date(new Date().getTime() - (long) ii * 24 * 60 * 60 * 1000))));
                            statProducts.document(idForAnyDay).addSnapshotListener((dd, error1) -> {
                                if(dd.exists() && dd.getData()!=null){
                                    if(dd.contains(d.getId())){
                                        Map<String, Object> map = dd.getData();
                                        Map<String, Object> sellerHelperMap = new HashMap<>();
                                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                                            if(Objects.equals(entry.getKey(), d.getId().toString())){
                                                Map<String, Object> map2 = (Map<String, Object>) entry.getValue();
                                                for (Map.Entry<String, Object> item : map2.entrySet()) {
                                                    if(!map2.containsValue(null)) {


                                                        if (Objects.equals(item.getKey().toString(), "totalCashBoughtPrice")) {
                                                            tCaBoPr[0] += Long.parseLong(item.getValue().toString());
                                                        }
                                                        if (Objects.equals(item.getKey().toString(), "totalCashQuantity")) {
                                                            tCaQ[0] += Long.parseLong(item.getValue().toString());
                                                        }
                                                        if (Objects.equals(item.getKey().toString(), "totalCreditBoughtPrice")) {
                                                            tCrBoPr[0] += Long.parseLong(item.getValue().toString());
                                                        }
                                                        if (Objects.equals(item.getKey().toString(), "totalCreditQuantity")) {
                                                            tCrQ[0] += Long.parseLong(item.getValue().toString());
                                                        }
                                                        if (Objects.equals(item.getKey().toString(), "totalCashPrice")) {
                                                            tCaPr[0] += Long.parseLong(item.getValue().toString());
                                                        }
                                                        if (Objects.equals(item.getKey().toString(), "totalCreditPrice")) {
                                                            tCrPr[0] += Long.parseLong(item.getValue().toString());
                                                        }
                                                        if (Objects.equals(item.getKey().toString(), "cashSalaryPercent")) {
                                                            caSP[0] = Long.parseLong(item.getValue().toString());
                                                        }
                                                        if (Objects.equals(item.getKey().toString(), "creditSalaryPercent")) {
                                                            crSP[0] = Long.parseLong(item.getValue().toString());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        sellerHelperMap.put("totalCashBoughtPrice", tCaBoPr[0]);
                                        sellerHelperMap.put("totalCashQuantity", tCaQ[0]);
                                        sellerHelperMap.put("totalCreditBoughtPrice", tCrBoPr[0]);
                                        sellerHelperMap.put("totalCreditQuantity", tCrQ[0]);
                                        sellerHelperMap.put("totalCashPrice", tCaPr[0]);
                                        sellerHelperMap.put("totalCreditPrice", tCrPr[0]);
                                        sellerHelperMap.put("cashSalaryPercent", caSP[0]);
                                        sellerHelperMap.put("creditSalaryPercent", crSP[0]);
                                        sellerHelperMap.put("userId", d.getId());
                                        sellerHelperMap.put("dateRange", idForTodayStats+" "+idForStartDate);
                                        sellerStatHelper.document(d.getId()).set(sellerHelperMap, SetOptions.merge());

                                    }
                                }
                            });
                        }

                    }
                }
            }
        });
    }
    private void addDefaultDataToRW() {
        sellerStatHelper.whereEqualTo("dateRange", idForTodayStats+" "+idForStartDate).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if(!queryDocumentSnapshots.isEmpty()){
                for(DocumentSnapshot d:queryDocumentSnapshots.getDocuments()){
                    if(d.exists()){
                        AdminSellerStatsModel dataModal = d.toObject(AdminSellerStatsModel.class);
                        dataModal.setId(d.getId());
                        dataModal.setTotalCashBoughtPrice(Long.parseLong(d.get("totalCashBoughtPrice").toString()));
                        dataModal.setTotalCreditBoughtPrice(Long.parseLong(d.get("totalCreditBoughtPrice").toString()));
                        dataModal.setTotalCashQuantity(Integer.parseInt(d.get("totalCashQuantity").toString()));
                        dataModal.setTotalCreditQuantity(Integer.parseInt(d.get("totalCreditQuantity").toString()));
                        dataModal.setCashSalaryPercent(Integer.parseInt(d.get("cashSalaryPercent").toString()));
                        dataModal.setCreditSalaryPercent(Integer.parseInt(d.get("creditSalaryPercent").toString()));
                        dataModal.setTotalCashPrice(Long.parseLong(d.get("totalCashPrice").toString()));
                        dataModal.setTotalCreditPrice(Long.parseLong(d.get("totalCreditPrice").toString()));
                        adminTotalStatsModel.add(dataModal);
                        adminReportFragmentTotalAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}