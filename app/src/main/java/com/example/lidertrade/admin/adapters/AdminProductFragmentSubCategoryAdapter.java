package com.example.lidertrade.admin.adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lidertrade.R;
import com.example.lidertrade.admin.helpers.MyDataBaseHelper;
import com.example.lidertrade.admin.models.AdminCategoryModel;
import com.example.lidertrade.admin.models.AdminSubCategoryModel;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminProductFragmentSubCategoryAdapter extends RecyclerView.Adapter<AdminProductFragmentSubCategoryAdapter.MyViewHolder> {

    Context context;
    MyDataBaseHelper myDB;
    ArrayList<AdminSubCategoryModel> subModelArrayList;
    OnItemClickListener listener;

    OnItemClick onItemClick;
    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }
    public interface OnItemClick {
        void getPosition(String data); //pass any things
    }


    OnItemClick2 onItemClick2;
    public void setOnItemClick(OnItemClick2 onItemClick2) {
        this.onItemClick2 = onItemClick2;
    }
    public interface OnItemClick2 {
        void getPosition(String data); //pass any things
    }

    public void setFilteredList(ArrayList<AdminSubCategoryModel> mFilteredList) {
        this.subModelArrayList = mFilteredList;
    }

    public interface OnItemClickListener {
        void onDeleteClick(String url);
    }

    public void setOnItemClickListener (OnItemClickListener mListener){
        listener = mListener;
    }

    public AdminProductFragmentSubCategoryAdapter(Context context, ArrayList<AdminSubCategoryModel> subModelArrayList) {
        this.context = context;
        this.subModelArrayList = subModelArrayList;
    }

    @NonNull
    @Override
    public AdminProductFragmentSubCategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_product_fragment_sub_category_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminProductFragmentSubCategoryAdapter.MyViewHolder holder, int position) {
        AdminSubCategoryModel subCategoryModel = subModelArrayList.get(position);
        holder.subCategoryName.setText(subCategoryModel.getSubcategoryName());
        FirebaseFirestore.getInstance().collection("Categories").document(subCategoryModel.getCategoryId()).get().addOnSuccessListener(d -> {
            if(d.exists() && d.get("categoryName")!=null){
                holder.categoryName.setText(Objects.requireNonNull(d.get("categoryName")).toString());
            }
        });


        int pos = holder.getAdapterPosition();
        String id = subModelArrayList.get(pos).getId();

        holder.categoryListDelete.setOnClickListener(view -> {
            if (listener != null){
                if (pos != RecyclerView.NO_POSITION){
                    listener.onDeleteClick(id);
                }
            }
        });
        holder.subCategoryListUpdate.setOnClickListener(view -> {
            updateSubCategory(subCategoryModel, pos);
        });


    }



    @Override
    public int getItemCount() {
        return subModelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView categoryName, subCategoryName;
        CardView categoryListDelete, subCategoryListUpdate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            subCategoryName = itemView.findViewById(R.id.subCategoryName);
            categoryName = itemView.findViewById(R.id.categoryName);
            categoryListDelete = itemView.findViewById(R.id.categoryListDelete);
            subCategoryListUpdate = itemView.findViewById(R.id.subCategoryListUpdate);

        }
    }


    private void updateSubCategory(AdminSubCategoryModel modal, int pos) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference subCatCol = db.collection("SubCategories");
        myDB = new MyDataBaseHelper(context);
        ArrayList<String> valueSub = new ArrayList<>();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference sRef = storage.getReference();
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.admin_product_fragment_subcategory_list_dialog);
        ArrayList<String> category = new ArrayList<>();
        Map<String, String> subCategoryMap = new HashMap<>();

        //Initializing the views of the dialog.
        final EditText modelName = dialog.findViewById(R.id.modelNameAdapter);
        modelName.setText(modal.getSubcategoryName());
        final AutoCompleteTextView autoCompleteTextView = dialog.findViewById(R.id.autoCompleteAdapter);
        autoCompleteTextView.setText(modal.getCategoryId());

        final ImageView subCategoryImage = dialog.findViewById(R.id.subCategoryImage);
        subCatCol.document(modal.getId()).get().addOnSuccessListener(d -> {
            if (d.exists()){
                if (d.get("subcategoryPic")!=null){
                    Glide.with(context).load(Objects.requireNonNull(d.get("subcategoryPic")).toString()).centerCrop().into(subCategoryImage);
                }else{
                    Glide.with(context).load(R.drawable.lider).centerCrop().into(subCategoryImage);
                }
            }
        });


        db.collection("Categories").addSnapshotListener((value, error) -> {
            assert value != null;
            if (!value.isEmpty()){
                List<DocumentSnapshot> list = value.getDocuments();
                valueSub.clear();
                for (DocumentSnapshot d : list) {
                    AdminCategoryModel dataModal = d.toObject(AdminCategoryModel.class);
                    assert dataModal != null;
                    valueSub.add(dataModal.getCategoryName());
                }
                ArrayAdapter<String> adapterSub = new ArrayAdapter<>(context,
                        R.layout.admin_product_fragment_sub_category_drop_down,
                        valueSub
                );
                autoCompleteTextView.setAdapter(adapterSub);
            }
        });

        final ImageView subBrowse = dialog.findViewById(R.id.subBrowse);
        subBrowse.setOnClickListener(view -> onItemClick.getPosition(modal.getId()));

        final ImageView subConfirm = dialog.findViewById(R.id.subConfirm);
        subConfirm.setOnClickListener(view -> {
            onItemClick2.getPosition(modal.getId());
            subCatCol.document(modal.getId()).get().addOnSuccessListener(d -> {
                if (d.exists()){
                    if (d.get("subcategoryPic")!=null){
                        Glide.with(context).load(Objects.requireNonNull(d.get("subcategoryPic")).toString()).centerCrop().into(subCategoryImage);
                    }else{
                        Glide.with(context).load(R.drawable.lider).centerCrop().into(subCategoryImage);
                    }
                }
            });
        });

        Button subReset = dialog.findViewById(R.id.subResetAdapter);
        subReset.setOnClickListener(view -> dialog.dismiss());
        Button subSubmit = dialog.findViewById(R.id.subSubmitAdapter);
        final String[] defCategoryName = new String[1];
        final String[] defCategoryID = new String[1];
        db.collection("Categories").document(modal.getCategoryId()).get()
                .addOnSuccessListener(d -> {
                    if (d.exists() && d.get("categoryName")!=null){
                        defCategoryID[0] = d.getId();
                        defCategoryName[0] = Objects.requireNonNull(d.get("categoryName")).toString();}
                });

        subSubmit.setOnClickListener(view -> {
            String choseCheck = autoCompleteTextView.getText().toString().trim();
            String modelNameText = modelName.getText().toString().trim();
            if (choseCheck.isEmpty()){
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setContentText("Iltimos, avval katalog tanlang!")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(SweetAlertDialog::cancel)
                        .show();
            }
            else if (modelNameText.isEmpty()){
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setContentText("Iltimos, avval toifa nomini kiritng !")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(SweetAlertDialog::cancel)
                        .show();
            }
            else{
                if(choseCheck.equals(defCategoryID[0]) || choseCheck.equals(defCategoryName[0])){
                    if(modelNameText.equals(modal.getSubcategoryName())){
                        new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                                .setContentText("O'zgartirish kiritilmadi!")
                                .setConfirmText("OK!")
                                .setConfirmClickListener(SweetAlertDialog::cancel)
                                .show();
                        dialog.dismiss();
                    }
                    else{
                        Query query = db.collection("SubCategories").whereEqualTo("subcategoryName", modelNameText);
                        AggregateQuery countQuery = query.count();
                        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                AggregateQuerySnapshot snapshot = task1.getResult();
                                if (snapshot.getCount() == 0){
                                    updateSubCategoryName(modal, subCatCol, modelNameText,dialog);
                                    notifyItemChanged(pos);
                                }
                                else{
                                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                            .setContentText("Toifa nomi: "+modelNameText+" bazada mavjud!")
                                            .setConfirmText("OK!")
                                            .setConfirmClickListener(SweetAlertDialog::cancel)
                                            .show();
                                }

                            }
                        });
                    }

                }
                else {

                    if(modelNameText.equals(modal.getSubcategoryName())){
                        updateAndDeleteInCategory(modal,choseCheck, db, defCategoryID[0], dialog);
                        notifyItemChanged(pos);
                    }
                    else{
                        Query query = db.collection("SubCategories").whereEqualTo("subcategoryName", modelNameText);
                        AggregateQuery countQuery = query.count();
                        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                AggregateQuerySnapshot snapshot = task1.getResult();
                                if (snapshot.getCount() == 0){
                                    updateAndDeleteInCategory(modal,choseCheck, db, defCategoryID[0], dialog);
                                    updateSubCategoryName(modal, subCatCol, modelNameText,dialog);
                                    notifyItemChanged(pos);
                                }
                                else{
                                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                            .setContentText("Toifa nomi: "+modelNameText+" bazada mavjud!")
                                            .setConfirmText("OK!")
                                            .setConfirmClickListener(SweetAlertDialog::cancel)
                                            .show();
                                }

                            }
                        });

                    }
                }
            }

        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }

    private void updateAndDeleteInCategory(AdminSubCategoryModel modal, String choseCheck, FirebaseFirestore db, String s, Dialog dialog) {
        db.collection("Categories").document(modal.getCategoryId()).update("subcategories", FieldValue.arrayRemove(modal.getId().toString()))
                .addOnFailureListener(e -> {
                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Katalogdan o'chirishda xatolik!")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(SweetAlertDialog::cancel)
                            .show();
                    dialog.dismiss();
                });
        db.collection("Categories").whereEqualTo("categoryName",choseCheck).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(!queryDocumentSnapshots.isEmpty()){
                        for(DocumentSnapshot d:queryDocumentSnapshots){
                            if(d.exists()){
                                d.getReference().update("subcategories", FieldValue.arrayUnion(modal.getId()))
                                        .addOnFailureListener(e -> {
                                            new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                                    .setContentText("Katalogga qo'shishda xatolik!")
                                                    .setConfirmText("OK!")
                                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                                    .show();
                                            dialog.dismiss();
                                        })
                                        .addOnSuccessListener(unused -> {

                                            db.collection("SubCategories").document(modal.getId()).update("categoryId", d.getId())
                                                    .addOnFailureListener(e -> {
                                                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                                            .setContentText("Katalog o'zgartirishda xatolik!")
                                                            .setConfirmText("OK!")
                                                            .setConfirmClickListener(SweetAlertDialog::cancel)
                                                            .show();
                                                    dialog.dismiss();
                                                    }).addOnSuccessListener(unused1 -> {
                                                        myDB.updateSubCategoryData(modal.getId(), modal.getSubcategoryName(), d.getId());
                                                        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                                                .setContentText("Muvafaqqiyatli o'zgartirildi!")
                                                                .setConfirmText("OK!")
                                                                .setConfirmClickListener(SweetAlertDialog::cancel)
                                                                .show();
                                                        dialog.dismiss();
                                                    });
                                        });
                                notifyDataSetChanged();
                            }
                        }
                    }
                });


    }

    private void updateSubCategoryName(AdminSubCategoryModel modal, CollectionReference subCatCol, String modelNameText, Dialog dialog) {
        Query query = subCatCol.whereEqualTo("subcategoryName", modelNameText);
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                AggregateQuerySnapshot snapshot = task.getResult();
                if(snapshot.getCount()<=1){
                    subCatCol.document(modal.getId()).update("subcategoryName", modelNameText)
                            .addOnSuccessListener(unused -> {
                                myDB.updateSubCategoryData(modal.getId(), modelNameText, modal.getCategoryId());
                            })
                            .addOnFailureListener(e -> {
                                new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                        .setContentText("Toifa nomini o'zgartirishda xatolik!")
                                        .setConfirmText("OK!")
                                        .setConfirmClickListener(SweetAlertDialog::cancel)
                                        .show();
                            });

                    dialog.dismiss();
                }
                else {
                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Bunday toifa nomi mavjud!")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(SweetAlertDialog::cancel)
                            .show();
                }
            }
        });

    }




}
