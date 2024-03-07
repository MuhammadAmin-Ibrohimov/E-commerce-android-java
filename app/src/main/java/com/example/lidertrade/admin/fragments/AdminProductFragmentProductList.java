package com.example.lidertrade.admin.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.adapters.AdminProductFragmentProductAdapter;
import com.example.lidertrade.admin.helpers.MyDataBaseHelper;
import com.example.lidertrade.admin.models.AdminProductModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminProductFragmentProductList extends Fragment {

    View v;
    MyDataBaseHelper myDB;
    ProgressBar progressBar;
    FirebaseStorage storage;
    ArrayList<Uri> mArrayUri;
    ArrayList<String>  UrlsList;
    SearchView prodSearchView;
    Uri imageurl;
    StorageReference sRef;
    FirebaseFirestore firestore;
    RecyclerView prodRecyclerView;
    ArrayList<AdminProductModel> productModelArrayList;
    AdminProductFragmentProductAdapter prodAdapter;
    public AdminProductFragmentProductList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.admin_product_fragment_product_list, container, false);
        firestore = FirebaseFirestore.getInstance();
        myDB = new MyDataBaseHelper(requireContext());
        storage = FirebaseStorage.getInstance();
        sRef = storage.getReference();
        mArrayUri = new ArrayList<>();
        UrlsList = new ArrayList<>();
        prodSearchView = v.findViewById(R.id.prodSearchView);
        prodSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterList(s);
                return false;
            }
        });

        prodRecyclerView = v.findViewById(R.id.prodRecyclerView);
        progressBar = v.findViewById(R.id.progressBar);
        prodRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        prodRecyclerView.setHasFixedSize(false);
        productModelArrayList = new ArrayList<>();
        prodAdapter = new AdminProductFragmentProductAdapter(getContext(), productModelArrayList);
        prodRecyclerView.setAdapter(prodAdapter);
        prodAdapter.setOnItemClickListener(url -> new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                .setContentText("Ushbu toifani o'chirib yubormoqchimisiz!")
                .setCancelText("Yo'q")
                .setConfirmText("Ha")
                .showCancelButton(true)
                .setCancelClickListener(SweetAlertDialog::cancel)
                .setConfirmClickListener(sweetAlertDialog1 -> {
                    progressBar.setVisibility(View.VISIBLE);
                    removeItem(url);
                    progressBar.setVisibility(View.GONE);
                    sweetAlertDialog1.cancel();
                })
                .show());
        prodAdapter.setOnItemClick(data -> { wayToImageLoad(); });
        prodAdapter.setOnItemClick2(this::uploadStorage);


        loadProductsModel();
        return v;
    }

    private void uploadStorage(String prodId) {
        progressBar.setVisibility(View.VISIBLE);
        for (int i = 0; i < mArrayUri.size(); i++) {
//----------------------GENERATE RANDOM ALPHANUMERIC STRING START
            int leftLimit = 48; // numeral '0'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 5;
            Random random = new Random();
            String generatedString = random.ints(leftLimit, rightLimit + 1)
                    .filter(ii -> (ii <= 57 || ii >= 65) && (ii <= 90 || ii >= 97))
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
//----------------------GENERATE RANDOM ALPHANUMERIC STRING END
            Bitmap bmp = null;
            Uri IndividualImage = mArrayUri.get(i);
            try {
                bmp = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), IndividualImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] fileInBytes = baos.toByteArray();

            if (IndividualImage != null) {
                StorageReference subRef = sRef.child("ProductPic/" + prodId + generatedString);
                UploadTask uploadTask = subRef.putBytes(fileInBytes);
                uploadTask.addOnSuccessListener(taskSnapshot -> subRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    UrlsList.add(String.valueOf(uri));
                    if (UrlsList.size() != 0 ) {
                        for(String j:UrlsList){
                            firestore.collection("products").document(prodId)
                                    .update("imageUrl", FieldValue.arrayUnion(j));
                            progressBar.setVisibility(View.GONE);
                            new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                    .setContentText("Rasmlar yuklandi!")
                                    .setConfirmText("OK!")
                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                    .show();
                        }

                    }

                }).addOnFailureListener(e -> {
                    new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Rasmlarni yuklashda xatolik!")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(SweetAlertDialog::cancel)
                            .show();
                    progressBar.setVisibility(View.GONE);
                }));
            }else{
                progressBar.setVisibility(View.GONE);
                new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Mahsulot uchun rasm yuklang!")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(SweetAlertDialog::cancel)
                        .show();
            }
        }
    }

    private void wayToImageLoad() {
        mArrayUri.clear();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        someActivityResultLauncher.launch(intent);
    }


    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        if (data.getClipData() != null) {
                            int cout = data.getClipData().getItemCount();
                            for (int i = 0; i < cout; i++) {
                                imageurl = data.getClipData().getItemAt(i).getUri();
                                mArrayUri.add(imageurl);
                            }
                        } else {
                            imageurl = data.getData();
                            mArrayUri.add(imageurl);
                        }
                    }

                }
            });


    @SuppressLint("NotifyDataSetChanged")
    private void removeItem(String url) {
        firestore.collection("products").document(url).get().addOnSuccessListener(d -> {
            if (d.exists()){
                ArrayList<String > aa = (ArrayList<String>) d.get("imageUrl");
                for (int i = 0; i < Objects.requireNonNull(aa).size(); i++){
                    StorageReference photoRef = storage.getReferenceFromUrl(Objects.requireNonNull(aa.get(i)));
                    photoRef.delete().addOnFailureListener(e -> {
                        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                .setContentText("0 Jarayonda xatolik!")
                                .setConfirmText("OK!")
                                .setConfirmClickListener(SweetAlertDialog::cancel)
                                .show();
                        progressBar.setVisibility(View.GONE);
                    });
                }
                firestore.collection("SubCategories")
                        .document(Objects.requireNonNull(d.get("subCategory")).toString())
                        .update("products", FieldValue.arrayRemove(url))
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                MyDataBaseHelper myDB = new MyDataBaseHelper(requireActivity());
                                myDB.deleteOneProduct(url);
                                new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                        .setContentText("Belgilangan mahsulot muvafaqqiyatli o'chirildi!")
                                        .setConfirmText("OK!")
                                        .setConfirmClickListener(SweetAlertDialog::cancel)
                                        .show();
                            }
                            else {
                                new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setContentText("1 Jarayonda xatolik!")
                                        .setConfirmText("OK!")
                                        .setConfirmClickListener(SweetAlertDialog::cancel)
                                        .show();
                            }
                        });

                firestore.collection("products").document(d.getId()).delete()
                        .addOnFailureListener(e -> {
                            new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setContentText("2 Jarayonda xatolik!")
                                    .setConfirmText("OK!")
                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                    .show();
                            progressBar.setVisibility(View.GONE);
                        });
            }else{
                new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                        .setContentText("3 Jarayonda xatolik!")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(SweetAlertDialog::cancel)
                        .show();
                progressBar.setVisibility(View.GONE);
            }
        });

        prodAdapter.notifyDataSetChanged();
    }

    private void loadProductsModel() {
        Cursor cursor = myDB.readAllProducts();
        firestore.collection("products")
                .addSnapshotListener((value, error) -> {
                    if (!value.isEmpty()) {
                        List<DocumentSnapshot> list = value.getDocuments();
                        List aList = new ArrayList();
                        productModelArrayList.clear();
                        for (DocumentSnapshot d : list) {
                            if (!myDB.existProductId(d.getId())) {
                                myDB.addNewProduct(d.getId(), Objects.requireNonNull(d.get("productName")).toString(), Integer.parseInt(Objects.requireNonNull(d.get("cashPrice")).toString()));
                            }
                            AdminProductModel dataModal = d.toObject(AdminProductModel.class);
                            assert dataModal != null;
                            dataModal.setProductId(d.getId());
                            productModelArrayList.add(dataModal);
                            aList.add(d.getId());
                        }
                        if(cursor.getCount()!=0){
                            while (cursor.moveToNext()){
                                if(!aList.contains(cursor.getString(0))){
                                    myDB.deleteOneProduct(cursor.getString(0));
                                }
                            }
                        }
                        prodAdapter.notifyDataSetChanged();
                    } else {
                        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                .setContentText("Hech qanday ma'lumot topilmadi")
                                .setConfirmText("OK!")
                                .setConfirmClickListener(SweetAlertDialog::cancel)
                                .show();
                    }
                });
    }

    private void filterList(String s) {
        ArrayList<AdminProductModel> mFilteredList = new ArrayList<>();
        for (AdminProductModel model : productModelArrayList){
            if (model.getProductName().toLowerCase().contains(s.toLowerCase().trim())){
                mFilteredList.add(model);
            }
        }
        this.prodAdapter.setFilteredList(mFilteredList);
        this.prodRecyclerView.setAdapter(prodAdapter);
    }
}