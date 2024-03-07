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
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.adapters.AdminReportFragmentProductsAdapter;
import com.example.lidertrade.admin.adapters.AdminReportFragmentTotalAdapter;
import com.example.lidertrade.admin.models.AdminBrandModel;
import com.example.lidertrade.admin.models.AdminProductModel;
import com.example.lidertrade.admin.models.AdminTotalStatsModel;
import com.example.lidertrade.admin.models.AdminUserModel;
import com.example.lidertrade.seller.adapters.SHFCategoryAdapter;
import com.example.lidertrade.seller.models.SHFSaleModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class AdminReportFragmentProducts extends Fragment {

    View view;
    TextView  dailyStatTextLine;
    DocumentReference defaultId;
    CardView sellerStatDataPickerCard;
    String idForTodayStats, idForYesterdayStats, myFormat, idForStartDate, idForEndDate;
    SimpleDateFormat dateFormat;
    Date date, yesterDay, aMonthBefore;
    CollectionReference statProducts, statMaker;
    private ArrayList<AdminTotalStatsModel>  adminTotalStatsModel;
    AdminReportFragmentProductsAdapter adminReportFragmentTotalAdapter;

    private FirebaseFirestore db;
    RecyclerView totalStatsRW;
    SearchView searchView;


    public AdminReportFragmentProducts() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.admin_report_fragment_products, container, false);
        db = FirebaseFirestore.getInstance();
        statProducts = db.collection("StatProducts");
        statMaker = db.collection("StatMaker");
//        Get Current date
        myFormat="dd-MM-yyyy";
        dateFormat = new SimpleDateFormat(myFormat, Locale.US);

        date = new Date();
        yesterDay = new Date(date.getTime() - 24*60*60*1000);
        aMonthBefore = new Date(date.getTime() - 30L *24*60*60*1000);
        idForYesterdayStats = String.valueOf((dateFormat.format(yesterDay)));
        idForTodayStats = String.valueOf((dateFormat.format(date)));
        idForStartDate = String.valueOf((dateFormat.format(aMonthBefore)));
        defaultId = statMaker.document(idForTodayStats+idForStartDate);
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

        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                filterList(s);
                return false;
            }
        });

        totalStatsRW = view.findViewById(R.id.totalStatsRW);
        adminTotalStatsModel = new ArrayList<>();
        totalStatsRW.setHasFixedSize(true);
        totalStatsRW.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adminReportFragmentTotalAdapter = new AdminReportFragmentProductsAdapter(adminTotalStatsModel, getContext());
        addDefaultDataToTable();
        addDefaultDataToRW();

        totalStatsRW.setAdapter(adminReportFragmentTotalAdapter);
        adminReportFragmentTotalAdapter.notifyDataSetChanged();
        return view;
    }
    private void addDataToTable(Long first, Long second) {
        String customStartDate = String.valueOf((dateFormat.format(first)));
        String customEndDate = String.valueOf((dateFormat.format(second)));
        statMaker.whereEqualTo("dataRange",customEndDate+" "+customStartDate).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(DocumentSnapshot d:task.getResult()){
                            d.getReference().delete();
                        }

                    }
                });
        db.collection("products").addSnapshotListener((value, error) -> {
            if(!value.isEmpty()){
                for(DocumentSnapshot d:value.getDocuments()){
                    if(d.exists()){
                        long nDay = ((second - first)/24/60/60/1000);
                        final long[] boPr = {0};
                        final long[] caPr = {0};
                        final long[] caQu = {0};
                        final long[] crPr = {0};
                        final long[] crQu = {0};
                        adminTotalStatsModel.clear();
                        for(int ii=0;ii<=nDay;ii++) {
                            String idForAnyDay = String.valueOf((dateFormat.format(new Date(second- (long) ii *24*60*60*1000))));
                            statProducts.document(idForAnyDay).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot dd, @Nullable FirebaseFirestoreException error) {
                                    if(dd.exists() && dd.getData()!=null){
                                        if(dd.contains(d.getId())){
                                            Map<String, Object> map = dd.getData();
                                            Map<String, Object> sellerHelperMap = new HashMap<>();
                                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                                if(Objects.equals(entry.getKey(), d.getId().toString())){
                                                    Map<String, Object> map2 = (Map<String, Object>) entry.getValue();
                                                    for (Map.Entry<String, Object> item : map2.entrySet()) {
                                                        if(!map2.containsValue(null)){
                                                            if(Objects.equals(item.getKey().toString(), "boughtPrice")){
                                                                boPr[0] = Long.parseLong(item.getValue().toString());
                                                            }
                                                            if(Objects.equals(item.getKey().toString(), "cashPrice")){
                                                                caPr[0] = Long.parseLong(item.getValue().toString());
                                                            }
                                                            if(Objects.equals(item.getKey().toString(), "creditPrice")){
                                                                crPr[0] = Long.parseLong(item.getValue().toString());
                                                            }
                                                            if(Objects.equals(item.getKey().toString(), "creditSoldQuantity")){
                                                                crQu[0] += Long.parseLong(item.getValue().toString());
                                                            }
                                                            if(Objects.equals(item.getKey().toString(), "cashSoldQuantity")){

                                                                caQu[0] += Long.parseLong(item.getValue().toString());
                                                            }
                                                        }

                                                    }
                                                }
                                            }
                                            sellerHelperMap.put("boughtPrice", boPr[0]);
                                            sellerHelperMap.put("cashPrice", caPr[0]);
                                            sellerHelperMap.put("cashSoldQuantity", caQu[0]);
                                            sellerHelperMap.put("creditPrice", crPr[0]);
                                            sellerHelperMap.put("creditSoldQuantity", crQu[0]);
                                            sellerHelperMap.put("productId", d.get("productName"));
                                            sellerHelperMap.put("dateRange", customEndDate+" "+customStartDate);
                                            statMaker.document(d.getId()).set(sellerHelperMap, SetOptions.merge());
                                        }
                                    }
                                    else{
                                        System.out.println(adminReportFragmentTotalAdapter.getItemCount());
                                        adminReportFragmentTotalAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }

                    }
                }
            }
        });
    }





    private void addDataToRW(String end, String start) {
        statMaker.whereEqualTo("dateRange", end+" "+start).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if(!queryDocumentSnapshots.isEmpty()){
                for(DocumentSnapshot d:queryDocumentSnapshots.getDocuments()){
                    if(d.exists()){
                        AdminTotalStatsModel dataModal = d.toObject(AdminTotalStatsModel.class);
                        dataModal.setId(d.getId());
                        dataModal.setProductName(d.get("productId").toString());
                        dataModal.setBoughtPrice(Long.parseLong(d.get("boughtPrice").toString()));
                        dataModal.setCashPrice(Long.parseLong(d.get("cashPrice").toString()));
                        dataModal.setCashSoldQuantity(Long.parseLong(d.get("cashSoldQuantity").toString()));
                        dataModal.setCreditPrice(Long.parseLong(d.get("creditPrice").toString()));
                        dataModal.setCreditSoldQuantity(Long.parseLong(d.get("creditSoldQuantity").toString()));
                        adminTotalStatsModel.add(dataModal);
                        adminReportFragmentTotalAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void addDefaultDataToTable() {
        db.collection("products").addSnapshotListener((value, error) -> {
            if(!value.isEmpty()){
                for(DocumentSnapshot d:value.getDocuments()){
                    if(d.exists()){
                        long nDay = 30;
                        final long[] boPr = {0};
                        final long[] caPr = {0};
                        final long[] caQu = {0};
                        final long[] crPr = {0};
                        final long[] crQu = {0};
                        ArrayList<String> idsList= new ArrayList<>();
                        ArrayList<Map<String, Object>> allDataList = new ArrayList<>();
                        for(int ii=0;ii<=nDay;ii++) {
                            String idForAnyDay = String.valueOf((dateFormat.format(new Date(new Date().getTime() - (long) ii * 24 * 60 * 60 * 1000))));
                            statProducts.document(idForAnyDay).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot dd, @Nullable FirebaseFirestoreException error) {
                                    if(dd.exists() && dd.getData()!=null){
                                        if(dd.contains(d.getId())){
                                            Map<String, Object> map = dd.getData();
                                            Map<String, Object> sellerHelperMap = new HashMap<>();
                                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                                if(Objects.equals(entry.getKey(), d.getId().toString())){
                                                    Map<String, Object> map2 = (Map<String, Object>) entry.getValue();
                                                    for (Map.Entry<String, Object> item : map2.entrySet()) {
                                                        if(!map2.containsValue(null)){
                                                            if(Objects.equals(item.getKey().toString(), "boughtPrice")){
                                                                boPr[0] = Long.parseLong(item.getValue().toString());
                                                            }
                                                            if(Objects.equals(item.getKey().toString(), "cashPrice")){
                                                                caPr[0] = Long.parseLong(item.getValue().toString());
                                                            }
                                                            if(Objects.equals(item.getKey().toString(), "creditPrice")){
                                                                crPr[0] = Long.parseLong(item.getValue().toString());
                                                            }
                                                            if(Objects.equals(item.getKey().toString(), "creditSoldQuantity")){
                                                                crQu[0] += Long.parseLong(item.getValue().toString());
                                                            }
                                                            if(Objects.equals(item.getKey().toString(), "cashSoldQuantity")){
                                                                caQu[0] += Long.parseLong(item.getValue().toString());
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            sellerHelperMap.put("boughtPrice", boPr[0]);
                                            sellerHelperMap.put("cashPrice", caPr[0]);
                                            sellerHelperMap.put("cashSoldQuantity", caQu[0]);
                                            sellerHelperMap.put("creditPrice", crPr[0]);
                                            sellerHelperMap.put("creditSoldQuantity", crQu[0]);
                                            sellerHelperMap.put("productId", d.get("productName"));
                                            sellerHelperMap.put("dateRange", idForTodayStats+" "+idForStartDate);
                                            statMaker.document(d.getId()).set(sellerHelperMap, SetOptions.merge());

                                        }
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
        statMaker.whereEqualTo("dateRange", idForTodayStats+" "+idForStartDate).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if(!queryDocumentSnapshots.isEmpty()){
                for(DocumentSnapshot d:queryDocumentSnapshots.getDocuments()){
                    if(d.exists()){
                        AdminTotalStatsModel dataModal = d.toObject(AdminTotalStatsModel.class);
                        dataModal.setId(d.getId());
                        dataModal.setProductName(d.get("productId").toString());
                        dataModal.setBoughtPrice(Long.parseLong(d.get("boughtPrice").toString()));
                        dataModal.setCashPrice(Long.parseLong(d.get("cashPrice").toString()));
                        dataModal.setCashSoldQuantity(Long.parseLong(d.get("cashSoldQuantity").toString()));
                        dataModal.setCreditPrice(Long.parseLong(d.get("creditPrice").toString()));
                        dataModal.setCreditSoldQuantity(Long.parseLong(d.get("creditSoldQuantity").toString()));
                        adminTotalStatsModel.add(dataModal);
                        adminReportFragmentTotalAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
    private void filterList(String s) {
        ArrayList<AdminTotalStatsModel> filteredList = new ArrayList<>();
        for (AdminTotalStatsModel model: adminTotalStatsModel){
            if (model.getProductName().toLowerCase().contains(s.toLowerCase().trim())){
                filteredList.add(model);
            }
        }
        this.adminReportFragmentTotalAdapter.setFilteredList(filteredList);
        this.totalStatsRW.setAdapter(adminReportFragmentTotalAdapter);
    }

}