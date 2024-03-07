package com.example.lidertrade.admin.fragments;

import static android.app.Activity.RESULT_OK;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.adapters.AdminProductFragmentSubCategoryAdapter;
import com.example.lidertrade.admin.helpers.MyDataBaseHelper;
import com.example.lidertrade.admin.models.AdminCategoryModel;
import com.example.lidertrade.admin.models.AdminSubCategoryModel;
import com.example.lidertrade.admin.models.AdminUserModel;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminProductFragmentSubCategory extends Fragment {

    FirebaseFirestore firestore;
    CollectionReference subCategoryCol;

    StorageReference sRef;
    MyDataBaseHelper myDB;
    ProgressBar progressBar;
    FirebaseStorage storage;
    DocumentReference arrayRef;
    String defLoadImg, cusLoadImg;

    View v;
    ArrayAdapter<String> adapter;
    AutoCompleteTextView autoCompleteTextView;
    RecyclerView subRecyclerView;

    Group hiddenGroup;
    CardView cardView;
    ImageView arrow;
    ArrayList<AdminSubCategoryModel> subModelArrayList;
    AdminProductFragmentSubCategoryAdapter subCategoryAdapter;
    Button subReset, subBrowse, subSubmit;
    EditText modelName;
    ImageView subCategoryImage;
    ArrayList<String> category;
    SearchView subSearchView;
    Map<String, String> subCategoryMap;
    ArrayList<String> subCategoryNameList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.admin_product_fragment_sub_category, container, false);

        autoCompleteTextView = (AutoCompleteTextView) v.findViewById(R.id.autoComplete);
        subRecyclerView = (RecyclerView) v.findViewById(R.id.subRecyclerView);
        firestore = FirebaseFirestore.getInstance();
        subCategoryCol = firestore.collection("SubCategories");
        storage = FirebaseStorage.getInstance();
        sRef = storage.getReference();

        cardView = v.findViewById(R.id.base_cardview);
        arrow = v.findViewById(R.id.show);
        hiddenGroup = v.findViewById(R.id.card_group);
        myDB = new MyDataBaseHelper(requireActivity());
        addBrandMethod();
        subRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        subRecyclerView.setHasFixedSize(false);
        subReset =v.findViewById(R.id.subReset);
        subSubmit =  v.findViewById(R.id.subSubmit);
        subBrowse =  v.findViewById(R.id.subBrowse);
        modelName =  v.findViewById(R.id.modelName);
        subCategoryImage =  v.findViewById(R.id.subCategoryImage);
        subSearchView =  v.findViewById(R.id.subSearchView);
        progressBar = v.findViewById(R.id.progressBar);
        subModelArrayList = new ArrayList<>();
        subCategoryNameList = new ArrayList<>();
        subCategoryAdapter = new AdminProductFragmentSubCategoryAdapter(getContext(), subModelArrayList);
        subRecyclerView.setAdapter(subCategoryAdapter);
        subCategoryMap = new HashMap<>();
        category = new ArrayList<>();
        defLoadImg = (subCategoryImage.getDrawable().toString());

        loadCategoriesToChoose();
        loadSearchFilter();





        subCategoryAdapter.setOnItemClickListener(url ->{
                    new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                            .setContentText("Ushbu toifani o'chirib yubormoqchimisiz!")
                            .setCancelText("Yo'q")
                            .setConfirmText("Ha")
                            .showCancelButton(true)
                            .setCancelClickListener(SweetAlertDialog::cancel)
                            .setConfirmClickListener(sweetAlertDialog1 -> {
                                progressBar.setVisibility(View.VISIBLE);
                                removeItemFromCategory(url);
                                removeItem(url);
                                progressBar.setVisibility(View.GONE);
                                sweetAlertDialog1.cancel();
                            })
                            .show();
                    subCategoryAdapter.notifyDataSetChanged();
        });

        ActivityResultLauncher<String> getImageUrl = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        subCategoryImage.setImageURI(result);
                        subCategoryAdapter.notifyDataSetChanged();
                    }
                }
        );

        ActivityResultLauncher<Intent> getImageFromCamera = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle bundle = result.getData().getExtras();
                        Bitmap bitmap = (Bitmap) bundle.get("data");
                        subCategoryImage.setImageBitmap(bitmap);
                        subCategoryImage.setMaxHeight(170);
                        subCategoryImage.setMaxWidth(170);
                    }
                }
        );
        ActivityResultLauncher<String> getImageUrlUpdate = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        subCategoryImage.setImageURI(result);
                    }
                }
        );
        subCategoryAdapter.setOnItemClick((AdminProductFragmentSubCategoryAdapter.OnItemClick) (subId) -> {
            getImageUrlUpdate.launch("image/*");
            subCategoryAdapter.notifyDataSetChanged();
        });
        subCategoryAdapter.setOnItemClick((AdminProductFragmentSubCategoryAdapter.OnItemClick2) subId -> {
            Bitmap bitmap = ((BitmapDrawable) subCategoryImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            StorageReference subRef = sRef.child("subCategoryPic/" +
                    subId.replaceAll("\\s", "").toLowerCase().trim());
            UploadTask uploadTask = subRef.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot -> subRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String url = uri.toString();
                                firestore.collection("SubCategories").document(subId)
                                        .update("subcategoryPic",url)
                                        .addOnSuccessListener(unused -> {
                                            new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                    .setContentText("Rasm Yuklandi")
                                                    .setConfirmText("OK!")
                                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                                    .show();
                                        })
                                        .addOnFailureListener(e -> {
                                            new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                                    .setContentText("Jarayonda xatolik1")
                                                    .setConfirmText("OK!")
                                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                                    .show();
                                        });

                            }))
                    .addOnFailureListener(e -> new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Jarayonda xatolik")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(SweetAlertDialog::cancel)
                            .show());
            subCategoryAdapter.notifyDataSetChanged();
        });


        submitButtonClick();
        resetButtonClick();





        loadSubcategoryDataToRecyclerView();
//        loadDataFromSQLITE();

        subBrowse.setOnClickListener(view -> getImageUrl.launch("image/*"));
        subCategoryImage.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            getImageFromCamera.launch(intent);
        });

        return v;
    }


    private void submitButtonClick() {
        subSubmit.setOnClickListener(view -> {
            cusLoadImg = (subCategoryImage.getDrawable().toString());
            String choseCheck = autoCompleteTextView.getText().toString().trim();
            String modelNameText = modelName.getText().toString().trim();

            if (choseCheck.isEmpty()){
                new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                        .setContentText("Iltimos, avval katalog tanlang!")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(SweetAlertDialog::cancel)
                        .show();
            }
            else if (modelNameText.isEmpty()){
                new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                        .setContentText("Iltimos, avval toifa nomini kiritng!")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(SweetAlertDialog::cancel)
                        .show();
            }
            else {
                Query query = firestore.collection("SubCategories").whereEqualTo("subcategoryName", modelNameText);
                AggregateQuery countQuery = query.count();
                countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        AggregateQuerySnapshot snapshot = task1.getResult();
                        if (snapshot.getCount() == 0){
                            DocumentReference subCategoryDoc = subCategoryCol.document();
                            progressBar.setVisibility(View.VISIBLE);
                            firestore.collection("Categories").whereEqualTo("categoryName", choseCheck).get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        if(!queryDocumentSnapshots.isEmpty()){
                                            for (DocumentSnapshot d:queryDocumentSnapshots){
                                                if (!Objects.equals(cusLoadImg, defLoadImg)){
                                                    Bitmap bitmap = ((BitmapDrawable) subCategoryImage.getDrawable()).getBitmap();
                                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                                    byte[] data = baos.toByteArray();

                                                    StorageReference subRef = sRef.child("subCategoryPic/" + subCategoryDoc.getId());
                                                    UploadTask uploadTask = subRef.putBytes(data);
                                                    uploadTask.addOnSuccessListener(taskSnapshot -> subRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                                        String url = uri.toString();
                                                        String documentName = subCategoryDoc.getId();

                                                        String categoryDocument = d.getId();
                                                        subCategoryMap.put("subcategoryName", modelNameText);
                                                        subCategoryMap.put("id", documentName);
                                                        subCategoryMap.put("subcategoryPic", url);
                                                        subCategoryMap.put("categoryId", categoryDocument);
                                                        subCategoryMap.put("products", null);

                                                        firestore.collection("SubCategories").document(documentName)
                                                                .set(subCategoryMap).addOnSuccessListener(unused -> {
                                                                    arrayRef = firestore.collection("Categories").document(categoryDocument);

                                                                    arrayRef.update("subcategories", FieldValue.arrayUnion(documentName)).addOnCompleteListener(task -> {
                                                                        if (task.isSuccessful()) {
                                                                            new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                                                    .setContentText("Yangi toifa muvafaqqiyatli qo'shildi!")
                                                                                    .setConfirmText("OK!")
                                                                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                                                                    .show();
                                                                            progressBar.setVisibility(View.GONE);
                                                                        } else {
                                                                            new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                                                                    .setContentText("Jarayonda xatolik3")
                                                                                    .setConfirmText("OK!")
                                                                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                                                                    .show();
                                                                            progressBar.setVisibility(View.GONE);
                                                                        }
                                                                    });
                                                                }).addOnFailureListener(e -> {
                                                                    new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                                                            .setContentText("Jarayonda xatolik2")
                                                                            .setConfirmText("OK!")
                                                                            .setConfirmClickListener(SweetAlertDialog::cancel)
                                                                            .show();
                                                                    progressBar.setVisibility(View.GONE);
                                                                });
                                                    })).addOnFailureListener(e -> {
                                                        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                                                .setContentText("Jarayonda xatolik1")
                                                                .setConfirmText("OK!")
                                                                .setConfirmClickListener(SweetAlertDialog::cancel)
                                                                .show();
                                                        progressBar.setVisibility(View.GONE);
                                                    });
                                                }
                                                else{
                                                    String url = null;
                                                    String documentName = subCategoryDoc.getId();
                                                    String categoryDocument = d.getId();
                                                    subCategoryMap.put("subcategoryName", modelNameText);
                                                    subCategoryMap.put("subcategoryPic", url);
                                                    subCategoryMap.put("id", documentName);
                                                    subCategoryMap.put("categoryId", categoryDocument);
                                                    subCategoryDoc.set(subCategoryMap).addOnSuccessListener(unused -> {
                                                        arrayRef = firestore.collection("Categories").document(categoryDocument);
                                                        arrayRef.update("subcategories", FieldValue.arrayUnion(documentName)).addOnCompleteListener(task -> {
                                                            if (task.isSuccessful()) {
                                                                new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                                        .setContentText("Yangi toifa yaratildi")
                                                                        .setConfirmText("OK!")
                                                                        .setConfirmClickListener(SweetAlertDialog::cancel)
                                                                        .show();
                                                                progressBar.setVisibility(View.GONE);
                                                            } else {
                                                                new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                                                        .setContentText("Jarayonda xatolik")
                                                                        .setConfirmText("OK!")
                                                                        .setConfirmClickListener(SweetAlertDialog::cancel)
                                                                        .show();
                                                                progressBar.setVisibility(View.GONE);
                                                            }
                                                        });
                                                    }).addOnFailureListener(e -> {
                                                                new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                                                        .setContentText("Jarayonda xatolik")
                                                                        .setConfirmText("OK!")
                                                                        .setConfirmClickListener(SweetAlertDialog::cancel)
                                                                        .show();
                                                                progressBar.setVisibility(View.GONE);
                                                            }
                                                    );

                                                }
                                                autoCompleteTextView.setText("");
                                                autoCompleteTextView.clearFocus();
                                                modelName.setText("");
                                                modelName.clearFocus();
                                                subCategoryImage.setImageResource(R.drawable.lider);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                                .setContentText("Kategoriya nomini topishda xatolik")
                                                .setConfirmText("OK!")
                                                .setConfirmClickListener(SweetAlertDialog::cancel)
                                                .show();
                                        progressBar.setVisibility(View.GONE);

                                    });


                            TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                            hiddenGroup.setVisibility(View.GONE);
                            arrow.setImageResource(android.R.drawable.arrow_down_float);
                        }
                        else{
                            new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                                    .setContentText("Toifa nomi: "+modelNameText+" bazada mavjud!")
                                    .setConfirmText("OK!")
                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                    .show();
                        }

                    }
                });

            }
        });
    }


    private void loadSubcategoryDataToRecyclerView() {
        Cursor cursor = myDB.readAllSubcategories();
        subCategoryNameList.clear();
        ArrayList<String> aList = new ArrayList<>();
        subCategoryCol.addSnapshotListener((value, error) -> {
            if (error!=null){
                String Tag = null;
                Log.e(Tag,"onEvent", error);
                return;
            }
            if (value != null && !value.isEmpty()){
                subModelArrayList.clear();
                aList.clear();
                for (QueryDocumentSnapshot d:value){
                    if (!myDB.existSubCategoryId(d.getId())) {
                        myDB.addNewSubcategory(d.getId(), Objects.requireNonNull(d.get("subcategoryName")).toString(),
                                (Objects.requireNonNull(d.get("categoryId")).toString()));
                    }

                    AdminSubCategoryModel dataModal = d.toObject(AdminSubCategoryModel.class);
                    aList.add(d.getId());
                    if(aList.size() == value.size()){
                        if(cursor.getCount()!=0){
                            while (cursor.moveToNext()){
                                if(!aList.contains(cursor.getString(0))){
                                    myDB.deleteOneSubcategory(cursor.getString(0));
                                }
                            }
                        }
                    }
                    subModelArrayList.add(dataModal);
                    subCategoryAdapter.notifyDataSetChanged();
                }
            }else {
                subModelArrayList.clear();
                String Tag = null;
                Log.e(Tag,"Xatolik bor!!!!");
                subCategoryAdapter.notifyDataSetChanged();
            }
        });
    }
    private void loadCategoriesToChoose() {
        firestore.collection("Categories").addSnapshotListener((value, error) -> {
            assert value != null;
            if (!value.isEmpty()){
                List<DocumentSnapshot> list = value.getDocuments();
                category.clear();
                for (DocumentSnapshot d : list) {
                    AdminCategoryModel dataModal = d.toObject(AdminCategoryModel.class);
                    assert dataModal != null;
                    category.add(dataModal.getCategoryName());
                }
                ArrayAdapter<String> adapterSub = new ArrayAdapter<>(requireContext(),
                        R.layout.admin_product_fragment_sub_category_drop_down,
                        category
                );
                autoCompleteTextView.setAdapter(adapterSub);
            }
        });
    }

    private void filterList(String s) {
        ArrayList<AdminSubCategoryModel> mFilteredList = new ArrayList<>();
        for (AdminSubCategoryModel model : subModelArrayList){
            if (model.getSubcategoryName().toLowerCase().contains(s.toLowerCase().trim())){
                mFilteredList.add(model);
            }
        }
        this.subCategoryAdapter.setFilteredList(mFilteredList);
        this.subRecyclerView.setAdapter(subCategoryAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void removeItemFromCategory(String url) {
        Query query = firestore.collection("Categories").whereArrayContains("subcategories", url);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            ArrayList<String> ids = new ArrayList<>();
            List<DocumentSnapshot> queries = queryDocumentSnapshots.getDocuments();
            for (DocumentSnapshot d : queries){
                DocumentReference ref = d.getReference();
                ref.update("subcategories", FieldValue.arrayRemove(url))
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                        .setContentText("Belgilangan toifa muvafaqqiyatli o'chirildi!")
                                        .setConfirmText("OK!")
                                        .setConfirmClickListener(SweetAlertDialog::cancel)
                                        .show();
                            }
                            else {
                                new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setContentText("Jarayonda xatolik!")
                                        .setConfirmText("OK!")
                                        .setConfirmClickListener(SweetAlertDialog::cancel)
                                        .show();
                            }
                        });
            }
        });

    }
    @SuppressLint("NotifyDataSetChanged")
    private void removeItem(String url) {
        firestore.collection("SubCategories").document(url).get().addOnSuccessListener(d -> {
            if (d.exists()){
                if(d.get("subcategoryPic")!=null){
                    try {
                        StorageReference photoRef = storage.getReferenceFromUrl((String) Objects.requireNonNull(d.get("subcategoryPic")));
                        photoRef.delete()
                                .addOnSuccessListener(aVoid ->
                                        firestore.collection("SubCategories").document(url)
                                                .delete().addOnFailureListener(e -> {
                                                    new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                                            .setContentText("Jarayonda xatolik!")
                                                            .setConfirmText("OK!")
                                                            .setConfirmClickListener(SweetAlertDialog::cancel)
                                                            .show();
                                                    progressBar.setVisibility(View.GONE);
                                                }).addOnSuccessListener(unused -> {
                                                    myDB.deleteOneSubcategory(url);
                                                    progressBar.setVisibility(View.GONE);
                                }))
                                .addOnFailureListener(exception -> {
                                    new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                            .setContentText("Jarayonda xatolik!")
                                            .setConfirmText("OK!")
                                            .setConfirmClickListener(SweetAlertDialog::cancel)
                                            .show();
                                    progressBar.setVisibility(View.GONE);
                                });
                    }catch (IllegalArgumentException error){
                        firestore.collection("SubCategories").document(url)
                                .delete().addOnFailureListener(e -> {
                                    new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                            .setContentText("Jarayonda xatolik!")
                                            .setConfirmText("OK!")
                                            .setConfirmClickListener(SweetAlertDialog::cancel)
                                            .show();
                                    progressBar.setVisibility(View.GONE);

                                });
                    }
                }else{
                    firestore.collection("SubCategories").document(url)
                            .delete().addOnFailureListener(e -> {
                                new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setContentText("Jarayonda xatolik!")
                                        .setConfirmText("OK!")
                                        .setConfirmClickListener(SweetAlertDialog::cancel)
                                        .show();
                                progressBar.setVisibility(View.GONE);
                            }).addOnSuccessListener(unused -> {
                                myDB.deleteOneSubcategory(url);
                                progressBar.setVisibility(View.GONE);
                            });
                }

            }else{
                new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Jarayonda xatolik!")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(SweetAlertDialog::cancel)
                        .show();
                progressBar.setVisibility(View.GONE);


            }
        });
        subCategoryAdapter.notifyDataSetChanged();
    }


    private void resetButtonClick() {
        subReset.setOnClickListener(view -> {
            autoCompleteTextView.setText("");
            autoCompleteTextView.clearFocus();
            modelName.setText("");
            modelName.clearFocus();
            subCategoryImage.setImageResource(R.drawable.lider);
            subCategoryImage.clearFocus();
        });
    }

    private void loadDataFromSQLITE() {
        Cursor cursor = myDB.readAllSubcategories();
        if(cursor.getCount()==0){
            Toast.makeText(requireContext(), "No Data", Toast.LENGTH_SHORT).show();
        }
        else{
            subModelArrayList.clear();
            subCategoryNameList.clear();
            while (cursor.moveToNext()){

                AdminSubCategoryModel dataModal = new AdminSubCategoryModel(cursor.getString(1), null,
                        cursor.getString(0),cursor.getString(2), null);
                assert dataModal != null;
                dataModal.setId(cursor.getString(0));
                subCategoryNameList.add(dataModal.getSubcategoryName());
                subModelArrayList.add(dataModal);
            }
            subCategoryAdapter.notifyDataSetChanged();
        }
    }

    private void addBrandMethod() {
        arrow.setOnClickListener(view -> {

            if(hiddenGroup.getVisibility() == View.VISIBLE){
                TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                hiddenGroup.setVisibility(View.GONE);
                arrow.setImageResource(android.R.drawable.arrow_down_float);
            }
            else {
                TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                hiddenGroup.setVisibility(View.VISIBLE);
                arrow.setImageResource(android.R.drawable.arrow_up_float);
            }
        });
    }
    private void loadSearchFilter() {
        subSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
    }

}