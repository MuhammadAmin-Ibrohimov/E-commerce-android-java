package com.example.lidertrade.admin.adapters;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.lidertrade.R;
import com.example.lidertrade.admin.helpers.MyDataBaseHelper;
import com.example.lidertrade.admin.models.AdminBrandModel;
import com.example.lidertrade.admin.models.AdminProductModel;
import com.example.lidertrade.admin.models.AdminProductModelImage;
import com.example.lidertrade.admin.models.AdminProductSpecificationsModel;
import com.example.lidertrade.admin.models.AdminSubCategoryModel;
import com.example.lidertrade.deliverer.helpers.NumberTextWatcherForThousand;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminProductFragmentProductAdapter extends RecyclerView.Adapter<AdminProductFragmentProductAdapter.MyViewHolder> {

    Context context;
    ArrayAdapter<String> adapterBrand, adapterSub;

    ArrayList<AdminProductModelImage> adminProductModelImage;
    ArrayList<AdminProductSpecificationsModel> adminProductModelSpec, spec;
    ArrayList<AdminProductModel> subModelArrayList;
    AdminProductFragmentProductAdapter.OnItemClickListener listener;
    OnItemClick onItemClick;
    OnItemClick2 onItemClick2;
    OnItemClick3 onItemClick3;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }
    public void setOnItemClick2(OnItemClick2 onItemClick2) {
        this.onItemClick2 = onItemClick2;
    }
    public void setOnItemClick3(OnItemClick3 onItemClick3) {
        this.onItemClick3 = onItemClick3;
    }

    public interface OnItemClick {
        void getPosition(String data); //pass any things
    }
    public interface OnItemClick2 {
        void getPosition(String data); //pass any things
    }
    public interface OnItemClick3 {
        void getPosition(String data, LinearLayout linearLayout); //pass any things
    }

    public void setFilteredList(ArrayList<AdminProductModel> mFilteredList) {
        this.subModelArrayList = mFilteredList;
    }

    public interface OnItemClickListener {
        void onDeleteClick(String url);
    }

    public void setOnItemClickListener (AdminProductFragmentProductAdapter.OnItemClickListener mListener){
        listener = mListener;
    }

    public AdminProductFragmentProductAdapter(Context context, ArrayList<AdminProductModel> subModelArrayList) {
        this.context = context;
        this.subModelArrayList = subModelArrayList;
    }

    @NonNull
    @Override
    public AdminProductFragmentProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_product_fragment_product_item, parent, false);
        AdminProductFragmentProductAdapter.MyViewHolder viewHolder = new AdminProductFragmentProductAdapter.MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminProductFragmentProductAdapter.MyViewHolder holder, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        AdminProductModel subCategoryModel = subModelArrayList.get(position);

        holder.productName.setText(subCategoryModel.getProductName());
        db.collection("SubCategories").document(subCategoryModel.getSubCategory()).get()
                        .addOnSuccessListener(d -> {
                            if(d.exists()){
                                holder.subCategoryName.setText(Objects.requireNonNull(d.get("subcategoryName")).toString());
                            }
                        });
        holder.subCategoryName.setText(subCategoryModel.getSubCategory());
//        List<SlideModel> slideModels = new ArrayList<>();
//        for (String i :subCategoryModel.getImageUrl()){
//            slideModels.add(new SlideModel(i, ScaleTypes.CENTER_INSIDE));
//        }
//        holder.productImage.setImageList(Collections.singletonList(slideModels.get(0)), ScaleTypes.CENTER_INSIDE);

            ArrayList<String> driverPermissions = subCategoryModel.getImageUrl();
        if(driverPermissions!=null || driverPermissions.size()!=0){
            String imageUrl = driverPermissions.get(0);
            Glide.with(context)
                    .load(Uri.parse(imageUrl))
                    .into(holder.productImage);
        }


        int pos = holder.getAdapterPosition();
        String id = subModelArrayList.get(pos).getProductId();

        holder.productListDelete.setOnClickListener(view -> {
            if (listener != null){
                if (pos != RecyclerView.NO_POSITION){
                    listener.onDeleteClick(id);
                }
            }
        });
        holder.productListUpdate.setOnClickListener(view -> updateProductInfo( subCategoryModel, holder));

    }


    @Override
    public int getItemCount() {
        return subModelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView productName, subCategoryName;
        ImageView productImage;
        CardView productListDelete, productListUpdate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            subCategoryName = itemView.findViewById(R.id.subCategoryName);
            productImage = itemView.findViewById(R.id.productImage);
            productListDelete = itemView.findViewById(R.id.productListDelete);
            productListUpdate = itemView.findViewById(R.id.productListUpdate);
        }
    }


    private void updateProductInfo(AdminProductModel modal, MyViewHolder holder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.admin_product_fragment_product_list_dialog);
        ArrayList<String> valueSub = new ArrayList<>();
        ArrayList<String> valueBrand = new ArrayList<>();
        spec = new ArrayList<>();
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

        //Initializing the views of the dialog.
//        Image Slider
        ImageSlider productImage = dialog.findViewById(R.id.productImage);
        ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        for(String sm:modal.getImageUrl()){
            slideModels.add(new SlideModel(sm, ScaleTypes.CENTER_INSIDE));
        }
        productImage.setImageList(slideModels, ScaleTypes.CENTER_INSIDE);

//        Brands List
        final AutoCompleteTextView autoCompleteBrand = dialog.findViewById(R.id.autoCompleteBrand);
        autoCompleteBrand.setText(modal.getBrand());
//        Subcategories List
        final AutoCompleteTextView autoCompleteSub = dialog.findViewById(R.id.autoCompleteSub);
        autoCompleteSub.setText(modal.getSubCategory());
//        Image select button
        final CardView imageSelect = dialog.findViewById(R.id.imageSelect);
        imageSelect.setOnClickListener(view -> {
            onItemClick.getPosition(modal.getProductId());
        });
//        Image confirm button
        final CardView imageConfirm = dialog.findViewById(R.id.imageConfirm);
        imageConfirm.setOnClickListener(view -> {
            onItemClick2.getPosition(modal.getProductId());
        });
//Subcategories for autocomplete
        db.collection("SubCategories").addSnapshotListener((value, error) -> {
            assert value != null;
            if (!value.isEmpty()){
                List<DocumentSnapshot> list = value.getDocuments();
                valueSub.clear();
                for (DocumentSnapshot d : list) {
                    AdminSubCategoryModel dataModal = d.toObject(AdminSubCategoryModel.class);
                    assert dataModal != null;
                    valueSub.add(dataModal.getSubcategoryName());
                }
                adapterSub = new ArrayAdapter<>(context,
                        R.layout.admin_product_fragment_sub_category_drop_down,
                        valueSub
                );
                autoCompleteSub.setAdapter(adapterSub);
            }
        });
//Brands for autocomplete
        db.collection("brands").addSnapshotListener((value, error) -> {
            assert value != null;
            if (!value.isEmpty()){
                List<DocumentSnapshot> list = value.getDocuments();
                valueBrand.clear();
                for (DocumentSnapshot d : list) {
                    AdminBrandModel dataModal = d.toObject(AdminBrandModel.class);
                    assert dataModal != null;
                    valueBrand.add(dataModal.getBrandName());
                }
                adapterBrand = new ArrayAdapter<>(context,
                        R.layout.admin_product_fragment_sub_category_drop_down,
                        valueBrand
                );
                autoCompleteBrand.setAdapter(adapterBrand);
            }
        });
//Product Name
        final EditText productName = dialog.findViewById(R.id.productName);
        productName.setText(modal.getProductName());
//        Product Model
        final EditText productModel = dialog.findViewById(R.id.productModel);
        productModel.setText(modal.getProductModel());
//        Inputted Price
        final EditText priceInput = dialog.findViewById(R.id.priceInput);
        priceInput.setText(String.valueOf(modal.getBoughtPrice()));
        priceInput.addTextChangedListener(new NumberTextWatcherForThousand(priceInput));
//        Product Quantity
        final EditText productQuantity = dialog.findViewById(R.id.productQuantity);
        productQuantity.setText(String.valueOf(modal.getProductQuantity()));
//Credit price percent
        final EditText soldPriceInput = dialog.findViewById(R.id.soldPriceInput);
        soldPriceInput.setText(String.valueOf(modal.getCreditPricePercent()));
//        Cash PRice percent
        final EditText cashPriceInput = dialog.findViewById(R.id.cashPriceInput);
        cashPriceInput.setText(String.valueOf(modal.getCashPricePercent()));
//        Product Description
        final EditText description = dialog.findViewById(R.id.description);
        description.setText(modal.getProductDescription());
//        Adding product Specification
        final ExtendedFloatingActionButton extendedFloatingActionButton = dialog.findViewById(R.id.extendedFloatingActionButton);
        LinearLayout linearLayout = dialog.findViewById(R.id.linearLayout);
        extendedFloatingActionButton.setOnClickListener(view -> {
            View view1 = LayoutInflater.from(context).inflate(R.layout.specification, linearLayout, false);
            ImageView delete = view1.findViewById(R.id.image_remove);
            delete.setOnClickListener(view2 -> removeView(view1, linearLayout));
            linearLayout.addView(view1);
        });

//------------------------------ Adapter for ImageURL ----------------------------//
        RecyclerView productImageRecyclerView = dialog.findViewById(R.id.productImageRecyclerView);
        LinearLayoutManager layoutManager  = new LinearLayoutManager( productImageRecyclerView.getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setInitialPrefetchItemCount(modal.getImageUrl().size());
        adminProductModelImage = new ArrayList<>();
        AdminProductFragmentProductAdapterImage childItemAdapter = new AdminProductFragmentProductAdapterImage(context, adminProductModelImage);
        productImageRecyclerView.setLayoutManager(layoutManager);
        productImageRecyclerView.setAdapter(childItemAdapter);
        productImageRecyclerView.setRecycledViewPool(viewPool);
        childItemAdapter.setOnItemClickListener(url -> new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setContentText("Ushbu rasmni o'chirib yubormoqchimisiz!")
                .setCancelText("Yo'q")
                .setConfirmText("Ha")
                .showCancelButton(true)
                .setCancelClickListener(SweetAlertDialog::cancel)
                .setConfirmClickListener(sweetAlertDialog1 -> {
                    try{
                        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(Objects.requireNonNull(url));
                        photoRef.delete()
                                .addOnSuccessListener(unused -> {
                                    new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                            .setContentText("Muvafaqqiyatli o'chirildi!")
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
                    }
                    catch (IllegalArgumentException error){

                    }
                    db.collection("products").document(modal.getProductId()).update("imageUrl",
                            FieldValue.arrayRemove(url));
                    sweetAlertDialog1.cancel();
                })
                .show());
// Adapter for SpecMODELS
        RecyclerView specListRecyclerView = dialog.findViewById(R.id.specListRecyclerView);
        LinearLayoutManager layoutManager2  = new LinearLayoutManager( specListRecyclerView.getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager2.setInitialPrefetchItemCount(modal.getProductSpecification().size());
        adminProductModelSpec = new ArrayList<>();
        AdminProductFragmentProductAdapterSpec childItemAdapter2 = new AdminProductFragmentProductAdapterSpec(context, adminProductModelSpec);
        specListRecyclerView.setLayoutManager(layoutManager2);
        specListRecyclerView.setAdapter(childItemAdapter2);
        specListRecyclerView.setRecycledViewPool(viewPool);
        childItemAdapter2.setOnItemClickListener(url ->
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setContentText("Ushbu xususiyatni o'chirib yubormoqchimisiz!")
                    .setCancelText("Yo'q")
                    .setConfirmText("Ha")
                    .showCancelButton(true)
                    .setCancelClickListener(SweetAlertDialog::cancel)
                    .setConfirmClickListener(sweetAlertDialog1 -> {
                        for (AdminProductSpecificationsModel s:modal.getProductSpecification()){
                            if (Objects.equals(s.getField(), url)){
                                db.collection("products").document(modal.getProductId()).update("productSpecification",
                                                FieldValue.arrayRemove(s))
                                        .addOnSuccessListener(unused ->
                                                new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                                        .setContentText("Muvafaqqiyatli o'chirildi!")
                                                        .setConfirmText("OK!")
                                                        .setConfirmClickListener(SweetAlertDialog::cancel)
                                                        .show())
                                        .addOnFailureListener(e -> {
                                            new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                                    .setContentText("Jarayonda xatolik!")
                                                    .setConfirmText("OK!")
                                                    .setConfirmClickListener(SweetAlertDialog::cancel)
                                                    .show();
                                        });
                            }
                        }
                        sweetAlertDialog1.cancel();
                    })
                    .show());
        getImageAndSpecData(db, modal, childItemAdapter,childItemAdapter2);
        Long priceInputLong = Long.parseLong(String.valueOf(priceInput.getText()).trim().replaceAll("[^A-Za-z0-9]", ""));
        Long cashPPInputLong = Long.parseLong(String.valueOf(cashPriceInput.getText()).trim().replaceAll("[^A-Za-z0-9]", ""));
        Long creditPPInputLong = Long.parseLong(String.valueOf(soldPriceInput.getText()).trim().replaceAll("[^A-Za-z0-9]", ""));
        final TextView soldPriceTextView = dialog.findViewById(R.id.cashPriceTextView);
        final TextView creditPrice = dialog.findViewById(R.id.creditPriceTextView);
        final TextView threePrice = dialog.findViewById(R.id.threePrice);
        final TextView sixPrice = dialog.findViewById(R.id.sixPrice);
        final TextView ninePrice = dialog.findViewById(R.id.ninePrice);
        final TextView twelvePrice = dialog.findViewById(R.id.twelvePrice);
        long xxxx = Long.parseLong(priceInput.getText().toString());
        long yyyy = Long.parseLong(cashPriceInput.getText().toString());
        long zzzz = Long.parseLong(soldPriceInput.getText().toString());
        soldPriceTextView.setText(String.valueOf(xxxx+xxxx*yyyy/100));
        creditPrice.setText(String.valueOf(xxxx+xxxx*zzzz/100));
        Map<String,Long> maps =  (modal.getCreditPayment());
        for (Map.Entry<String,Long> map:maps.entrySet()){
            if(Objects.equals(map.getKey(), "12 oylik")){
                twelvePrice.setText(map.getValue().toString()+" so'm");
            }
            else if(Objects.equals(map.getKey(), "9 oylik")){
                ninePrice.setText(map.getValue().toString()+" so'm");
            }
            else if(Objects.equals(map.getKey(), "6 oylik")){
                sixPrice.setText(map.getValue().toString()+" so'm");
            }
            else if(Objects.equals(map.getKey(), "3 oylik")){
                threePrice.setText(map.getValue().toString()+" so'm");
            }
        }

        final String[] aComBr = new String[1];
        final String[] aComSub = new String[1];
        final String[] pruNa = new String[1];
        final String[] pruMo = new String[1];
        final String[] pruDesc = new String[1];
        final int[] pruQu = new int[1];
        final long[] bPI = new long[1];
        final int[] caPPI = new int[1];
        final int[] crPPI = new int[1];
        autoCompleteBrand.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()!=0){
                    aComBr[0] = charSequence.toString();
                }
            }
            @Override public void afterTextChanged(Editable editable) { }
        });
        autoCompleteSub.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()!=0){
                    aComSub[0] = charSequence.toString();}
                }
            @Override public void afterTextChanged(Editable editable) { }
        });
        productName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()!=0){
                    pruNa[0] = charSequence.toString();}
            }
            @Override public void afterTextChanged(Editable editable) { }
        });
        productModel.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()!=0){
                    pruMo[0] = charSequence.toString();}
            }
            @Override public void afterTextChanged(Editable editable) { }
        });
        description.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()!=0){
                    pruDesc[0] = charSequence.toString();}
            }
            @Override public void afterTextChanged(Editable editable) { }
        });


        productQuantity.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()!=0){
                    pruQu[0] = Integer.parseInt(charSequence.toString());
                }
            }
            @Override public void afterTextChanged(Editable editable) { }
        });
        priceInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()!=0){
                    long pr = Long.parseLong(charSequence.toString().trim().replaceAll("[^A-Za-z0-9]", ""));
                    DecimalFormat decim = new DecimalFormat("#,###.##");
                    if (cashPPInputLong != 0){
                        long s = pr+pr*cashPPInputLong/100;
                        soldPriceTextView.setText(String.format("%s so'm", decim.format(s)));
                    }
                    if(creditPPInputLong!=0){
                        long crePr = pr+creditPPInputLong*pr/100;
                        long thp = crePr+crePr*9/100;
                        long sip = crePr+crePr*18/100;
                        long nip = crePr+crePr*27/100;
                        long twp = crePr+crePr*36/100;
                        threePrice.setText(String.format("%s so'm", decim.format(thp)));
                        sixPrice.setText(String.format("%s so'm", decim.format(sip)));
                        ninePrice.setText(String.format("%s so'm", decim.format(nip)));
                        twelvePrice.setText(String.format("%s so'm", decim.format(twp)));
                        creditPrice.setText(String.format("%s so'm", decim.format(crePr)));
                    }
                    bPI[0] = Long.parseLong(charSequence.toString().trim().replaceAll("[^A-Za-z0-9]", ""));}
                }
            @Override public void afterTextChanged(Editable editable) { }
        });
        cashPriceInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()!=0){
                    DecimalFormat decim = new DecimalFormat("#,###.##");
                    if(bPI[0]==0 && priceInputLong != 0){
                        long s = priceInputLong + priceInputLong*Long.parseLong(charSequence.toString())/100;
                        soldPriceTextView.setText(String.format("%s so'm", decim.format(s)));
                    } else if (bPI[0]!=0) {
                        long s = bPI[0] + bPI[0]*Long.parseLong(charSequence.toString())/100;
                        soldPriceTextView.setText(String.format("%s so'm", decim.format(s)));
                    }
                    caPPI[0] = Integer.parseInt(charSequence.toString());}
            }
            @Override public void afterTextChanged(Editable editable) { }
        });
        soldPriceInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()!=0){

                    if(bPI[0]==0 && priceInputLong != 0){
                        Long crePr = priceInputLong+priceInputLong*Long.parseLong(charSequence.toString())/100;
                        long thp = crePr+crePr*9/100;
                        long sip = crePr+crePr*18/100;
                        long nip = crePr+crePr*27/100;
                        long twp = crePr+crePr*36/100;
                        DecimalFormat decim = new DecimalFormat("#,###.##");
                        threePrice.setText(String.format("%s so'm", decim.format(thp)));
                        sixPrice.setText(String.format("%s so'm", decim.format(sip)));
                        ninePrice.setText(String.format("%s so'm", decim.format(nip)));
                        twelvePrice.setText(String.format("%s so'm", decim.format(twp)));
                        creditPrice.setText(String.format("%s so'm", decim.format(crePr)));
                    } else if (bPI[0]!=0) {
                        Long crePr = bPI[0]+bPI[0]*Long.parseLong(charSequence.toString())/100;
                        long thp = crePr+crePr*9/100;
                        long sip = crePr+crePr*18/100;
                        long nip = crePr+crePr*27/100;
                        long twp = crePr+crePr*36/100;
                        DecimalFormat decim = new DecimalFormat("#,###.##");
                        threePrice.setText(String.format("%s so'm", decim.format(thp)));
                        sixPrice.setText(String.format("%s so'm", decim.format(sip)));
                        ninePrice.setText(String.format("%s so'm", decim.format(nip)));
                        twelvePrice.setText(String.format("%s so'm", decim.format(twp)));
                        creditPrice.setText(String.format("%s so'm", decim.format(crePr)));
                    }
                    crPPI[0] = Integer.parseInt(charSequence.toString());
                }
            }
            @Override public void afterTextChanged(Editable editable) { }
        });

        Button productReset = dialog.findViewById(R.id.productReset);
        productReset.setOnClickListener(view -> dialog.dismiss());

        Button productSubmit = dialog.findViewById(R.id.productSubmit);
        productSubmit.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);

            if(productModel.length()==0 || productName.length()==0 || productQuantity.length()==0
            || priceInput.length()==0 ||soldPriceInput.length()==0 || cashPriceInput.length()==0 || description.length()==0){
                new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Avval barcha maydonlarni to'ldiring!")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(SweetAlertDialog::cancel)
                        .show();
            }
            else if (!checkValidation(linearLayout)){
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setContentText("Xususiyatini kiriting")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(SweetAlertDialog::cancel)
                        .show();
                progressBar.setVisibility(View.GONE);
            }
            else{
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setContentText("Qilingan o'zgarishlarni tasdiqlaysizmi!")
                        .setCancelText("Yo'q")
                        .setConfirmText("Ha")
                        .showCancelButton(true)
                        .setCancelClickListener(SweetAlertDialog::cancel)
                        .setConfirmClickListener(sweetAlertDialog1 -> {

                            String prB = modal.getBrand(), prS = modal.getSubCategory(), prNa = modal.getProductName(),
                                    prMo = modal.getProductModel(), prDe = modal.getProductDescription();

                            long gPQ = modal.getProductQuantity(), gBP = modal.getBoughtPrice(), gCPP = modal.getCreditPricePercent(), gCP = modal.getCreditPrice(),
                            gCaPP = modal.getCashPricePercent(), gCaP = modal.getCashPrice();
                            Map<String, Long> crPa = modal.getCreditPayment();
                            if (aComBr[0]==null && aComSub[0]==null && pruNa[0]==null && spec.size()==0  &&  pruMo[0]==null && pruDesc[0]==null &&
                                    pruQu[0]==0 && bPI[0]==0 && caPPI[0]==0 && crPPI[0]==0 ){
                                progressBar.setVisibility(View.GONE);
                                new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                                        .setContentText("O'zgartirish kiritilmadi!")
                                        .setConfirmText("OK!")
                                        .setConfirmClickListener(SweetAlertDialog::cancel)
                                        .show();
                                dialog.dismiss();
                                progressBar.setVisibility(View.GONE);
                            }
                            else {

                                if (!Objects.equals(aComBr[0], modal.getBrand()) && aComBr[0] != null) {
                                    prB = (aComBr[0]);
                                }
                                if ((!Objects.equals(aComSub[0], modal.getSubCategory())) && aComSub[0] != null) {
                                    prS = (aComSub[0]);
                                }
                                if (!Objects.equals(pruNa[0], modal.getProductName()) && pruNa[0] != null) {
                                    prNa = (pruNa[0]);
                                }
                                if ((!Objects.equals(pruMo[0], modal.getProductModel())) && pruMo[0] != null) {
                                    prMo = (pruMo[0]);
                                }
                                if (!Objects.equals(pruDesc[0], modal.getProductDescription()) && pruDesc[0] != null) {
                                    prDe = (pruDesc[0]);
                                }

                                if ((!Objects.equals(pruQu[0], modal.getProductQuantity())) && pruQu[0] != 0) {
                                    gPQ = (pruQu[0]);
                                }
                                if ((!Objects.equals(bPI[0], modal.getBoughtPrice())) && bPI[0] != 0) {
                                    gBP = (bPI[0]);
                                }
                                if ((!Objects.equals(crPPI[0], modal.getCreditPricePercent())) && crPPI[0] != 0) {
                                    gCPP = (crPPI[0]);
                                }
                                if ((!Objects.equals(crPPI[0], modal.getCashPricePercent())) && caPPI[0] != 0) {
                                    gCaPP = (caPPI[0]);
                                }
                                if(linearLayout.getChildCount()!=0)
                                    db.collection("products").document(modal.getProductId())
                                            .update("productSpecification", spec);
                                }
                                gCP = gBP + gBP*gCPP/100;
                                gCaP = gBP + gBP*gCaPP/100;
                                crPa.put("3 oylik", (gCP+gCP*9/100)/3);
                                crPa.put("6 oylik", (gCP+gCP*18/100)/6);
                                crPa.put("9 oylik", (gCP+gCP*27/100)/9);
                                crPa.put("12 oylik", (gCP+gCP*36/100)/12);

                                Query query = db.collection("products").whereEqualTo("subcategoryName", prNa);
                                AggregateQuery countQuery = query.count();
                                String finalProdBrand = prB;
                                String finalProdSubCategory = prS;
                                String finalProdName = prNa;
                                String finalProdModel = prB;
                                String finalProdDesc = prDe;

                                long finalProdQuantity = gPQ;
                                long finalProdBoughtPrice = gBP;
                                long finalProdCashPrice = gCaP;
                                long finalProdCashPricePercent = gCaPP;
                                long finalProdCreditPrice = gCP;
                                long finalProdCreditPricePercent = gCPP;
                                final String[] subIDs = new String[1];
                                countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    AggregateQuerySnapshot snapshot = task.getResult();
                                    if(snapshot.getCount()<=1){
                                        MyDataBaseHelper myDB = new MyDataBaseHelper(context);
                                        myDB.updateProductData(modal.getProductId(), finalProdName, Integer.parseInt(String.valueOf(finalProdCashPrice)));
                                        db.collection("products").document(modal.getProductId())
                                                .update("productName", finalProdName, "productModel", finalProdModel, "productDescription",
                                                        finalProdDesc, "productQuantity", finalProdQuantity, "creditPayment", crPa,"boughtPrice",
                                                        finalProdBoughtPrice,"creditPricePercent", finalProdCreditPricePercent,
                                                        "creditPrice", finalProdCreditPrice, "cashPrice", finalProdCashPrice, "cashPricePercent", finalProdCashPricePercent);

                                        db.collection("SubCategories").whereEqualTo("subcategoryName", finalProdSubCategory).get()
                                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                                    if(!queryDocumentSnapshots.isEmpty()){
                                                        for(DocumentSnapshot d:queryDocumentSnapshots){
                                                            if(d.exists()){
                                                                db.collection("products").document(modal.getProductId())
                                                                        .update("subCategory", d.getId(), "category",d.get("categoryId"));
                                                            }
                                                        }
                                                    }
                                                });
                                        db.collection("brands").whereEqualTo("brandName", finalProdBrand).get()
                                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                                    if(!queryDocumentSnapshots.isEmpty()){
                                                        for(DocumentSnapshot d:queryDocumentSnapshots){
                                                            if(d.exists()){
                                                                db.collection("products").document(modal.getProductId())
                                                                        .update("brand", d.get("brandName"));
                                                            }
                                                        }
                                                    }
                                                });
                                        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                                .setContentText("Muvafaqqiyatli o'zgartirildi!")
                                                .setConfirmText("OK!")
                                                .setConfirmClickListener(sweetAlertDialog -> {
                                                    sweetAlertDialog.dismiss();
                                                    dialog.dismiss();
                                                })
                                                .show();


                                        progressBar.setVisibility(View.GONE);
                                        childItemAdapter2.notifyDataSetChanged();
                                    }
                                }});


                            sweetAlertDialog1.cancel();
                        })
                        .show();
                        }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }

    private void removeView(View view1, LinearLayout linearLayout) {
        linearLayout.removeView(view1);

    }

    private void getImageAndSpecData(FirebaseFirestore db, AdminProductModel modal, AdminProductFragmentProductAdapterImage childItemAdapter, AdminProductFragmentProductAdapterSpec childItemAdapter2) {
        db.collection("products").document(modal.getProductId()).addSnapshotListener((d, error) -> {
            if (d.exists() && d.get("imageUrl")!=null ){
                adminProductModelImage.clear();
                AdminProductModel prod = d.toObject(AdminProductModel.class);
                List<String> urlList = prod.getImageUrl();
                for(String i:urlList){
                    AdminProductModelImage dataModal = new AdminProductModelImage(i);
                    adminProductModelImage.add(dataModal);
                }
                childItemAdapter.notifyDataSetChanged();
            }
        });
        db.collection("products").document(modal.getProductId()).addSnapshotListener((d, error) -> {
            if (d.exists() && d.get("productSpecification")!=null ) {
                adminProductModelSpec.clear();
                AdminProductModel prod = d.toObject(AdminProductModel.class);
                assert prod != null;
                List<AdminProductSpecificationsModel> list = prod.getProductSpecification();
                adminProductModelSpec.addAll(list);
                childItemAdapter2.notifyDataSetChanged();
            }
        });
    }

    private boolean checkValidation(LinearLayout linearLayout) {
        spec.clear();
        spec = adminProductModelSpec;
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

        if (spec.size() == 0 &&  linearLayout.getChildCount()==0){
            result = true;
        }
        else if (spec.size() == 0 &&  linearLayout.getChildCount()!=0){
            result = false;
        }
        else if (!result){

        }
        return result;
    }

}
