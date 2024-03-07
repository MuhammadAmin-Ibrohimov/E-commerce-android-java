package com.example.lidertrade.seller.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.lidertrade.R;
import com.example.lidertrade.admin.models.AdminProductModel;
import com.example.lidertrade.seller.adapters.SHFAllProductsAdapter;
import com.example.lidertrade.seller.adapters.SHFCategoryAdapter;
import com.example.lidertrade.seller.adapters.SHFSaleAdapter;
import com.example.lidertrade.seller.models.SellerCategoryModel;
import com.example.lidertrade.seller.models.SHFSaleModel;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;

public class SellerHomeFragment extends Fragment {

    private ArrayList<SellerCategoryModel> sellerCategoryModels;
    private ArrayList<SHFSaleModel> shfSaleModels;
    private ArrayList<AdminProductModel> shfAllProductsModels;


    private SHFCategoryAdapter shfCategoryAdapter;
    private SHFAllProductsAdapter shfAllProductsAdapter;
    private SHFSaleAdapter shfSaleAdapter;

    private RecyclerView sellerHFCategoryRW, sellerHFAllProductsRW, homeSaleRecyclerView;
    FirebaseFirestore db;
    private CollectionReference categories;
    private CollectionReference saleCollection;
    private CollectionReference products;
    private String filteredCategory;

    private MaterialCardView materialCardView;


    public SellerHomeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.seller_home_fragment, container, false);

        db = FirebaseFirestore.getInstance();

        sellerHFCategoryRW = view.findViewById(R.id.sellerHFCategoryRW);
        sellerCategoryModels = new ArrayList<>();
        sellerHFCategoryRW.setHasFixedSize(true);
        sellerHFCategoryRW.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categories = db.collection("Categories");
        shfCategoryAdapter = new SHFCategoryAdapter(sellerCategoryModels, getContext());
        sellerHFCategoryRW.setAdapter(shfCategoryAdapter);
        shfCategoryAdapter.notifyDataSetChanged();

        homeSaleRecyclerView = view.findViewById(R.id.sellerHFSaleRW);
        shfSaleModels = new ArrayList<>();
        homeSaleRecyclerView.setHasFixedSize(true);
        homeSaleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        saleCollection = db.collection("Sale");
        shfSaleAdapter = new SHFSaleAdapter(shfSaleModels, getContext());
        homeSaleRecyclerView.setAdapter(shfSaleAdapter);

        shfAllProductsModels = new ArrayList<>();
        sellerHFAllProductsRW = view.findViewById(R.id.sellerHFAllProductsRW);
        RecyclerView.LayoutManager layoutManager=new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL);
        sellerHFAllProductsRW.setLayoutManager(layoutManager);
        products = db.collection("products");
        shfAllProductsAdapter = new SHFAllProductsAdapter(shfAllProductsModels, getContext());
        sellerHFAllProductsRW.setAdapter(shfAllProductsAdapter);


        loadHomeCategoryRecyclerviewData();
        loadHomeSaleRecyclerviewData();
        loadHomeAllProductsRecyclerviewData();



        return view;
    }

    private void loadHomeCategoryRecyclerviewData(){
        categories.addSnapshotListener((value, error) -> {
            if (error!=null){
                Log.e(null,"onEvent", error);
                return;
            }
            if (value != null && !value.isEmpty()){
                sellerCategoryModels.clear();
                for (QueryDocumentSnapshot d:value){
                    SellerCategoryModel dataModal = d.toObject(SellerCategoryModel.class);
                    sellerCategoryModels.add(dataModal);
                    shfCategoryAdapter.notifyDataSetChanged();
                }
            }else {
                sellerCategoryModels.clear();
                Log.e(null,"Xatolik bor!!!!");
                shfCategoryAdapter.notifyDataSetChanged();
            }
        });
    }


    private void loadHomeSaleRecyclerviewData() {
        saleCollection.addSnapshotListener((value, error) -> {
            if (error!=null){
                Log.e(null,"onEvent", error);
                return;
            }
            if (value != null && !value.isEmpty()){
                shfSaleModels.clear();
                for (QueryDocumentSnapshot d:value){
                    SHFSaleModel dataModal = d.toObject(SHFSaleModel.class);
                    shfSaleModels.add(dataModal);
                    shfSaleAdapter.notifyDataSetChanged();
                }
            }else {
                shfSaleModels.clear();
                Log.e(null,"Xatolik bor!!!!");
                shfSaleAdapter.notifyDataSetChanged();
            }
        });
    }

    private void loadHomeAllProductsRecyclerviewData() {

        products.addSnapshotListener((value, error) -> {
            if (error!=null){
                Log.e(null,"onEvent", error);
                return;
            }
            if (value != null && !value.isEmpty()){
                shfAllProductsModels.clear();
                for (QueryDocumentSnapshot d:value){
                    AdminProductModel dataModal = d.toObject(AdminProductModel.class);
                    shfAllProductsModels.add(dataModal);
                    shfAllProductsAdapter.notifyDataSetChanged();
                }
            }else {
                shfAllProductsModels.clear();
                Log.e(null,"Xatolik bor!!!!");
                shfAllProductsAdapter.notifyDataSetChanged();
            }
        });
    }
}
