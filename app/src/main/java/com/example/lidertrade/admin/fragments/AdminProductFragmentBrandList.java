package com.example.lidertrade.admin.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.adapters.AdminProductFragmentBrandAdapter;
import com.example.lidertrade.admin.helpers.MyDataBaseHelper;
import com.example.lidertrade.admin.models.AdminBrandModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
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

public class AdminProductFragmentBrandList extends Fragment {

    View v;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    StorageReference sRef;

    Map<String, String> brandMap;
    Button browse, submit, reset;
    ImageView brandImage;
    Uri imageUri;
    EditText brandName;
    ProgressBar progressBar;
    MyDataBaseHelper myDB;

    Group hiddenGroup;
    CardView cardView;
    ImageView arrow;
    RecyclerView recyclerView;
    ArrayList<AdminBrandModel> modelArrayList;
    AdminProductFragmentBrandAdapter brandAdapter;
    SearchView searchView;
    String defLoadImg, cusLoadImg;
    ArrayList<String> empty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.admin_product_fragment_brand_list, container, false);

        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();
        CollectionReference brandCol = firestore.collection("brands");
        DocumentReference brandDoc = brandCol.document();
        sRef = storage.getReference();

        cardView = v.findViewById(R.id.base_cardview);
        arrow = v.findViewById(R.id.show);
        hiddenGroup = v.findViewById(R.id.card_group);
        myDB = new MyDataBaseHelper(requireActivity());
        addBrandMethod();

        browse = v.findViewById(R.id.browse);
        reset = v.findViewById(R.id.reset);
        submit = v.findViewById(R.id.submit);
        brandImage = v.findViewById(R.id.brandImage);
        brandName = v.findViewById(R.id.brandName);
        progressBar = v.findViewById(R.id.progressBar);
        brandMap = new HashMap<>();
        recyclerView = v.findViewById(R.id.recyclerView);
        searchView = v.findViewById(R.id.searchView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(false);
        modelArrayList = new ArrayList<>();
        brandAdapter =new AdminProductFragmentBrandAdapter(getContext(), modelArrayList);
        recyclerView.setAdapter(brandAdapter);
        empty = new ArrayList<>();
        defLoadImg = (brandImage.getDrawable().toString());

        loadBrandDataRW();

        brandAdapter.setonItemClickListener(url -> {
            progressBar.setVisibility(View.GONE);
            new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Eslatma")
                    .setContentText("Brand o'chirib yuboriladi. Rozimisiz?")
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
                        brandImage.setImageURI(result);
                        imageUri = result;
                    }
                }
        );

        ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle bundle = result.getData().getExtras();
                        Bitmap bitmap = (Bitmap) bundle.get("data");
                        brandImage.setImageBitmap(bitmap);
                        brandImage.setMaxHeight(170);
                        brandImage.setMaxWidth(170);
                    }
                }
        );
        brandAdapter.setOnItemClick(data -> getContent.launch("image/*"));
        brandAdapter.setOnItemClick2(brandId -> {
            Bitmap bitmap = ((BitmapDrawable) brandImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            StorageReference brandRef = sRef.child("brandPic/" + brandId);
            UploadTask uploadTask = brandRef.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot -> brandRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String url = uri.toString();
                                brandCol.document(brandId)
                                        .update("brandPic",url)
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


        browse.setOnClickListener(view -> getContent.launch("image/*"));

        brandImage.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intentActivityResultLauncher.launch(intent);
        });

        submit.setOnClickListener(view -> {
            String brandNameText = brandName.getText().toString().trim();
            cusLoadImg = (brandImage.getDrawable().toString());

            if (brandNameText.isEmpty()){
                Toast.makeText(getContext(), "Brand nomini kiriting", Toast.LENGTH_SHORT).show();
            }
            else {
                Query query = firestore.collection("brands").whereEqualTo("brandName", brandNameText);
                AggregateQuery countQuery = query.count();
                countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        AggregateQuerySnapshot snapshot = task.getResult();
                        if (snapshot.getCount() == 0){
                            new SweetAlertDialog(requireActivity(), SweetAlertDialog.NORMAL_TYPE)
                                    .setContentText("Yangi Brand qo'shmoqchimisiz!")
                                    .setCancelText("Yo'q")
                                    .setConfirmText("Ha")
                                    .showCancelButton(true)
                                    .setCancelClickListener(SweetAlertDialog::cancel)
                                    .setConfirmClickListener(sweetAlertDialog1 -> {
                                        progressBar.setVisibility(View.VISIBLE);

                                        if (Objects.equals(cusLoadImg, defLoadImg)){
                                            Bitmap bitmap = ((BitmapDrawable) brandImage.getDrawable()).getBitmap();
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
                                            byte[] data = baos.toByteArray();

                                            StorageReference subRef = sRef.child("BrandPic/" + brandDoc.getId());
                                            UploadTask uploadTask = subRef.putBytes(data);

                                            uploadTask.addOnSuccessListener(taskSnapshot -> subRef.getDownloadUrl().addOnSuccessListener(uri -> {

                                                String url = uri.toString();
                                                String documentName = brandDoc.getId();
                                                brandMap.put("id", documentName);
                                                brandMap.put("brandPic", url);
                                                brandMap.put("brandName", brandNameText);


                                                brandDoc.set(brandMap)
                                                        .addOnSuccessListener(unused -> {
                                                            myDB.addNewBrand(documentName, brandNameText);
                                                            progressBar.setVisibility(View.GONE);
                                                            new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                                    .setContentText("Yangi brand muvafaqqiyatli qo'shildi!")
                                                                    .setConfirmText("OK!")
                                                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                                                    .show();
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

                                        }else{
                                            String documentName = brandDoc.getId();
                                            brandMap.put("id", documentName);
                                            brandMap.put("brandPic", null);
                                            brandMap.put("brandName", brandNameText);

                                            brandDoc.set(brandMap)
                                                    .addOnSuccessListener(unused -> {
                                                        progressBar.setVisibility(View.GONE);
                                                        new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                                .setContentText("Yangi Brand muvafaqqiyatli qo'shildi!")
                                                                .setConfirmText("OK!")
                                                                .setConfirmClickListener(SweetAlertDialog::cancel)
                                                                .show();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        progressBar.setVisibility(View.GONE);
                                                        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                                                .setContentText("Jarayonda xatolik")
                                                                .setConfirmText("OK!")
                                                                .setConfirmClickListener(SweetAlertDialog::cancel)
                                                                .show();
                                                    });

                                        }

                                        brandMap.clear();
                                        brandImage.setImageResource(R.drawable.lider);
                                        brandName.setText("");
                                        brandName.clearFocus();
                                        sweetAlertDialog1.cancel();
                                        TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                                        hiddenGroup.setVisibility(View.GONE);
                                        arrow.setImageResource(android.R.drawable.arrow_down_float);
                                    })
                                    .show();
                        }
                        else{
                            new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setContentText("Brand nomi: "+brandNameText+" bazada mavjud")
                                    .setConfirmText("OK!")
                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                    .show();
                        }
                    } else {
                        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                .setContentText("Jarayonda xatolik")
                                .setConfirmText("OK!")
                                .setConfirmClickListener(SweetAlertDialog::cancel)
                                .show();
                    }
                });
            }
        });

        reset.setOnClickListener(view -> {
            brandName.setText("");
            brandName.clearFocus();
            brandImage.setImageResource(R.drawable.lider);
            brandImage.clearFocus();
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBrandDataRW();
    }

    private void loadBrandDataRW() {
        Cursor cursor = myDB.readAllBrands();
        firestore.collection("brands").addSnapshotListener((value, error) -> {

                    if (!value.isEmpty()) {
                        List<DocumentSnapshot> list = value.getDocuments();
                        modelArrayList.clear();
                        List aList = new ArrayList();
                        empty.clear();
                        for (DocumentSnapshot d : list) {
                            if (!myDB.existBrandId(d.getId())) {
                                myDB.addNewBrand(d.getId(), Objects.requireNonNull(d.get("brandName")).toString());
                            }
                            AdminBrandModel dataModal = d.toObject(AdminBrandModel.class);
                            modelArrayList.add(dataModal);
                            empty.add(dataModal.getBrandName());
                            aList.add(d.getId());
                            brandAdapter.notifyDataSetChanged();
                        }
                        if(cursor.getCount()!=0){
                            while (cursor.moveToNext()){
                                if(!aList.contains(cursor.getString(0))){
                                    myDB.deleteOneBrand(cursor.getString(0));
                                }
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "No data found in Database", Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void filterList(String s) {
        ArrayList<AdminBrandModel> filteredList = new ArrayList<>();
        for (AdminBrandModel model: modelArrayList){
            if (model.getBrandName().toLowerCase().contains(s.toLowerCase().trim())){
                filteredList.add(model);
            }
        }
        this.brandAdapter.setFilteredList(filteredList);
        this.recyclerView.setAdapter(brandAdapter);
    }
    @SuppressLint("NotifyDataSetChanged")
    private void removeItem(String url) {
        progressBar.setVisibility(View.VISIBLE);

        firestore.collection("brands").document(url).get().addOnSuccessListener(d -> {
            if (d.exists()){
                if (d.get("categoryPic") !=null){
                    try {

                        StorageReference photoRef = storage.getReferenceFromUrl((String) Objects.requireNonNull(d.get("brandPic")));
                        myDB.deleteOneBrand(url);
                        photoRef.delete().addOnSuccessListener(aVoid -> firestore.collection("brands").document(url)
                                .delete().addOnSuccessListener(unused -> {
                                    new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                            .setContentText("Brand o'chirib yuborildi!")
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
                        myDB.deleteOneBrand(url);
                        firestore.collection("brands").document(url)
                                .delete().addOnSuccessListener(unused -> {
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
                    myDB.deleteOneBrand(url);
                    firestore.collection("brands").document(url)
                            .delete().addOnSuccessListener(unused -> {
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
                new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Jarayonda xatolik!")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(SweetAlertDialog::cancel)
                        .show();
                progressBar.setVisibility(View.GONE);
            }
        });

        brandAdapter.notifyDataSetChanged();
    }
}
