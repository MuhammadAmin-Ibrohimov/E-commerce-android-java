package com.example.lidertrade.admin.adapters;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lidertrade.R;
import com.example.lidertrade.deliverer.helpers.NumberTextWatcherForThousand;
import com.example.lidertrade.deliverer.models.DebtBookModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminDebtFragmentDebtBookAdapter extends RecyclerView.Adapter<AdminDebtFragmentDebtBookAdapter.ViewHolder>{
    private ArrayList<DebtBookModel> dataModalArrayList;
    private Context context;
    DecimalFormat decim = new DecimalFormat("#,###.##");


    // constructor class for our Adapter
    public AdminDebtFragmentDebtBookAdapter(ArrayList<DebtBookModel> dataModalArrayList, Context context) {
        this.dataModalArrayList = dataModalArrayList;
        this.context = context;
    }

    public AdminDebtFragmentDebtBookAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.admin_debt_fragment_debt_book_item, parent, false));
    }

    public void setFilteredList(ArrayList<DebtBookModel> filteredList) {
        this.dataModalArrayList = filteredList;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // setting data to our views in Recycler view items.
        DebtBookModel modal = dataModalArrayList.get(position);
        holder.orderSendingListCustomerAddress.setText(String.format("%s", modal.getCustomerAddress()));
        holder.orderSendingListCustomerName.setText(modal.getCustomerName());
        holder.orderSendingListCustomerPhone.setText(modal.getCustomerPhone());
        long startDate = modal.getDebtDate();
        int deadline = modal.getDebtDeadline();
        long endDate = startDate + (long) deadline *24*60*60*1000;
        long nDays = (new Date().getTime() - endDate)/24/60/60/1000;
        String myFormat="dd-MM-yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        Date date = new Date(endDate);
        holder.orderSendingListCustomerDebt.setText(String.valueOf(decim.format(modal.getTotalDebt()))+" so'm");
        holder.orderSendingListCustomerDeadline.setText(dateFormat.format(date));
        if (nDays<0){
            holder.linearLayout.setBackgroundColor(context.getColor(R.color.CompletedOrders));
            holder.completeSelected.setText("Muddatiga "+(-1)*nDays+" kun qoldi");
        }else if (nDays>0){
            holder.completeSelected.setText("Muddatidan "+nDays+" kun o'tib ketdi");
            holder.linearLayout.setBackgroundColor(context.getColor(R.color.CanceledOrders));
        }
        else {
            holder.linearLayout.setBackgroundColor(context.getColor(R.color.PendingOrders));
            holder.completeSelected.setText("Bugun oxirgi muddat");
        }
        holder.completeSelectedOrder.setOnClickListener(view -> {
            receiveDebt(modal, position);
        });
    }


    @Override
    public int getItemCount() {
        // returning the size of array list.
        return dataModalArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our
        // views of recycler items.
        private TextView orderSendingListCustomerAddress, orderSendingListCustomerName, orderSendingListCustomerPhone,
               orderSendingListCustomerDeadline, completeSelected, orderSendingListCustomerDebt;
        private CardView completeSelectedOrder;
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing the views of recycler views.
            orderSendingListCustomerAddress = itemView.findViewById(R.id.orderSendingListCustomerAddress);
            orderSendingListCustomerName = itemView.findViewById(R.id.orderSendingListCustomerName);
            orderSendingListCustomerPhone = itemView.findViewById(R.id.orderSendingListCustomerPhone);
            completeSelectedOrder = itemView.findViewById(R.id.completeSelectedOrder);
            orderSendingListCustomerDeadline = itemView.findViewById(R.id.orderSendingListCustomerDeadline);
            completeSelected = itemView.findViewById(R.id.completeSelected);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            orderSendingListCustomerDebt = itemView.findViewById(R.id.orderSendingListCustomerDebt);
        }
    }



    private void receiveDebt(DebtBookModel modal, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference debtBook = db.collection("DebtBook");

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.admin_debt_fragment_debt_book_dialog);

        final EditText cashPercentDialog = dialog.findViewById(R.id.cashPercentDialog);
        cashPercentDialog.addTextChangedListener(new NumberTextWatcherForThousand(cashPercentDialog));
        final TextView totalDebt = dialog.findViewById(R.id.totalDebt);

        totalDebt.setText(String.format("To'lanmagan mablag': %s so'm", String.valueOf(decim.format(modal.getTotalDebt()))));
        final Button submit = dialog.findViewById(R.id.submit);
        cashPercentDialog.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>0){
                    long recMoney = Long.parseLong(String.valueOf(charSequence).trim().replaceAll("[^A-Za-z0-9]", ""));
                    totalDebt.setText(String.format("To'lanmagan mablag': %s so'm", String.valueOf(decim.format(modal.getTotalDebt() - recMoney))));
                }else {
                    totalDebt.setText(String.format("To'lanmagan mablag': %s so'm", String.valueOf(decim.format(modal.getTotalDebt()))));
                }
            }
            @Override public void afterTextChanged(Editable editable) {}
        });
        submit.setOnClickListener(view -> {
            try{
                long recMoney = Long.parseLong(String.valueOf(cashPercentDialog.getText().toString().trim().replaceAll("[^A-Za-z0-9]", "")));
                if(recMoney<modal.getTotalDebt()){
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setContentText(recMoney+" so'm qabul qilindi. Yana "+String.valueOf(modal.getTotalDebt()-recMoney)+" so'm qarzi mavjud. To'lovni yakunlaysizmi?" )
                            .setCancelText("Yo'q")
                            .setConfirmText("Ha")
                            .showCancelButton(true)
                            .setCancelClickListener(SweetAlertDialog::cancel)
                            .setConfirmClickListener(sweetAlertDialog1 -> {
                                debtBook.document(modal.getDebtId()).update("totalDebt",modal.getTotalDebt()-recMoney);
                                db.collection("Orders").document(modal.getDebtId()).get().addOnCompleteListener(task -> {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot d= task.getResult();
                                        if(d.exists()){
                                            Map<String,Object> outerMap = (Map<String, Object>) d.get("paymentMap");
                                            long payStat = Long.parseLong(d.get("paymentStatus").toString());
                                            long inCash = 0;
                                            long onlinePay = 0;
                                            long throughBank = 0;
                                            long accNum = 0;
                                            Map<String,Long> inMap = new HashMap<>();
                                            for(Map.Entry<String, Object> entry:outerMap.entrySet()){
                                                if(Objects.equals(entry.getKey(), "Naqd pul")){
                                                    inCash = Long.parseLong(entry.getValue().toString());
                                                }
                                                if(Objects.equals(entry.getKey(), "Click orqali")){
                                                    onlinePay = Long.parseLong(entry.getValue().toString());
                                                }
                                                if(Objects.equals(entry.getKey(), "Hisob Raqam orqali")){
                                                    accNum = Long.parseLong(entry.getValue().toString());
                                                }
                                                if(Objects.equals(entry.getKey(), "Karta orqali")){
                                                    throughBank = Long.parseLong(entry.getValue().toString());
                                                }
                                            }
                                            inMap.put("Click orqali",onlinePay);
                                            inMap.put("Hisob Raqam orqali",accNum);
                                            inMap.put("Karta orqali",throughBank);
                                            inMap.put("Naqd pul",inCash+recMoney);
                                            d.getReference().update("paymentMap",inMap, "paymentStatus", payStat-recMoney);
                                        }
                                    }
                                });
                                sweetAlertDialog1.cancel();
                                dialog.dismiss();
                            })
                            .show();
                }
                else if(recMoney>=modal.getTotalDebt()){
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setContentText(recMoney+" so'm qabul qilindi. Xaridorning qarzi to'landi. To'lovni yakunlaysizmi?" )
                            .setCancelText("Yo'q")
                            .setConfirmText("Ha")
                            .showCancelButton(true)
                            .setCancelClickListener(SweetAlertDialog::cancel)
                            .setConfirmClickListener(sweetAlertDialog1 -> {
                                sweetAlertDialog1.cancel();
                                db.collection("Orders").document(modal.getDebtId()).get().addOnCompleteListener(task -> {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot d= task.getResult();
                                        if(d.exists()){
                                            Map<String,Object> outerMap = (Map<String, Object>) d.get("paymentMap");
                                            long payStat = Long.parseLong(d.get("paymentStatus").toString());
                                            long inCash = 0;
                                            long onlinePay = 0;
                                            long throughBank = 0;
                                            long accNum = 0;
                                            Map<String,Long> inMap = new HashMap<>();
                                            for(Map.Entry<String, Object> entry:outerMap.entrySet()){
                                                if(Objects.equals(entry.getKey(), "Naqd pul")){
                                                    inCash = Long.parseLong(entry.getValue().toString());
                                                }
                                                if(Objects.equals(entry.getKey(), "Click orqali")){
                                                    onlinePay = Long.parseLong(entry.getValue().toString());
                                                }
                                                if(Objects.equals(entry.getKey(), "Hisob Raqam orqali")){
                                                    accNum = Long.parseLong(entry.getValue().toString());
                                                }
                                                if(Objects.equals(entry.getKey(), "Karta orqali")){
                                                    throughBank = Long.parseLong(entry.getValue().toString());
                                                }
                                            }
                                            inMap.put("Click orqali",onlinePay);
                                            inMap.put("Hisob Raqam orqali",accNum);
                                            inMap.put("Karta orqali",throughBank);
                                            inMap.put("Naqd pul",inCash+recMoney);
                                            d.getReference().update("paymentMap",inMap,
                                                    "paymentStatus", payStat-recMoney,
                                                    "packageStatus",2);
                                            debtBook.document(modal.getDebtId()).delete();
                                        }
                                    }
                                });

                                dataModalArrayList.remove(position);
                                notifyItemRemoved(position);
                                notifyDataSetChanged();
                                dialog.dismiss();
                            })
                            .show();
                }
            }catch (NumberFormatException e){
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setContentText("Avval barcha maydonlarni to'ldiring!")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(SweetAlertDialog::cancel)
                        .show();
            }

        });
        final Button reset = dialog.findViewById(R.id.reset);
        reset.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}

