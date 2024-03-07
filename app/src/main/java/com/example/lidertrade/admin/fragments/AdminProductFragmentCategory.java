package com.example.lidertrade.admin.fragments;

import static android.app.Activity.RESULT_OK;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.adapters.AdminProductFragmentCategoryAdapter;
import com.example.lidertrade.admin.helpers.MyDataBaseHelper;
import com.example.lidertrade.admin.models.AdminCategoryModel;

import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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

public class AdminProductFragmentCategory extends Fragment {

    View v;
    FirebaseFirestore firestore;
    CollectionReference categoryCol;
    DocumentReference categoryDoc;
    String categoryDocID;
    FirebaseStorage storage;
    StorageReference sRef;

    Map<String, String> categoryMap;
    Button browse, submit, reset;
    ImageView categoryImage;
    Uri imageUri;
    EditText categoryName;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    MyDataBaseHelper myDB;

    Group hiddenGroup;
    CardView cardView;
    ImageView arrow;
    ArrayList<AdminCategoryModel> modelArrayList;
    AdminProductFragmentCategoryAdapter categoryAdapter;
    SearchView searchView;
    String defLoadImg, cusLoadImg;
    ArrayList<String> empty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.admin_product_fragment_category, container, false);

        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();
        categoryCol = firestore.collection("Categories");
        categoryDoc = categoryCol.document();
        categoryDocID = categoryDoc.getId();

        cardView = v.findViewById(R.id.base_cardview);
        arrow = v.findViewById(R.id.show);
        hiddenGroup = v.findViewById(R.id.card_group);
        myDB = new MyDataBaseHelper(requireActivity());
        addProductMethod();

        sRef = storage.getReference();
        browse = v.findViewById(R.id.browse);
        reset = v.findViewById(R.id.reset);
        submit = v.findViewById(R.id.submit);
        categoryImage = v.findViewById(R.id.categoryImage);
        categoryName = v.findViewById(R.id.categoryName);
        progressBar = v.findViewById(R.id.progressBar);
        categoryMap = new HashMap<>();
        recyclerView = v.findViewById(R.id.recyclerView);
        searchView = v.findViewById(R.id.searchView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(false);
        modelArrayList = new ArrayList<>();
        categoryAdapter =new AdminProductFragmentCategoryAdapter(getContext(), modelArrayList);
        recyclerView.setAdapter(categoryAdapter);
        empty = new ArrayList<>();
        defLoadImg = (categoryImage.getDrawable().toString());


        loadCategoryDataRW();

        categoryAdapter.setonItemClickListener(url -> {
            progressBar.setVisibility(View.GONE);
            new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Eslatma")
                    .setContentText("Katalog o'chirib yuboriladi. Rozimisiz?")
                    .setCancelText("Yo'q")
                    .setConfirmText("Ha")
                    .showCancelButton(true)
                    .setCancelClickListener(SweetAlertDialog::cancel)
                    .setConfirmClickListener(sweetAlertDialog1 -> {

                        removeItem(url);
                        progressBar.setVisibility(View.GONE);
                        sweetAlertDialog1.cancel();
                    })
                    .show();
        });

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

        ActivityResultLauncher<String> getContent = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        categoryImage.setImageURI(result);
                        imageUri = result;
                    }
                }
        );
        categoryAdapter.setOnItemClick(data -> getContent.launch("image/*"));
        categoryAdapter.setOnItemClick2(catId -> {
            Bitmap bitmap = ((BitmapDrawable) categoryImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            StorageReference catRef = sRef.child("categoryPic/" + catId);
            UploadTask uploadTask = catRef.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot -> catRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String url = uri.toString();
                                firestore.collection("Categories").document(catId)
                                        .update("categoryPic",url)
                                        .addOnSuccessListener(unused -> {
                                            new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                    .setContentText("Rasm Yuklandi")
                                                    .setConfirmText("OK!")
                                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                                    .show();
                                        })
                                        .addOnFailureListener(e -> {
                                            new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                                    .setContentText("Jarayonda xatolik")
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
        });
        ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle bundle = result.getData().getExtras();
                        Bitmap bitmap = (Bitmap) bundle.get("data");
                        categoryImage.setImageBitmap(bitmap);
                        categoryImage.setMaxHeight(170);
                        categoryImage.setMaxWidth(170);
                    }
                }
        );

        browse.setOnClickListener(view -> getContent.launch("image/*"));

        categoryImage.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intentActivityResultLauncher.launch(intent);
        });

        submit.setOnClickListener(view -> {
            String categoryNameText = categoryName.getText().toString().trim();
            cusLoadImg = (categoryImage.getDrawable().toString());

            if (categoryNameText.isEmpty()){
                Toast.makeText(getContext(), "Katalog nomini kiriting", Toast.LENGTH_SHORT).show();
            }
            else {
                Query query = firestore.collection("Categories").whereEqualTo("categoryName", categoryNameText);
                AggregateQuery countQuery = query.count();
                countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        AggregateQuerySnapshot snapshot = task.getResult();
                        if (snapshot.getCount() == 0){
                            new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Eslatma")
                                    .setContentText("Yangi katalog qo'shmoqchimisiz!")
                                    .setCancelText("Yo'q")
                                    .setConfirmText("Ha")
                                    .showCancelButton(true)
                                    .setCancelClickListener(SweetAlertDialog::cancel)
                                    .setConfirmClickListener(sweetAlertDialog1 -> {
                                        progressBar.setVisibility(View.VISIBLE);
                                        if (Objects.equals(cusLoadImg, defLoadImg)){
                                            Bitmap bitmap = ((BitmapDrawable) categoryImage.getDrawable()).getBitmap();

                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
                                            byte[] data = baos.toByteArray();

                                            StorageReference subRef = sRef.child("categoryPic/" + categoryDocID);
                                            UploadTask uploadTask = subRef.putBytes(data);

                                            uploadTask.addOnSuccessListener(taskSnapshot -> subRef.getDownloadUrl().addOnSuccessListener(uri -> {

                                                String url = uri.toString();
                                                String documentName = categoryDocID;
                                                categoryMap.put("id", documentName);
                                                categoryMap.put("categoryPic", url);
                                                categoryMap.put("categoryName", categoryNameText);


                                                firestore.collection("Categories").document(documentName)
                                                        .set(categoryMap).addOnSuccessListener(unused -> {
                                                            myDB.addNewCategory(documentName, categoryNameText);
                                                            progressBar.setVisibility(View.GONE);
                                                            new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                                    .setContentText("Yangi katalog muvafaqqiyatli qo'shildi!")
                                                                    .setConfirmText("OK!")
                                                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                                                    .show();
                                                            TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                                                            hiddenGroup.setVisibility(View.GONE);
                                                            arrow.setImageResource(android.R.drawable.arrow_down_float);
                                                        }).addOnFailureListener(e -> {
                                                            progressBar.setVisibility(View.GONE);
                                                            new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                                                    .setContentText("Jarayonda xatolik")
                                                                    .setConfirmText("OK!")
                                                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                                                    .show();
                                                        });
                                            }).addOnFailureListener(e -> {
                                                progressBar.setVisibility(View.GONE);
                                                new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                                        .setContentText("Jarayonda xatolik")
                                                        .setConfirmText("OK!")
                                                        .setConfirmClickListener(SweetAlertDialog::cancel)
                                                        .show();
                                            }));

                                        }
                                        else{
                                            String documentName = categoryDocID;
                                            categoryMap.put("id", documentName);
                                            categoryMap.put("categoryPic", null);
                                            categoryMap.put("categoryName", categoryNameText);

                                            firestore.collection("Categories").document(documentName)
                                                    .set(categoryMap).addOnSuccessListener(unused -> {
                                                        progressBar.setVisibility(View.GONE);
                                                        new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                                .setContentText("Yangi katalog muvafaqqiyatli qo'shildi!")
                                                                .setConfirmText("OK!")
                                                                .setConfirmClickListener(SweetAlertDialog::cancel)
                                                                .show();
                                                        TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                                                        hiddenGroup.setVisibility(View.GONE);
                                                        arrow.setImageResource(android.R.drawable.arrow_down_float);
                                                    }).addOnFailureListener(e -> {
                                                        progressBar.setVisibility(View.GONE);
                                                        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                                                .setContentText("Jarayonda xatolik")
                                                                .setConfirmText("OK!")
                                                                .setConfirmClickListener(SweetAlertDialog::cancel)
                                                                .show();
                                                    });

                                        }

                                        categoryMap.clear();
                                        categoryImage.setImageResource(R.drawable.lider);
                                        categoryName.setText("");
                                        categoryName.clearFocus();
                                        sweetAlertDialog1.cancel();
                                    })
                                    .show();
                        }
                        else{
                            new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setContentText("Katalog nomi: "+categoryNameText+" bazada mavjud")
                                    .setConfirmText("OK!")
                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                    .show();
                        }
                    }
                });

            }

        });

        reset.setOnClickListener(view -> {
            categoryName.setText("");
            categoryName.clearFocus();
            categoryImage.setImageResource(R.drawable.lider);
            categoryImage.clearFocus();
        });
        return v;
    }

    private void loadCategoryDataRW() {
        Cursor cursor = myDB.readAllCategories();
        firestore.collection("Categories")
                .addSnapshotListener((value, error) -> {
                    assert value != null;
                    if (!value.isEmpty()) {
                        List<DocumentSnapshot> list = value.getDocuments();
                        ArrayList<String> aList = new ArrayList<>();
                        modelArrayList.clear();
                        empty.clear();
                        for (DocumentSnapshot d : list) {
                            if (!myDB.existCategoryId(d.getId())) {
                                myDB.addNewCategory(d.getId(), Objects.requireNonNull(d.get("categoryName")).toString());
                            }
                            AdminCategoryModel dataModal = d.toObject(AdminCategoryModel.class);
                            dataModal.setId(d.getId());
                            modelArrayList.add(dataModal);
                            empty.add(dataModal.getCategoryName());
                            aList.add(d.getId());
                            if(aList.size() == list.size()){
                                if(cursor.getCount()!=0){
                                    while (cursor.moveToNext()){
                                        if(!aList.contains(cursor.getString(0))){
                                            myDB.deleteOneCategory(cursor.getString(0));
                                        }
                                    }
                                }
                            }
                        }

                        categoryAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void filterList(String s) {
        ArrayList<AdminCategoryModel> filteredList = new ArrayList<>();
        for (AdminCategoryModel model: modelArrayList){
            if (model.getCategoryName().toLowerCase().contains(s.toLowerCase().trim())){
                filteredList.add(model);
            }
        }
        this.categoryAdapter.setFilteredList(filteredList);
        this.recyclerView.setAdapter(categoryAdapter);
    }
    @SuppressLint("NotifyDataSetChanged")
    private void removeItem(String url) {
        progressBar.setVisibility(View.VISIBLE);

        firestore.collection("Categories").document(url).get().addOnSuccessListener(d -> {
            if (d.exists()){
                if (d.get("categoryPic") !=null){
                    try {
                        StorageReference photoRef = storage.getReferenceFromUrl((String) Objects.requireNonNull(d.get("categoryPic")));

                        photoRef.delete().addOnSuccessListener(aVoid ->
                                firestore.collection("Categories").document(url)
                                        .delete()
                                        .addOnSuccessListener(unused -> {
                                            myDB.deleteOneCategory(url);
                                            new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                    .setContentText("Katalog o'chirib yuborildi!")
                                                    .setConfirmText("OK!")
                                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                                    .show();
                                            progressBar.setVisibility(View.GONE);
                                        })
                                        .addOnFailureListener(e -> {
                                            new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                                    .setContentText("Jarayonda xatolik!")
                                                    .setConfirmText("OK!")
                                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                                    .show();
                                            progressBar.setVisibility(View.GONE);

                                        })).addOnFailureListener(exception -> {
                            new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setContentText("Jarayonda xatolik!")
                                    .setConfirmText("OK!")
                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                    .show();
                            progressBar.setVisibility(View.GONE);
                        });
                    }
                    catch (IllegalArgumentException error){

                        firestore.collection("Categories").document(url)
                                .delete().addOnSuccessListener(unused -> {
                                    myDB.deleteOneCategory(url);
                                    new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                            .setContentText("Katalog o'chirib yuborildi!")
                                            .setConfirmText("OK!")
                                            .setConfirmClickListener(SweetAlertDialog::cancel)
                                            .show();
                                    progressBar.setVisibility(View.GONE);
                                }).addOnFailureListener(e -> {
                                    new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                            .setContentText("Jarayonda xatolik!")
                                            .setConfirmText("OK!")
                                            .setConfirmClickListener(SweetAlertDialog::cancel)
                                            .show();
                                    progressBar.setVisibility(View.GONE);
                                });
                    }

                }
                else{

                    firestore.collection("Categories").document(url)
                            .delete().addOnSuccessListener(unused -> {
                                myDB.deleteOneCategory(url);
                                new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                        .setContentText("Katalog o'chirib yuborildi!")
                                        .setConfirmText("OK!")
                                        .setConfirmClickListener(SweetAlertDialog::cancel)
                                        .show();
                                progressBar.setVisibility(View.GONE);
                            }).addOnFailureListener(e -> {
                                new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setContentText("Jarayonda xatolik!")
                                        .setConfirmText("OK!")
                                        .setConfirmClickListener(SweetAlertDialog::cancel)
                                        .show();
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

        categoryAdapter.notifyDataSetChanged();
    }


    private void addProductMethod() {
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
}