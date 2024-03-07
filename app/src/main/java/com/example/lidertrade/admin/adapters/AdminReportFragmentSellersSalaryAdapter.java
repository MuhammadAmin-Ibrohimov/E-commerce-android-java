package com.example.lidertrade.admin.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lidertrade.R;
import com.example.lidertrade.admin.models.AdminSellersSalaryModel;
import com.example.lidertrade.deliverer.helpers.NumberTextWatcherForThousand;
import com.example.lidertrade.seller.activities.SellerAllProductsActivity;
import com.example.lidertrade.seller.activities.SellerSubcategoryActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminReportFragmentSellersSalaryAdapter extends RecyclerView.Adapter<AdminReportFragmentSellersSalaryAdapter.ViewHolder> {
    ArrayList<AdminSellersSalaryModel> homeModelArrayList;
    Context context;
    DecimalFormat decim = new DecimalFormat("#,###.##");

    public AdminReportFragmentSellersSalaryAdapter(ArrayList<AdminSellersSalaryModel> homeModelArrayList, Context context) {
        this.homeModelArrayList = homeModelArrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public AdminReportFragmentSellersSalaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.admin_report_fragment_seller_salary_dialog_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdminReportFragmentSellersSalaryAdapter.ViewHolder holder, int position) {
        AdminSellersSalaryModel modal = homeModelArrayList.get(position);

        long ts = modal.getCashSalary()+modal.getCreditSalary();
        long tgs = modal.getGivenCashSalary()+modal.getGivenCreditSalary();
        long ucas = modal.getCashSalary()-modal.getGivenCashSalary();
        long ucrs = modal.getCreditSalary()-modal.getGivenCreditSalary();
        holder.month.setText(modal.getId());
        holder.creditSalary.setText(String.format(decim.format(modal.getCreditSalary())));
        holder.givenCreditSalary.setText(String.format(decim.format(modal.getGivenCreditSalary())));
        holder.cashSalary.setText(String.format(decim.format(modal.getCashSalary())));
        holder.givenCashSalary.setText(String.format(decim.format(modal.getGivenCashSalary())));
        holder.totalSalary.setText(String.format(decim.format(ts)));
        holder.givenTotalSalary.setText(String.format(decim.format(tgs)));
        holder.unpaidCashSalary.setText(String.format(decim.format(ucas)));
        holder.unpaidCreditSalary.setText(String.format(decim.format(ucrs)));
        holder.unpaidTotalSalary.setText(String.format(decim.format(ts-tgs)));

        if(ts<=tgs){
            holder.sellerSalaryPaid.setVisibility(View.VISIBLE);
            holder.sellerSalaryEdit.setVisibility(View.GONE);
            holder.sellerSalaryCard.setClickable(false);
        }
        else{
            holder.sellerSalaryPaid.setVisibility(View.GONE);
            holder.sellerSalaryEdit.setVisibility(View.VISIBLE);
            holder.sellerSalaryCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    paySellersSalary(modal);
                }
            });
        }
    }

    private void paySellersSalary(AdminSellersSalaryModel modal) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference sellersSalary = db.collection("SellersSalary");
        DocumentReference selSalDoc = sellersSalary.document(modal.getId());
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.admin_report_fragment_seller_salary_dialog_item_pay);

        final EditText cashPercentDialog = dialog.findViewById(R.id.cashPercentDialog);
        cashPercentDialog.addTextChangedListener(new NumberTextWatcherForThousand(cashPercentDialog));
        final EditText creditPercentDialog = dialog.findViewById(R.id.creditPercentDialog);
        creditPercentDialog.addTextChangedListener(new NumberTextWatcherForThousand(creditPercentDialog));

        final Button submit = dialog.findViewById(R.id.submit);
        submit.setOnClickListener(view -> {
            String cashPerText = cashPercentDialog.getText().toString().trim().replaceAll("[^A-Za-z0-9]", "");
            String creditPerText = creditPercentDialog.getText().toString().trim().replaceAll("[^A-Za-z0-9]", "");
            if (cashPerText.isEmpty() || creditPerText.isEmpty()){
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setContentText("Iltimos, avval maydonni to'ldiring!")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(SweetAlertDialog::cancel)
                        .show();
            }
            else{
                selSalDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot d= task.getResult();
                            if(d.exists()){
                                Map<String, Object> outerMap = d.getData();
                                for(Map.Entry<String, Object> entry:outerMap.entrySet()) {
                                    if (entry.getKey().toString().equals(modal.getSellerId())) {
                                        Map<String, Object> innerMap = (Map<String, Object>) entry.getValue();
                                        long caS = 0;
                                        long crS = 0;
                                        long gCrS = 0;
                                        long gCaS = 0;
                                        for(Map.Entry<String, Object> in:innerMap.entrySet()){
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
                                        Map<String,Map<String,Object>> outMap = new HashMap<>();
                                        Map<String,Object> inMap = new HashMap<>();
                                        inMap.put("sellerId",modal.getSellerId());
                                        inMap.put("creditSalary",crS);
                                        inMap.put("cashSalary",caS);
                                        inMap.put("givenCashSalary",gCaS+Long.parseLong(cashPerText));
                                        inMap.put("givenCreditSalary",gCrS+Long.parseLong(creditPerText));
                                        outMap.put(modal.getSellerId(),inMap);
                                        selSalDoc.set(outMap, SetOptions.merge());

                                    }
                                }
                            }
                        }
                    }
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
        return homeModelArrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView sellerName, month, cashSalary, creditSalary, totalSalary,
                givenCashSalary, givenCreditSalary, givenTotalSalary,
                unpaidCashSalary, unpaidCreditSalary, unpaidTotalSalary;
        ImageView sellerSalaryPaid, sellerSalaryEdit;
        CardView sellerSalaryCard;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sellerName = itemView.findViewById(R.id.sellerName);
            month = itemView.findViewById(R.id.month);
            cashSalary = itemView.findViewById(R.id.cashSalary);
            creditSalary = itemView.findViewById(R.id.creditSalary);
            totalSalary = itemView.findViewById(R.id.totalSalary);
            givenCashSalary = itemView.findViewById(R.id.givenCashSalary);
            givenCreditSalary = itemView.findViewById(R.id.givenCreditSalary);
            givenTotalSalary = itemView.findViewById(R.id.givenTotalSalary);
            unpaidCashSalary = itemView.findViewById(R.id.unpaidCashSalary);
            unpaidCreditSalary = itemView.findViewById(R.id.unpaidCreditSalary);
            unpaidTotalSalary = itemView.findViewById(R.id.unpaidTotalSalary);
            sellerSalaryCard = itemView.findViewById(R.id.sellerSalaryCard);
            sellerSalaryPaid = itemView.findViewById(R.id.sellerSalaryPaid);
            sellerSalaryEdit = itemView.findViewById(R.id.sellerSalaryEdit);
        }


    }
}
