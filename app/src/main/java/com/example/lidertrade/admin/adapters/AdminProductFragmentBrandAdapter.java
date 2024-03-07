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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lidertrade.R;
import com.example.lidertrade.admin.helpers.MyDataBaseHelper;
import com.example.lidertrade.admin.models.AdminBrandModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminProductFragmentBrandAdapter extends RecyclerView.Adapter<AdminProductFragmentBrandAdapter.MyViewHolder> {

    Context context;
    ArrayList<AdminBrandModel> modelArrayList;
    MyDataBaseHelper myDB;
    FirebaseFirestore db;
    OnItemClickListener mListener;




    OnItemClick onItemClick;
    OnItemClick2 onItemClick2;
    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }
    public void setOnItemClick2(OnItemClick2 onItemClick2) {
        this.onItemClick2 = onItemClick2;
    }
    public interface OnItemClick {
        void getPosition(String data); //pass any things
    }
    public interface OnItemClick2 {
        void getPosition(String data); //pass any things
    }





    public void setFilteredList(ArrayList<AdminBrandModel> filteredList) {
        this.modelArrayList = filteredList;
    }

    public interface OnItemClickListener {
        void onDeleteClick(String url);
    }

    public void setonItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public AdminProductFragmentBrandAdapter(Context context, ArrayList<AdminBrandModel> modelArrayList) {
        this.context = context;
        this.modelArrayList = modelArrayList;
    }

    @NonNull
    @Override
    public AdminProductFragmentBrandAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_product_fragment_brand_item, parent, false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminProductFragmentBrandAdapter.MyViewHolder holder, int position) {
        AdminBrandModel model = modelArrayList.get(position);
        holder.brandName.setText(model.getBrandName());

        int pos = holder.getAdapterPosition();
        String id = modelArrayList.get(pos).getId();

        holder.brandListDelete.setOnClickListener(view -> {
            if (mListener != null) {
                if (pos != RecyclerView.NO_POSITION) {
                    mListener.onDeleteClick(id);
                }
            }
        });
        holder.brandListUpdate.setOnClickListener(view -> updateBrandData(model));
    }


    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView brandName;
        CardView  brandListDelete, brandListUpdate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            brandName = itemView.findViewById(R.id.brandName);
            brandListDelete = itemView.findViewById(R.id.brandListDelete);
            brandListUpdate = itemView.findViewById(R.id.brandListUpdate);
        }
    }


    private void updateBrandData(AdminBrandModel modal) {
        myDB = new MyDataBaseHelper(context);
        db = FirebaseFirestore.getInstance();
        CollectionReference brandCol = db.collection("brands");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference sRef = storage.getReference();
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.admin_product_fragment_brand_list_dialog);

        final EditText brandName = dialog.findViewById(R.id.brandName);
        brandName.setText(modal.getBrandName());
        final ImageView brandImage = dialog.findViewById(R.id.brandImage);
        brandCol.document(modal.getId()).get().addOnSuccessListener(d -> {
            if (d.exists()){
                if (d.get("brandPic")!=null){
                    Glide.with(context).load(Objects.requireNonNull(d.get("brandPic")).toString()).centerCrop().into(brandImage);
                }else{
                    Glide.with(context).load(R.drawable.lider).centerCrop().into(brandImage);
                }
            }
        });
        final ImageView browse = dialog.findViewById(R.id.browse);
        browse.setOnClickListener(view -> onItemClick.getPosition(modal.getId()));

        final ImageView confirm = dialog.findViewById(R.id.confirm);
        confirm.setOnClickListener(view -> {
            onItemClick2.getPosition(modal.getId());
            brandCol.document(modal.getId()).addSnapshotListener((d, error) -> {
                if (d.exists()){
                    if (d.get("brandPic")!=null){
                        Glide.with(context).load(Objects.requireNonNull(d.get("brandPic")).toString()).centerCrop().into(brandImage);
                    }
                    else{
                        Glide.with(context).load(R.drawable.lider).centerCrop().into(brandImage);
                    }
                }
            });
        });
        final Button reset = dialog.findViewById(R.id.reset);
        reset.setOnClickListener(view -> dialog.dismiss());

        final Button submit = dialog.findViewById(R.id.submit);
        submit.setOnClickListener(view -> {
            String brandNameText = brandName.getText().toString().trim();
            if (brandNameText.isEmpty()){
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setContentText("Iltimos, avval katalog nomini kiritng!")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(SweetAlertDialog::cancel)
                        .show();
            }
            else{
                if(!brandNameText.equals(modal.getBrandName())){
                    Query query = db.collection("brands").whereEqualTo("brandName", brandNameText);
                    AggregateQuery countQuery = query.count();
                    countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            AggregateQuerySnapshot snapshot = task.getResult();
                            if (snapshot.getCount() == 0){
                                brandCol.document(modal.getId()).update("brandName", brandNameText)
                                        .addOnSuccessListener(unused -> {
                                            myDB.updateBrandData(modal.getId(), brandNameText);
                                            new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                                    .setContentText("Muvafaqqiyatli o'zgartirildi!")
                                                    .setConfirmText("OK!")
                                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                                    .show();
                                        })
                                        .addOnFailureListener(e -> {
                                            new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                                    .setContentText("Jarayonda xatolik!")
                                                    .setConfirmText("OK!")
                                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                                    .show();
                                        });
                                dialog.dismiss();
                            }
                            else{
                                new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                        .setContentText("Brand nomi: "+brandNameText+" bazada mavjud")
                                        .setConfirmText("OK!")
                                        .setConfirmClickListener(SweetAlertDialog::cancel)
                                        .show();
                            }
                        }
                    });
                }
                else{
                    new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                            .setContentText("Muvafaqqiyatli o'zgartirildi!")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(SweetAlertDialog::cancel)
                            .show();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

}
