package com.example.lidertrade.admin.adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lidertrade.R;
import com.example.lidertrade.admin.helpers.MyDataBaseHelper;
import com.example.lidertrade.admin.models.AdminCategoryModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminProductFragmentCategoryAdapter extends RecyclerView.Adapter<AdminProductFragmentCategoryAdapter.MyViewHolder> {

    Context context;
    ArrayList<AdminCategoryModel> modelArrayList;
    MyDataBaseHelper myDB;
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


    public void setFilteredList(ArrayList<AdminCategoryModel> filteredList) {
        this.modelArrayList = filteredList;
    }

    public interface OnItemClickListener {
        void onDeleteClick(String url);
    }

    public void setonItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public AdminProductFragmentCategoryAdapter(Context context, ArrayList<AdminCategoryModel> modelArrayList) {
        this.context = context;
        this.modelArrayList = modelArrayList;
    }

    @NonNull
    @Override
    public AdminProductFragmentCategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_product_fragment_category_item, parent, false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminProductFragmentCategoryAdapter.MyViewHolder holder, int position) {
        AdminCategoryModel model = modelArrayList.get(position);
        holder.categoryName.setText(model.getCategoryName());
        if (model.getCategoryPic()!=null){
            Glide.with(context).load(model.getCategoryPic()).centerCrop().into(holder.categoryImage);
        }else{
            Glide.with(context).load(R.drawable.lider).centerCrop().into(holder.categoryImage);
        }


        int pos = holder.getAdapterPosition();
        String id = modelArrayList.get(pos).getId();

        holder.categoryListDelete.setOnClickListener(view -> {
            if (mListener != null) {
                if (pos != RecyclerView.NO_POSITION) {
                    mListener.onDeleteClick(id);
                }
            }
        });
        holder.categoryListUpdate.setOnClickListener(view -> updateCategoryData(model));
    }



    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        ImageView categoryImage;
        CardView  categoryListUpdate, categoryListDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            categoryImage = itemView.findViewById(R.id.categoryImage);
            categoryListDelete = itemView.findViewById(R.id.categoryListDelete);
            categoryListUpdate = itemView.findViewById(R.id.categoryListUpdate);
        }
    }

    private void updateCategoryData(AdminCategoryModel modal) {
        myDB = new MyDataBaseHelper(context);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference catCol = db.collection("Categories");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference sRef = storage.getReference();
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.admin_product_fragment_category_dialog);

        final EditText categoryName = dialog.findViewById(R.id.categoryName);
        categoryName.setText(modal.getCategoryName());
        final ImageView categoryImage = dialog.findViewById(R.id.categoryImage);
        catCol.document(modal.getId()).addSnapshotListener((d, error) -> {
            if (d.exists()){
                if (d.get("categoryPic")!=null){
                    Glide.with(context).load(Objects.requireNonNull(d.get("categoryPic")).toString()).centerCrop().into(categoryImage);
                }else{
                    Glide.with(context).load(R.drawable.lider).centerCrop().into(categoryImage);
                }
            }
        });
        final ImageView browse = dialog.findViewById(R.id.browse);
        browse.setOnClickListener(view -> onItemClick.getPosition(modal.getId()));

        final ImageView confirm = dialog.findViewById(R.id.confirm);
        confirm.setOnClickListener(view -> {
            onItemClick2.getPosition(modal.getId());
            catCol.document(modal.getId()).addSnapshotListener((d, error) -> {
                if (d.exists()){
                    if (d.get("categoryPic")!=null){
                        Glide.with(context).load(Objects.requireNonNull(d.get("categoryPic")).toString()).centerCrop().into(categoryImage);
                    }else{
                        Glide.with(context).load(R.drawable.lider).centerCrop().into(categoryImage);
                    }
                }
            });
        });
        final Button reset = dialog.findViewById(R.id.reset);
        reset.setOnClickListener(view -> dialog.dismiss());

        final Button submit = dialog.findViewById(R.id.submit);
        submit.setOnClickListener(view -> {
            String categoryNameText = categoryName.getText().toString().trim();
            if (categoryNameText.isEmpty()){
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setContentText("Iltimos, avval katalog nomini kiritng!")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(SweetAlertDialog::cancel)
                        .show();
            }
            else{
                if(!categoryNameText.equals(modal.getCategoryName())){
                    Query query = db.collection("Categories").whereEqualTo("categoryName", categoryNameText);
                    AggregateQuery countQuery = query.count();
                    countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            AggregateQuerySnapshot snapshot = task.getResult();
                            if (snapshot.getCount() == 0){
                                catCol.document(modal.getId()).update("categoryName", categoryNameText)
                                        .addOnSuccessListener(unused -> {
                                            myDB.updateCategoryData(modal.getId(), categoryNameText);
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
                                        .setContentText("Katalog nomi: "+categoryNameText+" bazada mavjud")
                                        .setConfirmText("OK!")
                                        .setConfirmClickListener(SweetAlertDialog::cancel)
                                        .show();
                            }
                        }
                    });


                }
                else{
                    new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                            .setContentText("O'zgartirish kiritilmadi!")
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
