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

import androidx.recyclerview.widget.RecyclerView;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.models.AdminTotalStatsModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdminReportFragmentProductsAdapter extends RecyclerView.Adapter<AdminReportFragmentProductsAdapter.ViewHolder> {
    ArrayList<AdminTotalStatsModel> modelArrayList;
    Context context;
    LineChart lineChart;
    LineData lineData;
    LineDataSet lineDataSet, lineDataSet2;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    DecimalFormat decim = new DecimalFormat("#,###.##");

    public AdminReportFragmentProductsAdapter(ArrayList<AdminTotalStatsModel> homeModelArrayList, Context context) {
        this.modelArrayList = homeModelArrayList;
        this.context = context;
    }
    public void setFilteredList(ArrayList<AdminTotalStatsModel> filteredList) {
        this.modelArrayList = filteredList;
    }

    @NonNull
    @Override
    public AdminReportFragmentProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.admin_report_fragment_products_item, parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull AdminReportFragmentProductsAdapter.ViewHolder holder, int position) {
        AdminTotalStatsModel modal = modelArrayList.get(position);
        long tBP = modal.getBoughtPrice();
        if(modal.getId()!=null){
            long bp = modal.getBoughtPrice();
            CollectionReference statMaker = db.collection("StatMaker");
            CollectionReference statProducts = db.collection("StatProducts");
            long caP = modal.getCashPrice();
            long caQ = modal.getCashSoldQuantity();
            long crP = modal.getCreditPrice();
            long crQ = modal.getCreditSoldQuantity();
            long tp = caP*caQ+crP*crQ;
            long tbp = bp*(caQ+crQ);
            ArrayList lineEntries = new ArrayList<>();
            ArrayList lineEntries2 = new ArrayList<>();
            holder.productName.setText(modal.getProductName());
            holder.selectedDate.setText(decim.format(caQ)+" / " +decim.format(crQ));
            holder.totalPrice.setText(String.format(decim.format(tp)));
            holder.totalBoughtPrice.setText(String.format(decim.format(tbp)));
            holder.totalBenefitPrice.setText(String.format(decim.format(tp-tbp)));
            createEachProdStat(modal, statMaker, statProducts, lineEntries, lineEntries2);
            holder.productName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateProductData(modal, lineEntries, lineEntries2);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView selectedDate, productName, totalPrice, totalBoughtPrice, totalBenefitPrice;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            selectedDate = itemView.findViewById(R.id.selectedDate);
            productName = itemView.findViewById(R.id.productName);
            totalPrice = itemView.findViewById(R.id.totalPrice);
            totalBoughtPrice = itemView.findViewById(R.id.totalBoughtPrice);
            totalBenefitPrice = itemView.findViewById(R.id.totalBenefitPrice);
        }


    }

    private void updateProductData(AdminTotalStatsModel modal, ArrayList lineEntries, ArrayList lineEntries2) {
        db = FirebaseFirestore.getInstance();
        CollectionReference statMaker = db.collection("StatMaker");
        CollectionReference statProducts = db.collection("StatProducts");
        CollectionReference statProduct = db.collection("StatProduct");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference sRef = storage.getReference();
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        dialog.setContentView(R.layout.admin_report_fragment_products_item_dialog);
        lineChart = dialog.findViewById(R.id.lineChart);
        lineDataSet = new LineDataSet(lineEntries, "Xarajat");
        lineDataSet2 = new LineDataSet(lineEntries2, "Savdo");
        lineData = new LineData(lineDataSet, lineDataSet2);
        lineChart.setData(lineData);
        lineDataSet2.setColors(Color.BLUE);
        lineDataSet.setColor(Color.RED);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet2.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(12f);
        lineDataSet2.setValueTextSize(12f);


        final TextView customStatText = dialog.findViewById(R.id.customStatText);

        statMaker.document(modal.getId()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot d = task.getResult();
                if(d.exists()) {
                    customStatText.setText(d.get("dateRange").toString());
                }}});
        final ImageView confirm = dialog.findViewById(R.id.confirm);
        final Button reset = dialog.findViewById(R.id.reset);
        final Button submit = dialog.findViewById(R.id.submit);

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void createEachProdStat(AdminTotalStatsModel modal, CollectionReference statMaker, CollectionReference statProducts, ArrayList lineEntries, ArrayList lineEntries2) {
        statMaker.document(modal.getId()).get().addOnCompleteListener(task -> {
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
                        final long[] boPr = {0};
                        final long[] caPr = {0};
                        final long[] caQu = {0};
                        final long[] crPr = {0};
                        final long[] crQu = {0};
                        for(int ii=0;ii<=nDay;ii++) {
                            lineEntries.clear();
                            String idForAnyDay = format.format(new Date(endLong - (long) ii * 24 * 60 * 60 * 1000));
                            final int[] finalIi = {ii};
                            statProducts.document(idForAnyDay).get().addOnCompleteListener(task2 -> {
                                if(task2.isSuccessful()){
                                    DocumentSnapshot dd = task2.getResult();
                                    if(dd.exists() && dd.getData()!=null){
                                        if(dd.contains(d.getId())){
                                            Map<String, Object> map = dd.getData();
                                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                                if(Objects.equals(entry.getKey(), d.getId().toString())){
                                                    Map<String, Object> map2 = (Map<String, Object>) entry.getValue();
                                                    for (Map.Entry<String, Object> item : map2.entrySet()) {
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
                                                            crQu[0] = Long.parseLong(item.getValue().toString());
                                                        }
                                                        if(Objects.equals(item.getKey().toString(), "cashSoldQuantity")){
                                                            caQu[0] = Long.parseLong(item.getValue().toString());
                                                        }
                                                    }
                                                }
                                            }
                                            lineEntries.add(new Entry(finalIi[0], boPr[0]*(crQu[0]+caQu[0])));
                                            lineEntries2.add(new Entry(finalIi[0], crPr[0]*crQu[0]+caPr[0]*caQu[0]));
                                        }
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

}

