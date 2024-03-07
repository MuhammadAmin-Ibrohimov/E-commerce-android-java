package com.example.lidertrade.admin.adapters;

import android.app.Dialog;
import android.content.Context;
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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lidertrade.R;
import com.example.lidertrade.admin.helpers.MyDataBaseHelper;
import com.example.lidertrade.admin.models.AdminBrandModel;
import com.example.lidertrade.admin.models.AdminProductSpecificationsModel;
import com.example.lidertrade.admin.models.AdminReturnedProductsModel;
import com.example.lidertrade.seller.adapters.SHFCategoryAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminDebtFragmentReturnedAdapter extends RecyclerView.Adapter<AdminDebtFragmentReturnedAdapter.MyViewHolder> {

    Context context;
    ArrayList<AdminReturnedProductsModel> modelArrayList;
    FirebaseFirestore db;
    OnItemClickListener mListener;









    public void setFilteredList(ArrayList<AdminReturnedProductsModel> filteredList) {
        this.modelArrayList = filteredList;
    }

    public interface OnItemClickListener {
        void onDeleteClick(String url);
    }

    public void setonItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public AdminDebtFragmentReturnedAdapter(Context context, ArrayList<AdminReturnedProductsModel> modelArrayList) {
        this.context = context;
        this.modelArrayList = modelArrayList;
    }

    @NonNull
    @Override
    public AdminDebtFragmentReturnedAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_debt_fragment_returned_item, parent, false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminDebtFragmentReturnedAdapter.MyViewHolder holder, int position) {
        AdminReturnedProductsModel model = modelArrayList.get(position);
        db = FirebaseFirestore.getInstance();
        db.collection("products").document(model.getId()).get().addOnSuccessListener(d -> {
            if(d.exists()){
                holder.prodName.setText(d.get("productName").toString());
            }
        });
        holder.prodQuantity.setText(String.valueOf(model.getQuantity()));
        int pos = holder.getAdapterPosition();
        String id = modelArrayList.get(pos).getId();
        holder.brandListDelete.setOnClickListener(view -> {
            if (mListener != null) {
                if (pos != RecyclerView.NO_POSITION) {
                    mListener.onDeleteClick(id);
                    notifyDataSetChanged();
                }
            }
        });
        holder.brandListUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateReturnedProductsData(model);
            }
        });
    }


    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView prodQuantity,prodName;
        CardView  brandListDelete, brandListUpdate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            prodQuantity = itemView.findViewById(R.id.prodQuantity);
            prodName = itemView.findViewById(R.id.prodName);
            brandListDelete = itemView.findViewById(R.id.brandListDelete);
            brandListUpdate = itemView.findViewById(R.id.brandListUpdate);
        }
    }

    private void updateReturnedProductsData(AdminReturnedProductsModel modal) {
        db = FirebaseFirestore.getInstance();
        CollectionReference brandCol = db.collection("ReturnedProducts");
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.admin_debt_fragment_returned_dialog);

        final RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
        final ArrayList<AdminProductSpecificationsModel>  innerMap = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(dialog.getContext(), LinearLayoutManager.VERTICAL, false));
        final AdminDebtFragmentReturnedDialogAdapter shfCategoryAdapter = new AdminDebtFragmentReturnedDialogAdapter( dialog.getContext(), innerMap);
        recyclerView.setAdapter(shfCategoryAdapter);
        shfCategoryAdapter.notifyDataSetChanged();
        brandCol.document(modal.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot d) {
                if(d.exists() && d.get("detailedData")!=null){
                    Map<String ,Object> dataModal = (Map<String, Object>) d.get("detailedData");
                    AdminProductSpecificationsModel dataModel = new AdminProductSpecificationsModel();
                    for(Map.Entry<String, Object> n:dataModal.entrySet()){
                        String myFormat="dd-MM-yyyy";
                        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                        dataModel.setField(String.valueOf((dateFormat.format(Long.parseLong(n.getKey())))));
                        dataModel.setName((String) n.getValue());
                        innerMap.add(dataModel);
                        shfCategoryAdapter.notifyDataSetChanged();
                    }

                }
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}
