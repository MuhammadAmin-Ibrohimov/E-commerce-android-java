package com.example.lidertrade.admin.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.lidertrade.R;
import com.example.lidertrade.admin.adapters.AdminDebtFragmentReturnedAdapter;

import com.example.lidertrade.admin.models.AdminCategoryModel;
import com.example.lidertrade.admin.models.AdminProductModel;
import com.example.lidertrade.admin.models.AdminReturnedProductsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminDebtFragmentReturned extends Fragment {

    View v;
    FirebaseFirestore firestore;

    Map<String, String> brandMap;
    Button  submit, reset;
    ProgressBar progressBar;

    Group hiddenGroup;
    CardView cardView;
    ImageView arrow;
    RecyclerView recyclerView;
    ArrayList<String> products;
    ArrayList<AdminReturnedProductsModel> modelArrayList;
    AdminDebtFragmentReturnedAdapter brandAdapter;
    SearchView searchView;
    TextView causeReturning;
    AutoCompleteTextView autoComplete;
    ArrayList<String> empty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.admin_debt_fragment_returned, container, false);


        firestore = FirebaseFirestore.getInstance();
        CollectionReference retProds = firestore.collection("ReturnedProducts");
        DocumentReference retProdsDoc = retProds.document();

        cardView = v.findViewById(R.id.base_cardview);
        arrow = v.findViewById(R.id.show);
        hiddenGroup = v.findViewById(R.id.card_group);
        addBrandMethod();
        products = new ArrayList<>();

        causeReturning = v.findViewById(R.id.causeReturning);
        autoComplete = v.findViewById(R.id.autoComplete);
        reset = v.findViewById(R.id.reset);
        submit = v.findViewById(R.id.submit);
        progressBar = v.findViewById(R.id.progressBar);
        brandMap = new HashMap<>();
        recyclerView = v.findViewById(R.id.recyclerView);
        searchView = v.findViewById(R.id.searchView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(false);
        modelArrayList = new ArrayList<>();
        brandAdapter =new AdminDebtFragmentReturnedAdapter(getContext(), modelArrayList);
        recyclerView.setAdapter(brandAdapter);
        empty = new ArrayList<>();
        loadProductsToChoose();

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
                        progressBar.setVisibility(View.GONE);
                        firestore.collection("ReturnedProducts").document(url).delete();
                        brandAdapter.notifyDataSetChanged();
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

        submit.setOnClickListener(view -> {
            String autoCompleteString = autoComplete.getText().toString();
            String causeReturningString = causeReturning.getText().toString();

            if (autoCompleteString.isEmpty()){
                new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                        .setContentText("Iltimos, avval mahsulot nomini tanlang!")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(SweetAlertDialog::cancel)
                        .show();
            }
            else if (causeReturningString.isEmpty()){
                new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                        .setContentText("Iltimos, avval qaytarish sababini kiritng!")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(SweetAlertDialog::cancel)
                        .show();
            }
            else {
                firestore.collection("products").whereEqualTo("productName",autoCompleteString ).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(DocumentSnapshot d: queryDocumentSnapshots.getDocuments()){
                                    if(d.exists()){
                                        firestore.collection("ReturnedProducts").document(d.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    DocumentSnapshot dd = task.getResult();
                                                    if(dd.exists()){
                                                        Map<String,Object> innerMap = (Map<String, Object>) dd.get("detailedData");
                                                        innerMap.put(String.valueOf(new Date().getTime()),causeReturningString);
                                                        int qn = Integer.parseInt(dd.get("quantity").toString())+1;
                                                        dd.getReference().update("quantity", qn, "detailedData", innerMap);
                                                    }
                                                    else {
                                                        Map<String , String> innerMap = new HashMap<>();
                                                        Map<String , Object> outerMap = new HashMap<>();
                                                        innerMap.put(String.valueOf(new Date().getTime()),causeReturningString );
                                                        outerMap.put("id", d.getId());
                                                        outerMap.put("quantity", 1);
                                                        outerMap.put("detailedData", innerMap);
                                                        firestore.collection("ReturnedProducts").document(d.getId()).set(outerMap);
                                                    }
                                                    brandAdapter.notifyDataSetChanged();
                                                    new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                            .setContentText("Yakunlandi!")
                                                            .setConfirmText("OK!")
                                                            .setConfirmClickListener(SweetAlertDialog::cancel)
                                                            .show();
                                                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                                                    hiddenGroup.setVisibility(View.GONE);
                                                    arrow.setImageResource(android.R.drawable.arrow_down_float);
                                                    autoComplete.setText("");
                                                    autoComplete.clearFocus();
                                                    causeReturning.setText("");
                                                    causeReturning.clearFocus();
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
            }


        });
        resetButtonClick();
        return v;
    }


    private void resetButtonClick() {
        reset.setOnClickListener(view -> {
            autoComplete.setText("");
            autoComplete.clearFocus();
            causeReturning.setText("");
            causeReturning.clearFocus();
        });
    }
    private void loadProductsToChoose() {
        firestore.collection("products").addSnapshotListener((value, error) -> {
            assert value != null;
            if (!value.isEmpty()){
                List<DocumentSnapshot> list = value.getDocuments();
                products.clear();
                for (DocumentSnapshot d : list) {
                    AdminProductModel dataModal = d.toObject(AdminProductModel.class);
                    assert dataModal != null;
                    products.add(dataModal.getProductName());
                }
                ArrayAdapter<String> adapterSub = new ArrayAdapter<>(requireContext(),
                        R.layout.admin_product_fragment_sub_category_drop_down,
                        products
                );
                autoComplete.setAdapter(adapterSub);
            }
        });
    }

    private void loadBrandDataRW() {
        firestore.collection("ReturnedProducts")
                .addSnapshotListener((value, error) -> {
                    assert value != null;
                    if (!value.isEmpty()) {
                        List<DocumentSnapshot> list = value.getDocuments();
                        modelArrayList.clear();
                        empty.clear();
                        for (DocumentSnapshot d : list) {
                            if(d.exists()){
                                AdminReturnedProductsModel dataModal = d.toObject(AdminReturnedProductsModel.class);
                                modelArrayList.add(dataModal);
                                brandAdapter.notifyDataSetChanged();
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
        ArrayList<AdminReturnedProductsModel> filteredList = new ArrayList<>();
        for (AdminReturnedProductsModel model: modelArrayList){
            if(model.getId().toString().toLowerCase().contains(s.toLowerCase().trim())){
                filteredList.add(model);
            }
        }
        this.brandAdapter.setFilteredList(filteredList);
        this.recyclerView.setAdapter(brandAdapter);
    }
}
