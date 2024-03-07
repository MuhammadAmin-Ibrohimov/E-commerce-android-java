package com.example.lidertrade.admin.fragments;

import static android.app.Activity.RESULT_OK;

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
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;


import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.lidertrade.R;
import com.example.lidertrade.admin.adapters.AdminProductFragmentProductAdapter;
import com.example.lidertrade.admin.adapters.ImageSliderAdapter;
import com.example.lidertrade.admin.helpers.MyDataBaseHelper;
import com.example.lidertrade.admin.models.AdminBrandModel;
import com.example.lidertrade.admin.models.AdminProductModel;
import com.example.lidertrade.admin.models.AdminSubCategoryModel;
import com.example.lidertrade.admin.models.AdminProductSpecificationsModel;
import com.example.lidertrade.deliverer.helpers.NumberTextWatcherForThousand;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.checkerframework.checker.units.qual.A;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminProductFragmentProductAdd extends Fragment implements View.OnClickListener{
    CardView cardView;
    String subCategoryText, prodModelText, brandText, name,cashPriceText, soldPriceText,priceText, quantityText, descriptionText;
    ImageView arrow;
    Uri imageurl;
    Group hiddenGroup;
    View v;

    FirebaseFirestore firestore;
    ProgressBar progressBar;
    FirebaseStorage storage;
    StorageReference sRef;

    LinearLayout linearLayout;
    SliderView productImage;
    ExtendedFloatingActionButton addSpec;
    ArrayList<String> valueSub, valueBrand, UrlsList,productNameList;
    RecyclerView prodRecyclerView;
    ArrayList<AdminProductModel> productModelArrayList;
    AdminProductFragmentProductAdapter prodAdapter;
    ArrayAdapter<String> adapterBrand, adapterSub;
    ArrayList<Uri> mArrayUri;
    ImageSliderAdapter imageSliderAdapter;
    private final int PICK_IMAGE_CODE = 39;
    AutoCompleteTextView subCategoriesAuto,  brandAuto;
    Button productSubmit, choseImage, productReset;
    EditText productName,soldPriceInput,  priceInput, productQuantity, prodModel, cashPriceInput;
    TextView creditPrice,threePrice, sixPrice, ninePrice,twelvePrice, soldPriceTextView;
    ArrayList<AdminProductSpecificationsModel> spec;
    TextInputEditText description;
    MyDataBaseHelper myDB;

    public AdminProductFragmentProductAdd() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.admin_product_fragment_product_add, container, false);

        mArrayUri = new ArrayList<Uri>();
        myDB = new MyDataBaseHelper(requireContext());
        cardView = v.findViewById(R.id.base_cardview);
        arrow = v.findViewById(R.id.show);
        hiddenGroup = v.findViewById(R.id.card_group);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        sRef = storage.getReference();

        linearLayout = v.findViewById(R.id.linearLayout);
        addSpec = v.findViewById(R.id.extendedFloatingActionButton);
        subCategoriesAuto = v.findViewById(R.id.autoCompleteSub);
        brandAuto = v.findViewById(R.id.autoCompleteBrand);
        productSubmit = v.findViewById(R.id.productSubmit);
        productReset = v.findViewById(R.id.productReset);
        choseImage = v.findViewById(R.id.choseImage);

        productImage = v.findViewById(R.id.productImage);
        description = v.findViewById(R.id.description);
        progressBar = v.findViewById(R.id.progressBar);

        UrlsList = new ArrayList<>();
        priceInput = v.findViewById(R.id.priceInput);
        priceInput.addTextChangedListener(new NumberTextWatcherForThousand(priceInput));
        productQuantity = v.findViewById(R.id.productQuantity);
        prodModel = v.findViewById(R.id.productModel);
        productName = v.findViewById(R.id.productName);
        soldPriceInput = v.findViewById(R.id.soldPriceInput);
        cashPriceInput = v.findViewById(R.id.cashPriceInput);
        creditPrice = v.findViewById(R.id.creditPrice);
        threePrice = v.findViewById(R.id.threePrice);
        sixPrice = v.findViewById(R.id.sixPrice);
        ninePrice = v.findViewById(R.id.ninePrice);
        twelvePrice = v.findViewById(R.id.twelvePrice);
        soldPriceTextView = v.findViewById(R.id.soldPriceTextView);
        cashPriceInput.addTextChangedListener(new TextWatcher() {
            long p=0, pr=0, cp=0;
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>0 && priceInput.getText().length()>0) {

                    pr = Long.parseLong(String.valueOf(priceInput.getText()).trim().replaceAll("[^A-Za-z0-9]", ""));
                    cp = Long.parseLong(charSequence.toString());
                    p = (pr + pr*cp/100);
                    DecimalFormat decim = new DecimalFormat("#,###.##");
                    String stringValue = decim.format(p);
                    soldPriceTextView.setText(( stringValue+" so'm"));
                }else{ soldPriceTextView.setText("0 so'm");}
            }
            @Override public void afterTextChanged(Editable editable) { }
        });
        soldPriceInput.addTextChangedListener(new TextWatcher() {
            long sp=0,  tp=0, pr = 0, thp=0, sip=0, nip=0, twp=0;

            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>0 && priceInput.getText().length()>0){

                    pr = Long.parseLong(String.valueOf(priceInput.getText()).trim().replaceAll("[^A-Za-z0-9]", ""));
                    sp = Long.parseLong(charSequence.toString());

                    tp = (pr + pr*sp/100);
                    thp = (tp+tp*9/100)/3;
                    sip = (tp+tp*18/100)/6;
                    nip = (tp+tp*27/100)/9;
                    twp = (tp+tp*36/100)/12;
                    DecimalFormat decim = new DecimalFormat("#,###.##");
                    creditPrice.setText(String.format("%s so'm", decim.format(tp)));
                    threePrice.setText(String.format("%s so'm", decim.format(thp)));
                    sixPrice.setText(String.format("%s so'm", decim.format(sip)));
                    ninePrice.setText(String.format("%s so'm", decim.format(nip)));
                    twelvePrice.setText(String.format("%s so'm", decim.format(twp)));
                }else{
                    creditPrice.setText("0 so'm");
                    threePrice.setText("0 so'm");
                    sixPrice.setText("0 so'm");
                    ninePrice.setText("0 so'm");
                    twelvePrice.setText("0 so'm");
                }

            }
            @Override public void afterTextChanged(Editable editable) { }
        });



        addSpec.setOnClickListener(this);
        valueSub = new ArrayList<>();
        valueBrand = new ArrayList<>();
        spec = new ArrayList<>();

        productSubmit.setOnClickListener(this);
        productReset.setOnClickListener(view -> clearAllTheFields());
//        Cursor subCursor = myDB.readAllSubcategories();
//        if(subCursor.getCount()==0){
//            Toast.makeText(requireContext(), "No Data", Toast.LENGTH_SHORT).show();
//        }
//        else{
//            while (subCursor.moveToNext()){
//                System.out.println(subCursor.getString(1));
//                valueSub.add(subCursor.getString(1));
//            }
//            adapterSub = new ArrayAdapter<>(requireContext(),
//                        R.layout.admin_product_fragment_sub_category_drop_down,
//                        valueSub
//                );
//            subCategoriesAuto.setAdapter(adapterSub);
//        }
//        Cursor brandCursor = myDB.readAllBrands();
//        if(brandCursor.getCount()==0){
//            Toast.makeText(requireContext(), "No Data", Toast.LENGTH_SHORT).show();
//        }
//        else{
//            while (brandCursor.moveToNext()){
//                System.out.println(brandCursor.getString(1));
//                valueBrand.add(brandCursor.getString(1));
//            }
//            adapterBrand = new ArrayAdapter<>(requireContext(),
//                    R.layout.admin_product_fragment_sub_category_drop_down,
//                    valueBrand
//            );
//            brandAuto.setAdapter(adapterBrand);
//        }
        firestore.collection("SubCategories").addSnapshotListener((value, error) -> {
            assert value != null;
            if (!value.isEmpty()){
                List<DocumentSnapshot> list = value.getDocuments();
                valueSub.clear();
                for (DocumentSnapshot d : list) {
                    AdminSubCategoryModel dataModal = d.toObject(AdminSubCategoryModel.class);
                    assert dataModal != null;
                    valueSub.add(dataModal.getSubcategoryName());
                }
                adapterSub = new ArrayAdapter<>(requireContext(),
                        R.layout.admin_product_fragment_sub_category_drop_down,
                        valueSub
                );
                subCategoriesAuto.setAdapter(adapterSub);
            }
        });
        firestore.collection("brands").addSnapshotListener((value, error) -> {
            assert value != null;
            if (!value.isEmpty()){
                List<DocumentSnapshot> list = value.getDocuments();
                valueBrand.clear();
                for (DocumentSnapshot d : list) {
                    AdminBrandModel dataModal = d.toObject(AdminBrandModel.class);
                    assert dataModal != null;
                    valueBrand.add(dataModal.getBrandName());
                }
                adapterBrand = new ArrayAdapter<>(getContext(),
                        R.layout.admin_product_fragment_sub_category_drop_down,
                        valueBrand
                );
                brandAuto.setAdapter(adapterBrand);
            }
        });

        choseImage.setOnClickListener(view -> {
            wayToImageLoad();
        });
        return v;
    }




    private void wayToImageLoad() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (mArrayUri != null) {
            mArrayUri.clear();
        }
        someActivityResultLauncher.launch(intent);
    }
    private void clearAllTheFields() {
        subCategoriesAuto.setText("");
        subCategoriesAuto.clearFocus();
        brandAuto.setText("");
        brandAuto.clearFocus();
        productName.setText("");
        productName.clearFocus();
        prodModel.setText("");
        prodModel.clearFocus();
        priceInput.setText("");
        soldPriceInput.setText("");
        cashPriceInput.setText("");
        priceInput.clearFocus();
        soldPriceInput.clearFocus();
        cashPriceInput.clearFocus();
        productQuantity.setText("");
        productQuantity.clearFocus();
        linearLayout.removeAllViews();
        linearLayout.clearFocus();
        description.setText("");
        description.clearFocus();
        mArrayUri.clear();
        UrlsList.clear();
        productImage.clearFocus();
        productImage.removeAllViews();
        if (mArrayUri != null && imageSliderAdapter != null) {
            mArrayUri.clear();
            imageSliderAdapter.notifyDataSetChanged();
        }
    }
    private void settingAdapter() {
        imageSliderAdapter = new ImageSliderAdapter(mArrayUri, getContext());
        productImage.setSliderAdapter(imageSliderAdapter);
        productImage.setIndicatorAnimation(IndicatorAnimationType.WORM);
        productImage.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        productImage.startAutoCycle();
        imageSliderAdapter.notifyDataSetChanged();
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

                    settingAdapter();
                }
            });

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.extendedFloatingActionButton:
                addView();
                break;
            case R.id.productSubmit:
                 subCategoryText = subCategoriesAuto.getText().toString().trim();
                 brandText = brandAuto.getText().toString().trim();
                 name = productName.getText().toString().trim();
                 soldPriceText = soldPriceInput.getText().toString().trim();
                 cashPriceText = cashPriceInput.getText().toString().trim();
                 priceText = priceInput.getText().toString().trim();
                 quantityText = productQuantity.getText().toString().trim();
                 descriptionText = description.getText().toString().trim();

                if  (brandText.isEmpty()){
                    new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                            .setContentText("Brandini tanlang")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(SweetAlertDialog::cancel)
                            .show();
                }
                else if (subCategoryText.isEmpty()) {
                    new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                            .setContentText("Toifasini tanlang")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(SweetAlertDialog::cancel)
                            .show();
                }
                else if (name.isEmpty()) {
                    new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                            .setContentText("Nomini kiriting")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(SweetAlertDialog::cancel)
                            .show();
                }
                else if (priceText.isEmpty()){
                    new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                            .setContentText("Narxini kiriting")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(SweetAlertDialog::cancel)
                            .show();
                }
                else if (soldPriceText.isEmpty()){
                    new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                            .setContentText("Kredit ustama foizini kiriting")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(SweetAlertDialog::cancel)
                            .show();
                }
                else if (soldPriceText.length()>3){
                    new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                            .setContentText("Ustama foizi juda yuqori")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(SweetAlertDialog::cancel)
                            .show();
                }
                else if (cashPriceText.isEmpty()){
                    new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                            .setContentText("Naqd pul ustama foizini kiriting")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(SweetAlertDialog::cancel)
                            .show();
                }
                else if (cashPriceText.length()>3){
                    new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                            .setContentText("Ustama foizi juda yuqori")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(SweetAlertDialog::cancel)
                            .show();
                }
                else if (quantityText.isEmpty()){
                    new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                            .setContentText("Miqdorini kiriting")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(SweetAlertDialog::cancel)
                            .show();
                }
                else if (!checkValidation()){
                    new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                            .setContentText("Xususiyatini kiriting")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(SweetAlertDialog::cancel)
                            .show();
                }
                else if (descriptionText.isEmpty()){
                    new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                            .setContentText("Tasnifini kiriting")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(SweetAlertDialog::cancel)
                            .show();
                }
                else if(mArrayUri.size()==0){
                    new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                            .setContentText("Mahsulot uchun rasm tanlang")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(SweetAlertDialog::cancel)
                            .show();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    UploadIMages(subCategoriesAuto.getText().toString().trim());
                    mArrayUri.clear();
                }
                break;
        }
    }

    private void UploadIMages(String subCategoryText) {
        int ii=mArrayUri.size();
        for (int i = 0; i < mArrayUri.size(); i++) {

            System.out.println(mArrayUri);
            Bitmap bmp = null;
            Uri IndividualImage = mArrayUri.get(i);
            try {
                bmp = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), IndividualImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream  baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] fileInBytes = baos.toByteArray();

            if (IndividualImage != null && !TextUtils.isEmpty(name)) {
                StorageReference subRef = sRef.child("ProductPic/" + name + i);
                UploadTask uploadTask = subRef.putBytes(fileInBytes);

                int finalIi = ii;
                uploadTask.addOnSuccessListener(taskSnapshot -> subRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    UrlsList.add(String.valueOf(uri));
                    System.out.println(finalIi);
                    System.out.println(UrlsList.size());
                    if (UrlsList.size() == finalIi) {
                        System.out.println(true);
                        StoreLinks(UrlsList);
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
    private void StoreLinks(ArrayList<String> urlsList) {
        System.out.println("urlsList"+urlsList);
        String subCategoryText = subCategoriesAuto.getText().toString().trim();
        String name = productName.getText().toString().trim();
        String brand = brandAuto.getText().toString().trim();
        String priceText = priceInput.getText().toString().trim().replaceAll("[^A-Za-z0-9]", "");
        String soldPriceText = soldPriceInput.getText().toString().trim().replaceAll("[^A-Za-z0-9]", "");
        String cashPriceText = cashPriceInput.getText().toString().trim().replaceAll("[^A-Za-z0-9]", "");
        String quantityText = productQuantity.getText().toString().trim();
        String descriptionText = description.getText().toString().trim();
        String prodModelText = prodModel.getText().toString().trim();



        firestore.collection("SubCategories").whereEqualTo("subcategoryName",subCategoryText).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(!queryDocumentSnapshots.isEmpty()){
                        for(DocumentSnapshot d:queryDocumentSnapshots){
                            if(d.exists()){
                                int price = Integer.parseInt(priceText);
                                int soldPrice = Integer.parseInt(soldPriceText);
                                int cashPrice = Integer.parseInt(cashPriceText);
                                int originalSoldPrice = price + price*cashPrice/100;
                                int originalCreditPrice = price + price*soldPrice/100;
                                int quantity = Integer.parseInt(quantityText);
                                HashMap<String,Long> creditPayment = new HashMap<>();
                                creditPayment.put("3 oylik", (long) ((originalCreditPrice+originalCreditPrice* 9L /100)/3));
                                creditPayment.put("6 oylik", (long) ((originalCreditPrice+originalCreditPrice* 18L /100)/6));
                                creditPayment.put("9 oylik", (long) ((originalCreditPrice+originalCreditPrice* 27L /100)/9));
                                creditPayment.put("12 oylik", (long) ((originalCreditPrice+originalCreditPrice* 36L /100)/12));
                                boolean visibility = false;
                                firestore.collection("brands").whereEqualTo("brandName", brand).get()
                                        .addOnSuccessListener(queryDocumentSnapshots1 -> {
                                            if(!queryDocumentSnapshots1.isEmpty()){
                                                for (DocumentSnapshot dd:queryDocumentSnapshots1){
                                                    if (dd.exists()){
                                                        DocumentReference prodDocRef = firestore.collection("products").document();
                                                        AdminProductModel productModel = new AdminProductModel(dd.getId(), d.get("categoryId").toString(), d.getId(), name,
                                                                prodModelText,  prodDocRef.getId(),descriptionText,urlsList, price, cashPrice,
                                                                originalSoldPrice, soldPrice, originalCreditPrice, quantity, creditPayment, spec, visibility);

//        Adding Local Database
                                                        myDB = new MyDataBaseHelper(requireActivity());
                                                        myDB.addNewProduct(prodDocRef.getId(), name, originalSoldPrice);


                                                        prodDocRef.set(productModel).addOnSuccessListener(unused -> {
                                                                    progressBar.setVisibility(View.GONE);
                                                                    d.getReference().update("products", FieldValue.arrayUnion(productModel.getProductId()))
                                                                            .addOnSuccessListener(unused1 -> {
                                                                                clearAllTheFields();
                                                                                urlsList.clear();
                                                                                new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                                                        .setContentText("Yangi mahsulot muvafaqqiyatli qo'shildi!")
                                                                                        .setConfirmText("OK!")
                                                                                        .setConfirmClickListener(SweetAlertDialog::cancel)
                                                                                        .show();


                                                                                progressBar.setVisibility(View.GONE);

                                                                            });
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
                                                }
                                            }
                                        });
                            }
                            else{
                                progressBar.setVisibility(View.GONE);
                                new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setContentText("Bunday document mavjud emas")
                                        .setConfirmText("OK!")
                                        .setConfirmClickListener(SweetAlertDialog::cancel)
                                        .show();
                            }
                        }
                    }
                    else {
                        progressBar.setVisibility(View.GONE);
                        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                .setContentText("So'rov bo'yicha hech narsa topilmadi")
                                .setConfirmText("OK!")
                                .setConfirmClickListener(SweetAlertDialog::cancel)
                                .show();
                    }

                }).addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Jarayonda xatolik")
                            .setConfirmText("OK!")
                            .setConfirmClickListener(SweetAlertDialog::cancel)
                            .show();
                });


    }
    private boolean checkValidation() {
        spec.clear();
        boolean result = true;

        for (int i = 0; i<linearLayout.getChildCount(); i++){

            View specView = linearLayout.getChildAt(i);

            EditText specName = specView.findViewById(R.id.key);
            EditText specValue = specView.findViewById(R.id.value);

            AdminProductSpecificationsModel specModel = new AdminProductSpecificationsModel();

            if (!specName.getText().toString().equals("")){
                specModel.setName(specName.getText().toString().trim());
            }
            else {
                result = false;
                break;
            }

            if (!specValue.getText().toString().equals("")){
                specModel.setField(specValue.getText().toString().trim());
            }
            else {
                result = false;
                break;
            }

            spec.add(specModel);

        }

        if (spec.size() == 0){
            result = false;
            Toast.makeText(getContext(), "Xususiyat nomini kiriting", Toast.LENGTH_SHORT).show();
        }else if (!result){
            Toast.makeText(getContext(), "Xususiyat qiymatini kiriting", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    private void addView() {
        View view1 = getLayoutInflater().inflate(R.layout.specification, null, false);

        ImageView delete = view1.findViewById(R.id.image_remove);

        delete.setOnClickListener(view -> removeView(view1));

        linearLayout.addView(view1);
    }

    private void removeView(View view) {
        linearLayout.removeView(view);
    }

}